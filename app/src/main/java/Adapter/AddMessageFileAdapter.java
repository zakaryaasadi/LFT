package Adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orm.SugarRecord;

import java.util.List;

import Models.AttachmentClass;
import Utils.FileProcessing;
import com.shahbaapp.lft.R;

public class AddMessageFileAdapter extends RecyclerView.Adapter<AddMessageFileAdapter.MyViewHolder> {

    Context context;
    private List<AttachmentClass> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView clean;
        TextView fileName, fileSize;


        public MyViewHolder(View view) {
            super(view);
            fileName = view.findViewById(R.id.file_name);
            fileSize = view.findViewById(R.id.file_size);
            clean = view.findViewById(R.id.btn_clean);

        }
    }


    public AddMessageFileAdapter(Context context, List<AttachmentClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public AddMessageFileAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_add_message_file, parent, false);


        return new AddMessageFileAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final AttachmentClass file = OfferList.get(position);

        holder.fileName.setText(file.name);
        holder.fileSize.setText(FileProcessing.getFileSize(file.size));




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                FileProcessing.openFileDialog(file.path);
            }
        });

        holder.clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfferList.remove(file);
                SugarRecord.delete(file);
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }



}


