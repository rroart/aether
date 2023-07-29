package roart.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.ConfigConstants;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.database.IndexFilesAccessFactory;
import roart.service.ControlService;

public class SearchAccessFactory {

    private static Logger log = LoggerFactory.getLogger(SearchAccessFactory.class);

    public static SearchAccess get(NodeConfig nodeConf, ControlService controlService) {
        String type = configIndexing(nodeConf);
        // TODO make OO of this?
        SearchAccess search = null;
        if (type.equals(ConfigConstants.SEARCHENGINELUCENE)) {
            search = new LuceneSearchAccess(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.SEARCHENGINESOLR)) {
            search = new SolrSearchAccess(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.SEARCHENGINEELASTIC)) {
            search = new ElasticSearchAccess(nodeConf, controlService);
        }
        return search;
    }

    private static String configIndexing(NodeConfig nodeConf) {
        try {
            String index = null;
            if (nodeConf.wantLucene()) {
                index = ConfigConstants.SEARCHENGINELUCENE;
            } else if (nodeConf.wantSolr()) {
                index = ConfigConstants.SEARCHENGINESOLR;
            } else if (nodeConf.wantElastic()) {
                index = ConfigConstants.SEARCHENGINEELASTIC;
            }
            if (index != null) {
                //controlService.index = index;
                //roart.search.SearchDao.instance(index);
            }
            return index;
        } catch (Exception e) {
            // TODO propagate
            log.error(Constants.EXCEPTION, e);
            return null;
        }
    }
}
