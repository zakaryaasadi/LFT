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

import Models.StudentClass;
import Models.UserClass;
import com.shahbaapp.lft.R;

public class ExamTeacherStudentAdapter extends RecyclerView.Adapter<ExamTeacherStudentAdapter.MyViewHolder> {

    Context context;
    private List<StudentClass> OfferList;


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView personName;
        TextView userName, mark;
        ImageView profileImage;



        public MyViewHolder(View view) {
            super(view);

            personName =  view.findViewById(R.id.person_name);
            profileImage =  view.findViewById(R.id.profile_image);
            userName = view.findViewById(R.id.user_name);
            mark = view.findViewById(R.id.mark);
        }

    }


    public ExamTeacherStudentAdapter(Context context, List<StudentClass> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public ExamTeacherStudentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_teacher_student, parent, false);


        return new ExamTeacherStudentAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        StudentClass std = OfferList.get(position);
        final UserClass student = std.student;

        holder.personName.setText(student.fullName);
        holder.userName.setText("@" + student.userName);
        if(std.absent == 0)
            holder.mark.setText(String.valueOf(std.mark));
        if(student.profileImage != null){
            byte[] decodedString = Base64.decode(student.profileImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.profileImage.setImageBitmap(bitmap);
        }



    }


    @Override
    public int getItemCount() {
        return OfferList.size();

    }

}