package Models;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

@Table
public class SubcategoryClass{
    private long id;
    private String title;
    private int isSelected;
    private long categoryId;

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

    public boolean isSelected() {
        return isSelected == 1;
    }

   public void setSelected(boolean selected) {
        isSelected = selected ? 1 : 0;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
}