package roart.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.constants.Constants;
import roart.common.model.FileLocation;
import roart.common.model.FileObject;
import roart.common.model.IndexFiles;
import roart.common.queue.QueueElement;
import roart.common.service.ServiceParam;

public class FilterUtil {
    protected static Logger log = LoggerFactory.getLogger(FilterUtil.class);

    public static boolean indexFilter(IndexFiles index, ServiceParam serviceParam) {
        boolean filter = true;
        //filter = indexFilterIndex(index, serviceParam);
        if (!filter) {
            return false;
        }
        filter = indexFilterPath(index, serviceParam);
        if (!filter) {
            return false;
        }
        filter = indexFilterSuffix(index, serviceParam);
        if (!filter) {
            return false;
        }
        filter = indexFilterLanguage(index, serviceParam);
        if (!filter) {
            return false;
        }
        return indexFilterDate(index, serviceParam);
    }

    public static boolean indexFilterPath(IndexFiles index, ServiceParam element) {
        String path = element.path;
        if (path == null) {
            return true;
        }
        boolean contains = false;
        for (FileLocation fl : index.getFilelocations()) {
            if (fl.toString().contains(path)) {
                return true;
            }
        }
        return false;
    }

    public static boolean indexFilterSuffix(IndexFiles index, ServiceParam element) {
        if (element.suffix == null) {
            return true;
        }
        for (FileLocation fl : index.getFilelocations()) {
            if (fl.getFilename().endsWith(element.suffix)) {
                return true;
            }
        }
        return false;
    }

    public static boolean indexFilterLanguage(IndexFiles index, ServiceParam element) {
        String mylanguage = index.getLanguage();
        if (element.lang == null) {
            return true;
        }
        if (mylanguage.equals(element.lang)) {
            return true;
        }
        return false;

    }

    public static boolean indexFilterDate(IndexFiles index, ServiceParam element) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        String lowerdate = element.lowerdate;
        String higherdate = element.higherdate;
        Long tslow = null;
        if (lowerdate != null) {
            try {
                tslow = sdf.parse(lowerdate).getTime();
            } catch (ParseException e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
        Long tshigh = null;
        if (higherdate != null) {
            try {
                tshigh = sdf.parse(higherdate).getTime();
            } catch (ParseException e) {
                log.error(Constants.EXCEPTION, e);
            }
        }

        String timestamp = index.getTimestamp();
        if (tslow != null || tshigh != null) {
            if (timestamp != null) {
                if (tslow != null && new Long(timestamp).compareTo(tslow) >= 0) {
                    return false;
                }
                if (tshigh != null && new Long(timestamp).compareTo(tshigh) <= 0) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean indexFilterIndex(IndexFiles index, ServiceParam element) {
        boolean reindex = element.reindex;
        boolean indexed = index.getIndexed() != null && index.getIndexed();
        return reindex == indexed;
    }

    public static boolean filterSuffix(FileObject fo, ServiceParam element) {
        if (element.suffix == null) {
            return true;
        }
        return fo.toString().endsWith(element.suffix);
    }

}
