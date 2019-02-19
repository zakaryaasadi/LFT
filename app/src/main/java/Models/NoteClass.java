package Models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Table;

import java.util.Date;

@Table
public class NoteClass {
    public long id;
    public String note;
    public Date date;
    public long posterId;
    public String userName;
    public String fullName;
    public String profileImage;

    @Ignore
    public  UserClass poster;

    public void save(){
        posterId = poster.id;
        userName = poster.userName;
        fullName = poster.fullName;
        profileImage = poster.profileImage;
        SugarRecord.save(this);
    }

}
