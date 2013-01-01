package roart.lang;

import java.util.Date;
import java.util.ArrayList;
import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.Language;
import com.cybozu.labs.langdetect.LangDetectException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

public class LanguageDetect {

    private static Log log = LogFactory.getLog("LanguageDetect");

    private static boolean inited = false;

    public static void init(String profileDirectory) throws LangDetectException {
	File dir = new File(profileDirectory);
	/*
	if (dir != null && dir.listFiles() != null)
        for (File file: dir.listFiles()) {
	    log.info("fn " +file);
	}
	File dir2 = new File(".");
	if (dir2 != null)
        for (File file: dir2.listFiles()) {
	    log.info("fn2 " +file);
	}
	File dir3 = new File("..");
	if (dir3 != null)
        for (File file: dir3.listFiles()) {
	    log.info("fn3 " +file);
	}
	*/
	DetectorFactory.loadProfile(profileDirectory);
	inited = true;
    }
    
    public static String detect(String text) throws LangDetectException {
	if (!inited) init("./profiles/");
	Date d = logstart();
	Detector detector = DetectorFactory.create();
	detector.append(text);
	String retstr = detector.detect();
	logstop(d);
	return retstr;
    }

    public static ArrayList<Language> detectLangs(String text) throws LangDetectException {
	if (!inited) init("profiles");
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