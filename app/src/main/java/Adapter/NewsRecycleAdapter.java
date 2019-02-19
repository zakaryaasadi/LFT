package Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snatik.storage.Storage;

import java.util.List;

import Controller.CustomDate;
import Models.NewsClass;
import Utils.FileProcessing;
import Utils.ImageProcessing;
import shahbasoft.lft.NewsDetailActivity;
import shahbasoft.lft.NewsProfileActivity;
import shahbasoft.lft.R;


public class NewsRecycleAdapter extends RecyclerView.Adapter<NewsRecycleAdapter.MyViewHolder> {

    Context context;


    private List<NewsClass> OfferList;
    Bitmap bitmap;


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

            shareBtn = view.findViewById(R.id.share);

        }

    }


    public NewsRecycleAdapter(Context context, List<NewsClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public NewsRecycleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_explore, parent, false);


        return new NewsRecycleAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final NewsClass news = OfferList.get(position);

        holder.personName.setText(news.getPersonName());
        holder.userName.setText(" @"+news.getUserName());
        holder.date.setText(CustomDate.format(news.getCreationDate()));
        holder.headLine.setText(news.getHeadLine());
        holder.subCats.setText(news.getSubcategory().getTitle());

        holder.profileImage.setImageResource(R.drawable.profile);
        if(news.getProfileImage() != null){
            holder.profileImage.setImageBitmap(ImageProcessing.base64ToBitmap(news.getProfileImage()));
        }

        holder.imageView.setImageResource(R.drawable.no_image);
        if(news.getNewsImage() != null){
            holder.imageView.setImageBitmap(ImageProcessing.base64ToBitmap(news.getNewsImage()));
        }

        //new ImageNews(holder.imageView,news.getNewsImage()).load();

        holder.personName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, NewsProfileActivity.class);
                i.putExtra("userId",news.getUserId());
                context.startActivity(i);
            }
        });




        holder.imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, NewsDetailActivity.class);
                i.putExtra("newsId", news.getId());
                i.putExtra("personName",news.getPersonName());
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
                if (!news.getSharable())
                    return;
                if(news.getNewsImage() != null)
                    FileProcessing.shareFile(context, saveImageInCache(news), news.getTitle(), news.getBody());
                else
                    FileProcessing.shareFile(context, "", news.getTitle(), news.getBody());
            }
        });

    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }


    private String saveImageInCache(NewsClass item) {

        Storage storage = new Storage(context);
        String path = storage.getInternalCacheDirectory() + "/image.jpg";
        storage.createFile(path, Base64.decode(item.getNewsImage(), Base64.DEFAULT));
        return path;
    }

}


