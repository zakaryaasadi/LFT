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

import Models.CategoryClass;
import Controller.Common;
import com.shahbaapp.lft.NewsActivity;
import com.shahbaapp.lft.R;

public class NewsCategoryAdapter extends RecyclerView.Adapter<NewsCategoryAdapter.MyViewHolder>{

    Context context;


    private List<CategoryClass> OfferList;




    public class MyViewHolder extends RecyclerView.ViewHolder {



        TextView txt;
        ImageView image;

        public MyViewHolder(View view) {
            super(view);

            txt=(TextView)view.findViewById(R.id.title);


            image=(ImageView)view.findViewById(R.id.image);

        }

    }


    public NewsCategoryAdapter(Context context, List<CategoryClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public NewsCategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);


        return new NewsCategoryAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull final NewsCategoryAdapter.MyViewHolder holder, int position) {
        final CategoryClass category = OfferList.get(position);
        String title =  category.getTitle();
        holder.txt.setText(title);
        if(category.getImage() != null) {
            System.gc();
            byte[] decodedString = Base64.decode(category.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.image.setImageBitmap(bitmap);
            System.gc();
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.categoryClass = category;
                Intent i = new Intent(context, NewsActivity.class);
                context.startActivity(i);
            }
        });

    }



    @Override
    public int getItemCount() {
        return OfferList.size();

    }

}


