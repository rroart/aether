package roart.search;

import roart.model.ResultItem;
import roart.model.SearchDisplay;
import roart.util.EurekaConstants;
import roart.model.IndexFiles;

import java.util.List;

import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneSearchAccess extends SearchAccess {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public String getAppName() {
    	return EurekaConstants.LUCENE;
    }
    
}
