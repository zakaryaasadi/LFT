package Adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import Models.AttachmentClass;
import Utils.FileProcessing;
import com.shahbaapp.lft.AppLauncher;
import com.shahbaapp.lft.R;

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
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                String path = AppLauncher.DIR_FILES + File.separator + file.name;
                File f = new File(path);
                if (f.exists())
                    FileProcessing.openFileDialog( path);
                else
                    Toast.makeText(context, "File access failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }
}


