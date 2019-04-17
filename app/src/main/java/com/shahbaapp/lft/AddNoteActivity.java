package com.shahbaapp.lft;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Models.Result;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNoteActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView personName,addNote;
    private EditText note;
    private long studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Intent i = getIntent();

        personName =  findViewById(R.id.person_name);
        profileImage = findViewById(R.id.profile_image);
        addNote = findViewById(R.id.btn_post);
        note = findViewById(R.id.note);
        studentId = i.getLongExtra("studentId",0);

        personName.setText(Common.getUser().fullName);
        if(Common.getUser().profileImage != null){
            byte[] decodedString = Base64.decode(Common.getUser().profileImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImage.setImageBitmap(bitmap);
        }

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (note.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "Please enter note ", Toast.LENGTH_LONG).show();
                else{
                    Api api = DataFromApi.getApi();
                    Call<Result> call = api.AddNote(Common.getUser().id, studentId, note.getText().toString());

                    call.enqueue(new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            Result result = response.body();
                            if (result.statusCode == 200){
                                Toast.makeText(getApplicationContext(), result.status,Toast.LENGTH_SHORT).show();
                                finish();
                            }else
                                Toast.makeText(getApplicationContext(), result.status,Toast.LENGTH_SHORT).show();
                        }


                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }
}
