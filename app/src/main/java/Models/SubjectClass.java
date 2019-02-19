package Models;

import com.orm.SugarRecord;

public class SubjectClass {
    public long id;
    public String name;
    public void save(){
        SugarRecord.save(this);
    }
}
