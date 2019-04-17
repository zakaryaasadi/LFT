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

import java.util.ArrayList;
import java.util.List;

import Adapter.ExamStudentAdapter;
import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Models.NoteStudentClass;
import Models.NoteStudentResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.shahbaapp.lft.R;



public class ExamStudentActivity extends Fragment {

    private RecyclerView recyclerView;
    private ExamStudentAdapter bAdapter;
    private Api api;
    private List<NoteStudentClass> children;
    private SwipeRefreshLayout swipe;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_list_item, container, false);
        swipe = view.findViewById(R.id.swipe);
        swipe.getLayoutParams().height = Common.getScreenHeight(getActivity());
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);
        swipe.setOnRefreshListener(onRefreshListener);

        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        children = new ArrayList<>();
        bAdapter = new ExamStudentAdapter(getContext(), children);
        recyclerView.setAdapter(bAdapter);


        fetchDataFromApi();

        return view;

    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            children.clear();
            bAdapter.notifyDataSetChanged();
            fetchDataFromApi();
        }
    };


    private void fetchDataFromApi() {
        api = DataFromApi.getApi();
        swipe.setRefreshing(true);
        Call<NoteStudentResult> call = api.GetChildren(Common.getUser().id);

        call.enqueue(new Callback<NoteStudentResult>() {
            @Override
            public void onResponse(Call<NoteStudentResult> call, Response<NoteStudentResult> response) {
                swipe.setRefreshing(false);
                NoteStudentResult ch = response.body();
                if (ch.results != null)
                    if(ch.results.size() > 0) {
                        children.addAll(ch.results);
                        bAdapter.notifyDataSetChanged();
                    }
            }

            @Override
            public void onFailure(Call<NoteStudentResult> call, Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            }
        });

    }
    
}