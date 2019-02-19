package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import Models.SubcategoryClass;
import shahbasoft.lft.R;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder>{

    Context context;


    private List<SubcategoryClass> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {



        TextView txt;
        ImageView tick;
        LinearLayout linearLayout;

        public MyViewHolder(View view) {
            super(view);

            txt=(TextView)view.findViewById(R.id.txt);


            tick=(ImageView)view.findViewById(R.id.tick);
            linearLayout=(LinearLayout) view.findViewById(R.id.linear);


        }

    }


    public SubCategoryAdapter(Context context, List<SubcategoryClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public SubCategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sub_category, parent, false);


        return new SubCategoryAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final SubcategoryClass subcategory = OfferList.get(position);
        holder.txt.setText(subcategory.getTitle());

        if(subcategory.isSelected()){
            holder.tick.setVisibility(View.VISIBLE);
            holder.linearLayout.setVisibility(View.VISIBLE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (subcategory.isSelected()){
                    subcategory.setSelected(false);

                    holder.tick.setVisibility(View.GONE);
                    holder.linearLayout.setVisibility(View.GONE);

                }else {
                    subcategory.setSelected(true);
                    holder.tick.setVisibility(View.VISIBLE);
                    holder.linearLayout.setVisibility(View.VISIBLE);
                }

                subcategory.save();

            }
        });
    }



    @Override
    public int getItemCount() {
        return OfferList.size();

    }

}


