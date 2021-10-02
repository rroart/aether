package roart.search.elastic;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder.Item;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.config.NodeConfig;
import roart.common.searchengine.Constants;
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

	final static String myindex = "index";
	final static String mytype = "type";

	private ElasticConfig conf;

	public SearchElastic(String nodename, NodeConfig nodeConf) {
		conf = new ElasticConfig();
		String host = nodeConf.getElasticHost(); 
		String port = nodeConf.getElasticPort(); 

		try {
			conf.client = new PreBuiltTransportClient(Settings.EMPTY).
					addTransportAddress(new TransportAddress(InetAddress.getByName(host), new Integer(port)));
		} catch (Exception e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		}
	}

	public  SearchEngineConstructorResult destroy() {
		conf.client.close();
		return null;
	}

	public  SearchEngineIndexResult indexme(SearchEngineIndexParam index) {
		NodeConfig nodeConf = index.conf;
		String type = index.type;
		String md5 = index.md5;  
		//InputStream inputStream, 
		String dbfilename = index.dbfilename;
		String metadata[] = index.metadata;
		String lang = index.lang;
		String content = index.content;
		String classification = index.classification;
		//, IndexFiles index) {
		int retsize = content.length();
		// this to a method
		log.info("indexing " + md5);

		String cat = classification;

		String indexName = myindex;
		String typeName = mytype;
		try {
			IndexRequestBuilder irb = conf.client.prepareIndex(indexName, typeName, "" + md5).setSource(XContentFactory.jsonBuilder()
					.startObject()
					.field(Constants.ID, md5)
					.field(Constants.LANG, lang)
					.field(Constants.CAT, cat)
					.field(Constants.CONTENT, content)
					.startArray(Constants.METADATA + "array")
					.startObject()
					.array(Constants.METADATA, metadata)
					.endObject()
					.endArray()
					.endObject());
			IndexResponse response = irb.execute().actionGet();
		} catch (Exception e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		}
		SearchEngineIndexResult result = new SearchEngineIndexResult();
		result.size = retsize;
		return result;
	}

	public SearchEngineSearchResult searchme(SearchEngineSearchParam search) {
		String str = search.str;
		String searchtype = search.searchtype;

		int stype = new Integer(searchtype).intValue();
		try {
			SearchRequestBuilder q = conf.client.prepareSearch(myindex)
					.setTypes(mytype)
					//.setQuery(QueryBuilders.queryStringQuery(str))
					.setQuery(QueryBuilders.termQuery(Constants.CONTENT, str))                 // Query
					//.setQuery(QueryBuilders.simpleQueryStringQuery(str))
					.setFetchSource(new String[]{Constants.ID, Constants.LANG, Constants.METADATA}, new String[]{Constants.CONTENT})
					.setFrom(0)
					.setSize(100)
					.setExplain(true);
			if (search.conf.getHighlightmlt()) {
				q = q.highlighter(new HighlightBuilder().field(Constants.CONTENT));
			}
			SearchResponse response = q.execute().actionGet();//get();
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
		String id = search.str;
		String searchtype = search.searchtype;
		//SearchDisplay display) {
		MoreLikeThisQueryBuilder moreLikeThisRequestBuilder;
		//Item[] items = MoreLikeThisQueryBuilder.ids(id);
		Item item = new Item(myindex, mytype, id);
		Item likeItems[] = new Item[1];
		likeItems[0] = item;
		int count = nodeConf.getMLTCount();
		int mintf = nodeConf.getMLTMinTF();
		int mindf = nodeConf.getMLTMinDF();
		MoreLikeThisQueryBuilder queryBuilder = QueryBuilders.moreLikeThisQuery(likeItems)
				.minDocFreq(mindf)
				.minTermFreq(mintf);

		SearchResponse searchResponse = conf.client.prepareSearch(myindex)
				.setTypes(mytype)
				.setQuery(queryBuilder)
				.setFetchSource(new String[]{Constants.ID, Constants.LANG, Constants.METADATA}, new String[]{Constants.CONTENT})
				.setFrom(0)
				.setSize(count)
				.setExplain(true)
				.execute()
				.actionGet();

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
		String str = delete.delete;
		ActionFuture<DeleteResponse> action1 = conf.client.prepareDelete(myindex, mytype, str).execute();
		DeleteRequest deleteRequest = new DeleteRequest(myindex, mytype, str);
		ActionFuture<DeleteResponse> action2 = conf.client.delete(deleteRequest);
		int requestTimeout = 30;
		DeleteResponse response = action1.actionGet(requestTimeout);
		DeleteResponse response2 = action2.actionGet(requestTimeout);
		System.out.println("r1 " + response.toString());
		System.out.println("r2 " + response.toString());
		return null;
	}

}
