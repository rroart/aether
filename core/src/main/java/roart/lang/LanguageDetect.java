package roart.lang;

public abstract class LanguageDetect {

    private String[] languages = { "en" };
    
    protected void setLanguages(String[] languages) {
        this.languages = languages;
    }
    
    protected String[] getLanguages() {
        return languages;
    }
    
    public abstract String detect(String content);

     public boolean isSupportedLanguage(String language) {
        String[] langs = languages;
        for (String l : langs) {
            if (l.equals(language)) {
                return true;
            }
        }
        return false;
    }
    
}
