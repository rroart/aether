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
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder.Item;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.tika.metadata.Metadata;

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

    final static String myindex = "aether";
    
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
    /*
    if (lang == null) {
	  lang = "";
    }
    if (cat == null) {
	cat = "";
    }
    	 */
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

	String indexName = "bla";
	String typeName = "grr";
	try {
	IndexRequestBuilder irb = client.prepareIndex(indexName, typeName, "1").setSource(XContentFactory.jsonBuilder()
	.startObject()
    .field(Constants.ID, md5)
    .field(Constants.LANG, lang)
    .field(Constants.CAT, cat)
	.field(Constants.CONTENT, content)
	.startArray()
	.array(Constants.METADATA, mdarr.toArray(new String[0]))
	.endArray()
	.endObject());
	IndexResponse response = irb.execute().actionGet();
	System.out.println("re " + response.toString());
	} catch (Exception e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	}
	/*
	  if(response.isCreated()) {
		    System.out.println("Creating nested document succeeded.");
		  } else {
		    System.err.println("Creating nested document failed.");
		  }
	try {
	    ElasticInputDocument doc = new ElasticInputDocument();

	    // Fields "id","name" and "price" are already included in Elastic installation, you must add your new custom fields in SchemaXml.

	    // Create a collection of documents 

	    Collection<ElasticInputDocument> docs = new ArrayList<ElasticInputDocument>();
	    docs.add( doc );
	    server.add( docs );
	    server.commit();
	    UpdateRequest req = new UpdateRequest();
	    req.setAction( UpdateRequest.ACTION.COMMIT, false, false );
	    req.add( docs );
	    UpdateResponse rsp = req.process( server );
	} catch (IOException e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	    index.setNoindexreason(index.getNoindexreason() + "index exception " + e.getClass().getName() + " ");
	    return -1;
	} catch (ElasticServerException e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	    index.setNoindexreason(index.getNoindexreason() + "index exception " + e.getClass().getName() + " ");
	    return -1;
	} catch (Exception e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	    index.setNoindexreason(index.getNoindexreason() + "index exception " + e.getClass().getName() + " ");
	    return -1;
	}
	*/
	return retsize;
    }

    public static ResultItem[] searchme(String str, String searchtype, SearchDisplay display) {
	ResultItem[] strarr = new ResultItem[0];
	int stype = new Integer(searchtype).intValue();

	
	SearchResponse response = client.prepareSearch("index1", "index2")
	        .setTypes("type1", "type2")
	        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
	        .setQuery(QueryBuilders.termQuery("multi", "test"))                 // Query
	        .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
	        .setFrom(0).setSize(60).setExplain(true)
	        .get();
	
	 for (SearchHit hit : response.getHits()) {
	        Long id = hit.field("id").<Long>getValue();
	        System.out.println("re " + hit.toString());
	       //result.add(String.valueOf(id));
	    }
	 /*
	try {
	    //ElasticServer server = null; //getElasticServer();
	    //Construct a ElasticQuery 
	    String defType = null;
	    switch (stype) {
	    case 0:
		break;
	    case 1:
		defType = "lucene";
		break;
	    case 2:
		defType = "complexphrase";
		break;
	    case 3:
		defType = "surround";
		break;
	    case 4:
		defType = "simple";
		break;
	    }
	    ElasticQuery query = new ElasticQuery();
	    query.setIncludeScore(true);
	    query.setRows(new Integer(100));
	    if (defType != null) {
		query.set("defType", defType); 
	    }
	    //query.addSortField( "price", ElasticQuery.ORDER.asc );

	    if (SearchService.isHighlightMLT()) {
            query.add("hl", "true");
            query.add("hl.fl", Constants.CONTENT);
            query.add("hl.useFastVectorHighlighter", "true");
	    }
	    //    Query the server 

	    QueryResponse rsp = server.query( query );
	
	    ElasticDocumentList docs = rsp.getResults();

	    strarr = handleDocs(display, rsp, docs, true);
	} catch (ElasticServerException e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	} catch (Exception e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	}
	*/
	return strarr;
    }

    /*
	private static ResultItem[] handleDocs(SearchDisplay display,
			QueryResponse rsp, ElasticDocumentList docs, boolean dohighlight) throws Exception {
		ResultItem[] strarr;
		// To read Documents as beans, the bean must be annotated as given in the example. 
	    
	    strarr = new ResultItem[docs.size() + 1];
	    strarr[0] = IndexFiles.getHeaderSearch(display);
	    int i = -1;
	    for (ElasticDocument doc : docs) {
		i++;
		ElasticDocument d = doc;
		float score = (float) d.get("score"); 
		String md5 = (String) d.getFieldValue(Constants.ID);
		String lang = (String) d.getFieldValue(Constants.LANG);
		List<String> metadata = (List<String>) d.getFieldValue(Constants.METADATA);
		IndexFiles indexmd5 = IndexFilesDao.getByMd5(md5);
		String filename = indexmd5.getFilelocation();
		log.info((i + 1) + ". " + md5 + " : " + filename + " : " + score);
		String[] highlights = null;
		if (dohighlight && SearchService.isHighlightMLT()) {
			Map<String,Map<String,List<String>>> map = rsp.getHighlighting();
			Map<String,List<String>> map2 = map.get(md5);
			List<String> list = map2.get(Constants.CONTENT);
			highlights = new String[1];
			if (list != null && list.size() > 0) {
			highlights[0] = list.get(0);
			} else {
				highlights[0] = "none";
			}
		}
		strarr[i + 1] = IndexFiles.getSearchResultItem(indexmd5, lang, score, highlights, display, metadata);
	    }
		return strarr;
	}
*/
    
    public static ResultItem[] searchmlt(String id, String searchtype, SearchDisplay display) {
	ResultItem[] strarr = new ResultItem[0];
	MoreLikeThisQueryBuilder moreLikeThisRequestBuilder;
	
	Item item = new Item(myindex, "type", id);
	//item.
	Item likeItems[] = new Item[1];
	likeItems[0] = item;
	QueryBuilder queryBuilder = QueryBuilders.moreLikeThisQuery(likeItems);
	//SearchResponse searchResponse2 = client.prepareSearch(null).setQuery(queryBuilder);

	/*
	try {
	    int count = MyConfig.conf.configMap.get(MyConfig.Config.MLTCOUNT);
	    int mintf = MyConfig.conf.configMap.get(MyConfig.Config.MLTMINTF);
	    int mindf = MyConfig.conf.configMap.get(MyConfig.Config.MLTMINDF);
	    //Construct a ElasticQuery 
	    ElasticQuery query = new ElasticQuery();
	    query.setQuery( Constants.ID + ":" + id);
	    query.setIncludeScore(true);
	    query.add("mlt", "true");
	    query.add("mlt.fl", Constants.CONTENT);
	    query.add("mlt.count", "" + count);
        query.add("mlt.mindf", "" + mindf);
        query.add("mlt.mintf", "" + mintf);

	    //    Query the server 

	    log.info("query " + query);

	    HttpElasticClient mltserver = new HttpElasticClient(server.getBaseURL());
	    mltserver.setSoTimeout(600000); // bigger timeout, only diff
	    mltserver.setDefaultMaxConnectionsPerHost(100);
	    mltserver.setMaxTotalConnections(100);
	    mltserver.setFollowRedirects(false);  // defaults to false
	    // allowCompression defaults to false.
	    // Server side must support gzip or deflate for this to have any effect.
	    mltserver.setAllowCompression(true);

	    QueryResponse rsp = mltserver.query( query );
	    NamedList<Object> mlt = (NamedList<Object>) rsp.getResponse().get("moreLikeThis");
	    ElasticDocumentList docs = (ElasticDocumentList) mlt.getVal(0);

	    // To read Documents as beans, the bean must be annotated as given in the example. 
	    
	    strarr = handleDocs(display, rsp, docs, false);
	} catch (ElasticServerException e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	} catch (Exception e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	}
	*/
	return strarr;
    }

    public static void deleteme(String str) {
    	ListenableActionFuture<DeleteResponse> action1 = client.prepareDelete(myindex, "type", str).execute();
    	DeleteRequest deleteRequest = new DeleteRequest(myindex, "type", str);
    	ActionFuture<DeleteResponse> action2 = client.delete(deleteRequest);
    	int requestTimeout = 30;
    	DeleteResponse response = action1.actionGet(requestTimeout);
    	DeleteResponse response2 = action2.actionGet(requestTimeout);
System.out.println("r1 " + response.toString());
System.out.println("r2 " + response.toString());
    	/*
    	if (response.isNotFound()) {
    		logger.debug("Delete failed, document not found");
    	}
    	else {
    		logger.debug("Deleted document with id " + response.getId());
    	}    	
    	if (response2.isNotFound()) {
    		logger.debug("Delete failed, document not found");
    	}
    	else {
    		logger.debug("Deleted document with id " + response.getId());
    	} 
    	*/
/*
    	   	if (response.)
    	try {
            server.deleteById(str);
            server.commit();
            UpdateRequest req = new UpdateRequest();
            req.setAction( UpdateRequest.ACTION.COMMIT, false, false );
            req.deleteById(str);
            UpdateResponse rsp = req.process( server );
        } catch (IOException e) {
            log.error(roart.util.Constants.EXCEPTION, e);
        } catch (ElasticServerException e) {
            log.error(roart.util.Constants.EXCEPTION, e);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
        }
        */
   }

 }
