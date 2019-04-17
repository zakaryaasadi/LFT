package Controller;

import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.view.Display;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

import Models.CategoryClass;
import Models.ClassSubjectClass;
import Models.DocumentClass;
import Models.UserClass;
import Models.UserMessageClass;

public class Common {
    public static CategoryClass categoryClass;
    public static List<UserMessageClass> userMessageSent = new ArrayList<>();
    public static List<ClassSubjectClass> classes;
    public static List<DocumentClass> documents;


    public static UserClass getUser(){
        if(SugarRecord.count(UserClass.class,"IS_ACTIVE = 1",null) > 0)
            return SugarRecord.find(UserClass.class,"IS_ACTIVE = ?", new String[]{"1"}).get(0);
        return null;
    }

    public static int getScreenHeight(FragmentActivity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

}
