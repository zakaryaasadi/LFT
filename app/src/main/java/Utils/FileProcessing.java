package Utils;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Base64;

import com.shahbaapp.lft.AppLauncher;
import com.snatik.storage.Storage;

import java.io.File;


public class FileProcessing{


    public static final String FILE_PROVIDER = AppLauncher.context.getPackageName() + ".fileprovider";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void openFileDialog(String path) {

        if (!new File(path).exists())
            return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("*/*");
        intent.setData(getFileUri(path));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        AppLauncher.context.startActivity(Intent.createChooser(intent, "Choose an app"));
    }

    public static void shareFile(String path, String title, String text) {
        if (!new File(path).exists())
            return;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        if (path != null || path == "")
            intent.putExtra(Intent.EXTRA_STREAM, getFileUri(path));
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        AppLauncher.context.startActivity(Intent.createChooser(intent, "Choose an app"));

    }

    public static Uri getFileUri(String path) {
        File f = new File(path);
        Uri uri = FileProvider.getUriForFile(AppLauncher.context, FILE_PROVIDER, f);
        return uri;
    }


    public static String getFileSize(String size) {
        try {
            Double KB = Double.parseDouble(size);
            Double MB = KB / 1024;
            Double GB = MB / 1024;

            if (GB > 1)
                return String.valueOf(Math.round(GB * 100.0) / 100.0) + " GB";
            else if (MB > 1)
                return String.valueOf(Math.round(MB * 100.0) / 100.0) + " MB";

            return String.valueOf(Math.round(KB * 100.0) / 100.0) + " KB";
        } catch (Exception e) {
            return size;
        }
    }



    public static String saveImageInCache(String image) {

        Storage storage = new Storage(AppLauncher.context);
        String path = storage.getInternalCacheDirectory() + "/image.jpg";
        storage.createFile(path, Base64.decode(image, Base64.DEFAULT));
        return path;
    }

}
