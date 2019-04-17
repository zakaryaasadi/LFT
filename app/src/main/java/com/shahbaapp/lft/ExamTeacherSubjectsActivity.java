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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import Adapter.ExamTeacherSubjectsAdapter;
import Models.SubjectClass;


public class ExamTeacherSubjectsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExamTeacherSubjectsAdapter bAdapter;
    private List<SubjectClass> list;
    private SwipeRefreshLayout swipe;
    private ImageView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_teacher_subjects);

        swipe = findViewById(R.id.swipe);
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Intent i = getIntent();
        String strJson = i.getStringExtra("subjects");
        list = new Gson().fromJson(strJson, new TypeToken<List<SubjectClass>>(){}.getType());
        bAdapter = new ExamTeacherSubjectsAdapter(getApplicationContext(), list);
        recyclerView.setAdapter(bAdapter);
        goBack = findViewById(R.id.btn_back);


        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }


}

