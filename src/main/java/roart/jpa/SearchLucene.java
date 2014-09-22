package roart.jpa;

import roart.dao.IndexFilesDao;
import roart.model.HibernateUtil;
import roart.model.IndexFiles;
import roart.queue.IndexQueueElement;
import roart.queue.Queues;
import roart.lang.LanguageDetect;
import roart.model.ResultItem;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
 
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Fields;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.analyzing.AnalyzingQueryParser;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.queryparser.ext.ExtendableQueryParser;
import org.apache.lucene.queryparser.simple.SimpleQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.queries.mlt.MoreLikeThis;
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
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.tika.metadata.Metadata;

import roart.search.Constants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SearchLucene {
    private static Log log = LogFactory.getLog("SearchLucene");

    //public static int indexme(String type, String md5, InputStream inputStream) {
    //public static void indexme() {
    public static int indexme(String type, String md5, InputStream inputStream, String dbfilename, String metadata, List<ResultItem> retlist) {
    int retsize = 0;
    // create some index
    // we could also create an index in our ram ...
    // Directory index = new RAMDirectory();
	try {
	    Directory index = FSDirectory.open(new File(getLucenePath()+type));
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_0);
    IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_0, analyzer);
    IndexWriter w = new IndexWriter(index, iwc);
 
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

	String lang = LanguageDetect.detect(strLine);

	Document doc = new Document();
	doc.add(new TextField(Constants.ID, md5, Field.Store.YES));
	if (lang != null) {
	doc.add(new TextField(Constants.LANG, lang, Field.Store.YES));
	}
	doc.add(new TextField(Constants.CONTENT, i, Field.Store.NO));
	if (metadata != null) {
	    log.info("with md " + metadata.toString());
	    doc.add(new TextField(Constants.METADATA, metadata.toString(), Field.Store.NO));
	}
	Term oldTerm = new Term(Constants.TITLE, md5); // remove after reindex
	Term term = new Term(Constants.ID, md5);
	w.deleteDocuments(oldTerm); // remove after reindex
	doc.removeField(Constants.NAME);
	doc.removeField(Constants.TITLE);
	w.updateDocument(term, doc);
	//w.addDocument(doc);
 
    w.close();
    log.info("index generated");
  	} catch (Exception e) {
	    log.info("Error3: " + e.getMessage());
	    log.error("Exception", e);
	    return -1;
	}
    return retsize;
	}

    public static void indexme(String type) {

    // create some index
    // we could also create an index in our ram ...
    // Directory index = new RAMDirectory();
	try {
	    Directory index = FSDirectory.open(new File(getLucenePath()+type));
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_0);
    IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_0, analyzer);
    IndexWriter w = new IndexWriter(index, iwc);
 
    String filename = type;

	    String datadir = roart.util.Prop.getProp().getProperty("datadir");
	    FileInputStream fstream = new FileInputStream(datadir+type+".txt");
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine = null;
    // index some data
	    while ((strLine = br.readLine()) != null) {
		String i = strLine;
	log.info("indexing " + i);

	Document doc = new Document();

	doc.add(new TextField(Constants.CONTENT, i, Field.Store.YES));
	w.addDocument(doc);
	    }
 
    w.close();
    log.info("index generated");
  	} catch (Exception e) {
	    log.info("Error3: " + e.getMessage());
	    log.error("Exception", e);
	}
	}

    public static ResultItem[] searchme(String type, String str) {
		ResultItem[] strarr = new ResultItem[0];
	    try {
		Directory index = FSDirectory.open(new File(getLucenePath()+type));
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_0 );
    // parse query over multiple fields
    Query q = new MultiFieldQueryParser(Version.LUCENE_4_10_0, new String[]{Constants.CONTENT},
					analyzer).parse(str);
 
    // searching ...
    int hitsPerPage = 100;
    IndexReader ind = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(ind);
    //TopDocCollector collector = new TopDocCollector(hitsPerPage);
    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
    searcher.search(q, collector);
    ScoreDoc[] hits = collector.topDocs().scoreDocs;
 
    strarr = new ResultItem[hits.length + 1];
    strarr[0] = new ResultItem();
    strarr[0].add("Hit");
    strarr[0].add("Title");
    strarr[0].add("Score");
    // output results
    log.info("Found " + hits.length + " hits.");
    for (int i = 0; i < hits.length; ++i) {
	int docId = hits[i].doc;
	float score = hits[i].score;
	Document d = searcher.doc(docId);
	log.info((i + 1) + ". " + d.get(Constants.CONTENT) + ": "
			   + score);
	strarr[i + 1] = new ResultItem();
	strarr[i + 1].add("" + (i + 1)+ ". ");
	strarr[i + 1].add(d.get(Constants.CONTENT) + ": ");
	strarr[i + 1].add("" + score);
    }
  	} catch (Exception e) {
	    log.info("Error3: " + e.getMessage());
	    log.error("Exception", e);
	}

    return strarr;
}

    public static ResultItem[] searchme2(String str, String searchtype) {
	String type = "all";
	int stype = new Integer(searchtype).intValue();
		ResultItem[] strarr = new ResultItem[0];
	    try {
		Directory index = FSDirectory.open(new File(getLucenePath()+type));
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_0 );
    // parse query over multiple fields
    QueryParser cp = null;
    Query tmpQuery = null;
    switch (stype) {
    case 0:
	cp = new QueryParser(Version.LUCENE_4_10_0, Constants.CONTENT, analyzer);
	break;
    case 1:
	cp = new AnalyzingQueryParser(Version.LUCENE_4_10_0, Constants.CONTENT, analyzer);
	break;
    case 2:
	cp = new ComplexPhraseQueryParser(Version.LUCENE_4_10_0, Constants.CONTENT, analyzer);
	break;
    case 3:
	cp = new ExtendableQueryParser(Version.LUCENE_4_10_0, Constants.CONTENT, analyzer);
	break;
    case 4:
	cp = new MultiFieldQueryParser(Version.LUCENE_4_10_0, new String[]{Constants.TITLE, Constants.ID, Constants.CONTENT, Constants.NAME, Constants.LANG, Constants.METADATA}, analyzer); // remove after reindex
	break;
    case 5:
	tmpQuery = new SimpleQueryParser(analyzer, Constants.NAME).createPhraseQuery(Constants.NAME, str);
	break;
    }
    Query q = null;
    if (cp != null) {
	q = cp.parse(str);
    } else {
	q = tmpQuery;
    }
 
    // searching ...
    int hitsPerPage = 100;
    IndexReader ind = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(ind);
    //TopDocCollector collector = new TopDocCollector(hitsPerPage);
    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
    searcher.search(q, collector);
    ScoreDoc[] hits = collector.topDocs().scoreDocs;
 
    strarr = new ResultItem[hits.length + 1];
    strarr[0] = new ResultItem();
    strarr[0].add("Hit");
    strarr[0].add("Md5/Id");
    strarr[0].add("Filename");
    strarr[0].add("Lang");
    strarr[0].add("Timestamp");
    strarr[0].add("Convertsw");
    strarr[0].add("Converttime");
    strarr[0].add("Indextime");
    strarr[0].add("Score");
    // output results
    log.info("Found " + hits.length + " hits.");
    for (int i = 0; i < hits.length; ++i) {
	int docId = hits[i].doc;
	float score = hits[i].score;
	Document d = searcher.doc(docId);
	String md5 = d.get(Constants.TITLE); // remove after reindex
	if (md5 == null || md5.length() == 0) {
	    md5 = d.get(Constants.ID);
	}
	String lang = d.get(Constants.LANG);
	IndexFiles indexmd5 = IndexFilesDao.getByMd5(md5);
	String filename = indexmd5.getFilelocation().toString();
	String title = md5 + " " + filename;
	if (lang != null) {
	    title = title + " (" + lang + ") ";
	    lang = "(" + lang + ")";
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
	strarr[i + 1].add(timestamp);
	strarr[i + 1].add(convertsw);
	strarr[i + 1].add(converttime);
	strarr[i + 1].add(indexmd5.getTimeindex("%.2f"));
	strarr[i + 1].add("" + score);
    }
  	} catch (Exception e) {
	    log.info("Error3: " + e.getMessage());
	    log.error("Exception", e);
	}

    return strarr;
}

    // not yet usable, lacking termvector
    public static ResultItem[] searchsimilar(String md5i) {
	String type = "all";
		ResultItem[] strarr = new ResultItem[0];
	    try {
		Directory index = FSDirectory.open(new File(getLucenePath()+type));
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_0 );

    // searching ...
    int hitsPerPage = 100;
    IndexReader ind = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(ind);
    //TopDocCollector collector = new TopDocCollector(hitsPerPage);
    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);

    int totalDocs = ind.numDocs();
    Document found = null;
    int doc = 0;
    for(int m=0;m<totalDocs;m++) {
	Document thisDoc = null;
	try {
	    thisDoc = ind.document(m);
	}
	catch (Exception e) {
	    log.error("Exception", e);
	    continue;
	}
	String a2[] = thisDoc.getValues(Constants.TITLE);
	a2[0].trim();

	if (a2[0].equals(md5i)) {
	    System.out.println("m is " + m);
	    doc = m;
	    found = thisDoc;
	    break;
	}
    }
    if (found == null) {
	System.out.println("not found");
	return null;
    }

    MoreLikeThis mlt = new MoreLikeThis(ind);
    mlt.setAnalyzer(analyzer);
    String[] fields = { Constants.NAME /*, Constants.TITLE */ };
    mlt.setFieldNames(fields);
    mlt.setMinTermFreq(1);
    mlt.setMinDocFreq(1);
    mlt.setMinWordLen(1);
    //mlt.setMaxWordLen(10);
    mlt.setMaxQueryTerms(1000);
    // md5 orig source of doc you want to find similarities to
    Query query = mlt.like(doc);
    System.out.println("query doc " + doc + ":" + query.toString() + ":" + mlt.describeParams());
    System.out.println("hits " + searcher.search(query,100).totalHits);
    query = docsLike(doc, ind);
    System.out.println("query doc " + doc + ":" + query.toString());
    query = docsLike(doc, found, ind);
    System.out.println("query doc " + doc + ":" + query.toString());

    searcher.search(query, collector);
    ScoreDoc[] hits = collector.topDocs().scoreDocs;
 
    strarr = new ResultItem[hits.length];
    // output results
    log.info("Found " + hits.length + " hits.");
    for (int i = 0; i < hits.length; ++i) {
	int docId = hits[i].doc;
	float score = hits[i].score;
	Document d = searcher.doc(docId);
	String md5 = d.get(Constants.TITLE);
	String lang = d.get(Constants.LANG);
	IndexFiles files = IndexFilesDao.getByMd5(md5);
	String filename = files.getFilelocation().toString();
	String title = md5 + " " + filename;
	if (lang != null) {
	    title = title + " (" + lang + ") ";
	}
	log.info((i + 1) + ". " + title + ": "
			   + score);
	/*
	strarr[i] = "" + (i + 1) + ". " + title + ": "
			   + score;
	*/
    }
  	} catch (Exception e) {
	    log.info("Error3: " + e.getMessage());
	    log.error("Exception", e);
	}

    return strarr;
}

    // not yet usable, lacking termvector
    public static Query docsLike(int id, IndexReader ind/*, int max*/) throws IOException {
	//Document doc = reader.document(id);
	/*
	String[] authors = doc.getValues("author");
	BooleanQuery authorQuery = new BooleanQuery();
	for (String author : authors) {
	    authorQuery.add(new TermQuery(new Term("author", author)), BooleanClause.Occur.SHOULD);
	}
	authorQuery.setBoost(2.0f);
	*/
	Fields fields = ind.getTermVectors(id);
	/*
	String fname;
	java.util.Iterator it = fields.iterator();
	while ((fname = it.next()) != null) {
	    System.out.println("fname " + fname);
	}
	*/
	if (fields != null) {
	for (String fname2 : fields) {
	    System.out.println("fname " + fname2);
	}
	}
	System.out.println("here");
	Terms terms = ind.getTermVector(id, Constants.NAME);
	BooleanQuery subjectQuery = new BooleanQuery();
	if (terms != null) {
	System.out.println("size " + terms.size());
	TermsEnum termsEnum = terms.iterator((TermsEnum) null);
	BytesRef text;
	while((text = termsEnum.next()) != null) {
	    //Term t = termsEnum.term();
	    String t = text.utf8ToString();
	    System.out.println(/*"field=" + t.field() + */"text=" + t/*.text()*/);
	    TermQuery tq = new TermQuery(new Term(Constants.NAME, t/*.text()*/));
	    subjectQuery.add(tq, BooleanClause.Occur.SHOULD);
	}
	}
	BooleanQuery likeThisQuery = new BooleanQuery();
	//likeThisQuery.add(authorQuery, BooleanClause.Occur.SHOULD);
	likeThisQuery.add(subjectQuery, BooleanClause.Occur.SHOULD);
	//likeThisQuery.add(new TermQuery(new Term("isbn", doc.get("isbn"))), BooleanClause.Occur.MUST_NOT);
	return likeThisQuery;
	/*
	TopDocs hits = searcher.search(likeThisQuery, 10);
	int size = max;
	if (max > hits.scoreDocs.length) size = hits.scoreDocs.length;
	Document[] docs = new Document[size];
	for (int i = 0; i < size; i++) {
	    docs[i] = reader.document(hits.scoreDocs[i].doc);
	}
	return docs;
	*/
    }

    // not yet usable, lacking termvector
    public static Query docsLike(int id, Document doc, IndexReader ind/*, int max*/) throws IOException {
	//Document doc = reader.document(id);
	/*
	String[] authors = doc.getValues("author");
	BooleanQuery authorQuery = new BooleanQuery();
	for (String author : authors) {
	    authorQuery.add(new TermQuery(new Term("author", author)), BooleanClause.Occur.SHOULD);
	}
	authorQuery.setBoost(2.0f);
	*/
	/*
	String fname;
	java.util.Iterator it = fields.iterator();
	while ((fname = it.next()) != null) {
	    System.out.println("fname " + fname);
	}
	*/
	BooleanQuery subjectQuery = new BooleanQuery();
	String[] values = doc.getValues(Constants.NAME);
	System.out.println("or no fname");
	for (String fname2 : values) {
	    System.out.println("fname " + fname2);
	    String t = fname2;
	    System.out.println(/*"field=" + t.field() + */"text=" + t/*.text()*/);
	    TermQuery tq = new TermQuery(new Term(Constants.NAME, t/*.text()*/));
	    subjectQuery.add(tq, BooleanClause.Occur.SHOULD);
	}
	BooleanQuery likeThisQuery = new BooleanQuery();
	//likeThisQuery.add(authorQuery, BooleanClause.Occur.SHOULD);
	likeThisQuery.add(subjectQuery, BooleanClause.Occur.SHOULD);
	//likeThisQuery.add(new TermQuery(new Term("isbn", doc.get("isbn"))), BooleanClause.Occur.MUST_NOT);
	return likeThisQuery;
	/*
	TopDocs hits = searcher.search(likeThisQuery, 10);
	int size = max;
	if (max > hits.scoreDocs.length) size = hits.scoreDocs.length;
	Document[] docs = new Document[size];
	for (int i = 0; i < size; i++) {
	    docs[i] = reader.document(hits.scoreDocs[i].doc);
	}
	return docs;
	*/
    }

    public static void deleteme(String str) {
	try {
	    String type = "all";
	    Directory index = FSDirectory.open(new File(getLucenePath()+type));
	    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_0);
	    IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_0, analyzer);
	    IndexWriter iw = new IndexWriter(index, iwc);
	    //IndexReader r = IndexReader.open(index, false);
	    iw.deleteDocuments(new Term(Constants.TITLE, str));
	    iw.deleteDocuments(new Term(Constants.ID, str));
	    iw.close();
  	} catch (Exception e) {
	    log.info("Error3: " + e.getMessage());
	    log.error("Exception", e);
	}
    }

    // outdated, did run once, had a bug which made duplicates
    public static List<String> removeDuplicate() throws Exception {
	List<String> retlist = new ArrayList<String>();
	String type = "all";
	String field = Constants.TITLE;
	String indexDir = getLucenePath()+type;
	StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_0 );
	int docs = 0;
        int dups = 0;
	Directory index = FSDirectory.open(new File(getLucenePath()+type));
    IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_0, analyzer);
    IndexWriter iw = new IndexWriter(index, iwc);
    IndexReader ind = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(ind);
        int totalDocs = iw.numDocs();
        HashSet<Document> Doc = new HashSet<Document>();
        for(int m=0;m<totalDocs;m++) {
	    Document thisDoc = null;
	    try {
		thisDoc = ind.document(m);
	    } 
	    catch (Exception e) {
		log.error("Exception", e);
		continue;
	    }
	    String a2[] = thisDoc.getValues(field);
	    a2[0].trim();

	    if (a2[0].equals("")) {
		continue;
	    }
	    String queryExpression = "\""+a2[0]+"\"";     

	    QueryParser parser=new QueryParser(Version.LUCENE_4_10_0,field,analyzer);
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
		    // check
		    // check iw.deleteDocument(docId);//Delete Document by its Document ID
		}
	    } else {
		retlist.add("single " + a2[0]);
	    }
	    /*
	    Index indexmd5 = IndexDao.getByMd5(a2[0]);
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
        iw.close();//close index Reader
        
        /**
         * Open indexwriter to write duplicate document
         */
        //IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_0, analyzer);
        //IndexWriter iw = new IndexWriter(index, iwc);
        for(Document doc : Doc) {
	    //iw.addDocument(doc);
        }
        //iw.optimize() ;
        iw.close();//Close Index Writer
        
	/**
	 * Print statistics
	 */
	retlist.add("Entries Scanned:"+totalDocs);
        retlist.add("Duplicates:"+dups);
        retlist.add("Currently Present Entries:"+(docs+newDocs));
	return retlist;
    }//End of removeDuplicate method

    // not used, find out what it exactly does at a later time
    public static List<String> removeDuplicate2() throws Exception {
	List<String> retlist = new ArrayList<String>();
	String type = "all";
	String field = Constants.TITLE;
	String indexDir = getLucenePath()+type;
	StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_0 );
	int docs = 0;
        int dups = 0;
	Directory index = FSDirectory.open(new File(getLucenePath()+type));
	IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_0, analyzer);
	IndexWriter iw = new IndexWriter(index, iwc);
	IndexReader ind = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(ind);
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

    private static String getLucenePath() {
	return roart.util.Prop.getProp().getProperty(Constants.LUCENEPATH);
    }

}
