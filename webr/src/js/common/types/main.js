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
      function: string;
      name: string;
      path: string;
      file: string;
      suffix: string;
      lowerdate: string;
      higherdate: string;
      reindex: boolean;
      clean: boolean;
      md5checknew: boolean;
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
