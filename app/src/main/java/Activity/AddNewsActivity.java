package Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.snatik.storage.Storage;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.angmarch.views.NiceSpinner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Adapter.AddNewsImageAdapter;
import Controller.Common;
import Controller.Api;
import Controller.DataFromApi;
import Models.NewsClass;
import Models.NewsResult;
import Models.SubcategoryClass;
import Utils.ImageProcessing;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shahbasoft.lft.NewsActivity;
import shahbasoft.lft.R;

import static android.app.Activity.RESULT_OK;

public class AddNewsActivity extends Fragment {

    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_PICTURE = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private ImageView btnGallery, btnCamera, myProfileImage;
    private EditText title, post;
    private TextView btnPost, myPersonName;
    private NewsClass news;
    private Api api;
    private NiceSpinner sprCategory;
    private List<SubcategoryClass> subcategory;
    private Intent intentCamera;
    private AlertDialog dialog;
    private RecyclerView recyclerview;
    private List<String> images;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_news, container, false);
        news = new NewsClass();


        btnGallery = view.findViewById(R.id.btn_grallery);
        btnCamera = view.findViewById(R.id.btn_camera);
        title = view.findViewById(R.id.title);
        post = view.findViewById(R.id.post);
        btnPost = view.findViewById(R.id.btn_post);
        myPersonName = view.findViewById(R.id.person_name);
        myProfileImage = view.findViewById(R.id.profile_image);
        sprCategory = view.findViewById(R.id.spr_category);
        subcategory = Common.categoryClass.getSubcategories();


        recyclerview = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        images = new ArrayList<String>();
        AddNewsImageAdapter adapter = new AddNewsImageAdapter(getContext(), images);
        recyclerview.setAdapter(adapter);


        dialog = new SpotsDialog.Builder()
                .setContext(getContext())
                .setTheme(R.style.dialog)
                .setCancelable(false).build();

        intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(getContext().getExternalCacheDir(), "temp.jpg");
        Uri photoURI = FileProvider.getUriForFile(getContext(), "shahbasoft.lft.fileprovider", f);
        intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        if (Common.getUser().profileImage != null) {
            myProfileImage.setImageBitmap(ImageProcessing.base64ToBitmap(Common.getUser().profileImage));
        }

        myPersonName.setText(Common.getUser().fullName);

        List<String> data = new ArrayList<>();
        for (SubcategoryClass item : subcategory)
            data.add(item.getTitle());

        sprCategory.attachDataSource(new LinkedList<>(data));


        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pictureActionIntent = null;

                pictureActionIntent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(
                        pictureActionIntent,
                        GALLERY_PICTURE);
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


        api = DataFromApi.getApi();

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (title.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Please enter title for post", Toast.LENGTH_LONG).show();

                else if (post.getText().toString().equals(""))
                    Toast.makeText(getContext(), "Please enter your post", Toast.LENGTH_LONG).show();

                else if (images.size() == 0)
                    Toast.makeText(getContext(), "Please put image for news", Toast.LENGTH_LONG).show();

                else {
                    String[] words = post.getText().toString().split(" ");
                    String headline = "";
                    for (int i = 0; i < words.length; i++) {
                        if (i == 9)
                            break;
                        headline += words[i] + " ";
                    }


                    news.setUserId((int) Common.getUser().id);
                    news.setType(2);
                    news.setSubcategory(subcategory.get(sprCategory.getSelectedIndex()));
                    news.setTitle(title.getText().toString());
                    news.setHeadLine(headline);
                    news.setBody(post.getText().toString());
                    news.setSharable(true);
                    news.setPrivateNewsType(0);
                    dialog.setMessage("Sending...");
                    dialog.show();
                    Call<NewsResult> call = api.AddNews(news);

                    call.enqueue(new Callback<NewsResult>() {
                        @Override
                        public void onResponse(Call<NewsResult> call, Response<NewsResult> response) {
                            dialog.dismiss();
                            NewsResult newsResult = response.body();
                            if (newsResult != null) {
                                if(newsResult.results != null ) {
                                    uploadMultipart(newsResult.results.get(0).getId());
                                    NewsActivity.toolbar.setTitle("Explore");
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                            new ExploreActivity()).commit();
                                }else{
                                    Toast.makeText(getContext(), newsResult.status, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }


                        @Override
                        public void onFailure(Call<NewsResult> call, Throwable t) {
                            dialog.dismiss();
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

        Bitmap bitmap;
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {

            File f = new File(getContext().getExternalCacheDir()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }
            bitmap = BitmapFactory.decodeFile(f.getPath());
            images.add(ImageProcessing.bitmapToBase64(bitmap));
            recyclerview.getAdapter().notifyDataSetChanged();


        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {

                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    images.add(ImageProcessing.bitmapToBase64(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                recyclerview.getAdapter().notifyDataSetChanged();

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
                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();
                startActivityForResult(intentCamera, CAMERA_REQUEST);

            } else {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void uploadMultipart(long newsId) {
        Storage storage = new Storage(getContext());
        for (int i = 0; i < images.size(); i++)
            try {
                String image = images.get(i);
                String fileName = String.valueOf(i) + ".jpg";
                String path = storage.getInternalCacheDirectory() + File.separator + fileName;
                storage.createFile(path, ImageProcessing.base64ToBitmap(image));
                new MultipartUploadRequest(getContext(), Api.HOST + "news/addimage")
                        // starting from 3.1+, you can also use content:// URI string instead of absolute file
                        .addHeader("news_id", String.valueOf(newsId))
                        .addFileToUpload(path, fileName)
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload();
            } catch (Exception exc) {
                Log.e("AndroidUploadService", exc.getMessage(), exc);
            }
    }


}