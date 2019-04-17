package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import Utils.ImageProcessing;
import com.shahbaapp.lft.R;

public class DetailImageAdapter extends RecyclerView.Adapter<DetailImageAdapter.MyViewHolder> {

    Context context;


    private List<String> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        ImageView image;



        public MyViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
        }

    }


    public DetailImageAdapter(Context context, List<String> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public DetailImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail_image, parent, false);


        return new DetailImageAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        String imageBase = OfferList.get(position);

        holder.image.setImageBitmap(ImageProcessing.base64ToBitmap(imageBase));
    }



    @Override
    public int getItemCount() {
        return OfferList.size();

    }

}