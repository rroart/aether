package roart.lang;

import java.io.IOException;

import com.cybozu.labs.langdetect.LangDetectException;

public class LanguageDetectFactory {
    public enum Detect { CYBOZU, OPTIMAIZE };

    public static LanguageDetect getMe(Detect type) throws IOException, LangDetectException {
        switch (type) {
        case OPTIMAIZE:
            return LanguageDetectOptimaize.instance();
        default:
            return LanguageDetectCybozu.instance();
        }
    }
}
