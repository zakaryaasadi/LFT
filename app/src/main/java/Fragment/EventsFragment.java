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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.orm.SugarRecord;
import com.shahbaapp.lft.R;

import java.util.ArrayList;
import java.util.List;

import Adapter.EventsRecycleAdapter;
import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Models.EventClass;
import Models.EventResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventsRecycleAdapter bAdapter;
    private ProgressBar progressBar;
    private Api api;
    private List<EventClass> events = new ArrayList<>();
    private SwipeRefreshLayout swipe;


    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_events, container, false);

        swipe =view.findViewById(R.id.swipe);
        swipe.getLayoutParams().height = Common.getScreenHeight(getActivity());
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);
        swipe.setOnRefreshListener(onRefreshListener);


        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        bAdapter = new EventsRecycleAdapter(getActivity(), events);
        recyclerView.setAdapter(bAdapter);

        fetchDataFromApi();

        return view;
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener () {
        @Override
        public void onRefresh() {
            events.clear();
            bAdapter.notifyDataSetChanged();
            fetchDataFromApi();
        }
    };

    private void fetchDataFromApi() {
        swipe.setRefreshing(true);
        api = DataFromApi.getApi();

        Call<EventResult> call = api.Events();

        call.enqueue(new Callback<EventResult>() {
            @Override
            public void onResponse(Call<EventResult> call, Response<EventResult> response) {
                swipe.setRefreshing(false);
                EventResult request = response.body();
                if (request.results != null) {
                    events.clear();
                    events.addAll(request.results);
                    bAdapter.notifyDataSetChanged();
                    runLayoutAnimation();
                    saveToBb();
                }
            }


            @Override
            public void onFailure(Call<EventResult> call, Throwable t) {
                swipe.setRefreshing(false);
                fetchDataFromDb();
                bAdapter.notifyDataSetChanged();
                runLayoutAnimation();
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToBb(){
        SugarRecord.saveInTx(events);
    }
    private void fetchDataFromDb(){
        if(SugarRecord.count(EventClass.class) > 0) {
            events.addAll(SugarRecord.listAll(EventClass.class));
        }
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
