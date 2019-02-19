package Models;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

@Table
public class ChoiceClass {
    private long id;
    private String title;
    private float voteCount;
    private Boolean isChoiced;
    private long newsId;

    public void save(){
        SugarRecord.save(this);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(float voteCount) {
        this.voteCount = voteCount;
    }

    public Boolean isChoiced() {
        return isChoiced;
    }

    public void setChoiced(Boolean choiced) {

        isChoiced = choiced;

    }

    public long getNewsId() {
        return newsId;
    }

    public void setNewsId(long newsId) {
        this.newsId = newsId;
    }

}
