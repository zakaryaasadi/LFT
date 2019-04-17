package Models;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Table;

import java.util.List;

@Table
public class CategoryClass{
    private long id;
    private String title;
    private String image;
    @Ignore
    private List<SubcategoryClass> subcategories;


    public List<SubcategoryClass> getSubcategories() {
        subcategories = SugarRecord.find(SubcategoryClass.class, "CATEGORY_ID = ?", new String[]{String.valueOf(id)} );
        return subcategories;
    }

    public void setSubcategories(List<SubcategoryClass> subcategories) {
        this.subcategories = subcategories;
    }



    public void delete(){
        SugarRecord.delete(this);
    }

    public void save(){
        for(SubcategoryClass ite : subcategories){
            ite.setCategoryId(id);
            SugarRecord.save(ite);
        }
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
