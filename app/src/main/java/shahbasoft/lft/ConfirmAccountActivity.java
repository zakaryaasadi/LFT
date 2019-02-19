package shahbasoft.lft;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.util.Base64;
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
import com.orm.SugarRecord;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import Controller.Api;
import Controller.DataFromApi;
import Models.UserClass;
import Models.UserResult;
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
    private Bitmap bitmap;
    private String selectedImagePath, image;
    private  long userId;
    private Api api;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private AlertDialog dialog;
    private Intent intentCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_account);

        intentCamera = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(getExternalCacheDir(), "temp.jpg");
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(getApplicationContext(),"shahbasoft.lft.fileprovider",f));

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
        userId = i.getLongExtra("userId",0);
        fullName.setText(i.getStringExtra("fullName"));
        image = i.getStringExtra("image");
        if(image != null) {
            byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImage.setImageBitmap(bitmap);
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

                if(fullName.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "Please enter your Name", Toast.LENGTH_LONG).show();

                else{

                    Call<UserResult> call = null;
                    if(profileImage.getDrawable() != getResources().getDrawable(R.drawable.user))
                        bitmap = ((BitmapDrawable)profileImage.getDrawable()).getBitmap();
                    if(bitmap == null)
                        call = api.Update(userId, fullName.getText().toString());
                    else
                        call = api.Update(userId, fullName.getText().toString(),Bitmap2String(bitmap));

                    call.enqueue(new Callback<UserResult>() {
                        @Override
                        public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                            UserResult userResult = response.body();
                            if (userResult.results != null) {
                                saveToDb(userResult.results);
                                Intent i = new Intent(ConfirmAccountActivity.this, NewsCategoryActivity.class);
                                startActivity(i);
                            }else
                                Toast.makeText(getApplicationContext(), userResult.status, Toast.LENGTH_SHORT).show();
                        }


                        @Override
                        public void onFailure(Call<UserResult> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

    }


    private void saveToDb(UserClass user){
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
                        }else
                            startActivityForResult(intentCamera, CAMERA_REQUEST);

                    }
                });
        myAlertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


        bitmap = null;
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

            try {

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

                bitmap = Bitmap.createScaledBitmap(bitmap, profileImage.getMaxWidth(), profileImage.getMaxHeight(), true);

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



                profileImage.setImageBitmap(bitmap);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath = c.getString(columnIndex);
                c.close();


                bitmap = BitmapFactory.decodeFile(selectedImagePath); // load

                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);

                profileImage.setImageBitmap(bitmap);

            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String Bitmap2String(Bitmap bitmap){
        if(bitmap == null)
            return null;
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,      70, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
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
                            Picasso.get().load(url_profile_image.toString()).into(profileImage);
                            String name = object.getString("name") ;
                            fullName.setText(name);
                        }catch (JSONException e) {
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
