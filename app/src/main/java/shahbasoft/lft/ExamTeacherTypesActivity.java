package shahbasoft.lft;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Adapter.ExamTeacherTypesAdapter;
import Controller.Api;
import Controller.DataFromApi;
import Models.ExamsStudentClass;
import Models.ExamsStudentResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ExamTeacherTypesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExamTeacherTypesAdapter bAdapter;
    private Api api;
    private List<ExamsStudentClass> list;
    private SwipeRefreshLayout swipe;
    private ImageView goBack;
    private long subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_teacher_types);

        swipe = findViewById(R.id.swipe);
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);
        swipe.setOnRefreshListener(onRefreshListener);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        list = new ArrayList<>();
        bAdapter = new ExamTeacherTypesAdapter(getApplicationContext(), list);
        recyclerView.setAdapter(bAdapter);
        goBack = findViewById(R.id.btn_back);

        Intent i = getIntent();
        subjectId = i.getLongExtra("subjectId",0);
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
        Call<ExamsStudentResult> call = api.GetExamsStudents(subjectId);

        call.enqueue(new Callback<ExamsStudentResult>() {
            @Override
            public void onResponse(Call<ExamsStudentResult> call, Response<ExamsStudentResult> response) {
                swipe.setRefreshing(false);
                ExamsStudentResult examSubjectRes = response.body();
                list.addAll(examSubjectRes.results);
                bAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ExamsStudentResult> call, Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
            }
        });

    }

}

