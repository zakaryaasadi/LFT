package Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snatik.storage.Storage;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.angmarch.views.NiceSpinner;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Adapter.AddMessageFileAdapter;
import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Utils.FileProcessing;
import Utils.OpenFileDialog;
import Fragment.WhtsNewFragment;
import Models.AttachmentClass;
import Models.ClassSubjectClass;
import Models.MessageClass;
import Models.MessageResult;
import Models.NewsClass;
import Models.SubjectClass;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.shahbaapp.lft.AppLauncher;
import  com.shahbaapp.lft.R;

import static android.app.Activity.RESULT_OK;

public class AddHomeworkActivity extends Fragment {

    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_PICTURE = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_Gallery_REQUEST_CODE = 123;
    private ImageView btnGallery, btnCamera, newsImage, myProfileImage, btnAttach;
    private EditText title, post;
    private TextView btnPost, myPersonName;
    private NewsClass news;
    private Api api;
    private Bitmap bitmap = null;
    private String selectedImagePath;
    private LinearLayout layout;
    private NiceSpinner sprClass, sprSubject;
    private Intent intentCamera;
    private RecyclerView recyclerViewFile;
    private ArrayList<AttachmentClass> attachs;
    private AddMessageFileAdapter bAdapterFile;
    private Intent pictureActionIntent;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_homework, container, false);
        news = new NewsClass();


        btnGallery = view.findViewById(R.id.btn_grallery);
        btnCamera = view.findViewById(R.id.btn_camera);
        btnAttach = view.findViewById(R.id.btn_attach);
        title = view.findViewById(R.id.title);
        post = view.findViewById(R.id.post);
        btnPost = view.findViewById(R.id.btn_post);
        newsImage = view.findViewById(R.id.news_image);
        layout = view.findViewById(R.id.news_layout);
        myPersonName = view.findViewById(R.id.person_name);
        myProfileImage = view.findViewById(R.id.profile_image);
        sprClass = view.findViewById(R.id.spr_class);
        sprSubject = view.findViewById(R.id.spr_subject);

        recyclerViewFile = view.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewFile.setLayoutManager(layoutManager);
        recyclerViewFile.setItemAnimator(new DefaultItemAnimator());
        attachs = new ArrayList<>();
        bAdapterFile = new AddMessageFileAdapter(getContext(), attachs);
        recyclerViewFile.setAdapter(bAdapterFile);

        intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(getContext().getExternalCacheDir(), "temp.jpg");
        Uri photoURI = FileProcessing.getFileUri(f.getPath());
        intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        if (Common.getUser().profileImage != null) {
            byte[] decodedString = Base64.decode(Common.getUser().profileImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            myProfileImage.setImageBitmap(bitmap);
        }

        myPersonName.setText(Common.getUser().fullName);

        List<String> data = new ArrayList<>();
        for (ClassSubjectClass item : Common.classes)
            data.add(item.name);
        sprClass.attachDataSource(new LinkedList<>(data));

        data.clear();
        for (SubjectClass item : Common.classes.get(sprClass.getSelectedIndex()).subjects)
            data.add(item.name);
        sprSubject.attachDataSource(new LinkedList<>(data));

        sprClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<String> data = new ArrayList<>();
                for (SubjectClass item : Common.classes.get(position).subjects)
                    data.add(item.name);
                sprSubject.attachDataSource(new LinkedList<>(data));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pictureActionIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_Gallery_REQUEST_CODE);
                } else
                    startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
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
                new OpenFileDialog(getContext())
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


        api = DataFromApi.getApi();

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (title.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Please enter title for post", Toast.LENGTH_LONG).show();

                else if (post.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Please enter your post", Toast.LENGTH_LONG).show();

                else {
                    api = DataFromApi.getApi();
                    long subjectId = Common.classes.get(sprClass.getSelectedIndex()).subjects.get(sprSubject.getSelectedIndex()).id;
                    Call<MessageResult> call = api.AddHomework(Common.getUser().id,
                            subjectId,
                            title.getText().toString(),
                            post.getText().toString());

                    call.enqueue(new Callback<MessageResult>() {
                        @Override
                        public void onResponse(Call<MessageResult> call, Response<MessageResult> response) {
                            MessageResult result = response.body();
                            if (result.statusCode == 200) {
                                Toast.makeText(getContext(), result.status, Toast.LENGTH_SHORT).show();
                                uploadMultipart(result.results.get(0));
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new WhtsNewFragment()).commit();
                            } else
                                Toast.makeText(getContext(), result.status, Toast.LENGTH_SHORT).show();
                        }


                        @Override
                        public void onFailure(Call<MessageResult> call, Throwable t) {
                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {

            File f = new File(getContext().getExternalCacheDir()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }

            if (!f.exists()) {

                Toast.makeText(getContext(),

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
                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String selectedImagePath = c.getString(columnIndex);
                c.close();

                addFile(selectedImagePath);

            } else {
                Toast.makeText(getContext(), "Cancelled",
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

        Storage storage = new Storage(getContext());
        storage.copy(path, attach.path);





        bAdapterFile.notifyDataSetChanged();
    }


    public void uploadMultipart(MessageClass message) {

        for (AttachmentClass attach : attachs)
            try {
                new MultipartUploadRequest(getContext(), Api.HOST + "attach/add")
                        // starting from 3.1+, you can also use content:// URI string instead of absolute file
                        .addHeader("message_id", String.valueOf(message.getId()))
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