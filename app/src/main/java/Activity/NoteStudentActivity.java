package Activity;

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
import android.widget.Toast;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;
import Adapter.NoteStudentAdapter;
import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Models.NoteStudentClass;
import Models.NoteStudentResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.shahbaapp.lft.R;

public class NoteStudentActivity extends Fragment {

    private RecyclerView recyclerView;
    private NoteStudentAdapter bAdapter;
    private List<NoteStudentClass> students;

    View view;
    private SwipeRefreshLayout swipe;
    private Api api;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_note_student,container,false);
        swipe =view.findViewById(R.id.swipe);
        swipe.getLayoutParams().height = Common.getScreenHeight(getActivity()) - 250;
        swipe.setColorSchemeResources(R.color.orange, R.color.twittercolor, R.color.redBg);
        swipe.setOnRefreshListener(onRefreshListener);

        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        students = new ArrayList<>();
        bAdapter = new NoteStudentAdapter(getContext(), students);
        recyclerView.setAdapter(bAdapter);

        if(Common.getUser() != null)
            fetchDataFromApi();


        return view;
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener () {
        @Override
        public void onRefresh() {
            students.clear();
            bAdapter.notifyDataSetChanged();
            fetchDataFromApi();
        }
    };

    private void fetchDataFromApi() {
        swipe.setRefreshing(true);
        api = DataFromApi.getApi();

        Call<NoteStudentResult> call = api.NoteStudent(5602);

        call.enqueue(new Callback<NoteStudentResult>() {
            @Override
            public void onResponse(Call<NoteStudentResult> call, Response<NoteStudentResult> response) {
                swipe.setRefreshing(false);
                NoteStudentResult request = response.body();
                if (request.results != null) {
                    students.clear();
                    students.addAll(request.results);
                    bAdapter.notifyDataSetChanged();
                    saveToBb();
                } else
                    Toast.makeText(getContext(), request.status, Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onFailure(Call<NoteStudentResult> call, Throwable t) {
                swipe.setRefreshing(false);
                fetchDataFromDb();
                bAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDataFromDb() {
        if(students.size() < 5){ // No Connection
            students.clear();
            students.addAll(SugarRecord.listAll(NoteStudentClass.class, "ID DESC"));
        }
    }

    private void saveToBb() {
        SugarRecord.deleteAll(NoteStudentClass.class);
        SugarRecord.saveInTx(students);
    }
}
