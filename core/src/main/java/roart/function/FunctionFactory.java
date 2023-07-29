package roart.function;

import roart.common.config.NodeConfig;
import roart.common.service.ServiceParam;
import roart.service.ControlService;

public class FunctionFactory {

    public static AbstractFunction factory(ServiceParam param, NodeConfig nodeConf, ControlService controlService) {
        switch (param.function) {
        case INDEX:
            return new Index(param, nodeConf, controlService);
        case FILESYSTEM:
            return new Filesystem(param, nodeConf, controlService);
        case OVERLAPPING:
            return new Overlapping(param, nodeConf, controlService);
        case REINDEXSUFFIX:
            return new ReindexSuffix(param, nodeConf, controlService);
        case REINDEXDATE:
            return new ReindexDate(param, nodeConf, controlService);
        case MEMORYUSAGE:
            return new MemoryUsage(param, nodeConf, controlService);
        case NOTINDEXED:
            return new NotIndexed(param, nodeConf, controlService);
        case FILESYSTEMLUCENENEW:
            return new FilesystemLucenenew(param, nodeConf, controlService);
        case DBINDEX:
            return new DbIndex(param, nodeConf, controlService);
        case DBSEARCH:
            return new DbSearch(param, nodeConf, controlService);
        case DBCHECK:
            return new DbCheck(param, nodeConf, controlService);
        case CONSISTENTCLEAN:
            return new ConsistentClean(param, nodeConf, controlService);
        case SEARCH:
            return new Search(param, nodeConf, controlService);
        case SEARCHSIMILAR:
            return new SearchSimilar(param, nodeConf, controlService);
        case REINDEXLANGUAGE:
            return new ReindexLanguage(param, nodeConf, controlService);
        case DELETEPATH:
            return new DeletePath(param, nodeConf, controlService);
        case DBCLEAR:
            return new DbClear(param, nodeConf, controlService);
        case DBDROP:
            return new DbDrop(param, nodeConf, controlService);
        case DBCOPY:
            return new DbCopy(param, nodeConf, controlService);
        case INDEXCLEAN:
            return new IndexClean(param, nodeConf, controlService);
        case INDEXDELETE:
            return new IndexDelete(param, nodeConf, controlService);
        default:
            return null;
        }
    }
}
