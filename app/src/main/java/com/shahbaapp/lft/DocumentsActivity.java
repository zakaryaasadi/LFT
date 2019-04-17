package com.shahbaapp.lft;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Adapter.DocumentAdapter;
import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Models.DocumentClass;
import Models.DocumentResult;
import Models.UserClass;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DocumentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DocumentAdapter bAdapter;
    private Api api;
    public static List<DocumentClass> documents;
    private SwipeRefreshLayout swipe;
    private ImageView goBack;
    private long subjectId;
    public static List<UserClass> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        swipe = findViewById(R.id.swipe);
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);
        swipe.setOnRefreshListener(onRefreshListener);

        documents = new ArrayList<>();
        students = new ArrayList<>();

        Intent i = getIntent();
        subjectId = i.getLongExtra("subjectId",0);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        bAdapter = new DocumentAdapter(getApplicationContext(), documents, subjectId);
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
            documents.clear();
            bAdapter.notifyDataSetChanged();
            fetchDataFromApi();
        }
    };


    private void fetchDataFromApi() {
        api = DataFromApi.getApi();
        swipe.setRefreshing(true);
        Call<DocumentResult> call = api.GetDocuments(Common.getUser().id, subjectId);

        call.enqueue(new Callback<DocumentResult>() {
            @Override
            public void onResponse(Call<DocumentResult> call, Response<DocumentResult> response) {
                swipe.setRefreshing(false);
                DocumentResult r = response.body();
                if(r.results.documents.size() > 0 ){
                    documents.addAll(r.results.documents);
                    students.addAll(r.results.students);
                    bAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getApplicationContext(), "There are not documents", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DocumentResult> call, Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
            }
        });

    }

}

