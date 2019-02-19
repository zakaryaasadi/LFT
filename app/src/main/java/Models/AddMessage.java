package Models;

import java.util.ArrayList;
import java.util.List;

import Controller.Common;

public class AddMessage {
    public List<Long> toUsers;
    public List<AttachmentClass> attachs;
    public String title;
    public String body;

    public AddMessage(String title, String body) {
        this.title = title;
        this.body = body;
        toUsers = new ArrayList<>();
        for(UserMessageClass user : Common.userMessageSent)
            toUsers.add(user.id);
    }
}
