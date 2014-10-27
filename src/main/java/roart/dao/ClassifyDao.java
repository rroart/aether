package roart.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import roart.jpa.ClassifyJpa;
import roart.jpa.OpennlpClassifyJpa;
import roart.jpa.MahoutClassifyJpa;

import roart.model.ResultItem;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassifyDao {
    private static Logger log = LoggerFactory.getLogger("ClassifyDao");

    private static ClassifyJpa classifyJpa = null;

    public static void instance(String type) {
	System.out.println("instance " + type);
	log.info("instance " + type);
	if (type == null) {
	  return;
	}
	if (classifyJpa == null) {
	    if (type.equals("mahout")) {
		classifyJpa = new MahoutClassifyJpa();
	    }
	    if (type.equals("opennlp")) {
		classifyJpa = new OpennlpClassifyJpa();
	    }
	}
    }

    public static String classify(String type) {
	if (classifyJpa == null) {
	    return null;
	}
	return classifyJpa.classify(type);
    }

}
