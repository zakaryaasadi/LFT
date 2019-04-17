package com.shahbaapp.lft;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import Adapter.DocumentChoiceRecycleAdapter;
import Controller.Api;
import Controller.Common;
import Controller.DataFromApi;
import Models.DocQuestionClass;
import Models.DocumentClass;
import Models.Result;
import Models.UserClass;
import customfonts.EditText_Roboto_Regular;
import customfonts.MyTextView_Roboto_Regular;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddDocumentActivity extends AppCompatActivity {

    LinearLayout parent;
    private float heighScreen;
    private DocumentClass document;
    private TextView btnPost;
    private HashMap<Integer, EditText_Roboto_Regular> editTexts;
    private NiceSpinner sprStudents;
    private CheckBox isForSubject;
    private Toolbar toolbar;
    private long subjectId;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        heighScreen = getResources().getDisplayMetrics().density;
        toolbar = findViewById(R.id.toolbar);
        parent = findViewById(R.id.parent_linear);
        btnPost = findViewById(R.id.btn_post);
        sprStudents = findViewById(R.id.spr_students);
        isForSubject = findViewById(R.id.is_subject);



        List<String> data = new ArrayList<>();
        for(UserClass item : DocumentsActivity.students)
            data.add(item.fullName);

        sprStudents.attachDataSource(new LinkedList<>(data));


        isForSubject.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sprStudents.setEnabled(! isChecked);
            }
        });

        editTexts = new HashMap<>();

        Intent i = getIntent();
        int documentId = i.getIntExtra("id", 0);
        subjectId = i.getLongExtra("subjectId", 0);
        document = DocumentsActivity.documents.get(documentId);

        toolbar.setTitle(document.name);

        createViewElements();

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext().getApplicationContext(), "Sending...", Toast.LENGTH_SHORT).show();
                requestData();
            }
        });


    }

    private void requestData() {
        for (DocQuestionClass q : document.questions)
            if (q.type == 0) {
                q.value = editTexts.get(q.id).getText().toString();
            }

        Api api = DataFromApi.getApi();
        String s = new Gson().toJson(document);
        Call<Result> call = null;
        if(isForSubject.isChecked())
            call = api.AddDocument(Common.getUser().id, subjectId, 0, document);
        else
            call = api.AddDocument(Common.getUser().id, 0, DocumentsActivity.students.get( sprStudents.getSelectedIndex() ).id, document);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result request = response.body();
                if (request != null) {
                    Toast.makeText(getApplicationContext().getApplicationContext(), request.status, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }


            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext().getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void createViewElements() {
        for (DocQuestionClass q : document.questions) {
            if (q.type == 0)
                createEditView(q);
            else
                createChoice(q);
        }
    }

    private void createChoice(DocQuestionClass q) {

        //TextView
        MyTextView_Roboto_Regular tv = new MyTextView_Roboto_Regular(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) (20 * heighScreen), 0, 0);
        tv.setLayoutParams(params);

        tv.setTextColor(getResources().getColor(R.color.blackTextColor));
        tv.setLineSpacing(TypedValue.COMPLEX_UNIT_SP, 5f);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.3f);
        tv.setPadding((int) (18 * heighScreen), 0, (int) (18 * heighScreen), 0);
        tv.setText(q.name);

        //RecycleView
        RecyclerView rv = new RecyclerView(getApplicationContext());
        RecyclerView.LayoutParams par = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        par.setMargins(0, (int) (25 * heighScreen), 0, 0);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(new DocumentChoiceRecycleAdapter(getApplicationContext(), q));
        rv.setLayoutParams(par);
        rv.setNestedScrollingEnabled(false);

        LinearLayout p = createParentLayout();
        p.addView(tv);
        p.addView(rv);
        parent.addView(p, parent.getChildCount() - 1);

        createViewLine();
    }

    private void createEditView(DocQuestionClass d) {

        EditText_Roboto_Regular et = new EditText_Roboto_Regular(getApplicationContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, (int) (128 * heighScreen));
        params.setMargins((int) (20 * heighScreen), (int) (15 * heighScreen), 0, 0);

        et.setLayoutParams(params);

        et.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

        et.setBackgroundColor(getResources().getColor(R.color.transparent));

        et.setTextColor(getResources().getColor(R.color.blackTextColor));

        et.setGravity(Gravity.TOP);


        et.setHint(d.name);

        et.setLineSpacing(TypedValue.COMPLEX_UNIT_SP, 2.7f);

        et.setPadding(1, (int) (5 * heighScreen), 1, 1);

        et.setHintTextColor(getResources().getColor(R.color.hintcolor));

        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        editTexts.put(d.id, et);

        LinearLayout p = createParentLayout();
        p.addView(et);

        parent.addView(p, parent.getChildCount() - 1);

        createViewLine();
    }

    private LinearLayout createParentLayout() {
        LinearLayout p = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) (15 * heighScreen), 0, 0);
        p.setOrientation(LinearLayout.VERTICAL);

        p.setLayoutParams(params);
        return p;
    }

    private void createViewLine() {
        View view = new View(getApplicationContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, (int) (0.7 * heighScreen));
        params.setMargins((int) (55 * heighScreen), (int) (9.2 * heighScreen), (int) (14 * heighScreen), 0);

        view.setLayoutParams(params);

        view.setBackgroundColor(getResources().getColor(R.color.ViewLineColor));

        parent.addView(view, parent.getChildCount() - 1);
    }

}
