package hello;

public class Recipe {

    private long id;
    private String dayOfWeek;
    private String content;
    private String searchTag;

    public Recipe() {
    }

    public Recipe(long id, String dayOfWeek, String content, String searchTag) {

        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.content = content;
        this.searchTag = searchTag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSearchTag() {
        return searchTag;
    }

    public void setSearchTag(String searchTag) {
        this.searchTag = searchTag;
    }


}