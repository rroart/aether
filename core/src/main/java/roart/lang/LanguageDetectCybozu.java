package roart.lang;

import java.util.Date;
import java.util.ArrayList;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.Language;
import com.cybozu.labs.langdetect.LangDetectException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.ConfigConstants;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;

import java.io.*;

public class LanguageDetectCybozu extends LanguageDetect {

    private static Logger log = LoggerFactory.getLogger(LanguageDetectCybozu.class);

    private static LanguageDetect instance = null;

    private NodeConfig nodeConf;
    
    public static LanguageDetect instance() throws LangDetectException {
        if (instance == null) {
            instance = new LanguageDetectCybozu();
        }
        return instance;
    }

    private LanguageDetectCybozu() throws LangDetectException {
        init("./profiles/", nodeConf);
    }

    private static boolean inited = false;

    public void init(String profileDirectory, NodeConfig nodeConf) throws LangDetectException {
        DetectorFactory.loadProfile(profileDirectory);
        String[] mylang = nodeConf.getLanguages();
        if (mylang != null) {
            setLanguages(mylang);
        }
        this.nodeConf = nodeConf;
        inited = true;
    }

    @Override
    public String detect(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            if (!inited) init("./profiles/", nodeConf);
            Date d = logstart();
            Detector detector = DetectorFactory.create();
            detector.append(text);
            String retstr = detector.detect();
            logstop(d);
            log.info("language " + retstr);
            log.info("language2 " + detectLangs(text));
            return retstr;
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        } catch (Error e) {
            System.gc();
            log.error("Error " + Thread.currentThread().getId() + " " + text.length());
            log.error(Constants.ERROR, e);
        }
        return null;
    }

    public ArrayList<Language> detectLangs(String text) throws LangDetectException {
        if (!inited) init("profiles", nodeConf);
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.getProbabilities();
    }

    public static Date logstart() {
        return new Date();
    }

    public static void logstop (Date d) {
        Date n = new Date();
        long m = n.getTime() - d.getTime();
        log.info("timerStop " + m);
    }

}
