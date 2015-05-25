package roart.search;

import java.io.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.highlight.DefaultSolrHighlighter;
import org.apache.tika.metadata.Metadata;

import roart.service.SearchService;
import roart.util.ConfigConstants;
import roart.lang.LanguageDetect;

import roart.model.SearchDisplay;
import roart.model.ResultItem;
import roart.model.IndexFiles;
import roart.database.IndexFilesDao;


public class SearchSolr {
    private static Logger log = LoggerFactory.getLogger(SearchSolr.class);

    static HttpSolrClient server = null;

    public SearchSolr() {
	if (server != null) {
	    return;
	}
	String url = roart.util.Prop.getProp().getProperty(ConfigConstants.SOLRURL);
	server = new HttpSolrClient( url );
	log.info("server " + server);
	System.out.println("server " + server);
	server.setMaxRetries(1); // defaults to 0.  > 1 not recommended.
	server.setConnectionTimeout(5000); // 5 seconds to establish TCP
	// Setting the XML response parser is only required for cross
	// version compatibility and only when one side is 1.4.1 or
	// earlier and the other side is 3.1 or later.
	server.setParser(new XMLResponseParser()); 
	// binary parser is used by default
	// The following settings are provided here for completeness.
	// They will not normally be required, and should only be used 
	// after consulting javadocs to know whether they are truly required.
	server.setSoTimeout(60000);  // socket read timeout
	server.setDefaultMaxConnectionsPerHost(100);
	server.setMaxTotalConnections(100);
	server.setFollowRedirects(false);  // defaults to false
	// allowCompression defaults to false.
	// Server side must support gzip or deflate for this to have any effect.
	server.setAllowCompression(true);
    }

    public static int indexme(String type, String md5, InputStream inputStream, String dbfilename, Metadata metadata, String lang, String content, String classification, List<ResultItem> retlist, IndexFiles index) {
	int retsize = content.length();
	// this to a method
	log.info("indexing " + md5);

	String cat = classification;

	try {
	    SolrInputDocument doc = new SolrInputDocument();
	    doc.addField(Constants.ID, md5);
	    if (lang != null) {
		doc.addField(Constants.LANG, lang);
	    }
	    if (cat != null) {
		doc.addField(Constants.CAT, cat);
	    }
	    doc.addField(Constants.CONTENT, content);
	    if (metadata != null) {
		log.info("with md " + metadata);
		//doc.addField(Constants.METADATA, metadata);
        Metadata md = metadata;
        for (String name : md.names()) {
            String value = md.get(name);
            doc.addField(Constants.METADATA, name + ":" + value);
        }
	    }

	    // Fields "id","name" and "price" are already included in Solr installation, you must add your new custom fields in SchemaXml.

	    // Create a collection of documents 

	    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
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
	} catch (SolrServerException e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	    index.setNoindexreason(index.getNoindexreason() + "index exception " + e.getClass().getName() + " ");
	    return -1;
	} catch (Exception e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	    index.setNoindexreason(index.getNoindexreason() + "index exception " + e.getClass().getName() + " ");
	    return -1;
	}
	return retsize;
    }

    public static ResultItem[] searchme(String str, String searchtype, SearchDisplay display) {
	ResultItem[] strarr = new ResultItem[0];
	int stype = new Integer(searchtype).intValue();
	try {
	    //SolrServer server = null; //getSolrServer();
	    //Construct a SolrQuery 
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
	    SolrQuery query = new SolrQuery();
	    query.setQuery( str /*"*:*"*/ );
	    query.setIncludeScore(true);
	    query.setRows(new Integer(100));
	    if (defType != null) {
		query.set("defType", defType); 
	    }
	    //query.addSortField( "price", SolrQuery.ORDER.asc );

	    if (SearchService.isHighlightMLT()) {
            query.add("hl", "true");
            query.add("hl.fl", Constants.CONTENT);
            query.add("hl.useFastVectorHighlighter", "true");
	    }
	    //    Query the server 

	    QueryResponse rsp = server.query( query );
	
	    SolrDocumentList docs = rsp.getResults();

	    strarr = handleDocs(display, rsp, docs, true);
	} catch (SolrServerException e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	} catch (Exception e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	}
	return strarr;
    }

	private static ResultItem[] handleDocs(SearchDisplay display,
			QueryResponse rsp, SolrDocumentList docs, boolean dohighlight) throws Exception {
		ResultItem[] strarr;
		// To read Documents as beans, the bean must be annotated as given in the example. 
	    
	    strarr = new ResultItem[docs.size() + 1];
	    strarr[0] = IndexFiles.getHeaderSearch(display);
	    int i = -1;
	    for (SolrDocument doc : docs) {
		i++;
		SolrDocument d = doc;
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
				highlights[0] = null;
			}
		}
		strarr[i + 1] = IndexFiles.getSearchResultItem(indexmd5, lang, score, highlights, display, metadata);
	    }
		return strarr;
	}

    public static ResultItem[] searchmlt(String id, String searchtype, SearchDisplay display) {
	ResultItem[] strarr = new ResultItem[0];
	try {
	    //Construct a SolrQuery 
	    SolrQuery query = new SolrQuery();
	    query.setQuery( Constants.ID + ":" + id);
	    query.setIncludeScore(true);
	    query.add("mlt", "true");
	    query.add("mlt.fl", Constants.CONTENT);
	    query.add("mlt.count", "100");

	    //    Query the server 

	    log.info("query " + query);
	    QueryResponse rsp = server.query( query );
	    NamedList<Object> mlt = (NamedList<Object>) rsp.getResponse().get("moreLikeThis");
	    SolrDocumentList docs = (SolrDocumentList) mlt.getVal(0);

	    // To read Documents as beans, the bean must be annotated as given in the example. 
	    
	    strarr = handleDocs(display, rsp, docs, false);
	} catch (SolrServerException e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	} catch (Exception e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	}
	return strarr;
    }

    public static void deleteme(String str) {
        try {
            server.deleteById(str);
            server.commit();
            UpdateRequest req = new UpdateRequest();
            req.setAction( UpdateRequest.ACTION.COMMIT, false, false );
            req.deleteById(str);
            UpdateResponse rsp = req.process( server );
        } catch (IOException e) {
            log.error(roart.util.Constants.EXCEPTION, e);
        } catch (SolrServerException e) {
            log.error(roart.util.Constants.EXCEPTION, e);
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
        }
   }

 }
