package roart.jpa;

import roart.model.ResultItem;

import java.util.List;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class ClassifyJpa {

    private Log log = LogFactory.getLog(this.getClass());

    public abstract String classify(String type);

}

