package com.shahbaapp.lft;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import Adapter.DetailImageAdapter;
import Controller.Api;
import Controller.DataFromApi;
import Models.NewsClass;
import Models.NewsDetailResult;
import Utils.CustomDate;
import Utils.FileProcessing;
import Utils.ImageProcessing;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsDetailActivity extends AppCompatActivity {

    private ImageView profileImage,newsImage, share, goBack;
    private TextView personName, userName, date, title, body;

    private RecyclerView recyclerView;
    private List<String> images;
    private NewsClass news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent i = getIntent();

        String str = i.getStringExtra("news");

        news = new Gson().fromJson(str, NewsClass.class);


        personName = (TextView) findViewById(R.id.person_name);
        userName = (TextView) findViewById(R.id.user_name);
        date = (TextView) findViewById(R.id.date);
        title = (TextView) findViewById(R.id.title);
        body = (TextView) findViewById(R.id.body);


        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        images = new ArrayList<>();
        DetailImageAdapter bAdapter = new DetailImageAdapter(this, images);
        recyclerView.setAdapter(bAdapter);

        share = (ImageView) findViewById(R.id.share);
        profileImage = (ImageView) findViewById(R.id.profile_image);
        newsImage = (ImageView) findViewById(R.id.image);
        goBack = findViewById(R.id.btn_back);


        personName.setText(news.getPersonName());
        userName.setText(news.getUserName());
        date.setText(CustomDate.format(news.getCreationDate()));
        title.setText(news.getTitle());
        body.setText(news.getBody());

        if(news.getProfileImage() != null){
            profileImage.setImageBitmap(ImageProcessing.base64ToBitmap(news.getProfileImage()));
        }


        if(news.getNewsImage() != null){
            newsImage.setImageBitmap(ImageProcessing.base64ToBitmap(news.getNewsImage()));
        }


        share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!news.getSharable())
                    return;

                if(news.getNewsImage() != null)
                    FileProcessing.shareFile(FileProcessing.saveImageInCache(news.getNewsImage()), news.getTitle(), news.getBody());
                else
                    FileProcessing.shareFile("", news.getTitle(), news.getBody());
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fetchDataFromApi();

    }



    private void fetchDataFromApi(){
        Api api = DataFromApi.getApi();

        Call<NewsDetailResult> call = api.NewsDetail(news.getId());

        call.enqueue(new Callback<NewsDetailResult>() {
            @Override
            public void onResponse(Call<NewsDetailResult> call, Response<NewsDetailResult> response) {
                NewsDetailResult fetch = response.body();
                if(fetch.results != null){
                    images.addAll(fetch.results);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<NewsDetailResult> call, Throwable t) {
            }
        });
    }

}
