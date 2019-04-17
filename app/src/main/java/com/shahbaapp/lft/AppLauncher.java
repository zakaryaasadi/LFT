package com.shahbaapp.lft;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.orm.SchemaGenerator;
import com.orm.SugarContext;
import com.orm.SugarDb;
import com.snatik.storage.Storage;

import net.gotev.uploadservice.UploadService;

import java.io.File;



public class AppLauncher extends Application  {

    public static String DIR_FILES;
    public static String DIR_IMAGES;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        //Database
        SugarContext.init(this);
        SchemaGenerator schemaGenerator = new SchemaGenerator(this);
        schemaGenerator.createDatabase(new SugarDb(this).getDB());
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;


        //Downloader
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

        //Create folder app in SD
        createFoldeAppIfNotExist();

    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }




    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        CharSequence name = applicationInfo.loadLabel(context.getPackageManager());
        return String.valueOf(name);
    }


    public static Drawable getApplicationIcon(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        return applicationInfo.loadIcon(context.getPackageManager());
    }

    private String getApplicationName() {
        ApplicationInfo applicationInfo = getApplicationContext().getApplicationInfo();
        CharSequence name = applicationInfo.loadLabel(getPackageManager());
        return String.valueOf(name);
    }


    private void createFoldeAppIfNotExist(){
        // init
        Storage storage = new Storage(getApplicationContext());

        // get external storage
        String path = storage.getExternalStorageDirectory();

        // app dir
        String appDir = path + File.separator + getApplicationName();
        storage.createDirectory(appDir);

        // files dir
        DIR_FILES= appDir + File.separator + getApplicationName() +" Files";
        storage.createDirectory(DIR_FILES);

        // images dir
        DIR_IMAGES = appDir + File.separator + getApplicationName() +" Images";
        storage.createDirectory(DIR_IMAGES);
    }




}
