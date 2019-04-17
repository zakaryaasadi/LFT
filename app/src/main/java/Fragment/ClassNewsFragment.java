package Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.orm.SugarRecord;
import com.shahbaapp.lft.R;

import java.util.ArrayList;
import java.util.List;

import Adapter.NewsRecycleAdapter;
import Controller.Common;
import Controller.Api;
import Controller.DataFromApi;
import Models.NewsClass;
import Models.NewsResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ClassNewsFragment extends Fragment {

    View view;

    private RecyclerView recyclerView;
    private NewsRecycleAdapter bAdapter;
    private ArrayList<NewsClass> newsList;
    private Api api;
    private SwipeRefreshLayout swipe;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_private, container, false);

        swipe = view.findViewById(R.id.swipe);
        swipe.getLayoutParams().height = Common.getScreenHeight(getActivity());
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);
        swipe.setOnRefreshListener(onRefreshListener);

        newsList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        bAdapter = new NewsRecycleAdapter(getActivity(), newsList);
        recyclerView.setAdapter(bAdapter);

        fetchDataFromApi();


        return view;
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
        if (Common.getUser() != null) {

            Call<NewsResult> call = api.ClassNews(Common.categoryClass.getId(), Common.getUser().id);

            call.enqueue(new Callback<NewsResult>() {
                @Override
                public void onResponse(Call<NewsResult> call, Response<NewsResult> response) {
                    swipe.setRefreshing(false);
                    NewsResult news = response.body();
                    newsList.addAll(news.results);
                    bAdapter.notifyDataSetChanged();
                    runLayoutAnimation();
                    saveToDb();
                }


                @Override
                public void onFailure(Call<NewsResult> call, Throwable t) {
                    swipe.setRefreshing(false);
                    fetchDataFromDb();
                    bAdapter.notifyDataSetChanged();
                    runLayoutAnimation();
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void fetchDataFromDb() {
        List<NewsClass> fromDb = SugarRecord.find(NewsClass.class, "PRIVATE_NEWS_TYPE = 2 and SUBCATEGORY_FK = ?", new String[]{String.valueOf(Common.categoryClass.getId())}, null, "CREATION_DATE desc", null);
        if (fromDb.size() > 0) {
            newsList.addAll(fromDb);
        }
    }

    private void saveToDb() {
        SugarRecord.deleteAll(NewsClass.class, "PRIVATE_NEWS_TYPE = 2 and SUBCATEGORY_FK = ?", String.valueOf(Common.categoryClass.getId()));
        for (NewsClass item : newsList)
            item.save();
    }

    private void runLayoutAnimation() {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_right);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
}