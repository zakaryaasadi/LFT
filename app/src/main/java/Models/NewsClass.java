package Models;

import android.text.Html;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

import java.util.Date;

@Table
public class NewsClass
{
    protected long id;
    protected String personName;
    protected int userId;
    protected String userName;
    protected String profileImage;
    protected String title;
    protected Date creationDate;
    private long subcategoryFK;
    protected SubcategoryClass subcategory;
    protected String headLine;
    protected int type;
    protected String body;
    protected String newsImage;
    protected Boolean sharable;
    protected Date eventDate;
    protected int privateNewsType;

    public void setPrivateNewsType(int privateNewsType) {
        this.privateNewsType = privateNewsType;
    }

    public void save(){
        subcategoryFK = subcategory.getId();
        SugarRecord.save(this);
    }
    public void delete(){SugarRecord.delete(this);}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public SubcategoryClass getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(SubcategoryClass subcategory) {
        this.subcategory = subcategory;
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBody() {
        return Html.fromHtml(body).toString();
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(String newsImage) {
        this.newsImage = newsImage;
    }

    public Boolean getSharable() {
        return sharable;
    }

    public void setSharable(Boolean sharable) {
        this.sharable = sharable;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public PrivateNewsType getPrivateNewsType() {
		switch(privateNewsType){
			case 1 : return PrivateNewsType.Group;
			case 2 : return PrivateNewsType.Class;
			case 3 : return PrivateNewsType.Subject;
		}
        return PrivateNewsType.Public;
    }


}


enum PrivateNewsType {
    Public, Group, Class, Subject
}