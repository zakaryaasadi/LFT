package Activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import Adapter.SubCategoryAdapter;
import Controller.Common;
import shahbasoft.lft.R;

public class SubCategoryActivity extends Fragment {

    private RecyclerView recyclerView;
    private SubCategoryAdapter bAdapter;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_sub_category,container,false);
        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        bAdapter = new SubCategoryAdapter(getActivity(), Common.categoryClass.getSubcategories());
        recyclerView.setAdapter(bAdapter);
        return view;
    }
}
