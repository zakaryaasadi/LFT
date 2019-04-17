package com.shahbaapp.lft;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;
import Adapter.UserMessageAdapter;
import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Models.NoteStudentClass;
import Models.UserClass;
import Models.UsersResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserMessageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserMessageAdapter bAdapter;
    private List<UserClass> users;
    public RelativeLayout btnSend;
    private static final int REQUEST_CODE = 1111;

    View view;
    private SwipeRefreshLayout swipe;
    private Api api;
    private TextView txtSend;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message);

        btnSend = findViewById(R.id.btn_send);
        txtSend = findViewById(R.id.txt_send);
        swipe = findViewById(R.id.swipe);
        swipe.getLayoutParams().height = Common.getScreenHeight(this) + 1000;
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);
        swipe.setOnRefreshListener(onRefreshListener);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        users = new ArrayList<>();
        Common.userMessageSent.clear();
        bAdapter = new UserMessageAdapter(this, users, txtSend, btnSend);
        recyclerView.setAdapter(bAdapter);

        if (Common.getUser() != null)
            fetchDataFromApi();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserMessageActivity.this, AddMessageActivity.class);
                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            users.clear();
            bAdapter.notifyDataSetChanged();
            fetchDataFromApi();
        }
    };

    private void fetchDataFromApi() {
        swipe.setRefreshing(true);
        api = DataFromApi.getApi();

        Call<UsersResult> call = api.GetAllUser();

        call.enqueue(new Callback<UsersResult>() {
            @Override
            public void onResponse(Call<UsersResult> call, Response<UsersResult> response) {
                swipe.setRefreshing(false);
                UsersResult request = response.body();
                if (request.results != null) {
                    users.clear();
                    users.addAll(request.results);
                    bAdapter.notifyDataSetChanged();
                    saveToBb();
                } else
                    Toast.makeText(getApplicationContext(), request.status, Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailure(Call<UsersResult> call, Throwable t) {
                swipe.setRefreshing(false);
                fetchDataFromDb();
                bAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDataFromDb() {
        users.clear();
        users.addAll(SugarRecord.listAll(NoteStudentClass.class, "ID DESC"));
    }

    private void saveToBb() {
        SugarRecord.deleteAll(NoteStudentClass.class);
        SugarRecord.saveInTx(users);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE)
            if(resultCode == RESULT_OK)
                finish();
    }
}
