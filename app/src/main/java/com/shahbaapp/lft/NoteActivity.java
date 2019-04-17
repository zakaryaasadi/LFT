package com.shahbaapp.lft;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

import Adapter.NoteAdapter;
import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Models.NoteClass;
import Models.NoteResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NoteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private long studentId;
    private NoteAdapter bAdapter;
    private Api api;
    private List<NoteClass> notes;
    private SwipeRefreshLayout swipe;
    private TextView noNote;
    private ImageView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        swipe = findViewById(R.id.swipe);
        swipe.getLayoutParams().height = Common.getScreenHeight(this);
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);
        swipe.setOnRefreshListener(onRefreshListener);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        notes = new ArrayList<>();
        bAdapter = new NoteAdapter(getApplicationContext(), notes);
        recyclerView.setAdapter(bAdapter);

        noNote = findViewById(R.id.no_note);
        FloatingActionButton fab = findViewById(R.id.fab);
        goBack = findViewById(R.id.btn_back);

        Intent i = getIntent();
        studentId = i.getLongExtra("studentId",0);

        if(studentId == Common.getUser().id)
            fab.setVisibility(View.GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddNoteActivity.class);
                i.putExtra("studentId",studentId);
                startActivity(i);

            }
        });


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
            notes.clear();
            noNote.setVisibility(View.GONE);
            bAdapter.notifyDataSetChanged();
            fetchDataFromApi();
        }
    };


    private void fetchDataFromApi() {
        api = DataFromApi.getApi();
        swipe.setRefreshing(true);
        Call<NoteResult> call = api.Note(studentId);

        call.enqueue(new Callback<NoteResult>() {
            @Override
            public void onResponse(Call<NoteResult> call, Response<NoteResult> response) {
                swipe.setRefreshing(false);
                NoteResult note = response.body();
                if (note.results != null) {
                    if(note.results.size() == 0)noNote.setVisibility(View.VISIBLE);
                    notes.addAll(note.results);
                    bAdapter.notifyDataSetChanged();
                    saveToBb();
                } else
                    Toast.makeText(getApplicationContext(), note.status, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<NoteResult> call, Throwable t) {
                swipe.setRefreshing(false);
                fetchDataFromDb();
                bAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void fetchDataFromDb() {
            notes.clear();
            notes.addAll(SugarRecord.listAll(NoteClass.class, "ID DESC"));
    }

    private void saveToBb() {
        SugarRecord.deleteAll(NoteClass.class);
        for (NoteClass item : notes)
            item.save();
    }

    @Override
    protected void onStart() {
        super.onStart();
        notes.clear();
        noNote.setVisibility(View.GONE);
        bAdapter.notifyDataSetChanged();
        fetchDataFromApi();
    }
}
