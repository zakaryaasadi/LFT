package com.shahbaapp.lft;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.PermissionChecker;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import Controller.Api;
import Controller.DataFromApi;
import Models.UserClass;
import Models.UserResult;
import Utils.FileProcessing;
import Utils.ImageProcessing;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmAccountActivity extends Activity {

    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private ImageView profileImage;
    private TextView fullName, signIn;
    private Bitmap bMap;
    private String selectedImagePath, image;
    private long userId;
    private Api api;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private AlertDialog dialog;
    private Intent intentCamera;
    private Target target = new Target() {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            bMap = bitmap;
            profileImage.setImageBitmap(bMap);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_account);

        intentCamera = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(getExternalCacheDir(), "temp.jpg");
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, FileProcessing.getFileUri(f.getPath()));
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setTheme(R.style.dialog)
                .setCancelable(false).build();

        LoginWithfacebook();

        profileImage = findViewById(R.id.profile_image);
        fullName = findViewById(R.id.full_name);
        signIn = findViewById(R.id.sign_in);

        api = DataFromApi.getApi();

        Intent i = getIntent();
        userId = i.getLongExtra("userId", 0);
        fullName.setText(i.getStringExtra("fullName"));
        image = i.getStringExtra("image");
        if (image != null) {
            bMap = ImageProcessing.base64ToBitmap(image);
            profileImage.setImageBitmap(bMap);
        }


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fullName.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "Please enter your Name", Toast.LENGTH_LONG).show();

                else {

                    Call<UserResult> call = null;
//                    if (profileImage.getDrawable() != getResources().getDrawable(R.drawable.user))
//                        bitmap1 = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
                    if (bMap == null)
                        call = api.Update(userId, fullName.getText().toString());
                    else
                        call = api.Update(userId, fullName.getText().toString(), ImageProcessing.bitmapToBase64(bMap));

                    call.enqueue(new Callback<UserResult>() {
                        @Override
                        public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                            UserResult userResult = response.body();
                            if (userResult.results != null) {
                                saveToDb(userResult.results);
                                Intent i = new Intent(ConfirmAccountActivity.this, NewsCategoryActivity.class);
                                startActivity(i);
                            } else
                                Toast.makeText(getApplicationContext(), userResult.status, Toast.LENGTH_SHORT).show();
                        }


                        @Override
                        public void onFailure(Call<UserResult> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

    }


    private void saveToDb(UserClass user) {
        user.isActive = true;
        user.save();
    }

    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                this);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent pictureActionIntent = null;

                        pictureActionIntent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(
                                pictureActionIntent,
                                GALLERY_PICTURE);

                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (PermissionChecker.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    MY_CAMERA_REQUEST_CODE);
                        } else
                            startActivityForResult(intentCamera, CAMERA_REQUEST);

                    }
                });
        myAlertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


        bMap = null;
        selectedImagePath = null;

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {

            File f = new File(getExternalCacheDir()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }

            if (!f.exists()) {

                Toast.makeText(getBaseContext(),

                        "Error while capturing image", Toast.LENGTH_LONG)

                        .show();

                return;

            }

            Picasso.get().load(f).resize(250, 250).into(target);

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath = c.getString(columnIndex);
                c.close();

                bMap = BitmapFactory.decodeFile(selectedImagePath); // load

                bMap = ImageProcessing.resizeToBitmap(bMap, 250);

                profileImage.setImageBitmap(bMap);

            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void LoginWithfacebook() {
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_fb);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));


        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                dialog.setMessage("Receiving Data...");
                dialog.show();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        dialog.dismiss();
                        try {
                            URL url_profile_image = new URL("https:/graph.facebook.com/" + object.getString("id") + "/picture?width=250&height=250");

                            Picasso.get().load(url_profile_image.toString()).into(target);

                            String name = object.getString("name");
                            fullName.setText(name);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email");
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getApplicationContext(), "camera permission granted", Toast.LENGTH_LONG).show();
                startActivityForResult(intentCamera, CAMERA_REQUEST);

            } else {

                Toast.makeText(getApplicationContext(), "camera permission denied", Toast.LENGTH_LONG).show();

            }
        }
    }

}
