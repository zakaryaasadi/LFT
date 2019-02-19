package Models;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

@Table
public class GroupClass {
    public long id;
    public String name;
    public void save(){
        SugarRecord.save(this);
    }
}
