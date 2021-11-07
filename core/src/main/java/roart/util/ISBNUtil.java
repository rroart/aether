package roart.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.ISBNValidator;

public class ISBNUtil {

    private String[] regexps = new String[] {
            "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]",
            "(?=(?:[0-9]+[- ]){4})[- 0-9]{17}",
            "(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}",
            "97[89][0-9]{10}",
            "[0-9X]{10}"
    };

    ISBNValidator validator = new ISBNValidator();

    public String extract(String text, boolean test) {
        if (text == null) {
            return null;
        }
        Pattern[] patterns = new Pattern[regexps.length];
        for (int i = 0; i < regexps.length; i++) {
            patterns[i] = Pattern.compile(regexps[i]);            
        }
        int len = text.length();
        String first = text.substring(0, Math.min(len, 4096));
        String last = null;
        if (len >= 4096) {
            last = text.substring(len - 4096);
        }
        String result = search(test, patterns, first);   
        if (result == null && last != null) {
            result = search(test, patterns, last);
        }
        return result;
    }

    private String search(boolean test, Pattern[] patterns, String text) {
        String result = null;
        for (int i = 0; i < regexps.length; i++) {
            Pattern pattern = patterns[i];
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                boolean valid = validator.isValid(matcher.group());
                if (test) {
                    System.out.println("match " + i + " " + valid + " " + matcher.group());
                }
                if (valid) {
                    if (!test) {
                        return matcher.group();
                    } else {
                        if (result == null) {
                            result = matcher.group();
                        }
                    }
                }
            }
        }
        return result;
    }
}
