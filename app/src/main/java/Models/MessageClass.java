package Models;

import android.text.Html;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Table;

import java.util.Date;
import java.util.List;

@Table
public class MessageClass {
    private long id;

    private String title;
    private String body;
    public int status;
    private Date date;
    private int isRead;
    private int groupId;
    private int subjectId;
    private int fromUserId;
    @Ignore
    private UserMessageClass fromUser;
    @Ignore
    private GroupClass group;
    @Ignore
    private SubjectClass subject;
    @Ignore
    private List<AttachmentClass> attachments;


    public void delete() {
        SugarRecord.deleteInTx(getAttachments());
        SugarRecord.delete(this);
    }

    public void save() {
        SugarRecord.saveInTx(attachments);
        fromUserId = Integer.valueOf(String.valueOf(fromUser.id));
        fromUser.save();

        if (group != null) {
            groupId = Integer.valueOf(String.valueOf(group.id));
            group.save();
        }

        if (subject != null) {
            subjectId = Integer.valueOf(String.valueOf(subject.id));
//                        subject.save();
        }

        if (isRead != 1 && SugarRecord.findById(MessageClass.class, id) != null)
            isRead = SugarRecord.findById(MessageClass.class, id).isRead;
    }

    public UserMessageClass getFromUser() {
        if (fromUser == null)
            return SugarRecord.findById(UserMessageClass.class, fromUserId);
        return fromUser;
    }

    public List<AttachmentClass> getAttachments() {
        attachments = SugarRecord.find(AttachmentClass.class, "MESSAGE_ID = ?", new String[]{String.valueOf(id)});
        return attachments;
    }

    public List<AttachmentClass> getAttachments(AttachmentType type) {
        int t = 0;
        if (type == AttachmentType.FILE)
            t = 1;
        attachments = SugarRecord.find(AttachmentClass.class, "MESSAGE_ID = ? and TYPE = ?", new String[]{String.valueOf(id), String.valueOf(t)});
        return attachments;
    }

    public boolean isRead() {
        MessageClass message = SugarRecord.findById(MessageClass.class, id);
        if (message != null)
            return message.isRead == 1;
        return isRead == 1;
    }

    public SubjectClass getSubject() {
        try {
            if (subject == null && subjectId != 0)
                return SugarRecord.findById(SubjectClass.class, subjectId);

            return subject;
        } catch (Exception e) {
            return null;
        }
    }

    public GroupClass getGroup() {

        if (group == null && groupId != 0)
            return SugarRecord.findById(GroupClass.class, groupId);

        return group;
    }


    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setFromUser(UserMessageClass fromUser) {
        this.fromUser = fromUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body != null ? Html.fromHtml(body).toString() : "";
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public void setGroup(GroupClass group) {
        this.group = group;
    }


    public void setSubject(SubjectClass subject) {
        this.subject = subject;
    }


    public void setAttachments(List<AttachmentClass> attachments) {
        this.attachments = attachments;
    }

    public void setRead(boolean read) {
        isRead = read ? 1 : 0;
    }
}
