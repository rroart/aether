package roart.convert.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.convert.ConvertAbstract;
import roart.common.config.NodeConfig;
import roart.common.convert.ConvertParam;
import roart.common.convert.ConvertResult;

//import roart.queue.TikaQueueElement;

public class Calibre extends ConvertAbstract {

    private static Logger log = LoggerFactory.getLogger(Calibre.class);

    public Calibre(String nodename, NodeConfig nodeConf) {
        
    }
    
    @Override
    public ConvertResult convert(ConvertParam param) {
        boolean retry = false;
        String filename = null;
        //String dirname = null;
        String tmp = null;
        String output = null;
        String retlistid = null;
        //TikaQueueElement el = null;
        
		    File file = new File(filename);
	    String dirname = file.getParent();
	    File dir = new File(dirname);
	    boolean w = dir.canWrite();
	    if (!w) {
		dir.setWritable(true);
	    }
	    long execstart = System.currentTimeMillis();
	    String[] arg = { filename, tmp };
	    //output = executeTimeout("/usr/bin/ebook-convert", arg, retlistid, null /*el*/);
	    if (output != null) {
		//el.convertsw = "calibre";
		long time = execstart - System.currentTimeMillis();
		//el.index.setConverttime("" + time);
        } else {
            log.info("ebook-convert no output");
        }
	if (!w) {
	    dir.setWritable(false);
	}
	retry = true;
	return null;
    }
}
