package Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import Controller.CustomDate;
import Models.ExamClass;
import Models.ExamSubjectClass;
import Models.SubjectClass;
import Models.UserClass;
import shahbasoft.lft.ExamSubjectsActivity;
import shahbasoft.lft.MarksActivity;
import shahbasoft.lft.R;

public class ExamSubjectsAdapter extends RecyclerView.Adapter<ExamSubjectsAdapter.MyViewHolder> {

    Context context;


    private List<ExamSubjectClass> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView counter, name, max, min, mark;
        LinearLayout isSuccess;




        public MyViewHolder(View view) {
            super(view);

            counter = view.findViewById(R.id.counter);
            name = view.findViewById(R.id.name);
            max = view.findViewById(R.id.max);
            min = view.findViewById(R.id.min);
            mark = view.findViewById(R.id.mark);
            isSuccess = view.findViewById(R.id.is_success);
        }

    }


    public ExamSubjectsAdapter(Context context, List<ExamSubjectClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public ExamSubjectsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_subject, parent, false);


        return new ExamSubjectsAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final ExamSubjectClass examSubject = OfferList.get(position);

        if(getMark(examSubject) < getPassMark(examSubject))
            holder.isSuccess.setBackgroundColor(context.getResources().getColor(R.color.FAIL));

        holder.counter.setText(String.valueOf(position + 1) + ".");
        holder.name.setText(examSubject.subject.name);
        holder.max.setText(String.valueOf( getMaxMark(examSubject) ));
        holder.min.setText(String.valueOf( getPassMark(examSubject) ));
        holder.mark.setText(String.valueOf( getMark(examSubject) ));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MarksActivity.class);
                i.putExtra("exam", new Gson().toJson(examSubject.marks));
                context.startActivity(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

    private float getMark(ExamSubjectClass examSubject){
        float s = 0;
        for(ExamClass e : examSubject.marks){
            if(e.absent == 0)
                s += e.mark * e.weight / 100;
        }
        return s;
    }

    private float getPassMark(ExamSubjectClass examSubject){
        float s = 0;
        for(ExamClass e : examSubject.marks){
            s += e.min * e.weight / 100;
        }
        return s;
    }

    private float getMaxMark(ExamSubjectClass examSubject){
        float s = 0;
        for(ExamClass e : examSubject.marks){
            s += e.max * e.weight / 100;
        }
        return s;
    }

}