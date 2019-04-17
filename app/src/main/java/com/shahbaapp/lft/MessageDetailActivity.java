package com.shahbaapp.lft;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.orm.SugarRecord;

import java.util.List;
import Adapter.MessageFileAdapter;
import Controller.Common;
import Utils.CustomDate;
import Models.AttachmentClass;
import Models.MessageClass;

public class MessageDetailActivity extends AppCompatActivity {

    private ImageView profileImage, goBack;
    private TextView personName, myPersonName, group, date, title, body;

    private MessageClass message;
    private RecyclerView recyclerViewFile;;
    private MessageFileAdapter bAdapterFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        Intent i = getIntent();
        long messageId = i.getLongExtra("messageId", 0);
        message = SugarRecord.findById(MessageClass.class, messageId);


        recyclerViewFile = findViewById(R.id.recyclerView_file);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext());
        recyclerViewFile.setLayoutManager(layoutManager1);
        recyclerViewFile.setItemAnimator(new DefaultItemAnimator());
        List<AttachmentClass> attachs = message.getAttachments();
        bAdapterFile = new MessageFileAdapter(getApplicationContext(), attachs);
        recyclerViewFile.setAdapter(bAdapterFile);




        personName = findViewById(R.id.person_name);
        group = findViewById(R.id.group);
        date = findViewById(R.id.date);
        title =  findViewById(R.id.title);
        body =  findViewById(R.id.body);
        myPersonName = findViewById(R.id.my_person_name);

        profileImage = findViewById(R.id.profile_image);

        goBack = findViewById(R.id.btn_back);


        personName.setText(message.getFromUser().fullName);

        if(message.getGroup() != null)
            group.setText(message.getGroup().name);
        else if(message.getSubject() != null)
            group.setText(message.getSubject().name);

        date.setText(CustomDate.format(message.getDate()) );
        title.setText(message.getTitle());
        body.setText(message.getBody());
        myPersonName.setText(Common.getUser().fullName);

        if(message.getFromUser().profileImage != null){
            byte[] decodedString = Base64.decode(message.getFromUser().profileImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImage.setImageBitmap(bitmap);
        }


        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}
