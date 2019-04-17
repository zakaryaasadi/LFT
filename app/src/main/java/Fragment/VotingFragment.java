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

import java.util.ArrayList;
import java.util.List;

import Adapter.VotingRecycleAdapter;
import Controller.Common;
import Controller.Api;
import Controller.DataFromApi;
import Utils.EndlessRecyclerViewScrollListener;
import Models.VotingClass;
import Models.VotingResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.shahbaapp.lft.R;

public class VotingFragment extends Fragment {

    private RecyclerView recyclerView;
    private VotingRecycleAdapter bAdapter;
    private List<VotingClass> list = new ArrayList<>();


    View view;
    private SwipeRefreshLayout swipe;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int totalNews = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_voting,container,false);


        swipe = view.findViewById(R.id.swipe);
        swipe.getLayoutParams().height = Common.getScreenHeight(getActivity());
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);
        swipe.setOnRefreshListener(onRefreshListener);

        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        bAdapter = new VotingRecycleAdapter(getActivity(), list);
        recyclerView.setAdapter(bAdapter);

        fetchDataFromApi(1);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(totalNews != totalItemsCount){
                    if(page * 5 > totalItemsCount)
                        fetchDataFromApi(page);
                    else
                        fetchDataFromApi(page + 1);
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        return view;
    }

    private void fetchDataFromApi(int page) {
        swipe.setRefreshing(true);
        Api api = DataFromApi.getApi();

        int userId = Common.getUser() == null ? 0 : (int) Common.getUser().id;
        Call<VotingResult> call = api.Voting(userId,page);

        call.enqueue(new Callback<VotingResult>() {
            @Override
            public void onResponse(Call<VotingResult> call, Response<VotingResult> response) {
                swipe.setRefreshing(false);
                VotingResult voting = response.body();
                if(voting.results != null){
                    list.addAll(voting.results);
                    bAdapter.notifyDataSetChanged();
                    totalNews = voting.numResult;
                    runLayoutAnimation();
                    saveToDb();

                }else
                    Toast.makeText(getContext(),voting.status,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<VotingResult> call, Throwable t) {
                swipe.setRefreshing(false);
                fetchDataFromDb();
                bAdapter.notifyDataSetChanged();
                runLayoutAnimation();
                Toast.makeText(getContext(), t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchDataFromDb(){
        if(list.size() < 5){
            list.clear();
            list.addAll(SugarRecord.listAll(VotingClass.class, "CREATION_DATE DESC"));
        }
    }

    private void saveToDb(){
        for(VotingClass item : list)
            item.save();
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            list.clear();
            bAdapter.notifyDataSetChanged();
            scrollListener.resetState();
            fetchDataFromApi(1);
        }
    };

    private void runLayoutAnimation() {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_right);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
}
