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
import android.widget.Toast;

import java.util.List;

import Controller.CustomDate;
import Models.NewsClass;
import shahbasoft.lft.NewsDetailActivity;
import shahbasoft.lft.R;


public class NewsProfileAdapter extends RecyclerView.Adapter<NewsProfileAdapter.MyViewHolder> {

    Context context;


    private List<NewsClass> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView personName;
        TextView userName;
        ImageView profileImage;


        TextView date;
        TextView headLine;
        ImageView imageView;
        TextView subCats;

        LinearLayout shareBtn;



        public MyViewHolder(View view) {
            super(view);

            personName = (TextView) view.findViewById(R.id.person_name);
            userName = (TextView) view.findViewById(R.id.user_name);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);


            date = (TextView) view.findViewById(R.id.date);

            headLine = (TextView) view.findViewById(R.id.head_line);
            imageView = (ImageView) view.findViewById(R.id.image);
            subCats = (TextView) view.findViewById(R.id.sub_cats);

            shareBtn =  view.findViewById(R.id.share);

        }

    }


    public NewsProfileAdapter(Context context, List<NewsClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public NewsProfileAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_explore, parent, false);


        return new NewsProfileAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final NewsClass news = OfferList.get(position);

        holder.personName.setText(news.getPersonName());
        holder.userName.setText(" @"+news.getUserName());
        holder.date.setText(CustomDate.format(news.getCreationDate()));
        holder.headLine.setText(news.getHeadLine());
        holder.subCats.setText(news.getSubcategory().getTitle());

        if(news.getProfileImage() != null){
            byte[] decodedString = Base64.decode(news.getProfileImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.profileImage.setImageBitmap(bitmap);
        }


        if(news.getNewsImage() != null){
            byte[] decodedString = Base64.decode(news.getNewsImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.imageView.setImageBitmap(bitmap);
        }




        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, NewsDetailActivity.class);
                i.putExtra("newsId", news.getId());
                i.putExtra("personName",news.getPersonName());
                i.putExtra("userName"," @"+news.getUserName());
                i.putExtra("profileImage",news.getProfileImage());
                i.putExtra("title",news.getTitle());
                i.putExtra("date",CustomDate.format(news.getCreationDate()));
                i.putExtra("body",news.getBody());
                i.putExtra("newsImage",news.getNewsImage());
                i.putExtra("sharable",news.getSharable());
                context.startActivity(i);

            }
        });

        holder.shareBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Toast.makeText(context,"hi",Toast.LENGTH_LONG);
            }
        });

    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

}


