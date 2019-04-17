package Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import Models.DocumentClass;
import com.shahbaapp.lft.AddDocumentActivity;
import com.shahbaapp.lft.R;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.MyViewHolder>{

    private long subjectId;
    Context context;


    private List<DocumentClass> OfferList;




    public class MyViewHolder extends RecyclerView.ViewHolder {



        TextView txt;

        public MyViewHolder(View view) {
            super(view);

            txt=(TextView)view.findViewById(R.id.title);

        }

    }


    public DocumentAdapter(Context context, List<DocumentClass> offerList, long subjectId) {
        this.OfferList = offerList;
        this.context = context;
        this.subjectId = subjectId;
    }

    @Override
    public DocumentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);


        return new DocumentAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull final DocumentAdapter.MyViewHolder holder, final int position) {
        final DocumentClass doc = OfferList.get(position);
        holder.txt.setText(doc.name);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AddDocumentActivity.class);
                i.putExtra("id", position);
                i.putExtra("subjectId", subjectId);
                context.startActivity(i);
            }
        });

    }



    @Override
    public int getItemCount() {
        return OfferList.size();

    }

}


