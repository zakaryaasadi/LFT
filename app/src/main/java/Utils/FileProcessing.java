package Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;


public class FileProcessing {

    private static final String FILE_PROVIDER = "shahbasoft.lft.fileprovider";

    public static void openFileDialog(Context context, String path){

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("*/*");
        intent.setData(getFileUri(context, path));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Choose an app"));

    }

    public static void shareFile(Context context, String path, String title, String text){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        if(path != null || path == "")
            intent.putExtra(Intent.EXTRA_STREAM, getFileUri(context, path));
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Choose an app"));

    }

    public static Uri getFileUri(Context context, String path) {
        File f = new File(path);
        Uri uri = FileProvider.getUriForFile(context, FILE_PROVIDER, f);
        return uri;
    }


    public static String getFileSize(String size){
        try {
            Double KB = Double.parseDouble(size);
            Double MB = KB / 1024;
            Double GB = MB / 1024;

            if (GB > 1)
                return String.valueOf(Math.round(GB * 100.0) / 100.0) + " GB";
            else if (MB > 1)
                return String.valueOf(Math.round(MB * 100.0) / 100.0) + " MB";

            return String.valueOf(Math.round(KB * 100.0) / 100.0) + " KB";
        }catch(Exception e) {
            return size;
        }
    }

}
