package Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import Controller.Common;
import Models.UserClass;
import Models.UserMessageClass;
import com.shahbaapp.lft.R;

public class UserMessageAdapter extends RecyclerView.Adapter<UserMessageAdapter.MyViewHolder> {

    Context context;
    private TextView txtSent;
    private RelativeLayout btnSend;
    private List<UserClass> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView personName;
        TextView userName;
        ImageView profileImage;
        CheckBox chkSend;



        public MyViewHolder(View view) {
            super(view);

            personName =  view.findViewById(R.id.person_name);
            profileImage =  view.findViewById(R.id.profile_image);
            userName = view.findViewById(R.id.user_name);
            chkSend = view.findViewById(R.id.chk_send);
        }

    }


    public UserMessageAdapter(Context context, List<UserClass> offerList, TextView txtSent, RelativeLayout btnSend) {
        this.OfferList = offerList;
        this.context = context;
        this.txtSent = txtSent;
        this.btnSend = btnSend;
    }

    @Override
    public UserMessageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_message, parent, false);


        return new UserMessageAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final UserClass user = OfferList.get(position);
        final UserMessageClass userMessage = new Gson().fromJson(new Gson().toJson(user), UserMessageClass.class);
        holder.personName.setText(user.fullName);
        holder.userName.setText("@" + user.userName);
        if(user.profileImage != null){
            byte[] decodedString = Base64.decode(user.profileImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.profileImage.setImageBitmap(bitmap);
        }




        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(!holder.chkSend.isChecked()) {
                    holder.chkSend.setChecked(true);
                }else {
                    holder.chkSend.setChecked(false);
                }



            }
        });


        holder.chkSend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Common.userMessageSent.add(userMessage);
                }else{
                    Common.userMessageSent.remove(userMessage);
                }
                if(Common.userMessageSent.size() > 0){
                    btnSend.setVisibility(View.VISIBLE);
                    txtSent.setText("SEND(" + String.valueOf(Common.userMessageSent.size()) + ")");
                }else{
                    btnSend.setVisibility(View.GONE);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return OfferList.size();
    }

}