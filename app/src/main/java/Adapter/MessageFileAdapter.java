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
import android.widget.Toast;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.io.File;
import java.util.List;

import Controller.Api;
import Models.AttachmentClass;
import Utils.FileProcessing;
import com.shahbaapp.lft.AppLauncher;
import com.shahbaapp.lft.R;

public class MessageFileAdapter extends RecyclerView.Adapter<MessageFileAdapter.MyViewHolder> {

    Context context;


    private List<AttachmentClass> OfferList;
    private String url = Api.HOST + "attach/getfile?attach_id=";


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView fileComplete, download;
        DonutProgress progress;
        TextView fileName, fileSize;
        int downloadId;


        public MyViewHolder(View view) {
            super(view);

            progress = view.findViewById(R.id.progress_bar);
            fileName = view.findViewById(R.id.file_name);
            fileSize = view.findViewById(R.id.file_size);
            fileComplete = view.findViewById(R.id.file_complete);
            download = view.findViewById(R.id.download);

        }


        private void initFetchFile(final AttachmentClass file) {


            String urlFile = url + String.valueOf(file.id);
            downloadId = PRDownloader.download(urlFile, AppLauncher.DIR_FILES, file.name)
                    .build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {
                            progress.setVisibility(View.VISIBLE);
                            download.setVisibility(View.GONE);
                        }
                    })
                    .setOnPauseListener(new OnPauseListener() {
                        @Override
                        public void onPause() {
                            progress.setVisibility(View.GONE);
                            download.setVisibility(View.VISIBLE);
                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel() {
                            progress.setVisibility(View.GONE);
                            download.setVisibility(View.VISIBLE);
                        }
                    })
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {
                            float precent = progress.currentBytes * 1000 / progress.totalBytes;
                            precent /= 10;
                            MyViewHolder.this.progress.setProgress(precent);
                        }
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            progress.setVisibility(View.GONE);
                            fileComplete.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Error error) {
                            Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                            progress.setVisibility(View.GONE);
                            download.setVisibility(View.VISIBLE);
                        }

                    });
        }
    }


    public MessageFileAdapter(Context context, List<AttachmentClass> offerList) {
        this.OfferList = offerList;
        this.context = context; }

    @Override
    public MessageFileAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_file_detail, parent, false);


        return new MessageFileAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final AttachmentClass file = OfferList.get(position);

        holder.fileName.setText(file.name);
        holder.fileSize.setText(FileProcessing.getFileSize(file.size));

        if (!exists(file) && (PRDownloader.getStatus(holder.downloadId) == Status.UNKNOWN
                || PRDownloader.getStatus(holder.downloadId) == Status.PAUSED
                || PRDownloader.getStatus(holder.downloadId) == Status.CANCELLED)) {
            holder.download.setVisibility(View.VISIBLE);
            holder.progress.setVisibility(View.GONE);
            holder.fileComplete.setVisibility(View.GONE);
        } else if (exists(file)) {
            holder.download.setVisibility(View.GONE);
            holder.progress.setVisibility(View.GONE);
            holder.fileComplete.setVisibility(View.VISIBLE);
        }


        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.download.setVisibility(View.GONE);
                holder.progress.setVisibility(View.VISIBLE);
                holder.fileComplete.setVisibility(View.GONE);
                if (PRDownloader.getStatus(holder.downloadId) == Status.UNKNOWN)
                    holder.initFetchFile(file);
                else if(PRDownloader.getStatus(holder.downloadId) == Status.PAUSED)
                    PRDownloader.resume(holder.downloadId);
            }
        });

        holder.progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PRDownloader.pause(holder.downloadId);
            }
        });

        holder.fileComplete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                String path = AppLauncher.DIR_FILES + File.separator + file.name;
                File f = new File(path);
                if(f.exists())
                    FileProcessing.openFileDialog(path);
                else
                    Toast.makeText(context, "File access failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

    private boolean exists(AttachmentClass file) {
        File f = new File(AppLauncher.DIR_FILES, file.name);
        return f.exists();
    }
}


