package Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;

import com.orm.SugarRecord;

import java.util.List;

import Controller.Api;
import Controller.Common;
import Utils.CustomDate;
import Controller.DataFromApi;
import Models.MessageClass;
import Models.Result;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.shahbaapp.lft.MessageDetailActivity;
import com.shahbaapp.lft.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{

    Context context;


    private List<MessageClass> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {



        TextView personName;
        TextView title;
        ImageView profileImage;


        TextView date;
        TextView headLine;
        TextView group;
        ImageView isReadImage, messageImage;

        public MyViewHolder(View view) {
            super(view);

            personName = (TextView) view.findViewById(R.id.person_name);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);
            date = (TextView) view.findViewById(R.id.date);

            title = (TextView) view.findViewById(R.id.title);

            headLine = (TextView) view.findViewById(R.id.head_line);
            group = (TextView) view.findViewById(R.id.group);
            isReadImage = view.findViewById(R.id.is_read);
            messageImage = view.findViewById(R.id.image);

        }

    }


    public MessageAdapter(Context context, List<MessageClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);


        return new MessageAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final MessageClass message = OfferList.get(position);

        holder.personName.setText(message.getFromUser().fullName);
        holder.title.setText(message.getTitle());
        holder.date.setText( CustomDate.format(message.getDate()) );
        holder.headLine.setText(message.getBody());

        if(message.status == 2){
            holder.messageImage.setImageDrawable(context.getResources().getDrawable(R.drawable.homework2));
        }


        if(message.getFromUser().profileImage != null){
            byte[] decodedString = Base64.decode(message.getFromUser().profileImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.profileImage.setImageBitmap(bitmap);
        }

        if(message.isRead()){
            holder.isReadImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmarkblank));
            if(message.status == 1)
                holder.messageImage.setImageDrawable(context.getResources().getDrawable(R.drawable.message_already_read));
        }

        if(message.getGroup() != null){
            holder.group.setVisibility(View.VISIBLE);
            holder.group.setText(message.getGroup().name);
        }

        if(message.getSubject() != null){
            holder.group.setVisibility(View.VISIBLE);
            holder.group.setText(message.getSubject().name);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, MessageDetailActivity.class);
                i.putExtra("messageId",message.getId());

                if(!message.isRead()) {
                    message.setRead(true);
                    message.save();
                    isRead(message.getId());
                    SugarRecord.save(message);
                }

                context.startActivity(i);

            }
        });
    }



    @Override
    public int getItemCount() {
        return OfferList.size();

    }

    private void isRead(long messageId) {
        Api api = DataFromApi.getApi();
        Call<Result> call = api.MessageIsRead(Common.getUser().id, messageId);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
            }
        });
    }

}


