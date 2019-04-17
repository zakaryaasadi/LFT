package com.shahbaapp.lft;

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

import Adapter.DocumentClassesSubjectsAdapter;
import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Models.ClassSubjectClass;
import Models.ClassSubjectResult;
import Models.SubjectClass;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DocumentClassesSubjectsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DocumentClassesSubjectsAdapter bAdapter;
    private Api api;
    private List<SubjectClass> list;
    private SwipeRefreshLayout swipe;
    private ImageView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_teacher_classes);

        swipe = findViewById(R.id.swipe);
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);
        swipe.setOnRefreshListener(onRefreshListener);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        list = new ArrayList<>();
        bAdapter = new DocumentClassesSubjectsAdapter(getApplicationContext(), list);
        recyclerView.setAdapter(bAdapter);
        goBack = findViewById(R.id.btn_back);


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
            list.clear();
            bAdapter.notifyDataSetChanged();
            fetchDataFromApi();
        }
    };


    private void fetchDataFromApi() {
        api = DataFromApi.getApi();
        swipe.setRefreshing(true);
        Call<ClassSubjectResult> call = api.GetClasses(Common.getUser().id);

        call.enqueue(new Callback<ClassSubjectResult>() {
            @Override
            public void onResponse(Call<ClassSubjectResult> call, Response<ClassSubjectResult> response) {
                swipe.setRefreshing(false);
                ClassSubjectResult examSubjectRes = response.body();
                load(examSubjectRes.results);
                bAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ClassSubjectResult> call, Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void load(List<ClassSubjectClass> list){

        for(ClassSubjectClass mClass : list){
            for(SubjectClass subject : mClass.subjects)
                subject.name = mClass.name + " - " + subject.name;

            this.list.addAll(mClass.subjects);
        }
    }

}

