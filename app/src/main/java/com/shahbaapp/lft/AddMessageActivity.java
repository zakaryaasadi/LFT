package com.shahbaapp.lft;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.snatik.storage.Storage;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.util.ArrayList;

import Adapter.AddMessageFileAdapter;
import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Utils.FileProcessing;
import Utils.OpenFileDialog;
import Models.AddMessage;
import Models.AttachmentClass;
import Models.MessageClass;
import Models.MessageResult;
import Models.UserClass;
import Utils.ImageProcessing;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddMessageActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_PICTURE = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_Gallery_REQUEST_CODE = 123;
    private ImageView profileImage;
    private TextView personName, btnPost;
    private EditText title, post;
    private Intent intentCamera;
    private Intent pictureActionIntent;
    private ImageView btnGallery, btnCamera;
    private Bitmap bitmap;
    private RecyclerView recyclerViewFile;
    private AddMessageFileAdapter bAdapterFile;
    private ImageView btnAttach;
    private ArrayList<AttachmentClass> attachs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);

        btnGallery = findViewById(R.id.btn_grallery);
        btnCamera = findViewById(R.id.btn_camera);
        btnPost = findViewById(R.id.btn_post);
        btnAttach = findViewById(R.id.btn_attach);
        personName = findViewById(R.id.person_name);
        profileImage = findViewById(R.id.profile_image);
        post = findViewById(R.id.post);
        title = findViewById(R.id.title);


        recyclerViewFile = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewFile.setLayoutManager(layoutManager);
        recyclerViewFile.setItemAnimator(new DefaultItemAnimator());
        attachs = new ArrayList<>();
        bAdapterFile = new AddMessageFileAdapter(this, attachs);
        recyclerViewFile.setAdapter(bAdapterFile);


        if (Common.userMessageSent.size() == 1) {
            personName.setText(Common.userMessageSent.get(0).fullName);
            if (Common.userMessageSent.get(0).profileImage != null) {
                profileImage.setImageBitmap(ImageProcessing.base64ToBitmap(Common.userMessageSent.get(0).profileImage));
            }
        } else {
            String toUserString = "";
            for (UserClass _user : Common.userMessageSent)
                toUserString += _user.fullName + ", ";

            personName.setText(toUserString.substring(0, toUserString.length() - 2));
        }


        intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(getApplicationContext().getExternalCacheDir(), "temp.jpg");
        Uri photoURI = FileProcessing.getFileUri(f.getPath());
        intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);


        btnGallery.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                pictureActionIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (PermissionChecker.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_Gallery_REQUEST_CODE);
                } else
                    startActivityForResult(pictureActionIntent, GALLERY_PICTURE);

            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (PermissionChecker.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_REQUEST_CODE);
                } else
                    startActivityForResult(intentCamera, CAMERA_REQUEST);
            }
        });


        btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OpenFileDialog(AddMessageActivity.this)
                        .setFileIcon(getResources().getDrawable(R.drawable.file))
                        .setFolderIcon(getResources().getDrawable(R.drawable.folder))
                        .setAccessDeniedMessage("can't read file!")
                        .setOpenDialogListener(new OpenFileDialog.OpenDialogListener() {
                            @Override
                            public void OnSelectedFile(String fileName) {
                                addFile(fileName);
                            }
                        }).show();
            }
        });


        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (title.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "Please enter title for post", Toast.LENGTH_SHORT).show();

                else if (post.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "Please enter your post", Toast.LENGTH_SHORT).show();

                else if (!isNetworkConnected())
                    Toast.makeText(getApplicationContext(), "No Network", Toast.LENGTH_SHORT).show();

                else {
                    Api api = DataFromApi.getApi();
                    AddMessage message = new AddMessage(title.getText().toString(), post.getText().toString());
                    Call<MessageResult> call = api.AddMessage(Common.getUser().id, message);

                    call.enqueue(new Callback<MessageResult>() {
                        @Override
                        public void onResponse(Call<MessageResult> call, Response<MessageResult> response) {
                            MessageResult result = response.body();
                            if (result.statusCode == 200) {
                                Toast.makeText(getApplicationContext(), result.status, Toast.LENGTH_SHORT).show();
                                saveToDB(result.results.get(0));
                                uploadMultipart(result.results.get(0));
                                setResult(RESULT_OK);
                                finish();
                            } else
                                Toast.makeText(getApplicationContext(), result.status, Toast.LENGTH_SHORT).show();
                        }


                        @Override
                        public void onFailure(Call<MessageResult> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    private void saveToDB(MessageClass message) {
        for (AttachmentClass attach : attachs)
            attach.messageId = message.getId();
        message.setAttachments(attachs);
        message.save();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {

            File f = new File(getApplicationContext().getExternalCacheDir()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }

            if (!f.exists()) {

                Toast.makeText(getApplicationContext(),

                        "Error while capturing image", Toast.LENGTH_LONG)

                        .show();

                return;

            }

            try {

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

                int rotate = 0;
                try {
                    ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
                addFile(f.getPath());


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String selectedImagePath = c.getString(columnIndex);
                c.close();

                addFile(selectedImagePath);

            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(intentCamera, CAMERA_REQUEST);
            }
        } else if (requestCode == MY_Gallery_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
            }
        }
    }


    private void addFile(String path) {
        AttachmentClass attach = new AttachmentClass();
        File src = new File(path);

        int size = (int) src.length();
        attach.name = path.split("/")[path.split("/").length - 1];
        attach.size = String.valueOf(size / 1024);


        attach.path = AppLauncher.DIR_FILES + File.separator + attach.name;
        attachs.add(attach);

        Storage storage = new Storage(getApplicationContext());
        storage.copy(path, attach.path);





        bAdapterFile.notifyDataSetChanged();
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    public void uploadMultipart(MessageClass message) {

        for (AttachmentClass attach : attachs)
            try {

                new MultipartUploadRequest(getApplicationContext(), Api.HOST + "attach/add")
                        // starting from 3.1+, you can also use content:// URI string instead of absolute file
                        .addHeader("message_id", String.valueOf( message.getId() ))
                        .addHeader("attach_no", String.valueOf( attachs.size() ))
                        .addFileToUpload(attach.path, "file")
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload();


                attach.isUploaded = 1;
                attach.save();
            } catch (Exception exc) {
                Log.e("AndroidUploadService", exc.getMessage(), exc);
            }
    }

}
