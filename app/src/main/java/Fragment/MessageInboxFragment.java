package Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

import Adapter.MessageAdapter;
import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Utils.EndlessRecyclerViewScrollListener;
import Models.MessageClass;
import Models.MessageResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.shahbaapp.lft.R;

public class MessageInboxFragment extends Fragment {

    private RecyclerView recyclerView;
    private MessageAdapter bAdapter;
    private List<MessageClass> messages;

    View view;
    private Api api;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int totalMessages;
    private ImageView bRelaod;
    private TextView noMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.content_message,container,false);

        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        bRelaod = view.findViewById(R.id.btn_reload);

        noMessage = view.findViewById(R.id.no_note);

        messages = new ArrayList<>();
        bAdapter = new MessageAdapter(getActivity(), messages);
        recyclerView.setAdapter(bAdapter);

        if(Common.getUser() != null)
            fetchDataFromApi(1);


        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(totalMessages != totalItemsCount){
                    if(page * 5 > totalItemsCount)
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

        Call<MessageResult> call = api.Message(Common.getUser().id, page);

        call.enqueue(new Callback<MessageResult>() {
            @Override
            public void onResponse(Call<MessageResult> call, Response<MessageResult> response) {
                MessageResult request = response.body();
                if (request.results != null) {
                    messages.addAll(request.results);
                    bAdapter.notifyDataSetChanged();
                    totalMessages = request.numResult;
                    saveToBb();
                }else{
                    noMessage.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onFailure(Call<MessageResult> call, Throwable t) {
                bRelaod.setVisibility(View.VISIBLE);
                fetchDataFromDb();
                bAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDataFromDb() {
        if(messages.size() < 5){
            messages.clear();
            messages.addAll(SugarRecord.find(MessageClass.class, "STATUS = ?",new String[]{"1"},null,"ID DESC",null));
        }
    }

    private void saveToBb() {
        for (MessageClass item : messages){
            item.save();
            SugarRecord.save(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}
