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

public class AddNewsImageAdapter extends RecyclerView.Adapter<AddNewsImageAdapter.MyViewHolder> {

    Context context;
    private List<String> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image, cancel;

        public MyViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            cancel = view.findViewById(R.id.cancel);
        }
    }


    public AddNewsImageAdapter(Context context, List<String> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public AddNewsImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_add_image, parent, false);


        return new AddNewsImageAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        String i = OfferList.get(position);

        holder.image.setImageBitmap(ImageProcessing.base64ToBitmap(i) );

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfferList.remove(position);
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return OfferList.size();
    }



}


