package roart.search.elastic;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder.Item;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.NodeConfig;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.model.Inmemory;
import roart.common.inmemory.model.InmemoryUtil;
import roart.common.searchengine.Constants;
import roart.common.searchengine.SearchEngineConstructorParam;
import roart.common.searchengine.SearchEngineConstructorResult;
import roart.common.searchengine.SearchEngineDeleteParam;
import roart.common.searchengine.SearchEngineDeleteResult;
import roart.common.searchengine.SearchEngineIndexParam;
import roart.common.searchengine.SearchEngineIndexResult;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.common.searchengine.SearchResult;
import roart.search.SearchEngineAbstractSearcher;

public class SearchElastic extends SearchEngineAbstractSearcher {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	private ElasticConfig conf;

	public SearchElastic(String nodename, NodeConfig nodeConf) {
		conf = new ElasticConfig();
		String myindex = nodeConf.elasticIndex();
		String host = nodeConf.getElasticHost(); 
		String port = nodeConf.getElasticPort(); 

		try {
			conf.client = new RestHighLevelClient(
			        RestClient.builder(
			                new HttpHost(host, Integer.valueOf(port), "http")));
			        /*
			new PreBuiltXPackTransportClient(Settings.builder()
			        //.put("cluster.name", "myClusterName")
			        //.put("xpack.security.user", "transport_client_user:x-pack-test-password")
			        .build())
			    .addTransportAddress(new TransportAddress(InetAddress.getByName(host), Integer.valueOf(port)));
			    */
	                    conf.client.indices().create(new CreateIndexRequest(myindex), RequestOptions.DEFAULT);
	                    XContentBuilder mappingBuilder = XContentFactory.jsonBuilder().prettyPrint()
	                            .startObject()
	                            .startObject("properties")
	                                .startObject(Constants.CONTENT)
	                                .field("type", "text")
	                                .field("store", "true")
	                                .field("term_vector", "with_positions_offsets")
	                                .endObject()
	                            .endObject()
	                        .endObject();
                            conf.client.indices().putMapping(new PutMappingRequest(myindex).source(mappingBuilder), RequestOptions.DEFAULT);
			} catch (Exception e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		}
	}

	@Override
	public SearchEngineConstructorResult clear(SearchEngineConstructorParam param) {
	    try {
	    NodeConfig nodeConf = param.conf;
	    String myindex = nodeConf.elasticIndex();
	    BulkByScrollResponse response = conf.client.deleteByQuery(new DeleteByQueryRequest(myindex), RequestOptions.DEFAULT);
            } catch (Exception e) {
                log.error(roart.common.constants.Constants.EXCEPTION, e);
            }
	    return new SearchEngineConstructorResult();
	}

	@Override
	public SearchEngineConstructorResult drop(SearchEngineConstructorParam param) {
	    try {
	    NodeConfig nodeConf = param.conf;
	    String myindex = nodeConf.elasticIndex();
	    DeleteResponse response = conf.client.delete(new DeleteRequest(myindex), RequestOptions.DEFAULT);
            } catch (Exception e) {
                log.error(roart.common.constants.Constants.EXCEPTION, e);
            }
	    return new SearchEngineConstructorResult();	        
	}

	public  SearchEngineConstructorResult destroy() {
	    try {
	        conf.client.close();
	    } catch (IOException e) {
	        log.error(roart.common.constants.Constants.EXCEPTION, e);
	    }
	    return null;
	}

	public  SearchEngineIndexResult indexme(SearchEngineIndexParam index) {
		NodeConfig nodeConf = index.conf;
		String md5 = index.md5;  
		//InputStream inputStream, 
		String dbfilename = index.dbfilename;
		String metadata[] = index.metadata;
		String lang = index.lang;
		String classification = index.classification;
                Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
                String content = inmemory.read(index.message);
                if (!InmemoryUtil.validate(index.message.getId(), content)) {
                    SearchEngineIndexResult result = new SearchEngineIndexResult();
                    result.noindexreason = "invalid";
                    result.size = -1;
                    return result;
                }
		//, IndexFiles index) {
		int retsize = content.length();
		// this to a method
		log.info("indexing {}", md5);

		String cat = classification;

		String indexName = nodeConf.elasticIndex();
		try {
		    XContentBuilder builder = XContentFactory.jsonBuilder()
		            .startObject()
		            .field(Constants.ID, md5)
		            .field(Constants.LANG, lang)
		            .field(Constants.CAT, cat)
		            .field(Constants.CONTENT, content)
		            //.startObject()
		            .startArray(Constants.METADATA);
		    for (String data : metadata) {
		        builder.value(data);
		    }
		    builder.endArray()
		    //.endObject()
		    .endObject();
		    IndexResponse response = conf.client.index(new IndexRequest(indexName).id(md5).source(builder), RequestOptions.DEFAULT);
		} catch (Exception e) {
		    log.error(roart.common.constants.Constants.EXCEPTION, e);
		}
		SearchEngineIndexResult result = new SearchEngineIndexResult();
		result.size = retsize;
		return result;
	}

	public SearchEngineSearchResult searchme(SearchEngineSearchParam search) {
                NodeConfig nodeConf = search.conf;
	        String myindex = nodeConf.elasticIndex();
		String str = search.str;
		String searchtype = search.searchtype;

		int stype = new Integer(searchtype).intValue();
		try {
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder
					//.setQuery(QueryBuilders.queryStringQuery(str))
					.query(QueryBuilders.termQuery(Constants.CONTENT, str))                 // Query
					//.setQuery(QueryBuilders.simpleQueryStringQuery(str))		.f
					.fetchSource(new String[]{Constants.ID, Constants.LANG, Constants.METADATA}, new String[]{Constants.CONTENT})
					.from(0)
					.size(100)
					.explain(true);
			if (search.conf.getHighlightmlt()) {
				searchSourceBuilder.highlighter(new HighlightBuilder().field(Constants.CONTENT).maxAnalyzedOffset(999999));
			}
			SearchResponse response = conf.client.search(new SearchRequest(myindex).source(searchSourceBuilder), RequestOptions.DEFAULT);
			SearchHits docs = response.getHits();

			SearchEngineSearchResult result = handleDocs(search, response, docs, true);
			return result;
		} catch (Exception e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		}
		return null;
	}

	private SearchEngineSearchResult handleDocs(SearchEngineSearchParam search, SearchResponse rsp, SearchHits docs, boolean dohighlight) throws Exception {
		SearchEngineSearchResult result = new SearchEngineSearchResult();
		result.results = new SearchResult[docs.getHits().length];
		// To read Documents as beans, the bean must be annotated as given in the example. 

		try {			
			int i = -1;
			for (SearchHit doc : docs.getHits()) {
				i++;
				SearchHit d = doc;
				float score = (float) d.getScore(); 
				Map<String, Object> map = d.getSourceAsMap();
				String md5 = (String) map.get(Constants.ID);
				String lang = (String) map.get(Constants.LANG);
				// TODO fix metadata
				List<String> metadata = null; //new ArrayList(map.get(Constants.METADATA));
				String[] highlights = null;
				if (dohighlight && search.conf.getHighlightmlt()) {
					Map<String, HighlightField> m = d.getHighlightFields();
					HighlightField hlf = m.get(Constants.CONTENT);
					highlights = new String[1];
					highlights[0] = "none";
					if (hlf != null) {
						Text[] frags = hlf.getFragments();
						if (frags != null && frags.length > 0) {
							highlights[0] = frags[0].toString();
						}				
					}
				}
				SearchResult res = new SearchResult();
				res.md5 = md5;
				res.score = score;
				res.lang = lang;
				res.highlights = highlights;
				// TODO fix metadata
				res.metadata = null; // metadata;
				result.results[i] = res;
			}
		} catch (Exception e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		}
		return result;
	}

	public SearchEngineSearchResult searchmlt(SearchEngineSearchParam search) {
		NodeConfig nodeConf = search.conf;
                String myindex = nodeConf.elasticIndex();
		String id = search.str;
		String searchtype = search.searchtype;
		//SearchDisplay display) {
		MoreLikeThisQueryBuilder moreLikeThisRequestBuilder;
		//Item[] items = MoreLikeThisQueryBuilder.ids(id);
		Item item = new Item(myindex, id);
		Item likeItems[] = new Item[1];
		likeItems[0] = item;
		int count = nodeConf.getMLTCount();
		int mintf = nodeConf.getMLTMinTF();
		int mindf = nodeConf.getMLTMinDF();
		MoreLikeThisQueryBuilder queryBuilder = QueryBuilders.moreLikeThisQuery(likeItems)
				.minDocFreq(mindf)
				.minTermFreq(mintf);

                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder
				.query(queryBuilder)
				.fetchSource(new String[]{Constants.ID, Constants.LANG, Constants.METADATA}, new String[]{Constants.CONTENT})
				.from(0)
				.size(count)
				.explain(true);

		                SearchResponse searchResponse = null;
                        try {
                            searchResponse = conf.client.search(new SearchRequest(myindex).source(searchSourceBuilder), RequestOptions.DEFAULT);
                        } catch (IOException e) {
                            log.error(roart.common.constants.Constants.EXCEPTION, e);
                        }
		try {
			SearchHits docs = searchResponse.getHits();
			SearchEngineSearchResult result = handleDocs(search, searchResponse, docs, false);
			return result;
		} catch (Exception e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		}
		return null;
	}

	// TODO untested
	public SearchEngineDeleteResult deleteme(SearchEngineDeleteParam delete) {
                NodeConfig nodeConf = delete.conf;
                String myindex = nodeConf.elasticIndex();
		String str = delete.delete;
		//ActionFuture<DeleteResponse> action1 = conf.client.delete(myindex, mytype, str).execute();
		DeleteRequest deleteRequest = new DeleteRequest(myindex, str);
		DeleteResponse action2 = null;
        try {
            action2 = conf.client.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(roart.common.constants.Constants.EXCEPTION, e);
        }
		int requestTimeout = 30;
		//DeleteResponse response = action1.actionGet(requestTimeout);
		//DeleteResponse response2 = action2.actionGet(requestTimeout);
		//System.out.println("r1 " + response.toString());
		System.out.println("r2 " + action2.toString());
		return null;
	}

}
