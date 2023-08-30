/*
export type mainType = {
  title: string,
  description: string,
  source: string,
}
*/

class ServiceParam {
// enum Function { INDEX, FILESYSTEM, OVERLAPPING, REINDEXSUFFIX, REINDEXDATE, MEMORYUSAGE, NOTINDEXED, FILESYSTEMLUCENENEW, DBINDEX, DBSEARCH, CONSISTENTCLEAN, SEARCH, SEARCHSIMILAR, REINDEXLANGUAGE, DELETEPATH }
      config: object;
      functionn: string;
      name: string;
      add: string;
      file: string;
      suffix: string;
      lowerdate: string;
      higherdate: string;
      reindex: boolean;
      md5change: boolean;
      clean: boolean;
      path: string;
      md5: string;
      md5checknew: boolean;
      dirname: string;
      lang: string;
      webpath: string;
      async: boolean;
}

class ServiceResult {
    config : object;
    list: ResultItem[][];
     error: string;
}

class ResultItem {

    items : object[];
}

class SearchEngineSearchParam {
    conf: object;
 str: string;
    searchtype: string;
}

class SearchEngineSearchResult {
    results: SearchResult[];
    list : string[];
}

class SearchResult {
    md5: string;
    score: number;
    lang: string;
    highlights: string[];
    display: string;
    metadata: string[];
}

class DatabaseLanguagesResult  {
    languages: string[]
}

export { ServiceParam, ServiceResult, ResultItem, SearchEngineSearchParam, SearchEngineSearchResult, SearchResult, DatabaseLanguagesResult }
