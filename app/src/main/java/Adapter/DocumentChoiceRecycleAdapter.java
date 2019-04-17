package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Models.DocAnswerClass;
import Models.DocQuestionClass;
import com.shahbaapp.lft.R;


public class DocumentChoiceRecycleAdapter extends RecyclerView.Adapter<DocumentChoiceRecycleAdapter.MyViewHolder> {


    private DocQuestionClass question;
    Context context;
    private List<DocAnswerClass> OfferList;
    List<View> checkboxs = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView choice;
        CheckBox chkChoice;



        public MyViewHolder(View view) {
            super(view);

            choice = (TextView) view.findViewById(R.id.txt_choice);
            chkChoice = (CheckBox) view.findViewById(R.id.chk_choice);
        }

    }


    public DocumentChoiceRecycleAdapter(Context context, DocQuestionClass q) {
        this.question = q;
        this.OfferList = q.answers;
        this.context = context;
    }

    @Override
    public DocumentChoiceRecycleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doc_choice, parent, false);

        checkboxs.add(itemView);

        return new DocumentChoiceRecycleAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final DocAnswerClass item = OfferList.get(position);

        holder.choice.setText(item.name);


        holder.chkChoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                item.isChecked = isChecked;
            }
        });

        holder.chkChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.chkChoice.isChecked() &&  question.type == 1) {
                        unCheckedAllCheckbox(item);
                }else if(!holder.chkChoice.isChecked() &&  question.type == 1) {
                    holder.chkChoice.setChecked(true);
                    return;
                }

                item.isChecked = holder.chkChoice.isChecked();
            }
        });

    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

    private void unCheckedAllCheckbox(DocAnswerClass item) {
        for (int i = 0; i < question.answers.size(); i++) {
            final CheckBox mCheckBox = checkboxs.get(i).findViewById(R.id.chk_choice);
            if (mCheckBox.isChecked() && item.id != question.answers.get(i).id) {
                mCheckBox.setChecked(false);
            }
        }
    }

}

