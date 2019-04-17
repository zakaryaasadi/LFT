package Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import Models.SubjectClass;
import com.shahbaapp.lft.ExamTeacherTypesActivity;
import com.shahbaapp.lft.R;

public class ExamTeacherSubjectsAdapter extends RecyclerView.Adapter<ExamTeacherSubjectsAdapter.MyViewHolder> {

    Context context;


    private List<SubjectClass> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView name;




        public MyViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.title);
        }

    }


    public ExamTeacherSubjectsAdapter(Context context, List<SubjectClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public ExamTeacherSubjectsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_classes, parent, false);


        return new ExamTeacherSubjectsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final SubjectClass mSubject = OfferList.get(position);

        holder.name.setText(mSubject.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ExamTeacherTypesActivity.class);
                i.putExtra("subjectId", mSubject.id);
                context.startActivity(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

}