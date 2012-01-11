package roart.search;

import roart.model.Index;
import roart.model.Files;
import roart.model.HibernateUtil;

import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
 
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.util.Version;
import org.apache.lucene.index.Term;
//import org.apache.lucene.search.Hits;
import org.apache.lucene.search.TopDocs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SearchLucene {
    private static Log log = LogFactory.getLog("SearchLucene");

    public static int indexme(String type, String md5, InputStream inputStream) {
	int retsize = 0;
    // create some index
    // we could also create an index in our ram ...
    // Directory index = new RAMDirectory();
	try {
	    Directory index = FSDirectory.open(new File(Constants.PATH+type));
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
    IndexWriter w = new IndexWriter(index, analyzer, false,
				    IndexWriter.MaxFieldLength.UNLIMITED);
 
	    DataInputStream in = new DataInputStream(inputStream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String line = null;
    // index some data
	    StringBuilder result = new StringBuilder();
	    while ((line = br.readLine()) != null) {
		result.append(line);
	    }
	    String strLine = result.toString();
	    String i = strLine;
	    retsize = strLine.length();

	log.info("indexing " + md5);

	Document doc = new Document();
	doc.add(new Field(Constants.TITLE, md5, Field.Store.YES,
			  Field.Index.ANALYZED));
	doc.add(new Field(Constants.NAME, i, Field.Store.NO,
			  Field.Index.ANALYZED));
	Term term = new Term(Constants.TITLE, md5);
	w.updateDocument(term, doc);
	//w.addDocument(doc);
 
    w.close();
    log.info("index generated");
  	} catch (Exception e) {
	    log.info("Error3: " + e.getMessage());
	    log.error("Exception", e);
	}
	return retsize;
	}

    public static void indexme(String type) {

    // create some index
    // we could also create an index in our ram ...
    // Directory index = new RAMDirectory();
	try {
	    Directory index = FSDirectory.open(new File(Constants.PATH+type));
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
    IndexWriter w = new IndexWriter(index, analyzer, true,
				    IndexWriter.MaxFieldLength.UNLIMITED);
 
    String filename = type;

	    FileInputStream fstream = new FileInputStream("/home/roart/data/"+type+".txt");
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine = null;
    // index some data
	    while ((strLine = br.readLine()) != null) {
		String i = strLine;
	log.info("indexing " + i);

	Document doc = new Document();

	doc.add(new Field(Constants.TITLE, i, Field.Store.YES,
			  Field.Index.ANALYZED));
	doc.add(new Field(Constants.NAME, i, Field.Store.YES,
			  Field.Index.ANALYZED));
	w.addDocument(doc);
	    }
 
    w.close();
    log.info("index generated");
  	} catch (Exception e) {
	    log.info("Error3: " + e.getMessage());
	    log.error("Exception", e);
	}
	}

    public static String [] searchme(String type, String str) {
		String[] strarr = new String[0];
	    try {
		Directory index = FSDirectory.open(new File(Constants.PATH+type));
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35 );
    // parse query over multiple fields
    Query q = new MultiFieldQueryParser(Version.LUCENE_35, new String[]{Constants.TITLE, Constants.NAME},
					analyzer).parse(str);
 
    // searching ...
    int hitsPerPage = 100;
    IndexSearcher searcher = new IndexSearcher(index);
    //TopDocCollector collector = new TopDocCollector(hitsPerPage);
    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
    searcher.search(q, collector);
    ScoreDoc[] hits = collector.topDocs().scoreDocs;
 
    strarr = new String[hits.length];
    // output results
    log.info("Found " + hits.length + " hits.");
    for (int i = 0; i < hits.length; ++i) {
	int docId = hits[i].doc;
	float score = hits[i].score;
	Document d = searcher.doc(docId);
	log.info((i + 1) + ". " + d.get(Constants.TITLE) + ": "
			   + score);
	strarr[i] = "" + (i + 1) + ". " + d.get(Constants.TITLE) + ": "
			   + score;
    }
  	} catch (Exception e) {
	    log.info("Error3: " + e.getMessage());
	    log.error("Exception", e);
	}

    return strarr;
}

    public static String [] searchme(String str) {
	String type = "all";
		String[] strarr = new String[0];
	    try {
		Directory index = FSDirectory.open(new File(Constants.PATH+type));
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35 );
    // parse query over multiple fields
    Query q = new MultiFieldQueryParser(Version.LUCENE_35, new String[]{Constants.TITLE, Constants.NAME},
					analyzer).parse(str);
 
    // searching ...
    int hitsPerPage = 100;
    IndexSearcher searcher = new IndexSearcher(index);
    //TopDocCollector collector = new TopDocCollector(hitsPerPage);
    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
    searcher.search(q, collector);
    ScoreDoc[] hits = collector.topDocs().scoreDocs;
 
    strarr = new String[hits.length];
    // output results
    log.info("Found " + hits.length + " hits.");
    for (int i = 0; i < hits.length; ++i) {
	int docId = hits[i].doc;
	float score = hits[i].score;
	Document d = searcher.doc(docId);
	String md5 = d.get(Constants.TITLE);
	String filename = null;
	List<Files> files = Files.getByMd5(md5);
	if (files != null && files.size() > 0) {
	    Files file = files.get(0);
	    filename = file.getFilename();
	}
	String title = md5 + " " + filename;
	log.info((i + 1) + ". " + title + ": "
			   + score);
	strarr[i] = "" + (i + 1) + ". " + title + ": "
			   + score;
    }
  	} catch (Exception e) {
	    log.info("Error3: " + e.getMessage());
	    log.error("Exception", e);
	}

    return strarr;
}

    public static void deleteme(String str) {
	try {
	    String type = "all";
	    Directory index = FSDirectory.open(new File(Constants.PATH+type));
	    IndexReader r = IndexReader.open(index, false);
	    r.deleteDocuments(new Term(Constants.TITLE, str));
	    r.close();
  	} catch (Exception e) {
	    log.info("Error3: " + e.getMessage());
	    log.error("Exception", e);
	}
    }

    public static List<String> removeDuplicate() throws Exception {
	List<String> retlist = new ArrayList<String>();
	String type = "all";
	String field = Constants.TITLE;
	String indexDir = Constants.PATH+type;
	StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35 );
	int docs = 0;
        int dups = 0;
	Directory index = FSDirectory.open(new File(Constants.PATH+type));
	IndexReader ind = IndexReader.open(index, false);
        IndexSearcher searcher = new IndexSearcher(index);
        int totalDocs = ind.numDocs();
        HashSet<Document> Doc = new HashSet<Document>();
        for(int m=0;m<totalDocs;m++) {
	    Document thisDoc = null;
	    try {
		thisDoc = ind.document(m);
	    } 
	    catch (Exception e) {
		continue;
	    }
	    String a2[] = thisDoc.getValues(field);
	    a2[0].trim();

	    if (a2[0].equals("")) {
		continue;
	    }
	    String queryExpression = "\""+a2[0]+"\"";     

	    QueryParser parser=new QueryParser(Version.LUCENE_35,field,analyzer);
	    Query queryJ=parser.parse(queryExpression);

	    /**
	     *  Perform duplicate check-searching
	     */
	    TopDocs topdocs = searcher.search(queryJ,100000);

	    if (topdocs.totalHits>1) {
		dups++;
		retlist.add("Duplicates found:"+topdocs.totalHits+" for "+a2[0]);
		ScoreDoc[] hits = topdocs.scoreDocs; 
		//Document d = searcher.doc(docId);

		Doc.add(searcher.doc(hits[0].doc));
		for(int i=1;i<hits.length;i++) {
		    //Document d = searcher.doc(docId);
		    int docId = hits[i].doc;
		    retlist.add("Deleting document #:"+docId);
		    ind.deleteDocument(docId);//Delete Document by its Document ID
		}
	    } else {
		retlist.add("single " + a2[0]);
	    }
	    /*
	    Index indexmd5 = Index.getByMd5(a2[0]);
	    if (indexmd5 == null) {
		retlist.add("delete1 " + a2[0]);
		ind.deleteDocument(topdocs.scoreDocs[0].doc);
	    }
	    if (indexmd5 != null) {
		Boolean indexed = indexmd5.getIndexed();
		if (indexed != null && indexed.booleanValue()) {
		} else {
		    retlist.add("delete2 " + a2[0]);
		    ind.deleteDocument(topdocs.scoreDocs[0].doc);
		}
	    }
	    */
                
	}//end of for loop
        int newDocs = ind.numDocs();
        ind.close();//close index Reader
        
        /**
         * Open indexwriter to write duplicate document
         */
        IndexWriter iw = new IndexWriter(index,analyzer,false,IndexWriter.MaxFieldLength.UNLIMITED);
        for(Document doc : Doc) {
	    //iw.addDocument(doc);
        }
        iw.optimize() ;
        iw.close();//Close Index Writer
        
	/**
	 * Print statistics
	 */
	retlist.add("Entries Scanned:"+totalDocs);
        retlist.add("Duplicates:"+dups);
        retlist.add("Currently Present Entries:"+(docs+newDocs));
	return retlist;
    }//End of removeDuplicate method

    public static List<String> removeDuplicate2() throws Exception {
	List<String> retlist = new ArrayList<String>();
	String type = "all";
	String field = Constants.TITLE;
	String indexDir = Constants.PATH+type;
	StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35 );
	int docs = 0;
        int dups = 0;
	Directory index = FSDirectory.open(new File(Constants.PATH+type));
        IndexWriter iw = new IndexWriter(index,analyzer,false,IndexWriter.MaxFieldLength.UNLIMITED);
	IndexReader ind = IndexReader.open(index);
        IndexSearcher searcher = new IndexSearcher(index);
        int totalDocs = ind.numDocs();
        HashSet<Document> Doc = new HashSet<Document>();
        for(int m=0;m<totalDocs;m++) {
	    Document thisDoc = null;
	    try {
		thisDoc = ind.document(m);
		String a2[] = thisDoc.getValues(field);
		a2[0].trim();
		if (a2[0].equals("")) { 
		    continue;
		}
		Term term = new Term(Constants.TITLE, a2[0]);
		iw.updateDocument(term, thisDoc);
	    } catch (Exception e) {
		retlist.add("" + e);
		log.error("Exception", e);
	    }
	}
	/**
	 * Print statistics
	 */
	retlist.add("Entries Scanned:"+totalDocs);
	return retlist;
    }//End of removeDuplicate method

}