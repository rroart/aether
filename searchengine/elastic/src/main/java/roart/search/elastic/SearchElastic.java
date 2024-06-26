package roart.search.elastic;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;

import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jsonb.JsonbJsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.PutMappingRequest;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch._types.mapping.TermVectorOption;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.HighlighterType;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.transport.rest_client.RestClientOptions;

import roart.common.config.NodeConfig;
import roart.common.constants.Constants;
import roart.common.inmemory.common.Inmemory;
import roart.common.inmemory.factory.InmemoryFactory;
import roart.common.inmemory.util.InmemoryUtil;
import roart.common.searchengine.SearchConstants;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchElastic extends SearchEngineAbstractSearcher {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static final Integer BUFFER_LIMIT = 1024 * 1024 * 1024;

    private ElasticConfig conf;

    public SearchElastic(String configname, String configid, NodeConfig nodeConf) {
        super(configname, configid, nodeConf);
        conf = new ElasticConfig();
        String myindex = nodeConf.elasticIndex();
        String host = nodeConf.getElasticHost(); 
        String port = nodeConf.getElasticPort(); 
        String username = nodeConf.getElasticUsername(); 
        String password = nodeConf.getElasticPassword(); 
        boolean ssl = nodeConf.getElasticSsl();

        try {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            // Create the low-level client
            RestClient restclient = RestClient.builder(
                    new HttpHost(host, Integer.valueOf(port), ssl ? "https" : "http"))
                    .setHttpClientConfigCallback(new HttpClientConfigCallback() {
                        @Override
                        public HttpAsyncClientBuilder customizeHttpClient(
                                HttpAsyncClientBuilder httpClientBuilder) {
                            HttpAsyncClientBuilder builder = httpClientBuilder
                                    .setDefaultCredentialsProvider(credentialsProvider);
                            if (ssl) {
                                SSLContext sslContext = ssl();
                                builder.setSSLContext(sslContext);
                            }
                            return builder;
                        }
                    }) 
                    .build();

            // Hairy way to set the buffer limit
            HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory factory = new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(BUFFER_LIMIT);
            RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
            builder.setHttpAsyncResponseConsumerFactory(factory);
            RequestOptions requestOptions = builder.build();
            RestClientOptions restClientOptions = new RestClientOptions(requestOptions);
            
            // Create the transport with a Jackson mapper
            RestClientTransport transport = new RestClientTransport(
                    restclient, new JacksonJsonpMapper(), restClientOptions);

            // And create the API client
            conf.client = new ElasticsearchClient(transport);

            BooleanResponse response = conf.client.indices().exists(b -> b.index(myindex));

            if (response.value()) {
                return;
            }

            conf.client.indices().create(c -> c.index(myindex)
                    .mappings(m -> m
                            .properties(SearchConstants.CONTENT, p -> p
                                    .text(t -> t.termVector(TermVectorOption.WithPositionsOffsets)
                                            .store(true))
                                    )
                            )
                    );
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
    }

    public SSLContext ssl() {
        SSLContext context = null;
        if (true /*esConnectionConfiguration.isTrustingAllCertificates()*/) {
            try {
                context = SSLContext.getInstance("TLS");
                context.init(null, new TrustManager[]{new MyTrustManager()}, null);
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
        return context;
    }    

    @Override
    public SearchEngineConstructorResult clear(SearchEngineConstructorParam param) {
        try {
            NodeConfig nodeConf = param.conf;
            String myindex = nodeConf.elasticIndex();
            conf.client.indices().delete(d -> d.index(myindex));
            // BulkByScrollResponse response = conf.client.deleteByQuery(new DeleteByQueryRequest(myindex), RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return new SearchEngineConstructorResult();
    }

    @Override
    public SearchEngineConstructorResult drop(SearchEngineConstructorParam param) {
        try {
            String myindex = nodeConf.elasticIndex();
            log.info("myindex"+myindex);
            conf.client.indices().delete(d -> d.index(myindex));
            // DeleteResponse response = conf.client.delete(new DeleteRequest(myindex), RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return new SearchEngineConstructorResult();	        
    }

    public  SearchEngineConstructorResult destroy() {
        try {
            // conf.client.close();
            //NodeConfig nodeConf = param.conf;
            //String myindex = nodeConf.elasticIndex();
            //conf.client.indices().close(c -> c.index(myindex));
            throw new IOException();
        } catch (IOException e) {
            log.error(Constants.EXCEPTION, e);
        }
        return null;
    }

    public  SearchEngineIndexResult indexme(SearchEngineIndexParam index) {
        String md5 = index.md5;  
        String[] metadata = index.metadata;
        String lang = index.lang;
        String classification = index.classification;
        Inmemory inmemory = InmemoryFactory.get(nodeConf.getInmemoryServer(), nodeConf.getInmemoryHazelcast(), nodeConf.getInmemoryRedis());
        try (InputStream contentStream = inmemory.getInputStream(index.message)) {
            if (!InmemoryUtil.validate(index.message.getMd5(), contentStream)) {
                SearchEngineIndexResult result = new SearchEngineIndexResult();
                result.noindexreason = this.getClass().getSimpleName() + " " +  "invalid";
                result.size = -1;
                return result;
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        String content = InmemoryUtil.convertWithCharset(IOUtil.toByteArray(inmemory.getInputStream(index.message), BUFFER_LIMIT / 3));
        int retsize = content.length();

        log.info("indexing {}", md5);
        log.info("sizing {} {} {}", md5, content.length(), content.getBytes().length);

        String cat = classification;

        SearchEngineIndexResult result = new SearchEngineIndexResult();
        try {
            String myindex = nodeConf.elasticIndex();
            Appdata appdata = new Appdata();
            appdata.cat = cat;
            appdata.content = content;
            appdata.lang = lang;
            appdata.metadata = Arrays.asList(metadata);
            conf.client.index(i -> i
                    .index(myindex)
                    .id(md5)
                    .document(appdata)
                    .refresh(Refresh.True));

            // IndexResponse response = conf.client.index(new IndexRequest(indexName).id(md5).source(builder), RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            result.noindexreason = this.getClass().getSimpleName() + " " +  "index exception " + e.getClass().getName() + " " + e.getMessage();
            result.size = -1;
            return result;
        }
        result.size = retsize;
        return result;
    }

    public SearchEngineSearchResult searchme(SearchEngineSearchParam search) {
        String myindex = nodeConf.elasticIndex();
        String str = search.str;
        String searchtype = search.searchtype;

        int stype = new Integer(searchtype).intValue();
        try {
            SearchRequest.Builder sr = new SearchRequest.Builder()
                    .index(myindex)
                    .query(q -> q
                            .term(t -> t
                                    .field(SearchConstants.CONTENT)
                                    .value(v -> v.stringValue(str))))
                    .from(0)
                    .size(100)
                    .explain(true);
            if (nodeConf.getHighlightmlt()) {
                sr.highlight(h -> h.fields(SearchConstants.CONTENT, v -> v.type(HighlighterType.Unified)));
            }
            SearchResponse<Appdata> response = conf.client.search(sr.build(), Appdata.class);
            HitsMetadata<Appdata> docs = response.hits();

            SearchEngineSearchResult result = handleDocs(search, response, docs, true);
            return result;
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return null;
    }

    private SearchEngineSearchResult handleDocs(SearchEngineSearchParam search, SearchResponse rsp, HitsMetadata<Appdata> docs, boolean dohighlight) throws Exception {
        SearchEngineSearchResult result = new SearchEngineSearchResult();
        result.results = new SearchResult[docs.hits().size()];
        // To read Documents as beans, the bean must be annotated as given in the example. 

        try {			
            int i = -1;
            for (Hit<Appdata> doc : docs.hits()) {
                i++;
                Hit<Appdata> d = doc;
                double score = d.score();
                Map<String, JsonData> map = d.fields();
                log.info("bla" + map);
                Appdata data = d.source();
                //String md5 = (String) map.get(Constants.ID).toString();
                //String lang = (String) map.get(Constants.LANG).toString();
                // TODO fix metadata
                List<String> metadata = null; //new ArrayList(map.get(Constants.METADATA));
                String[] highlights = null;

                if (dohighlight && nodeConf.getHighlightmlt()) {
                    Map<String, List<String>> m = d.highlight();
                    log.info("m"+m);
                    List<String> hlf = m.get(SearchConstants.CONTENT);
                    highlights = new String[1];
                    highlights[0] = "none";
                    if (hlf != null) {
                        String frags = hlf.get(0);
                        if (frags != null && frags.length() > 0) {
                            highlights[0] = frags;
                        }				
                    }
                }

                SearchResult res = new SearchResult();
                res.md5 = d.id();
                res.score = Float.valueOf((float) score);
                res.lang = data.lang;
                res.highlights = highlights;
                // TODO fix metadata
                res.metadata = data.metadata;
                result.results[i] = res;
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return result;
    }

    @Override
    public SearchEngineSearchResult searchmlt(SearchEngineSearchParam search) {
        String myindex = nodeConf.elasticIndex();
        String id = search.str;
        String searchtype = search.searchtype;
        int count = nodeConf.getMLTCount();
        int mintf = nodeConf.getMLTMinTF();
        int mindf = nodeConf.getMLTMinDF();

        SearchRequest.Builder sr = new SearchRequest.Builder()
                .index(myindex)
                .query(q -> q
                        .moreLikeThis(m -> m
                                .like(l -> l
                                        .document(d -> d
                                                .index(myindex)
                                                .id(id)))                                .minTermFreq(mintf)
                                .minDocFreq(mindf)))
                .from(0)
                .size(count)
                .explain(true);

        SearchResponse<Appdata> response = null;
        try {
            response = conf.client.search(sr.build(), Appdata.class);
        } catch (IOException e) {
            log.error(Constants.EXCEPTION, e);            
        }
        HitsMetadata<Appdata> docs = response.hits();

        try {
            SearchEngineSearchResult result = handleDocs(search, response, docs, false);
            return result;
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return null;
    }

    // TODO untested
    public SearchEngineDeleteResult deleteme(SearchEngineDeleteParam delete) {
        String myindex = nodeConf.elasticIndex();
        String str = delete.delete;
        //ActionFuture<DeleteResponse> action1 = conf.client.delete(myindex, mytype, str).execute();
        try {
            conf.client.delete(d -> d
                    .index(myindex)
                    .id(str)
                    );
        } catch (IOException e) {
            log.error(Constants.EXCEPTION, e);
        }
        return null;
    }

    public static class Appdata {
        public String lang;
        public String cat;
        public String content;
        public List<String> metadata;
    }

    private static class MyTrustManager implements X509TrustManager
    {
        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return null;
        }


        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType)
        {
        }


        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType)
        {
        }
    }
}
