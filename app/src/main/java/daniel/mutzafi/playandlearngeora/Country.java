package daniel.mutzafi.playandlearngeora;

public class Country {
    private String name;
    private String capital;
    private String flagUrl;
    private long population;
    private String region;
    private String currency;
    private String languages;
    private double area;
    private String borders;

    public Country(String name, String capital, String flagUrl, long population, String region, 
                   String currency, String languages, double area, String borders) {
        this.name = name;
        this.capital = capital;
        this.flagUrl = flagUrl;
        this.population = population;
        this.region = region;
        this.currency = currency;
        this.languages = languages;
        this.area = area;
        this.borders = borders;
    }

    public String getName() { return name; }
    public String getCapital() { return capital; }
    public String getFlagUrl() { return flagUrl; }
    public long getPopulation() { return population; }
    public String getRegion() { return region; }
    public String getCurrency() { return currency; }
    public String getLanguages() { return languages; }
    public double getArea() { return area; }
    public String getBorders() { return borders; }
}
