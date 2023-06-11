package roart.function;

import roart.common.service.ServiceParam;

public class FunctionFactory {

    public static AbstractFunction factory(ServiceParam param) {
        switch (param.function) {
        case INDEX:
            return new Index(param);
        case FILESYSTEM:
            return new Filesystem(param);
        case OVERLAPPING:
            return new Overlapping(param);
        case REINDEXSUFFIX:
            return new ReindexSuffix(param);
        case REINDEXDATE:
            return new ReindexDate(param);
        case MEMORYUSAGE:
            return new MemoryUsage(param);
        case NOTINDEXED:
            return new NotIndexed(param);
        case FILESYSTEMLUCENENEW:
            return new FilesystemLucenenew(param);
        case DBINDEX:
            return new DbIndex(param);
        case DBSEARCH:
            return new DbSearch(param);
        case DBCHECK:
            return new DbCheck(param);
        case CONSISTENTCLEAN:
            return new ConsistentClean(param);
        case SEARCH:
            return new Search(param);
        case SEARCHSIMILAR:
            return new SearchSimilar(param);
        case REINDEXLANGUAGE:
            return new ReindexLanguage(param);
        case DELETEPATH:
            return new DeletePath(param);
        case DBCLEAR:
            return new DbClear(param);
        case DBDROP:
            return new DbDrop(param);
        case DBCOPY:
            return new DbCopy(param);
        case INDEXCLEAN:
            return new IndexClean(param);
        case INDEXDELETE:
            return new IndexDelete(param);
        default:
            return null;
        }
    }
}
