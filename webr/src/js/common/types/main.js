export type mainType = {
  title: string,
  description: string,
  source: string,
}

export type ServiceParam = {
// enum Function { INDEX, FILESYSTEM, OVERLAPPING, REINDEXSUFFIX, REINDEXDATE, MEMORYUSAGE, NOTINDEXED, FILESYSTEMLUCENENEW, DBINDEX, DBSEARCH, CONSISTENTCLEAN, SEARCH, SEARCHSIMILAR, REINDEXLANGUAGE, DELETEPATH }
      config: NodeConfig,
      functionn: string,
      name: string,
      add: string,
      file: string,
      suffix: string,
      lowerdate: string,
      higherdate: string,
      reindex: boolean,
      md5change: boolean,
      clean: boolean,
      path: string,
      md5: string,
      md5checknew: boolean,
      dirname: string,
      lang: string,
      webpath: string,
}

export type ServiceResult = {
    config : NodeConfig,
    list: ResultItem[][],
     error: string,
}

export type ResultItem = {

    items : object[],
}

export type SearchEngineSearchParam = {
    nodename : string,
    conf: NodeConfig,
 str: string,
    searchtype: string,
}

export type SearchEngineSearchResult = {
    results: SearchResult[],
    list : string[],
}

export type SearchResult = {
    md5: string,
    score: number,
    lang: string,
    highlights: string[],
    display: string,
    metadata: string[],
}

export type DatabaseLanguagesResult  = {
    languages: string[]
}

export { ServiceParam, ServiceResult, ResultItem, SearchEngineSearchParam, SearchEngineSearchResult, SearchResult, DatabaseLanguagesResult }
