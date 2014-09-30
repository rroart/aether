package roart.jpa;

import java.io.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
 
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.client.solrj.SolrServerException;

import roart.search.Constants;
import roart.lang.LanguageDetect;

import roart.model.ResultItem;
import roart.model.IndexFiles;
import roart.dao.IndexFilesDao;

public class SearchSolr {
    private static Log log = LogFactory.getLog("SearchSolr");

    static HttpSolrServer server = null;

    public SearchSolr() {
	if (server != null) {
	    return;
	}
	String url = roart.util.Prop.getProp().getProperty("solrurl");
	server = new HttpSolrServer( url );
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

    public static int indexme(String type, String md5, InputStream inputStream, String dbfilename, String metadata, String lang, String content, String classification, List<ResultItem> retlist) {
	int retsize = 0;
	// this to a method
	String strLine = null;
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
		doc.addField(Constants.METADATA, metadata);
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
	    log.error("Exception", e);
	    return -1;
	} catch (SolrServerException e) {
	    log.error("Exception", e);
	    return -1;
	}
	return retsize;
    }

    public static void indexme(String type) {
    }

    public static ResultItem[] searchme(String type, String str) {
	ResultItem[] strarr = new ResultItem[0];
	System.out.println("searchme");
	return strarr;
    }

    public static ResultItem[] searchme2(String str, String searchtype) {
	ResultItem[] strarr = new ResultItem[0];
	String myclassify = roart.util.Prop.getProp().getProperty("myclassify");
	boolean doclassify = myclassify != null && myclassify.length() > 0;
	boolean admin = "admin".equals((String) com.vaadin.ui.UI.getCurrent().getSession().getAttribute("user"));
	try {
	    //SolrServer server = null; //getSolrServer();
	    //Construct a SolrQuery 

	    SolrQuery query = new SolrQuery();
	    query.setQuery( str /*"*:*"*/ );
	    query.setIncludeScore(true);
	    //query.addSortField( "price", SolrQuery.ORDER.asc );

	    //    Query the server 

	    QueryResponse rsp = server.query( query );
	
	    SolrDocumentList docs = rsp.getResults();

	    // To read Documents as beans, the bean must be annotated as given in the example. 
	    
	    //List<Item> beans = rsp.getBeans(Item.class);
	    strarr = new ResultItem[docs.size() + 1];
	    strarr[0] = new ResultItem();
	    strarr[0].add("Hit");
	    strarr[0].add("Md5/Id");
	    strarr[0].add("Filename");
	    strarr[0].add("Lang");
	    if (doclassify) {
		strarr[0].add("Classification");
	    }
	    strarr[0].add("Timestamp");
	    if (admin) {
	    strarr[0].add("Convertsw");
	    strarr[0].add("Converttime");
	    strarr[0].add("Indextime");
	    if (doclassify) {
		strarr[0].add("Classificationtime");
	    }
	    }
	    strarr[0].add("Score");
	    int i = -1;
	    for (SolrDocument doc : docs) {
		i++;
		SolrDocument d = doc;
		float score = (float) d.get("score"); 
		String md5 = (String) d.getFieldValue(Constants.ID);
		String lang = (String) d.getFieldValue(Constants.LANG);
		IndexFiles indexmd5 = IndexFilesDao.getByMd5(md5);
		String filename = indexmd5.getFilelocation();
		String title = md5 + " " + filename;
		if (lang != null) {
		    title = title + " (" + lang + ") ";
		}
		String timestamp = null;
		String convertsw = null;
		String converttime = null;
		if (indexmd5 != null) {
		    timestamp = indexmd5.getTimestamp();
		    if (timestamp != null) {
			Long l = new Long(timestamp);
			Date date = new Date(l.longValue());
			title = title + " (" + date.toString() + ") ";
			timestamp = date.toString();
		    }
		    convertsw = indexmd5.getConvertsw();
		    if (convertsw != null) {
			title = title + " (" + convertsw + " " + indexmd5.getConverttime("%.2f") + "s) ";
		    }
		    converttime = indexmd5.getConverttime("%.2f");
		}
		log.info((i + 1) + ". " + title + ": "
			 + score);
		strarr[i + 1] = new ResultItem();
		strarr[i + 1].add((i + 1) + ". ");
		strarr[i + 1].add(md5);
		strarr[i + 1].add(filename);
		strarr[i + 1].add(lang);
		if (doclassify) {
		    strarr[i + 1].add(indexmd5.getClassification());
		}
		strarr[i + 1].add(timestamp);
		if (admin) {
		strarr[i + 1].add(convertsw);
		strarr[i + 1].add(converttime);
		strarr[i + 1].add(indexmd5.getTimeindex("%.2f"));
		if (doclassify) {
		    strarr[i + 1].add(indexmd5.getTimeclass("%.2f"));
		}
		}
		strarr[i + 1].add("" + score);
	    }
	} catch (SolrServerException e) {
	    log.error("Exception", e);
	} catch (Exception e) {
	    log.error("Exception", e);
	}
	return strarr;
    }

    public static ResultItem[] searchsimilar(String md5i) {
	return null;
    }

    /*
    public static Query docsLike(int id, IndexReader ind) throws IOException {
    }

    public static Query docsLike(int id, Document doc, IndexReader ind) throws IOException {
    }

    public static void deleteme(String str) {
    }

    public static List<String> removeDuplicate() throws Exception {
    }

    public static List<String> cleanup2() throws Exception {
    }

    public static List<String> removeDuplicate2() throws Exception {
    }
    */
}
