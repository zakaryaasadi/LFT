package Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.orm.SugarRecord;
import com.shahbaapp.lft.R;

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


public class HomeworkActivity extends Fragment {


    private RecyclerView recyclerView;
    private MessageAdapter bAdapter;
    private List<MessageClass> messages;
    private SwipeRefreshLayout swipe;
    private Api api;
    private EndlessRecyclerViewScrollListener scrollListener;

    View view;
    private int totalHomeworks;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_homework,container,false);

        swipe =view.findViewById(R.id.swipe);
        swipe.getLayoutParams().height = Common.getScreenHeight(getActivity()) + 250;
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);
        swipe.setOnRefreshListener(onRefreshListener);

        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        messages = new ArrayList<>();
        bAdapter = new MessageAdapter(getActivity(), messages);
        recyclerView.setAdapter(bAdapter);

        if(Common.getUser() != null)
            fetchDataFromApi(1);


        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(totalHomeworks != totalItemsCount){
                    if(page * 5 > totalItemsCount)
                        fetchDataFromApi(page);
                    else
                        fetchDataFromApi(page + 1);
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);


        return view;
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener () {
        @Override
        public void onRefresh() {
            messages.clear();
            bAdapter.notifyDataSetChanged();
            scrollListener.resetState();
            fetchDataFromApi(1);
        }
    };

    private void fetchDataFromApi(int page) {
        swipe.setRefreshing(true);
        api = DataFromApi.getApi();

        Call<MessageResult> call = api.Homework(Common.getUser().id, page);

        call.enqueue(new Callback<MessageResult>() {
            @Override
            public void onResponse(Call<MessageResult> call, Response<MessageResult> response) {
                swipe.setRefreshing(false);
                MessageResult request = response.body();
                if (request.results != null) {
                    messages.clear();
                    messages.addAll(request.results);
                    totalHomeworks = request.numResult;
                    bAdapter.notifyDataSetChanged();
                    saveToBb();
                } else
                    Toast.makeText(getContext(), "No exist Homework", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailure(Call<MessageResult> call, Throwable t) {
                swipe.setRefreshing(false);
                fetchDataFromDb();
                bAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDataFromDb() {
        if(messages.size() < 5){
            messages.clear();
            messages.addAll( SugarRecord.find(MessageClass.class, "STATUS = ?",new String[]{"2"},null,"ID DESC",null) );
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
