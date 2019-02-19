package Models;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Table;

import java.util.Date;
import java.util.List;

@Table
public class VotingClass extends NewsClass{
    private int totalVotes ;
    private int voteType;
    private int voteResult;
    private Boolean voteCount;
    private Date expireDate;
    @Ignore
    private List<ChoiceClass> choices;

    public List<ChoiceClass> getChoices() {
        if(choices != null)
            return choices;
        choices = SugarRecord.find(ChoiceClass.class, "NEWS_ID = ?",new String[]{String.valueOf(id)} );
        return choices;
    }

    public void setChoices(List<ChoiceClass> choices) {
        this.choices = choices;
    }

    public void save(){
        for (ChoiceClass item: choices)
            item.save();
        SugarRecord.save(this);
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public int getVoteType() {
        return voteType;
    }

    public void setVoteType(int voteType) {
        this.voteType = voteType;
    }

    public int getVoteResult() {
        return voteResult;
    }

    public void setVoteResult(int voteResult) {
        this.voteResult = voteResult;
    }

    public Boolean getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Boolean voteCount) {
        this.voteCount = voteCount;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }
}
