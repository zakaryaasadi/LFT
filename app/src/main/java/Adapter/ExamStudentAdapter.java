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

import java.util.List;

import Models.NoteStudentClass;
import Models.UserClass;
import com.shahbaapp.lft.ExamSubjectsActivity;
import com.shahbaapp.lft.R;

public class ExamStudentAdapter extends RecyclerView.Adapter<ExamStudentAdapter.MyViewHolder> {

    Context context;
    private List<NoteStudentClass> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView personName;
        TextView userName;
        ImageView profileImage;



        public MyViewHolder(View view) {
            super(view);

            personName =  view.findViewById(R.id.person_name);
            profileImage =  view.findViewById(R.id.profile_image);
            userName = view.findViewById(R.id.user_name);
        }

    }


    public ExamStudentAdapter(Context context, List<NoteStudentClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public ExamStudentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note_student, parent, false);


        return new ExamStudentAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final UserClass student = OfferList.get(position);

        holder.personName.setText(student.fullName);
        holder.userName.setText("@" + student.userName);
        if(student.profileImage != null){
            byte[] decodedString = Base64.decode(student.profileImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.profileImage.setImageBitmap(bitmap);
        }




        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ExamSubjectsActivity.class);
                i.putExtra("studentId",student.id);
                context.startActivity(i);
            }
        });

    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

}