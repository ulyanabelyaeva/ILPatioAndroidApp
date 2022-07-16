package Models;

public class Food {

    private int id, category, cost;
    private String img, title, description;

    public Food(int id, int cost, int category, String img, String title, String description) {
        this.id = id;
        this.cost = cost;
        this.category = category;
        this.img = img;
        this.title = title;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    public int getCategory() { return category; }
    public void setCategory(int category) { this.category = category; }
    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
