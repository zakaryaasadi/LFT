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
import android.widget.TextView;

import java.util.List;

import Utils.CustomDate;
import Models.EventClass;
import com.shahbaapp.lft.NewsDetailActivity;
import com.shahbaapp.lft.R;

public class EventsRecycleAdapter extends RecyclerView.Adapter<EventsRecycleAdapter.MyViewHolder> {

    Context context;


    private List<EventClass> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView personName;
        TextView title;
        ImageView profileImage;


        TextView date;
        TextView headLine;
        ImageView imageView;
        TextView subCats;



        public MyViewHolder(View view) {
            super(view);

            personName = (TextView) view.findViewById(R.id.person_name);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);
            date = (TextView) view.findViewById(R.id.date);

            title = (TextView) view.findViewById(R.id.title);

            headLine = (TextView) view.findViewById(R.id.head_line);
            imageView = (ImageView) view.findViewById(R.id.image);
            subCats = (TextView) view.findViewById(R.id.sub_cats);

        }

    }


    public EventsRecycleAdapter(Context context, List<EventClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public EventsRecycleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);


        return new EventsRecycleAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final EventClass news = OfferList.get(position);

        holder.personName.setText(news.getPersonName());
        holder.date.setText(CustomDate.format(news.getCreationDate()));
        holder.title.setText(news.getTitle());
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

    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

}