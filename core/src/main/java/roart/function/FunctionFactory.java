package roart.function;

import roart.common.config.NodeConfig;
import roart.common.service.ServiceParam;

public class FunctionFactory {

    public static AbstractFunction factory(ServiceParam param, NodeConfig nodeConf) {
        switch (param.function) {
        case INDEX:
            return new Index(param, nodeConf);
        case FILESYSTEM:
            return new Filesystem(param, nodeConf);
        case OVERLAPPING:
            return new Overlapping(param, nodeConf);
        case REINDEXSUFFIX:
            return new ReindexSuffix(param, nodeConf);
        case REINDEXDATE:
            return new ReindexDate(param, nodeConf);
        case MEMORYUSAGE:
            return new MemoryUsage(param, nodeConf);
        case NOTINDEXED:
            return new NotIndexed(param, nodeConf);
        case FILESYSTEMLUCENENEW:
            return new FilesystemLucenenew(param, nodeConf);
        case DBINDEX:
            return new DbIndex(param, nodeConf);
        case DBSEARCH:
            return new DbSearch(param, nodeConf);
        case DBCHECK:
            return new DbCheck(param, nodeConf);
        case CONSISTENTCLEAN:
            return new ConsistentClean(param, nodeConf);
        case SEARCH:
            return new Search(param, nodeConf);
        case SEARCHSIMILAR:
            return new SearchSimilar(param, nodeConf);
        case REINDEXLANGUAGE:
            return new ReindexLanguage(param, nodeConf);
        case DELETEPATH:
            return new DeletePath(param, nodeConf);
        case DBCLEAR:
            return new DbClear(param, nodeConf);
        case DBDROP:
            return new DbDrop(param, nodeConf);
        case DBCOPY:
            return new DbCopy(param, nodeConf);
        case INDEXCLEAN:
            return new IndexClean(param, nodeConf);
        case INDEXDELETE:
            return new IndexDelete(param, nodeConf);
        default:
            return null;
        }
    }
}
