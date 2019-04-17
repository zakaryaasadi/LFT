package com.shahbaapp.lft;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Models.Result;
import Models.StudentClass;
import Models.SubjectClass;
import Models.UserClass;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMarkActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView personName, addMark;
    private EditText mark;
    private double maxMark;
    private long examId;
    private int absent = 0;
    private CheckBox isAbsent;
    private NiceSpinner sprCategory;
    private List<SubjectClass> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mark);

        Intent i = getIntent();
        maxMark = i.getFloatExtra("maxMark",0);
        examId = i.getLongExtra("examId",0);
        String strJson = i.getStringExtra("students");
        list = new Gson().fromJson(strJson, new TypeToken<List<SubjectClass>>(){}.getType());

        personName =  findViewById(R.id.person_name);
        profileImage = findViewById(R.id.profile_image);
        addMark = findViewById(R.id.btn_post);
        mark = findViewById(R.id.mark);
        isAbsent = findViewById(R.id.is_absent);
        sprCategory = findViewById(R.id.spr_category);

        List<String> data = new ArrayList<>();
        for(SubjectClass item : list)
            data.add(item.name);
        sprCategory.attachDataSource(new LinkedList<>(data));

        isAbsent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                absent = isChecked ? 1:0;
                mark.setEnabled(!isChecked);
            }
        });


        personName.setText(Common.getUser().fullName);
        if(Common.getUser().profileImage != null){
            byte[] decodedString = Base64.decode(Common.getUser().profileImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImage.setImageBitmap(bitmap);
        }

        addMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });


    }

    private void submit(){
        double m = 0.0;
        if (mark.getText().toString().equals("") && absent == 0) {
            Toast.makeText(getApplicationContext(), "Please enter mark ", Toast.LENGTH_LONG).show();
        }else{
            if(absent > 0)
                m = 0.0;
            else
                m = Double.valueOf(mark.getText().toString());

            if(m > maxMark)
                Toast.makeText(getApplicationContext(), "Please enter mark less from " + String.valueOf(maxMark), Toast.LENGTH_LONG).show();
            else{
                Api api = DataFromApi.getApi();
                long stdId = list.get(sprCategory.getSelectedIndex()).id;
                Call<Result> call = api.AddMark(stdId, examId, absent, m);

                final double finalM = m;
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Result result = response.body();
                        if (result.statusCode == 200) {
                            Toast.makeText(getApplicationContext(), result.status, Toast.LENGTH_SHORT).show();
                            UserClass std = new UserClass();
                            SubjectClass student = list.get(sprCategory.getSelectedIndex());
                            std.id = student.id;
                            std.fullName = student.name.toUpperCase();
                            std.userName = student.name;
                            StudentClass stdWithMark = new StudentClass();
                            stdWithMark.student = std;
                            stdWithMark.absent = absent;
                            stdWithMark.mark = finalM;
                            ExamTeacherStudentActivity.list.add(stdWithMark);
                            finish();
                        }
                    }


                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
