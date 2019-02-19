package shahbasoft.lft;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Adapter.DetailImageAdapter;
import Controller.Api;
import Controller.DataFromApi;

import Models.NewsDetailResult;
import Utils.FileProcessing;
import Utils.ImageProcessing;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsDetailActivity extends AppCompatActivity {

    private ImageView profileImage,newsImage, share, goBack;
    private TextView personName, userName, date, title, body;

    private String strPerson, strUserName, strDate, strTitle, strBody, strProfileImage, strNewsImage;
    private Boolean sharable;
    private long newsId;
    private RecyclerView recyclerView;
    private List<String> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent i = getIntent();

        newsId = i.getLongExtra("newsId", 0);
        strPerson = i.getStringExtra("personName");
        strUserName = i.getStringExtra("userName");
        strDate = i.getStringExtra("date");
        strTitle = i.getStringExtra("title");
        strBody = i.getStringExtra("body");
        strProfileImage = i.getStringExtra("profileImage");
        strNewsImage = i.getStringExtra("newsImage");
        sharable = i.getBooleanExtra("sharable",false);


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


        personName.setText(strPerson);
        userName.setText(strUserName);
        date.setText(strDate);
        title.setText(strTitle);
        body.setText(strBody);

        if(strProfileImage != null){
            profileImage.setImageBitmap(ImageProcessing.base64ToBitmap(strProfileImage));
        }


        if(strNewsImage != null){
            newsImage.setImageBitmap(ImageProcessing.base64ToBitmap(strNewsImage));
        }


        share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!sharable)
                    return;

                FileProcessing.shareFile(getApplicationContext(),
                        getApplicationContext().getExternalCacheDir() + "/image.jpg", strTitle, strBody);
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

        Call<NewsDetailResult> call = api.NewsDetail(newsId);

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
