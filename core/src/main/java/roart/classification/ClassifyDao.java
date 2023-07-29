package roart.classification;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import roart.common.config.ConfigConstants;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.inmemory.model.InmemoryMessage;
import roart.service.ControlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassifyDao {
    private static Logger log = LoggerFactory.getLogger(ClassifyDao.class);

    private ClassifyAccess classify = null;

    private NodeConfig nodeConf;

    private ControlService controlService;
    
    public ClassifyDao(NodeConfig nodeConf, ControlService controlService) {
        super();
        this.nodeConf = nodeConf;
        this.classify = ClassifyAccessFactory.get(nodeConf, controlService);
        this.controlService = controlService;
    }

    public String classify(InmemoryMessage message, String language) {
        if (classify == null) {
            return null;
        }
        return classify.classify(message, language);
    }

}
