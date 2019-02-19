package Models;

import com.orm.SugarRecord;

public class ClassClass {
    public long id;
    public String name;
    public void save(){
        SugarRecord.save(this);
    }
}
