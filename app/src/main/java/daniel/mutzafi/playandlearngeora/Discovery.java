package daniel.mutzafi.playandlearngeora;

public class Discovery {
    private String explorerName;
    private String year;
    private String description;
    private String imageUrl;

    public Discovery(String explorerName, String year, String description, String imageUrl) {
        this.explorerName = explorerName;
        this.year = year;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getExplorerName() {
        return explorerName;
    }

    public String getYear() {
        return year;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
