package roart.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import roart.jpa.ClassifyJpa;
import roart.jpa.MahoutClassifyJpa;

import roart.model.ResultItem;
 
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClassifyDao {
    private static Log log = LogFactory.getLog("ClassifyDao");

    private static ClassifyJpa classifyJpa = null;

    public static void instance(String type) {
	System.out.println("instance " + type);
	log.info("instance " + type);
	if (classifyJpa == null) {
	    if (type.equals("mahout")) {
		classifyJpa = new MahoutClassifyJpa();
	    }
	}
    }

    public static String classify(String type) {
	return classifyJpa.classify(type);
    }

}
