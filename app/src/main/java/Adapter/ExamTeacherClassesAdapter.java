package Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import Models.ClassSubjectClass;
import com.shahbaapp.lft.ExamTeacherSubjectsActivity;
import com.shahbaapp.lft.R;

public class ExamTeacherClassesAdapter extends RecyclerView.Adapter<ExamTeacherClassesAdapter.MyViewHolder> {

    Context context;


    private List<ClassSubjectClass> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView name;




        public MyViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.title);
        }

    }


    public ExamTeacherClassesAdapter(Context context, List<ClassSubjectClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public ExamTeacherClassesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_classes, parent, false);


        return new ExamTeacherClassesAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final ClassSubjectClass mClass = OfferList.get(position);

        holder.name.setText(mClass.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ExamTeacherSubjectsActivity.class);
                i.putExtra("subjects", new Gson().toJson(mClass.subjects));
                context.startActivity(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

}