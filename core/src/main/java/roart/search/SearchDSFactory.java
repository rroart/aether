package roart.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.ConfigConstants;
import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.service.ControlService;

public class SearchDSFactory {

    private static Logger log = LoggerFactory.getLogger(SearchDSFactory.class);

    public static SearchDS get(NodeConfig nodeConf, ControlService controlService) {
        String type = configIndexing(nodeConf);
        // TODO make OO of this?
        SearchDS search = null;
        if (type.equals(ConfigConstants.SEARCHENGINELUCENE)) {
            search = new LuceneSearchDS(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.SEARCHENGINESOLR)) {
            search = new SolrSearchDS(nodeConf, controlService);
        }
        if (type.equals(ConfigConstants.SEARCHENGINEELASTIC)) {
            search = new ElasticSearchDS(nodeConf, controlService);
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
