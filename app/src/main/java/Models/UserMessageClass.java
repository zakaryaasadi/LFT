package Models;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

@Table
public class UserMessageClass extends UserClass {
    public long messageId;

    @Override
    public void save() {
        super.save();
        SugarRecord.save(this);
    }
}
