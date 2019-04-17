package com.shahbaapp.lft;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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

import Adapter.MessageFileSentAdapter;
import Utils.CustomDate;
import Models.AttachmentClass;
import Models.MessageSentClass;
import Models.UserMessageClass;

public class MessageDetailSentActivity extends AppCompatActivity {

    private ImageView profileImage, goBack;
    private TextView personName, toUser, group, date, title, body;

    private MessageSentClass message;
    private RecyclerView recyclerView;
    private MessageFileSentAdapter bAdapterFile;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        Intent i = getIntent();
        long messageId = i.getLongExtra("messageId", 0);
        message = SugarRecord.findById(MessageSentClass.class, messageId);

        recyclerView = findViewById(R.id.recyclerView_file);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager1);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        List<AttachmentClass> attachs = message.getAttachments();
        bAdapterFile = new MessageFileSentAdapter(this, attachs);
        recyclerView.setAdapter(bAdapterFile);


        personName = findViewById(R.id.person_name);
        group = findViewById(R.id.group);
        date = findViewById(R.id.date);
        title = findViewById(R.id.title);
        body = findViewById(R.id.body);
        toUser = findViewById(R.id.my_person_name);

        profileImage = findViewById(R.id.profile_image);

        goBack = findViewById(R.id.btn_back);


        personName.setText(message.getFromUser().fullName);

        if (message.getGroup() != null)
            group.setText(message.getGroup().name);
        else if (message.getSubject() != null)
            group.setText(message.getSubject().name);

        date.setText(CustomDate.format(message.getDate()));
        title.setText(message.getTitle());
        body.setText(message.getBody());


        String toUserString = "";
        if (message.getToUser().size() > 1){
            for (UserMessageClass _user : message.getToUser())
                toUserString += _user.fullName + ", ";
            toUserString = toUserString.substring(0, toUserString.length() - 2);
        }else if(message.getToUser().size() == 1)
            toUserString = message.getToUser().get(0).fullName;

        toUser.setText(toUserString);

        toUser.setText(toUserString);

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
        bAdapterFile.notifyDataSetChanged();

    }
}
