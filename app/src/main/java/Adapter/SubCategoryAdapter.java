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
import com.shahbaapp.lft.R;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {

    private final float heighScreen;
    Context context;


    private List<SubcategoryClass> OfferList;
    private onSelectItemListener mOnSelectItem;
    private LinearLayout preSubCat;


    public interface onSelectItemListener {
        public void onSelectItem(long subcategoryId);
    }

    public void setOnSelectItem(onSelectItemListener onSelectItem) {
        mOnSelectItem = onSelectItem;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        ImageView image;
        TextView title;
        LinearLayout subCat;

        public MyViewHolder(View view) {
            super(view);


            image = (ImageView) view.findViewById(R.id.image);
            title = (TextView) view.findViewById(R.id.title);
            subCat = view.findViewById(R.id.sub_cats);
        }

    }


    public SubCategoryAdapter(Context context, List<SubcategoryClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
        heighScreen = context.getResources().getDisplayMetrics().density;
    }

    @Override
    public SubCategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sub_category, parent, false);


        return new SubCategoryAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryAdapter.MyViewHolder holder, int position) {
        final SubcategoryClass sub = OfferList.get(position);
        holder.title.setText(sub.getTitle());
        final LinearLayout subCat = holder.subCat;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnDefaultToTextView(subCat);
                subCat.setPadding(0, (int) (5 * heighScreen), 0, 0);
                mOnSelectItem.onSelectItem(sub.getId());
            }
        });
    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

    private void returnDefaultToTextView(LinearLayout subCat) {

        if (preSubCat == null){
            preSubCat = subCat;
            return;
        }

        preSubCat.setPadding(0, (int) (15 * heighScreen), 0, 0);
        preSubCat = subCat;
    }

}



