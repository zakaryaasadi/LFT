package com.shahbaapp.lft;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import Adapter.ExamAdapter;
import Models.ExamClass;


public class MarksActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExamAdapter bAdapter;
    private List<ExamClass> exams;
    private ImageView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Details");


        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Intent i = getIntent();
        String strJson = i.getStringExtra("exam");
        exams = new Gson().fromJson(strJson, new TypeToken<List<ExamClass>>(){}.getType());
        bAdapter = new ExamAdapter(getApplicationContext(), exams);
        recyclerView.setAdapter(bAdapter);
        goBack = findViewById(R.id.btn_back);




//        goBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });


    }


}

