package Models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Table;

import java.util.ArrayList;

@Table
public class UserClass {
    public long id;
    public String userName;
    public String fullName;
    public String profileImage;
    public boolean isActive;
    private long typeId;
    @Ignore
    private UserTypeClass type;

    public void save(){
        if(SugarRecord.findById(UserClass.class, id) != null)
            isActive = SugarRecord.findById(UserClass.class, id).isActive;

        if(type != null)
        {
            typeId = type.id;
            type.save();
        }
        SugarRecord.save(this);
    }

    public UserTypeClass getType() {
        type = SugarRecord.findById(UserTypeClass.class,typeId);
        return type;
    }

    public void delete(){
        SugarRecord.delete(this);
    }

}








