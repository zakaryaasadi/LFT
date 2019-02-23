package Models;
import com.orm.SugarRecord;
import com.orm.dsl.Table;

@Table
public class AttachmentClass {
    public long id;
    public long messageId;
    public String name;
    public String path;
    private int type;
    public String size;
    public int isUploaded = 0;
    public AttachmentType getType() {
        switch(type) {
            case 0:
                return AttachmentType.JPG;
            case 1:
                return AttachmentType.FILE;
        }
        return AttachmentType.FILE;
    }

    public void save(){
        SugarRecord.save(this);
    }
}


