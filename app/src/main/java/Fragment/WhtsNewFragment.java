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
import com.orm.SugarRecord;
import java.util.ArrayList;
import java.util.List;
import Adapter.NewsRecycleAdapter;
import Controller.Common;
import Controller.Api;
import Controller.DataFromApi;
import Utils.EndlessRecyclerViewScrollListener;
import Models.NewsClass;
import Models.NewsResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.shahbaapp.lft.R;

public class WhtsNewFragment extends Fragment {

    View view;

    private Api api;
    private NewsRecycleAdapter bAdapter;


    private RecyclerView recyclerView;


    private List<NewsClass> newsList;

    private SwipeRefreshLayout swipe;
    private RecyclerView.LayoutManager layoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    int totalNews = 0;
    public long subcategoryId;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_whts_new, container, false);
        newsList = new ArrayList<>();

        swipe = view.findViewById(R.id.swipe);
        swipe.getLayoutParams().height = Common.getScreenHeight(getActivity()) + 250;
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);
        swipe.setRefreshing(true);
        swipe.setOnRefreshListener(onRefreshListener);



        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        bAdapter = new NewsRecycleAdapter(getActivity(), newsList);
        recyclerView.setAdapter(bAdapter);

        api = DataFromApi.getApi();
        fetchDataFromApi(1);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalNews != totalItemsCount) {
                    if (page * 5 > totalItemsCount)
                        fetchDataFromApi(page);
                    else
                        fetchDataFromApi(page + 1);
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);


        return view;
    }


    private void fetchDataFromApi(final int page) {
        Call<NewsResult> call = api.News(subcategoryId, page);

        call.enqueue(new Callback<NewsResult>() {
            @Override
            public void onResponse(Call<NewsResult> call, Response<NewsResult> response) {
                swipe.setRefreshing(false);
                NewsResult news = response.body();
                if (news.results != null) {
                    addNews(page, news.results);
                    bAdapter.notifyDataSetChanged();
//                            runLayoutAnimation();
                    totalNews = news.numResult;
                    saveToDb(WhtsNewFragment.this.subcategoryId);
                }
            }

            @Override
            public void onFailure(Call<NewsResult> call, Throwable t) {
                swipe.setRefreshing(false);
                fetchDataFromDb(WhtsNewFragment.this.subcategoryId);
                bAdapter.notifyDataSetChanged();
//                        runLayoutAnimation();
            }
        });
    }


    private void fetchDataFromDb(long id) {
        if (newsList.size() < 5) {
            List<NewsClass> temp = SugarRecord.find(NewsClass.class, "PRIVATE_NEWS_TYPE = 0 and SUBCATEGORY_FK = ?", new String[]{String.valueOf(id)}, null, "TYPE ASC, CREATION_DATE DESC", null);
            if (temp.size() > 0) {
                newsList.addAll(temp);
            }
        }
    }


    private void saveToDb(long subcategoryId) {
//        if (newsList.size() > 0)
//            if (newsType == NewsType.HOT)
//                SugarRecord.deleteAll(NewsClass.class, "TYPE = 1 and PRIVATE_NEWS_TYPE = 0 and SUBCATEGORY_FK = ?", String.valueOf(subcategoryId));
//            else
//                SugarRecord.deleteAll(NewsClass.class, "TYPE = 2 and PRIVATE_NEWS_TYPE = 0 and SUBCATEGORY_FK = ?", String.valueOf(subcategoryId));

//        if (newsList.size() > 0)
//            SugarRecord.deleteAll(NewsClass.class, "PRIVATE_NEWS_TYPE = 0 and SUBCATEGORY_FK = ?", String.valueOf(subcategoryId));

        for (NewsClass item : newsList)
            item.save();

    }

    private void addNews(int page, List<NewsClass> newNews) {
        int countItems = 5;
        if (page * countItems > newsList.size())
            newsList.addAll(newNews);
        else {
            int start = (page - 1) * countItems;
            for (NewsClass item : newNews)
                newsList.set(start++, item);

        }
    }


    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            newsList.clear();
            bAdapter.notifyDataSetChanged();
            scrollListener.resetState();
            fetchDataFromApi(1);
        }
    };


    private void runLayoutAnimation() {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_left);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
}
