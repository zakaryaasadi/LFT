package com.shahbaapp.lft;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;
import Adapter.NewsCategoryAdapter;
import Controller.Api;
import Controller.DataFromApi;
import Models.CategoryClass;
import Models.CategoryResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsCategoryAdapter bAdapter;
    private List<CategoryClass> categories = new ArrayList<>();
    private Toolbar toolbar;
    private Api api;
    private SwipeRefreshLayout swipe;
    TextView slogen;
    ImageView logo;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slogen = findViewById(R.id.slogen);
        logo = findViewById(R.id.logo);

        swipe = findViewById(R.id.swipe);
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);
        swipe.setOnRefreshListener(onRefreshListener);
        swipe.setRefreshing(true);


        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(NewsCategoryActivity.this,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        bAdapter = new NewsCategoryAdapter(NewsCategoryActivity.this, categories);
        recyclerView.setAdapter(bAdapter);

        fetchDataFromApi();

        slogen.setText(AppLauncher.getApplicationName(getApplicationContext()));
        logo.setImageDrawable(AppLauncher.getApplicationIcon(getApplicationContext()));


        if (PermissionChecker.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }

    }




    private void fetchDataFromApi(){
        api = DataFromApi.getApi();

        Call<CategoryResult> call = api.NewsCategory();

        call.enqueue(new Callback<CategoryResult>() {
            @Override
            public void onResponse(Call<CategoryResult> call, Response<CategoryResult> response) {
                toolbar.setTitle("Choose Category");
                swipe.setRefreshing(false);

                CategoryResult fetch = response.body();
                if(categories != null && fetch != null){
                    categories.clear();
                    categories.addAll(fetch.results);
                    bAdapter.notifyDataSetChanged();
                    runLayoutAnimation();
                    saveToDb();

                }else{
                    fetchDateFromDb();
                }
            }


            @Override
            public void onFailure(Call<CategoryResult> call, Throwable t) {
                toolbar.setTitle("Waiting network ...");
                swipe.setRefreshing(false);
                fetchDateFromDb();
                Toast.makeText(getApplicationContext(), t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDateFromDb(){
        if(SugarRecord.count(CategoryClass.class) > 0) {
            categories.clear();
            categories.addAll(SugarRecord.listAll(CategoryClass.class));
            bAdapter.notifyDataSetChanged();
            runLayoutAnimation();
        }
    }

    private void saveToDb(){
        for(CategoryClass category : SugarRecord.listAll(CategoryClass.class))
            category.delete();

        for(CategoryClass category : categories)
            category.save();
    }

    private void runLayoutAnimation() {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipe.setRefreshing(true);
            fetchDataFromApi();
        }
    };

}
