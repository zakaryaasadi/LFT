package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import Models.ExamClass;
import com.shahbaapp.lft.R;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.MyViewHolder> {

    Context context;


    private List<ExamClass> OfferList;


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


    public ExamAdapter(Context context, List<ExamClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public ExamAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_subject, parent, false);


        return new ExamAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        ExamClass exam = OfferList.get(position);

        if(exam.mark < exam.min)
            holder.isSuccess.setBackgroundColor(context.getResources().getColor(R.color.FAIL));

        holder.counter.setText(String.valueOf(position + 1) + ".");
        holder.name.setText( exam.examName );
        holder.max.setText(String.valueOf( exam.max ));
        holder.min.setText(String.valueOf( exam.min ));
        holder.mark.setText(String.valueOf( exam.mark ));
    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

}