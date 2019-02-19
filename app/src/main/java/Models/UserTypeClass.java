package Models;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

@Table
public class UserTypeClass {
    public long id;
    public int userType;

    public void save() {
        SugarRecord.save(this);
    }
}


