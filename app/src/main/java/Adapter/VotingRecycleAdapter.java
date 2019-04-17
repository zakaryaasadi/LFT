package Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import Models.ChoiceClass;
import Utils.CustomDate;
import Models.VotingClass;
import com.shahbaapp.lft.R;

public class VotingRecycleAdapter extends RecyclerView.Adapter<VotingRecycleAdapter.MyViewHolder> {

    Context context;


    private List<VotingClass> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView personName;
        TextView userName;
        TextView title;
        ImageView profileImage;


        TextView date;
        TextView headLine;
        ImageView imageView;
        private RecyclerView recyclerView;
        TextView voteCount;
        LinearLayout resultLayout;
        TextView wonPrecentage;
        TextView wonChoice;



        public MyViewHolder(View view) {
            super(view);

            personName = (TextView) view.findViewById(R.id.person_name);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);
            userName = (TextView) view.findViewById(R.id.user_name);
            date = (TextView) view.findViewById(R.id.date);

            title = (TextView) view.findViewById(R.id.title);

            headLine = (TextView) view.findViewById(R.id.head_line);
            imageView = (ImageView) view.findViewById(R.id.image);
            voteCount = (TextView) view.findViewById(R.id.total_voting);

            recyclerView = view.findViewById(R.id.recyclerView);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            resultLayout = (LinearLayout) view.findViewById(R.id.result_layout);
            wonChoice = view.findViewById(R.id.won_choice);
            wonPrecentage = view.findViewById(R.id.won_precentage);

        }

    }


    public VotingRecycleAdapter(Context context, List<VotingClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public VotingRecycleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voting, parent, false);


        return new VotingRecycleAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        VotingClass voting = OfferList.get(position);


        holder.personName.setText(voting.getPersonName());
        holder.date.setText(CustomDate.format(voting.getCreationDate()));
        holder.title.setText(voting.getTitle());
        holder.headLine.setText(voting.getHeadLine());
        holder.userName.setText("@" + voting.getUserName());

        if(voting.getVoteResult() == 3 || (voting.getVoteResult() == 1 && voting.getExpireDate().getTime() < new Date().getTime()) )
            holder.voteCount.setText("Total Vote: " + String.valueOf(voting.getTotalVotes()));


        if(voting.getProfileImage() != null){
            byte[] decodedString = Base64.decode(voting.getProfileImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.profileImage.setImageBitmap(bitmap);
        }

        if(voting.getNewsImage() != null){
            byte[] decodedString = Base64.decode(voting.getNewsImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.imageView.setImageBitmap(bitmap);
        }

        if(voting.getExpireDate().getTime() > new Date().getTime()){
            ChoiceRecycleAdapter bAdapter = new ChoiceRecycleAdapter(context,voting);
            holder.recyclerView.setAdapter(bAdapter);
            bAdapter.notifyDataSetChanged();
        }else if(voting.getVoteResult() == 1 && voting.getExpireDate().getTime() < new Date().getTime()){
            Collections.sort(voting.getChoices(), descending);

            float pre = Math.round(voting.getChoices().get(0).getVoteCount() / voting.getTotalVotes() * 100);
            holder.wonPrecentage.setText(String.valueOf(pre) + "%");
            holder.wonChoice.setText(voting.getChoices().get(0).getTitle());
            holder.resultLayout.setVisibility(View.VISIBLE);

        }


    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }
    
    private Comparator<ChoiceClass> descending = new Comparator<ChoiceClass>() {
        @Override
        public int compare(ChoiceClass lhs, ChoiceClass rhs) {
            return lhs.getVoteCount() > rhs.getVoteCount() ? -1 : (lhs.getVoteCount() < rhs.getVoteCount()) ? 1 : 0;
        }
    };

}