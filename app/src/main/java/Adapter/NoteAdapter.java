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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import Utils.CustomDate;
import Models.NoteClass;
import Models.UserClass;
import com.shahbaapp.lft.R;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {

    Context context;


    private List<NoteClass> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView personName, userName, date, txtNote;
        ImageView profileImage;



        public MyViewHolder(View view) {
            super(view);

            personName =  view.findViewById(R.id.person_name);
            profileImage = view.findViewById(R.id.profile_image);
            date =  view.findViewById(R.id.date);
            userName = view.findViewById(R.id.user_name);
            txtNote = view.findViewById(R.id.note);
        }

    }


    public NoteAdapter(Context context, List<NoteClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public NoteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);


        return new NoteAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        NoteClass note = OfferList.get(position);

        if(note.poster != null) {
            note.poster = new UserClass();
            note.poster.fullName = note.fullName;
            note.poster.userName = note.userName;
            note.poster.profileImage = note.profileImage;
        }

        holder.personName.setText(note.poster.fullName);
        holder.userName.setText("@" + note.poster.userName);
        holder.date.setText(CustomDate.format(note.date));
        holder.txtNote.setText(note.note);

        if(note.profileImage != null){
            byte[] decodedString = Base64.decode(note.profileImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.profileImage.setImageBitmap(bitmap);
        }

    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

}