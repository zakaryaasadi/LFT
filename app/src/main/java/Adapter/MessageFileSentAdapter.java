package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import Models.AttachmentClass;
import Utils.FileProcessing;
import shahbasoft.lft.R;

public class MessageFileSentAdapter extends RecyclerView.Adapter<MessageFileSentAdapter.MyViewHolder> {

    Context context;


    private List<AttachmentClass> OfferList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fileName, fileSize;


        public MyViewHolder(View view) {
            super(view);

            fileName = view.findViewById(R.id.file_name);
            fileSize = view.findViewById(R.id.file_size);

        }


    }


    public MessageFileSentAdapter(Context context, List<AttachmentClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public MessageFileSentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_file_sent_detail, parent, false);


        return new MessageFileSentAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final AttachmentClass file = OfferList.get(position);

        holder.fileName.setText(file.name);
        holder.fileSize.setText(FileProcessing.getFileSize(file.size));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileProcessing.openFileDialog(context, file.path);
            }
        });
    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }
}


