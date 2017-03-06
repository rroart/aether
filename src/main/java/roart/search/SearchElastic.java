package roart.search;

import java.io.*;
import java.net.InetAddress;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.lucene.search.MoreLikeThisQuery;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder.Item;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.tika.metadata.Metadata;
import org.apache.tools.ant.taskdefs.Execute;

import roart.service.SearchService;
import roart.lang.LanguageDetect;
import roart.model.SearchDisplay;
import roart.model.ResultItem;
import roart.model.IndexFiles;
import roart.config.ConfigConstants;
import roart.config.MyConfig;
import roart.database.IndexFilesDao;


public class SearchElastic {
	private static Logger log = LoggerFactory.getLogger(SearchElastic.class);

	final static String myindex = "index";
	final static String mytype = "type";
	
	static Client client = null;

	public SearchElastic() {
		if (client != null) {
			return;
		}
		String host = MyConfig.conf.elastichost; 
		String port = MyConfig.conf.elasticport; 

		try {
			client = new PreBuiltTransportClient(Settings.EMPTY).
					addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), new Integer(port)));
		} catch (Exception e) {
			log.error(roart.util.Constants.EXCEPTION, e);
		}
	}

	public static int indexme(String type, String md5, InputStream inputStream, String dbfilename, Metadata metadata, String lang, String content, String classification, IndexFiles index) {
		int retsize = content.length();
		// this to a method
		log.info("indexing " + md5);

		String cat = classification;
		List mdarr = new ArrayList();
		if (metadata == null) {
			//md = "";

		} else {
			log.info("with md " + metadata);
			//doc.addField(Constants.METADATA, metadata);
			Metadata md = metadata;
			for (String name : md.names()) {
				String value = md.get(name);
				mdarr.add(name + ":" + value);
			}
		}

		String indexName = myindex;
		String typeName = mytype;
		try {
			IndexRequestBuilder irb = client.prepareIndex(indexName, typeName, "" + md5).setSource(XContentFactory.jsonBuilder()
					.startObject()
					.field(Constants.ID, md5)
					.field(Constants.LANG, lang)
					.field(Constants.CAT, cat)
					.field(Constants.CONTENT, content)
					.startArray(Constants.METADATA + "array")
					.startObject()
					.array(Constants.METADATA, mdarr.toArray(new String[0]))
					.endObject()
					.endArray()
					.endObject());
			IndexResponse response = irb.execute().actionGet();
		} catch (Exception e) {
			log.error(roart.util.Constants.EXCEPTION, e);
		}
		return retsize;
	}

	public static ResultItem[] searchme(String str, String searchtype, SearchDisplay display) {
		ResultItem[] strarr = new ResultItem[0];

		int stype = new Integer(searchtype).intValue();
		try {
			SearchRequestBuilder q = client.prepareSearch(myindex)
					.setTypes(mytype)
					//.setQuery(QueryBuilders.queryStringQuery(str))
					.setQuery(QueryBuilders.termQuery(Constants.CONTENT, str))                 // Query
					//.setQuery(QueryBuilders.simpleQueryStringQuery(str))
					.setFetchSource(new String[]{Constants.ID, Constants.LANG, Constants.METADATA}, new String[]{Constants.CONTENT})
					.setFrom(0)
					.setSize(100)
					.setExplain(true);
			if (SearchService.isHighlightMLT()) {
				q = q.highlighter(new HighlightBuilder().field(Constants.CONTENT));
			}
			SearchResponse response = q.execute().actionGet();//get();
			SearchHits docs = response.getHits();

			strarr = handleDocs(display, response, docs, true);
		} catch (Exception e) {
			log.error(roart.util.Constants.EXCEPTION, e);
		}
		return strarr;
	}

	private static ResultItem[] handleDocs(SearchDisplay display,
			SearchResponse rsp, SearchHits docs, boolean dohighlight) throws Exception {
		ResultItem[] strarr;
		// To read Documents as beans, the bean must be annotated as given in the example. 

		strarr = new ResultItem[(int) (docs.getHits().length + 1)];
		try {
			strarr[0] = IndexFiles.getHeaderSearch(display);
			int i = -1;
			for (SearchHit doc : docs.getHits()) {
				i++;
				SearchHit d = doc;
				float score = (float) d.getScore(); 
				Map<String, Object> map = d.getSource();
				String md5 = (String) map.get(Constants.ID);
				String lang = (String) map.get(Constants.LANG);
				List<String> metadata = null; //new ArrayList(map.get(Constants.METADATA));
				IndexFiles indexmd5 = IndexFilesDao.getByMd5(md5);
				String filename = indexmd5.getFilelocation();
				log.info((i + 1) + ". " + md5 + " : " + filename + " : " + score);
				String[] highlights = null;
				if (dohighlight && SearchService.isHighlightMLT()) {
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
				strarr[i + 1] = IndexFiles.getSearchResultItem(indexmd5, lang, score, highlights, display, metadata);
			}
		} catch (Exception e) {
			log.error(roart.util.Constants.EXCEPTION, e);
		}
		return strarr;
	}

	public static ResultItem[] searchmlt(String id, String searchtype, SearchDisplay display) {
		ResultItem[] strarr = new ResultItem[0];
		MoreLikeThisQueryBuilder moreLikeThisRequestBuilder;
		Item[] items = MoreLikeThisQueryBuilder.ids(id);
		Item item = new Item(myindex, mytype, id);
		Item likeItems[] = new Item[1];
		likeItems[0] = item;
		int count = MyConfig.conf.configMap.get(MyConfig.Config.MLTCOUNT);
		int mintf = MyConfig.conf.configMap.get(MyConfig.Config.MLTMINTF);
		int mindf = MyConfig.conf.configMap.get(MyConfig.Config.MLTMINDF);
		MoreLikeThisQueryBuilder queryBuilder = QueryBuilders.moreLikeThisQuery(items)
				.minDocFreq(mindf)
				.minTermFreq(mintf);

		SearchResponse searchResponse = client.prepareSearch(myindex)
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
			strarr = handleDocs(display, searchResponse, docs, false);
		} catch (Exception e) {
			log.error(roart.util.Constants.EXCEPTION, e);
		}
		return strarr;
	}

	// TODO untested
	public static void deleteme(String str) {
		ListenableActionFuture<DeleteResponse> action1 = client.prepareDelete(myindex, mytype, str).execute();
		DeleteRequest deleteRequest = new DeleteRequest(myindex, mytype, str);
		ActionFuture<DeleteResponse> action2 = client.delete(deleteRequest);
		int requestTimeout = 30;
		DeleteResponse response = action1.actionGet(requestTimeout);
		DeleteResponse response2 = action2.actionGet(requestTimeout);
		System.out.println("r1 " + response.toString());
		System.out.println("r2 " + response.toString());
	}

}
