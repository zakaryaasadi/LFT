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

import Models.ExamsStudentClass;
import Models.SubjectClass;
import shahbasoft.lft.ExamTeacherStudentActivity;
import shahbasoft.lft.ExamTeacherTypesActivity;
import shahbasoft.lft.R;

public class ExamTeacherTypesAdapter extends RecyclerView.Adapter<ExamTeacherTypesAdapter.MyViewHolder> {

    Context context;


    private List<ExamsStudentClass> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView name;




        public MyViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.title);
        }

    }


    public ExamTeacherTypesAdapter(Context context, List<ExamsStudentClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public ExamTeacherTypesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_classes, parent, false);


        return new ExamTeacherTypesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final ExamsStudentClass mExam = OfferList.get(position);

        holder.name.setText(mExam.examName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ExamTeacherStudentActivity.class);
                i.putExtra("examId", mExam.examId);
                i.putExtra("maxMark", mExam.max);
                i.putExtra("title", mExam.examName);
                i.putExtra("students", new Gson().toJson(mExam.students) );
                context.startActivity(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

}