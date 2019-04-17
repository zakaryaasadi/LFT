package Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

import Adapter.MessageSentAdapter;
import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Utils.EndlessRecyclerViewScrollListener;

import Models.MessageSentClass;
import Models.MessageSentResult;
import Models.UserMessageClass;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.shahbaapp.lft.R;

public class MessageSentFragment extends Fragment {

    private RecyclerView recyclerView;
    private MessageSentAdapter bAdapter;
    private List<MessageSentClass> messages;

    View view;
    private Api api;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int totalMessages;
    private ImageView bRelaod;
    private TextView noMessage;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.content_message, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        bRelaod = view.findViewById(R.id.btn_reload);

        noMessage = view.findViewById(R.id.no_note);


        messages = new ArrayList<>();
        bAdapter = new MessageSentAdapter(getActivity(), messages);
        recyclerView.setAdapter(bAdapter);

        if (Common.getUser() != null)
            fetchDataFromApi(1);


        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalMessages != totalItemsCount) {
                    if (page * 5 > totalItemsCount)
                        fetchDataFromApi(page);
                    else
                        fetchDataFromApi(page + 1);
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);


        bRelaod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickReload();
            }
        });

        return view;
    }

    private void onClickReload() {
        noMessage.setVisibility(View.GONE);
        bRelaod.setVisibility(View.GONE);
        messages.clear();
        bAdapter.notifyDataSetChanged();
        scrollListener.resetState();
        fetchDataFromApi(1);
    }

    private void fetchDataFromApi(int page) {
        api = DataFromApi.getApi();

        Call<MessageSentResult> call = api.MessageSent(Common.getUser().id, page);

        call.enqueue(new Callback<MessageSentResult>() {
            @Override
            public void onResponse(Call<MessageSentResult> call, Response<MessageSentResult> response) {
                MessageSentResult request = response.body();
                if (request.results != null) {
                    messages.addAll(request.results);
                    totalMessages = request.numResult;
                    bAdapter.notifyDataSetChanged();
                    runLayoutAnimation();
                    saveToBb();
                }else{
                    noMessage.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onFailure(Call<MessageSentResult> call, Throwable t) {
                fetchDataFromDb();
                bAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDataFromDb() {
        if (messages.size() < 5) {
            messages.clear();
            messages.addAll(SugarRecord.listAll(MessageSentClass.class, "ID DESC"));
        }
    }

    private void saveToBb() {
        for (MessageSentClass item : messages) {
            String str = new Gson().toJson(Common.getUser());
            item.setFromUser(new Gson().fromJson(str, UserMessageClass.class));
            item.save();
            SugarRecord.save(item);
        }
    }

    private void runLayoutAnimation() {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_left);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

}
