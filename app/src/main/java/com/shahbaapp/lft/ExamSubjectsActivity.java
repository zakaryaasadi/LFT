package com.shahbaapp.lft;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Adapter.ExamSubjectsAdapter;
import Controller.Api;
import Controller.DataFromApi;
import Models.ExamSubjectClass;
import Models.ExamSubjectResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ExamSubjectsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private long studentId;
    private ExamSubjectsAdapter bAdapter;
    private Api api;
    private List<ExamSubjectClass> examSubjects;
    private SwipeRefreshLayout swipe;
    private ImageView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_subject);

        swipe = findViewById(R.id.swipe);
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);
        swipe.setOnRefreshListener(onRefreshListener);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        examSubjects = new ArrayList<>();
        bAdapter = new ExamSubjectsAdapter(getApplicationContext(), examSubjects);
        recyclerView.setAdapter(bAdapter);
        goBack = findViewById(R.id.btn_back);

        Intent i = getIntent();
        studentId = i.getLongExtra("studentId", 0);

        fetchDataFromApi();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            examSubjects.clear();
            bAdapter.notifyDataSetChanged();
            fetchDataFromApi();
        }
    };


    private void fetchDataFromApi() {
        api = DataFromApi.getApi();
        swipe.setRefreshing(true);
        Call<ExamSubjectResult> call = api.GetMarks(studentId);

        call.enqueue(new Callback<ExamSubjectResult>() {
            @Override
            public void onResponse(Call<ExamSubjectResult> call, Response<ExamSubjectResult> response) {
                swipe.setRefreshing(false);
                ExamSubjectResult examSubjectRes = response.body();
                examSubjects.addAll(examSubjectRes.results);
                bAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ExamSubjectResult> call, Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
            }
        });

    }

}

