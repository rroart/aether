package roart.common.database;

public class DatabaseLanguagesResult extends DatabaseResult {
    public String[] languages;

    public String[] getLanguages() {
        return languages;
    }

    public void setLanguages(String[] languages) {
        this.languages = languages;
    }
}
