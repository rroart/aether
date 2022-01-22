package roart.search.solr;

import java.io.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.client.solrj.SolrServerException;

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
import roart.common.util.IOUtil;
import roart.search.SearchEngineAbstractSearcher;

public class SearchSolr extends SearchEngineAbstractSearcher {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	private SolrConfig conf;

	public SearchSolr(String nodename, NodeConfig nodeConf) {
		conf = new SolrConfig();
		String url = nodeConf.getSolrurl() + nodeConf.solrIndex();
		HttpSolrClient server = new HttpSolrClient.Builder(url)
		        .withSocketTimeout(60000)
		        .withConnectionTimeout(5000)
		        .build();
 		conf.server = server;
		log.info("server " + server);
		System.out.println("server " + server);
		// Setting the XML response parser is only required for cross
		// version compatibility and only when one side is 1.4.1 or
		// earlier and the other side is 3.1 or later.
		server.setParser(new XMLResponseParser());
		// binary parser is used by default
		// The following settings are provided here for completeness.
		// They will not normally be required, and should only be used 
		// after consulting javadocs to know whether they are truly required.
		// server.setDefaultMaxConnectionsPerHost(100);
		// server.setMaxTotalConnections(100);
		//server.setFollowRedirects(true);  // defaults to false
		// allowCompression defaults to false.
		// Server side must support gzip or deflate for this to have any effect.
		// server.setAllowCompression(true);
	}

	public SearchEngineConstructorResult destroy() throws IOException {
		conf.server.close();
		return null;
	}

	public void deconstruct() {
	}

	@Override
	public SearchEngineConstructorResult clear(SearchEngineConstructorParam param) {
	    try {
	        NodeConfig nodeConf = param.conf;
	        conf.server.deleteByQuery( "*:*" );
	        conf.server.commit();
	    } catch (Exception e) {
	        log.error(roart.common.constants.Constants.EXCEPTION, e);
	    }
	    return new SearchEngineConstructorResult();
	}

        @Override
        public SearchEngineConstructorResult drop(SearchEngineConstructorParam param) {
            return clear(param);
        }
	
	public SearchEngineIndexResult indexme(SearchEngineIndexParam index) {
	        NodeConfig nodeConf = index.conf;
		String type = index.type;
		String md5 = index.md5; 
		//InputStream inputStream,
		String[] metadata = index.metadata;
		String lang = index.lang;
		String classification = index.classification;
                Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
                InputStream contentStream = inmemory.getInputStream(index.message);
                if (!InmemoryUtil.validate(index.message.getMd5(), contentStream)) {
                    SearchEngineIndexResult result = new SearchEngineIndexResult();
                    result.noindexreason = "invalid";
                    result.size = -1;
                    return result;
                }
                String content = InmemoryUtil.convertWithCharset(IOUtil.toByteArray1G(inmemory.getInputStream(index.message)));
		int retsize = content.length();
		// this to a method
		log.info("indexing " + md5);
		SearchEngineIndexResult result = new SearchEngineIndexResult();

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
				String[] md = metadata;
				for (String name : md) {
					String value = name;
					doc.addField(Constants.METADATA, name);
				}
			}

			// Fields "id","name" and "price" are already included in Solr installation, you must add your new custom fields in SchemaXml.

			// Create a collection of documents 

			Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			docs.add( doc );
			conf.server.add( docs );
			conf.server.commit();
			UpdateRequest req = new UpdateRequest();
			req.setAction( UpdateRequest.ACTION.COMMIT, false, false );
			req.add( docs );
			UpdateResponse rsp = req.process( conf.server );
		} catch (IOException e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);	    
			result.noindexreason = "index exception " + e.getClass().getName();
			result.size = -1;
			return result;
		} catch (SolrServerException e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);
			result.noindexreason = "index exception " + e.getClass().getName();
			result.size = -1;
			return result;
		} catch (Exception e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);
			result.noindexreason = "index exception " + e.getClass().getName();
			result.size = -1;
			return result;
		}
		result.size = retsize;
		return result;
	}

	public SearchEngineSearchResult searchme(SearchEngineSearchParam search) {
		NodeConfig nodeConf = search.conf;
		String str = search.str;
		String searchtype = search.searchtype;
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

			if (nodeConf.getHighlightmlt()) {
				query.add("hl", "true");
				query.add("hl.fl", Constants.CONTENT);
				query.add("hl.method", "fastVector");
			}
			//    Query the server 

			final Map<String, String> queryParamMap = new HashMap<>();
			//queryParamMap.put("q", "rook");
			//queryParamMap.put("fl", "id, name");
			//queryParamMap.put("sort", "id asc");
                        queryParamMap.put("fl", "*, score");
			queryParamMap.put("q", str /*"*:*"*/ );
                        query.setIncludeScore(true);
                        queryParamMap.put("rows", "100");
                        if (defType != null) {
                                queryParamMap.put("defType", defType); 
                        }
                        //query.addSortField( "price", SolrQuery.ORDER.asc );

                        if (nodeConf.getHighlightmlt()) {
                                queryParamMap.put("hl", "true");
                                queryParamMap.put("hl.fl", Constants.CONTENT);
                                queryParamMap.put("hl.method", "fastVector");
                        }
			MapSolrParams queryParams = new MapSolrParams(queryParamMap);			
                        //QueryResponse rsp = conf.server.query( queryParams );
			QueryResponse rsp = conf.server.query( query, METHOD.POST );

			SolrDocumentList docs = rsp.getResults();

			SearchEngineSearchResult result = handleDocs(search, rsp, docs, true);
			return result;
		} catch (SolrServerException e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		} catch (Exception e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		}
		return null;
	}

	private SearchEngineSearchResult handleDocs(SearchEngineSearchParam param, QueryResponse rsp, SolrDocumentList docs, boolean dohighlight) throws Exception {
		SearchEngineSearchResult result = new SearchEngineSearchResult();
		result.results = new SearchResult[docs.size()];
		// To read Documents as beans, the bean must be annotated as given in the example. 

		int i = -1;
		for (SolrDocument doc : docs) {
			i++;
			SolrDocument d = doc;
			float score = (float) d.get("score"); 
			String md5 = (String) d.getFieldValue(Constants.ID);
			String lang = (String) d.getFieldValue(Constants.LANG);
			List<String> metadata = (List<String>) d.getFieldValue(Constants.METADATA);
			String[] highlights = null;
			if (dohighlight && param.conf.getHighlightmlt()) {
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

	public SearchEngineSearchResult searchmlt(SearchEngineSearchParam search) {
		NodeConfig nodeConf = search.conf;
		String id = search.str;
		String searchtype = search.searchtype;
		try {
			int count = nodeConf.getMLTCount();
			int mintf = nodeConf.getMLTMinTF();
			int mindf = nodeConf.getMLTMinDF();
			//Construct a SolrQuery 
			SolrQuery query = new SolrQuery();
			query.setQuery( Constants.ID + ":" + id);
			query.setIncludeScore(true);
			query.add("mlt", "true");
			query.add("mlt.fl", Constants.CONTENT);
			query.add("mlt.count", "" + count);
			query.add("mlt.mindf", "" + mindf);
			query.add("mlt.mintf", "" + mintf);

			//    Query the server 

			log.info("query " + query);

			HttpSolrClient mltserver = new HttpSolrClient.Builder(conf.server.getBaseURL())
	                        .withSocketTimeout(600000) // bigger timeout, only diff
	                        .withConnectionTimeout(5000)
			        .build();
			// mltserver.setDefaultMaxConnectionsPerHost(100);
			// mltserver.setMaxTotalConnections(100);
			mltserver.setFollowRedirects(false);  // defaults to false
			// allowCompression defaults to false.
			// Server side must support gzip or deflate for this to have any effect.
			// mltserver.setAllowCompression(true);

			QueryResponse rsp = mltserver.query( query );
			NamedList<Object> mlt = (NamedList<Object>) rsp.getResponse().get("moreLikeThis");
			SolrDocumentList docs = (SolrDocumentList) mlt.getVal(0);

			// To read Documents as beans, the bean must be annotated as given in the example. 

			SearchEngineSearchResult result = handleDocs(search, rsp, docs, false);
			return result;
		} catch (SolrServerException e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		} catch (Exception e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		}
		return null;
	}

	public SearchEngineDeleteResult deleteme(SearchEngineDeleteParam delete) {
		try {
			String str = delete.delete;
			conf.server.deleteById(str);
			conf.server.commit();
			UpdateRequest req = new UpdateRequest();
			req.setAction( UpdateRequest.ACTION.COMMIT, false, false );
			req.deleteById(str);
			UpdateResponse rsp = req.process( conf.server );
		} catch (IOException e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		} catch (SolrServerException e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		} catch (Exception e) {
			log.error(roart.common.constants.Constants.EXCEPTION, e);
		}
		return null;
	}

}
