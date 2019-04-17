package com.shahbaapp.lft;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import Adapter.ExamTeacherStudentAdapter;
import Controller.Api;
import Controller.DataFromApi;
import Models.ExamStudentsDontMarkResult;
import Models.StudentClass;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ExamTeacherStudentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExamTeacherStudentAdapter bAdapter;
    public  static List<StudentClass> list;
    private ImageView goBack;
    private TextView title;
    private long examId;
    private float maxMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_teacher_student);
        title = findViewById(R.id.title);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Intent i = getIntent();
        examId = i.getLongExtra("examId", 0);
        maxMark = i.getFloatExtra("maxMark", 0);
        String txtTitle = i.getStringExtra("title");
        String strJson = i.getStringExtra("students");

        title.setText(txtTitle);
        list = new Gson().fromJson(strJson, new TypeToken<List<StudentClass>>(){}.getType());
        bAdapter = new ExamTeacherStudentAdapter(getApplicationContext(), list);
        recyclerView.setAdapter(bAdapter);
        goBack = findViewById(R.id.btn_back);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Api api = DataFromApi.getApi();
                Call<ExamStudentsDontMarkResult> call = api.GetStudentsDontMark(examId);

                call.enqueue(new Callback<ExamStudentsDontMarkResult>() {
                    @Override
                    public void onResponse(Call<ExamStudentsDontMarkResult> call, Response<ExamStudentsDontMarkResult> response) {
                        ExamStudentsDontMarkResult result = response.body();
                        if (result.results != null) {
                            if(result.results.size() > 0){
                                Intent i = new Intent(ExamTeacherStudentActivity.this, AddMarkActivity.class);
                                i.putExtra("examId", examId);
                                i.putExtra("maxMark", maxMark);
                                i.putExtra("students",new Gson().toJson(result.results));
                                startActivity(i);
                            }else{
                                Toast.makeText(getApplicationContext(), "All students have marks",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }


                    @Override
                    public void onFailure(Call<ExamStudentsDontMarkResult> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "No Internet",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        bAdapter.notifyDataSetChanged();
    }
}

