package roart.common.webflux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import roart.common.constants.EurekaConstants;
import roart.common.util.MathUtil;

public class WebFluxUtil {
    private static Logger log = LoggerFactory.getLogger(WebFluxUtil.class);

    public static <T> T sendMe(Class<T> myclass, Object param, String host, String port, String path) {
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String url = "http://" + host + ":" + port + "/" + path;
        return sendMeInner(myclass, param, url, objectMapper);
    }
        
    public static <T> T sendMe(Class<T> myclass, Object param, String url) {
        return sendMeInner(myclass, param, url, null);
    }
    
    public static <T> T sendMe(Class<T> myclass, String url, Object param, String path) {
        return sendMeInner(myclass, param, url + path, null);
    }
    
    public static <T> T sendMeInner(Class<T> myclass, Object param, String url, ObjectMapper objectMapper) {
        long time = System.currentTimeMillis();
        if (objectMapper != null) {
            ExchangeStrategies jacksonStrategy = ExchangeStrategies.builder()
                    .codecs(config -> {
                        config.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                        config.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                    }).build();
            WebClient.builder().exchangeStrategies(jacksonStrategy);
        }
        WebClient webClient = WebClient.create();
        T result = webClient
                .mutate()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(-1))
                        .build())
                .build()
                .post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromObject(param))
                .retrieve()
                .bodyToMono(myclass)
                .onErrorMap(Exception::new)
                .block();
        
        //Mono.just
        //log.info("resultme " + regr.getHeaders().size() + " " + regr.getHeaders().getContentLength() + " " + regr.toString());
        log.debug("Rq time {}s for {} ", MathUtil.round((double) (System.currentTimeMillis() - time) / 1000, 1), url);
        return result;
    }

}
