package Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import Utils.CustomDate;
import Models.MessageSentClass;
import Models.UserClass;
import Utils.ImageProcessing;
import com.shahbaapp.lft.MessageDetailSentActivity;
import com.shahbaapp.lft.R;

public class MessageSentAdapter extends RecyclerView.Adapter<MessageSentAdapter.MyViewHolder> {

    Context context;


    private List<MessageSentClass> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView personName;
        TextView title;
        ImageView profileImage;


        TextView date;
        TextView headLine;
        TextView group;
        ImageView isUploadedImage, messageImage;

        public MyViewHolder(View view) {
            super(view);

            personName = (TextView) view.findViewById(R.id.person_name);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);
            date = (TextView) view.findViewById(R.id.date);

            title = (TextView) view.findViewById(R.id.title);

            headLine = (TextView) view.findViewById(R.id.head_line);
            group = (TextView) view.findViewById(R.id.group);
            isUploadedImage = view.findViewById(R.id.is_uploaded);
            messageImage = view.findViewById(R.id.image);

        }

    }


    public MessageSentAdapter(Context context, List<MessageSentClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public MessageSentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_sent, parent, false);


        return new MessageSentAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final MessageSentClass message = OfferList.get(position);

        String toUserString = "";
        for (UserClass _user : message.getToUser())
            toUserString += _user.fullName + ", ";

        if(toUserString.length() > 0 )
            toUserString = toUserString.substring(0, toUserString.length() - 2);


        holder.personName.setText(toUserString);
        holder.title.setText(message.getTitle());
        holder.date.setText(CustomDate.format(message.getDate()));
        holder.headLine.setText(message.getBody());


        if (message.getToUser().size() == 1)
            if (message.getToUser().get(0).profileImage != null) {
                holder.profileImage.setImageBitmap(ImageProcessing.base64ToBitmap(message.getToUser().get(0).profileImage));
            }

        if (!message.isUpload()) {
            holder.isUploadedImage.setVisibility(View.VISIBLE);
        }

        if (message.getGroup() != null) {
            holder.group.setVisibility(View.VISIBLE);
            holder.group.setText(message.getGroup().name);
        }

        if (message.getSubject() != null) {
            holder.group.setVisibility(View.VISIBLE);
            holder.group.setText(message.getSubject().name);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, MessageDetailSentActivity.class);
                i.putExtra("messageId", message.getId());

                context.startActivity(i);

            }
        });
    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

}


