package com.shahbaapp.lft;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import Adapter.NewsProfileAdapter;
import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Models.NewsClass;
import Models.NewsResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsProfileActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private int userId;
    private NewsProfileAdapter bAdapter;
    private Api api;
    private List<NewsClass> newsList;
    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        swipe = findViewById(R.id.swipe);
        swipe.getLayoutParams().height = Common.getScreenHeight(this);
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);
        swipe.setOnRefreshListener(onRefreshListener);

        recyclerView = findViewById(R.id.news_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        newsList = new ArrayList<>();
        bAdapter = new NewsProfileAdapter(getApplicationContext(), newsList);
        recyclerView.setAdapter(bAdapter);

        Intent i = getIntent();

        userId = i.getIntExtra("userId",0);

        fetchDataFromApi();

    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            newsList.clear();
            bAdapter.notifyDataSetChanged();
            fetchDataFromApi();
        }
    };

    private void fetchDataFromApi() {
        api = DataFromApi.getApi();
        swipe.setRefreshing(true);
        Call<NewsResult> call = api.ProfileNews(userId);

        call.enqueue(new Callback<NewsResult>() {
            @Override
            public void onResponse(Call<NewsResult> call, Response<NewsResult> response) {
                swipe.setRefreshing(false);
                NewsResult news = response.body();
                if (news.results != null) {
                    newsList.addAll(news.results);
                    runLayoutAnimation();
                } else
                    Toast.makeText(getApplicationContext(), news.status, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<NewsResult> call, Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void runLayoutAnimation() {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

}
