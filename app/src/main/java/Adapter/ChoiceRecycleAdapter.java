package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Models.ChoiceClass;
import Controller.Common;
import Controller.Api;
import Controller.DataFromApi;
import Models.Result;
import Models.VotingClass;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.shahbaapp.lft.R;

public class ChoiceRecycleAdapter extends RecyclerView.Adapter<ChoiceRecycleAdapter.MyViewHolder> {

    Context context;
    List<View> checkboxs = new ArrayList<>();


    private List<ChoiceClass> OfferList;
    private VotingClass voting;
    private Api api;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView choice;
        TextView precentage;
        CheckBox chkChoice;
        View progressBar;



        public MyViewHolder(View view) {
            super(view);

            choice = (TextView) view.findViewById(R.id.txt_choice);
            precentage = (TextView) view.findViewById(R.id.txt_precentage);
            chkChoice = (CheckBox) view.findViewById(R.id.chk_choice);
            progressBar = (View) view.findViewById(R.id.bar_precentage);
        }

    }


    public ChoiceRecycleAdapter(Context context, VotingClass voteClass) {
        this.voting = voteClass;
        this.OfferList = voteClass.getChoices();
        this.context = context;
        api = DataFromApi.getApi();
    }

    @Override
    public ChoiceRecycleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_choice, parent, false);

        checkboxs.add(itemView);

        return new ChoiceRecycleAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final ChoiceClass itemChoice = OfferList.get(position);

        float precentage = 0;
        Boolean showResult = false;

        if(voting.getTotalVotes() > 0)
            precentage = Math.round( itemChoice.getVoteCount() / voting.getTotalVotes()  * 100);

        holder.choice.setText(itemChoice.getTitle());

        if(Common.getUser() != null)
            holder.chkChoice.setChecked(itemChoice.isChoiced());

        switch (voting.getVoteResult()){
            case 1:
                showResult = voting.getExpireDate().getTime() < new Date().getTime();
                break;

            case 3:
                showResult = true;
                break;
        }

        if(showResult){
            holder.progressBar.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, precentage));
            holder.precentage.setText(String.valueOf((int) precentage) +" %");
        }




        holder.chkChoice.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(Common.getUser() != null){
                    if(voting.getVoteType() == 1) {
                        unCheckedAllCheckbox();
                    }
                    Reload(holder, itemChoice,holder.chkChoice.isChecked());

                }else{
                    Toast.makeText(context,"Please, you should Login",Toast.LENGTH_SHORT).show();
                    holder.chkChoice.setChecked(false);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

    private void unCheckedAllCheckbox() {
        for (int i = 0; i < voting.getChoices().size(); i++) {
            final ChoiceClass itemChoice = voting.getChoices().get(i);
            final CheckBox mCheckBox = checkboxs.get(i).findViewById(R.id.chk_choice);
            if (mCheckBox.isChecked()) {
                Call<Result> call = api.DelVote(Common.getUser().id, itemChoice.getId());
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        mCheckBox.setChecked(!(response.body().statusCode == 200));
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                    }
                });
            }
        }
    }


    private void Reload(@NonNull final MyViewHolder holder, final ChoiceClass itemChoice, Boolean b) {
        if (b) {
            Call<Result> call = api.AddVote(Common.getUser().id, itemChoice.getId());
            call.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if(response.body().statusCode == 200){

                        itemChoice.setChoiced(response.body().statusCode == 200);
                        itemChoice.setVoteCount(itemChoice.getVoteCount()+1);
                        voting.setTotalVotes(voting.getTotalVotes() + 1);
                        itemChoice.save();
                        notifyDataSetChanged();
                        holder.chkChoice.setChecked(itemChoice.isChoiced());
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {

                }
            });
        } else {
            Call<Result> call = api.DelVote(Common.getUser().id, itemChoice.getId());
            call.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if(response.body().statusCode == 200) {
                        itemChoice.setChoiced(!(response.body().statusCode == 200));
                        itemChoice.setVoteCount(itemChoice.getVoteCount() - 1);
                        voting.setTotalVotes(voting.getTotalVotes() - 1);
                        itemChoice.save();
                        notifyDataSetChanged();
                        holder.chkChoice.setChecked(itemChoice.isChoiced());
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {

                }
            });
        }
    }

}

