package Models;


import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Table;

import java.util.List;

@Table
public class MessageSentClass extends MessageClass {
    @Ignore
    public List<UserMessageClass> toUser;

    public boolean isUpload(){
        for(AttachmentClass item : getAttachments())
            if(item.isUploaded == 0)
                return false;
        return  true;
    }

    @Override
    public void save() {
        super.save();
        for(UserMessageClass user : toUser) {
            user.messageId = getId();
            user.save();
        }
    }

    public List<UserMessageClass> getToUser(){
        if(toUser == null)
            toUser = SugarRecord.find(UserMessageClass.class, "MESSAGE_ID = ?",new String[]{String.valueOf(getId())} );
        return toUser;
    }
}
