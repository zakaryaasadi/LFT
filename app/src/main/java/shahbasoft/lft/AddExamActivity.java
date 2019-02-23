package shahbasoft.lft;

import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.angmarch.views.NiceSpinner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Models.ExamTypesResult;
import Models.Result;
import Models.SubjectClass;
import Utils.ImageProcessing;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExamActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView personName, addExam;
    private EditText name, max, pass, date;
    private long subjectId;
    private NiceSpinner sprCategory;
    private List<SubjectClass> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mark);

        Intent i = getIntent();
        subjectId = i.getLongExtra("subjectId", 0);

        personName = findViewById(R.id.person_name);
        profileImage = findViewById(R.id.profile_image);
        addExam = findViewById(R.id.btn_post);
        name = findViewById(R.id.name);
        max = findViewById(R.id.max);
        pass = findViewById(R.id.pass);
        date = findViewById(R.id.date);
        sprCategory = findViewById(R.id.spr_category);

        personName.setText(Common.getUser().fullName);
        if (Common.getUser().profileImage != null) {
            profileImage.setImageBitmap(ImageProcessing.base64ToBitmap(Common.getUser().profileImage));
        }


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepicker();
            }
        });

        addExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        fetchExamType();


    }


    private void fetchExamType() {

        Api api = DataFromApi.getApi();
        Call<ExamTypesResult> call = api.GetExamTypes(subjectId);

        call.enqueue(new Callback<ExamTypesResult>() {
            @Override
            public void onResponse(Call<ExamTypesResult> call, Response<ExamTypesResult> response) {
                ExamTypesResult result = response.body();
                if (result.results != null) {
                    list = result.results;
                    List<String> data = new ArrayList<>();
                    for (SubjectClass item : list)
                        data.add(item.name);
                    sprCategory.attachDataSource(new LinkedList<>(data));
                }
            }


            @Override
            public void onFailure(Call<ExamTypesResult> call, Throwable t) {
                Toast.makeText(AddExamActivity.this, "There is not internet", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }


    private void datepicker() {
        Calendar calendar = Calendar.getInstance();

//        Calendar now = Calendar.getInstance();
//        DatePickerDialog dpd = DatePickerDialog.newInstance(
//                new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//                        String d = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
//                        date.setText(d);
//                    }
//                },
//                now.get(Calendar.YEAR), // Initial year selection
//                now.get(Calendar.MONTH), // Initial month selection
//                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
//        );
//
//        dpd.setMinDate(calendar);
//        dpd.show(getSupportFragmentManager(), "Datepickerdialog");
    }

    private void submit() {

        String eName, eDate;
        int eMax, ePass;
        if (name.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter exam name ", Toast.LENGTH_LONG).show();
        } else if (max.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter exam max mark ", Toast.LENGTH_LONG).show();
        } else if (pass.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter exam min mark ", Toast.LENGTH_LONG).show();
        } else if (date.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter date ", Toast.LENGTH_LONG).show();
        } else {
            eName = name.getText().toString();
            eDate = date.getText().toString();
            eMax = Integer.valueOf(max.getText().toString());
            ePass = Integer.valueOf(pass.getText().toString());
            if (eMax <= ePass)
                Toast.makeText(getApplicationContext(), "Passing score must me less that Max score" + String.valueOf(eMax), Toast.LENGTH_LONG).show();

            else {
                Api api = DataFromApi.getApi();
                long eTypeId = list.get(sprCategory.getSelectedIndex()).id;
                DateFormat format = new SimpleDateFormat("MMMM d, yyyy");
                try {
                    Date d = format.parse(eDate);
                    Call<Result> call = api.AddExam(Common.getUser().id, subjectId, eTypeId, eMax, ePass, eName, d);


                    call.enqueue(new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            Result result = response.body();
                            if (result.statusCode == 200) {

                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), result.status, Toast.LENGTH_SHORT).show();
                            }
                        }


                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
