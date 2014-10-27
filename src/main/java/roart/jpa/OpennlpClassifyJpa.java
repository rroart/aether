package roart.jpa;

import roart.model.ResultItem;

import java.util.List;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpennlpClassifyJpa extends ClassifyJpa {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public String classify(String type) {
	return OpennlpClassify.classify(type);
    }


}

