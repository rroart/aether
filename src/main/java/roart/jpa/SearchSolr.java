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

import roart.model.Files;
import roart.model.Index;
import roart.dao.FilesDao;
import roart.dao.IndexDao;

public class SearchSolr {
    private static Log log = LogFactory.getLog("SearchSolr");

    static HttpSolrServer server = null;

    public SearchSolr() {
	if (server != null) {
	    return;
	}
	String url = "http://localhost:8983/solr/mystuff";
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

    public static int indexme(String type, String md5, InputStream inputStream, String dbfilename, String metadata, List<String> retlist) {
	int retsize = 0;
	// this to a method
	String strLine = null;
	try {
	    DataInputStream in = new DataInputStream(inputStream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String line = null;
	    // index some data
	    StringBuilder result = new StringBuilder();
	    while ((line = br.readLine()) != null) {
		result.append(line);
	    }
	    strLine = result.toString();
	    retsize = strLine.length();
	} catch (IOException e) {
	    log.error("Exception", e);
	}

	log.info("indexing " + md5);

	// move this to a method
	String lang = null;
	try {
	    lang = LanguageDetect.detect(strLine);
	    log.info("language " + lang);
	    log.info("language2 " + LanguageDetect.detectLangs(strLine));
	} catch (Exception e) {
	    log.error("exception", e);
	}

	String i = strLine;
	try {
	    SolrInputDocument doc = new SolrInputDocument();
	    doc.addField("id", md5);
	    doc.addField(Constants.TITLE, md5);
	    if (lang != null) {
		doc.addField(Constants.LANG, lang);
	    }
	    doc.addField(Constants.NAME, i);
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
	} catch (SolrServerException e) {
	    log.error("Exception", e);
	}
	return retsize;
    }

    public static void indexme(String type) {
    }

    public static String [] searchme(String type, String str) {
	String[] strarr = new String[0];
	System.out.println("searchme");
	return strarr;
    }

    public static String [] searchme2(String str, String searchtype) {
	String[] strarr = new String[0];
	System.out.println("searchme2");
	try {
	    //SolrServer server = null; //getSolrServer();
	    //Construct a SolrQuery 

	    SolrQuery query = new SolrQuery();
	    query.setQuery( str /*"*:*"*/ );
	    //query.addSortField( "price", SolrQuery.ORDER.asc );

	    //    Query the server 

	    QueryResponse rsp = server.query( query );
	
	    SolrDocumentList docs = rsp.getResults();

	    // To read Documents as beans, the bean must be annotated as given in the example. 
	    
	    //List<Item> beans = rsp.getBeans(Item.class);
	    strarr = new String[docs.size()];
	    int i = -1;
	    for (SolrDocument doc : docs) {
		i++;
	System.out.println("searchme2 doc " + i);
		float score = 0;
		SolrDocument d = doc;
		String md5 = (String) d.getFieldValue(Constants.TITLE);
		String lang = (String) d.getFieldValue(Constants.LANG);
		String filename = null;
		List<Files> files = FilesDao.getByMd5(md5);
		if (files != null && files.size() > 0) {
		    Files file = files.get(0);
		    filename = file.getFilename();
		}
		String title = md5 + " " + filename;
		if (lang != null) {
		    title = title + " (" + lang + ") ";
		}
		Index indexmd5 = IndexDao.getByMd5(md5);
		if (indexmd5 != null) {
		    String timestamp = indexmd5.getTimestamp();
		    if (timestamp != null) {
			Long l = new Long(timestamp);
			Date date = new Date(l.longValue());
			title = title + " (" + date.toString() + ") ";
		    }
		    String convertsw = indexmd5.getConvertsw();
		    if (convertsw != null) {
			title = title + " (" + convertsw + ") ";
		    }
		}
		log.info((i + 1) + ". " + title + ": "
			 + score);
		strarr[i] = "" + (i + 1) + ". " + title + ": "
		    + score;
	    }
	} catch (SolrServerException e) {
	    log.error("Exception", e);
	} catch (Exception e) {
	    log.error("Exception", e);
	}
	return strarr;
    }

    public static String [] searchsimilar(String md5i) {
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
