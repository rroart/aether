package roart.search.lucene;

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

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.HashSet;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
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
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.queryparser.ext.ExtendableQueryParser;
import org.apache.lucene.queryparser.simple.SimpleQueryParser;
import org.apache.lucene.queryparser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryparser.flexible.core.QueryParserHelper;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
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
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchLucene extends SearchEngineAbstractSearcher {
	private static Logger log = LoggerFactory.getLogger(SearchLucene.class);

	public SearchLucene(String nodename, NodeConfig nodeConf) {
		org.apache.lucene.search.BooleanQuery.setMaxClauseCount(16384);
	}

	public static void deconstruct(String nodename) {
	}

        @Override
	public SearchEngineConstructorResult clear(SearchEngineConstructorParam param) {
	    try {
	        NodeConfig conf = param.conf;
	        Directory lindex = FSDirectory.open(getLucenePath(conf));
	        StandardAnalyzer analyzer = new StandardAnalyzer();
	        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
	        IndexWriter indexWriter = new IndexWriter(lindex, iwc);
	        indexWriter.deleteAll();
	        indexWriter.commit();                
	    } catch (Exception e) {
	        log.error(roart.common.constants.Constants.EXCEPTION, e);
	    }
	    return new SearchEngineConstructorResult();
	}

	@Override
	public SearchEngineConstructorResult drop(SearchEngineConstructorParam param) {
	    return clear(param);
	}

	//public static int indexme(String type, String md5, InputStream inputStream) {
	//public static void indexme() {
	public synchronized SearchEngineIndexResult indexme(SearchEngineIndexParam index) {
		NodeConfig conf = index.conf;
                NodeConfig nodeConf = conf;
                String indexName = nodeConf.luceneIndex();
		String md5 = index.md5; 
		//InputStream inputStream, 
		String[] metadata = index.metadata;
		String lang = index.lang;
		String classification = index.classification;
		Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
		String content = inmemory.read(index.message);
		if (!InmemoryUtil.validate(index.message.getMd5(), content)) {
                    SearchEngineIndexResult result = new SearchEngineIndexResult();
                    result.noindexreason = "invalid";
                    result.size = -1;
                    return result;
	        }

		int retsize = 0;
		// create some index
		// we could also create an index in our ram ...
		// Directory index = new RAMDirectory();
		try {
			Directory lindex = FSDirectory.open(getLucenePath(conf));
			StandardAnalyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			IndexWriter w = new IndexWriter(lindex, iwc);

			retsize = content.length();

			log.info("indexing " + md5);

			String cat = classification;

			Document doc = new Document();
			doc.add(new TextField(Constants.ID, md5, Field.Store.YES));
			if (cat != null) {
				doc.add(new TextField(Constants.CAT, cat, Field.Store.YES));
			}
			if (lang != null) {
				doc.add(new TextField(Constants.LANG, lang, Field.Store.YES));
			}
			Field.Store store = Field.Store.NO;
			if (conf.getHighlightmlt()) {
				FieldType fieldtype = new FieldType(TextField.TYPE_STORED);
				fieldtype.setStoreTermVectors(true);
				fieldtype.setStoreTermVectorOffsets(true);
				fieldtype.setStoreTermVectorPositions(true);
				fieldtype.freeze();
				Field mytextfield = new Field(Constants.CONTENT, content, fieldtype);
				doc.add(mytextfield);
			} else {
				doc.add(new TextField(Constants.CONTENT, content, Field.Store.NO));
			}
			if (metadata != null) {
				log.info("with md {}", Arrays.asList(metadata));
				//doc.add(new TextField(Constants.METADATA, metadata.toString(), Field.Store.NO));
				String[] md = metadata;
				for (String name : md) {
					String value = name;
					doc.add(new TextField(Constants.METADATA, name, Field.Store.YES));
				}
			}
			//Term oldTerm = new Term(Constants.TITLE, md5); // remove after reindex
			Term term = new Term(Constants.ID, md5);
			//w.deleteDocuments(oldTerm); // remove after reindex
			//doc.removeField(Constants.NAME);
			//doc.removeField(Constants.TITLE);
			w.updateDocument(term, doc);
			//w.addDocument(doc);

			w.close();
			log.info("index generated " + md5);
		} catch (Exception e) {
			log.info("Error3: " + e.getMessage());
			log.error(roart.common.constants.Constants.EXCEPTION, e);
			SearchEngineIndexResult result = new SearchEngineIndexResult();
			result.noindexreason = "index exception " + e.getClass().getName();
			result.size = -1;
			return result;
		}
		SearchEngineIndexResult result = new SearchEngineIndexResult();
		result.size = retsize;        
		return result;
	}

	public SearchEngineConstructorResult destroy() throws IOException {
		return null;
	}

	private static Path getLucenePath(NodeConfig conf) {
		return new File(conf.getLucenepath() + conf.luceneIndex()).toPath();
	}

	public SearchEngineSearchResult searchme(SearchEngineSearchParam search) {
		String str = search.str;
		String searchtype = search.searchtype;

		int stype = new Integer(searchtype).intValue();
		try {
			Directory index = FSDirectory.open(getLucenePath(search.conf));
			StandardAnalyzer analyzer = new StandardAnalyzer();
			// parse query over multiple fields
			QueryParser cp = null;
			Query tmpQuery = null;
			switch (stype) {
			case 0:
				StandardQueryParser queryParserHelper = new StandardQueryParser();
				tmpQuery = queryParserHelper.parse(str, Constants.CONTENT); 
				break;
			case 1:
				cp = new QueryParser(Constants.CONTENT, analyzer);
				break;
			case 2:
				cp = new ComplexPhraseQueryParser(Constants.CONTENT, analyzer);
				break;
			case 3:
				cp = new ExtendableQueryParser(Constants.CONTENT, analyzer);
				break;
			case 4:
				cp = new MultiFieldQueryParser(new String[]{Constants.ID, Constants.CONTENT, Constants.CAT, Constants.LANG, Constants.METADATA}, analyzer);
				break;
			case 5:
				tmpQuery = org.apache.lucene.queryparser.surround.parser.QueryParser.parse(str).makeLuceneQueryField(Constants.CONTENT, new BasicQueryFactory());
				break;
			case 6:
				cp = new QueryParser(Constants.CONTENT, analyzer);
				break;
			case 7:
				tmpQuery = new SimpleQueryParser(analyzer, Constants.CONTENT).createPhraseQuery(Constants.CONTENT, str);
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
			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, hitsPerPage);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			SearchEngineSearchResult result = handleDocs(search, q, ind, searcher, hits, true);
			return result;
		} catch (Exception e) {
			log.info("Error3: " + e.getMessage());
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		}

		return null;
	}

	// or could use docid as id instead of md5 here and there
	public static int searchdocid(NodeConfig conf, String md5) {
		try {
			Directory index = FSDirectory.open(getLucenePath(conf));
			StandardAnalyzer analyzer = new StandardAnalyzer();
			Query tmpQuery = new SimpleQueryParser(analyzer, Constants.CONTENT).createPhraseQuery(Constants.ID, md5);
			Query q = tmpQuery;

			// searching ...
			IndexReader ind = DirectoryReader.open(index);
			IndexSearcher searcher = new IndexSearcher(ind);
			TopScoreDocCollector collector = TopScoreDocCollector.create(1, 1);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			if (hits.length > 0) {
				return hits[0].doc;
			}
		} catch (Exception e) {
			log.info("Error4: " + e.getMessage());
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		}
		return -1;
	}

	private static SearchEngineSearchResult handleDocs(SearchEngineSearchParam search, Query q,
			IndexReader ind, IndexSearcher searcher, ScoreDoc[] hits, boolean dohighlight)
					throws IOException, Exception {
		SearchEngineSearchResult result = new SearchEngineSearchResult();
		result.results = new SearchResult[hits.length];

		FastVectorHighlighter highlighter = null;
		if (search.conf.getHighlightmlt()) {
			highlighter = new FastVectorHighlighter();
		}    
		// output results
		log.info("Found " + hits.length + " hits.");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			float score = hits[i].score;
			Document d = searcher.doc(docId);
			String md5 = d.get(Constants.ID);
			String lang = d.get(Constants.LANG);
			String[] metadataArray = d.getValues(Constants.METADATA);
			List<String> metadata = null;
			if (metadataArray != null) {
				metadata = Arrays.asList(metadataArray);
			}

			String[] highlights = { "none" };
			if (dohighlight && search.conf.getHighlightmlt()) {
				FieldQuery fieldQuery  = highlighter.getFieldQuery( q, ind );
				String[] bestFragments = highlighter.getBestFragments(fieldQuery, ind, docId, Constants.CONTENT, 100, 1);
				highlights = bestFragments;
			}
			SearchResult res = new SearchResult();
			res.md5 = md5;
			res.score = score;
			res.lang = lang;
			res.highlights = highlights;
			res.metadata = metadata;
			result.results[i] = res;
		}
		return result;
	}

	// not yet usable, lacking termvector
	public SearchEngineSearchResult searchmlt(SearchEngineSearchParam search) {
		NodeConfig conf = search.conf;
		String md5i = search.str;
		String searchtype = search.searchtype;
		try {
			Directory index = FSDirectory.open(getLucenePath(conf));
			StandardAnalyzer analyzer = new StandardAnalyzer();

			int count = conf.getMLTCount();
			int mintf = conf.getMLTMinTF();
			int mindf = conf.getMLTMinDF();

			// searching ...
			int hitsPerPage = count;
			IndexReader ind = DirectoryReader.open(index);
			IndexSearcher searcher = new IndexSearcher(ind);
			//TopDocCollector collector = new TopDocCollector(hitsPerPage);
			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, hitsPerPage);

			int totalDocs = ind.numDocs();
			//Document found = null;
			int doc = 0;
			doc = searchdocid(conf, md5i);
			if (doc < 0) {
				log.error("not found " + md5i);
				return null;
			} else {
				log.info("md5 " + md5i + " has docid " + doc);
			}
			/*
    for(int m=0;m<totalDocs;m++) {
	Document thisDoc = null;
	try {
	    thisDoc = ind.document(m);
	}
	catch (Exception e) {
	    log.error(roart.util.Constants.EXCEPTION, e);
	    continue;
	}
	String a2[] = thisDoc.getValues(Constants.ID);
        if (a2 == null || a2.length == 0) {
            continue;
        }
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
			 */

			MoreLikeThis mlt = new MoreLikeThis(ind);
			mlt.setAnalyzer(analyzer);
			String[] fields = { Constants.CONTENT };
			mlt.setFieldNames(fields);
			mlt.setMinDocFreq(mindf);
			mlt.setMinTermFreq(mintf);
			// md5 orig source of doc you want to find similarities to
			Query query = mlt.like(doc);
			log.info("query doc " + doc + ":" + query.toString() + ":" + mlt.describeParams());
			//System.out.println("hits " + searcher.search(query, 100).totalHits);
			searcher.search(query, collector);
			//query = docsLike(doc, ind);
			//System.out.println("query doc " + doc + ":" + query.toString());
			//query = docsLike(doc, found, ind);
			//System.out.println("query doc " + doc + ":" + query.toString());

			//searcher.search(query, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			SearchEngineSearchResult result = handleDocs(search, query, ind, searcher, hits, false);
			return result;
		} catch (Exception e) {
			log.info("Error3: " + e.getMessage());
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		}

		return null;
	}

	/**
	 * Delete from lucene the entry with given id
	 * 
	 * @param str md5 id
	 */

	public SearchEngineDeleteResult deleteme(SearchEngineDeleteParam delete) {
		try {
			Directory index = FSDirectory.open(getLucenePath(delete.conf));
			StandardAnalyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			IndexWriter iw = new IndexWriter(index, iwc);
			//IndexReader r = IndexReader.open(index, false);
			//iw.deleteDocuments(new Term(Constants.TITLE, str));
			iw.deleteDocuments(new Term(Constants.ID, delete.delete));
			iw.close();
		} catch (Exception e) {
			log.info("Error3: " + e.getMessage());
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		}
		return null;
	}

}
