package roart.lang;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

public class LanguageDetectOptimaize extends LanguageDetect {

    private static LanguageDetect instance = null;

    public static LanguageDetect instance() throws IOException {
        if (instance == null) {
            instance = new LanguageDetectOptimaize();
        }
        return instance;
    }

    List<LanguageProfile> languageProfiles;
    LanguageDetector languageDetector;
    TextObjectFactory textObjectFactory;

    LanguageDetectOptimaize() throws IOException {
        languageProfiles = new LanguageProfileReader().readAllBuiltIn();

        languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfiles)
                .build();

        textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();

    }

    @Override
    public String detect(String content) {
        TextObject textObject = textObjectFactory.forText(content);
        Optional<LdLocale> lang = languageDetector.detect(textObject);
        if (lang.isPresent()) {
            return lang.get().getLanguage();
        } else {
            return null;
        }
    }

}
