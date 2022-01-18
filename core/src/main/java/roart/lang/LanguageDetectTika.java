package roart.lang;

import org.apache.tika.language.detect.LanguageDetector;

public class LanguageDetectTika extends LanguageDetect {

    @Override
    public String detect(String content) {
        LanguageDetector detector = null; //new OptimaizeLangDetector().loadModels();
        detector.addText(content);
        return detector.detect().getLanguage();
        }

}
