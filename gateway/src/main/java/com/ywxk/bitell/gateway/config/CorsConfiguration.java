package com.ywxk.bitell.gateway.config;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author Hikaru
 * Created  on 2018/5/5.
 */
@Configuration
@Slf4j
public class CorsConfiguration {

  @Autowired
  private CorsProperties corsProperties;

  private static final String ALLOWED_METHODS = "GET, PUT, POST, DELETE, OPTIONS";
  private static final String MAX_AGE = "3600";


  @Bean
  @Order(1)
  public WebFilter corsFilter() {
    return (ServerWebExchange ctx, WebFilterChain chain) -> {
      ServerHttpRequest request = ctx.getRequest();

      if (CorsUtils.isCorsRequest(request)) {

        String orgin = request.getHeaders().getFirst(HttpHeaders.ORIGIN);
        log.info("origin:{}", orgin);
        if ((!CollectionUtils.isEmpty(corsProperties.getAllowOrigins())) && corsProperties
            .getAllowOrigins().contains(orgin)) {
          HttpHeaders httpHeaders = request.getHeaders();
          List<String> headerKeyList = new ArrayList<>(httpHeaders.keySet());
          log.info("heads:{}", headerKeyList);
          ServerHttpResponse response = ctx.getResponse();
          HttpHeaders headers = response.getHeaders();
          headers.add("Access-Control-Allow-Origin", orgin);
          headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);
          headers.add("Access-Control-Max-Age", MAX_AGE);

          headers.add("Access-Control-Allow-Headers", headerKey(headerKeyList,httpHeaders));

          headers.add("Access-Control-Allow-Credentials", "true");
          if (request.getMethod() == HttpMethod.OPTIONS) {
            response.setStatusCode(HttpStatus.OK);
            return Mono.empty();
          }
        }

      }


      return chain.filter(ctx);
    };
  }

  private String headerKey(List<String> headerKeyList, HttpHeaders httpHeaders) {
    List<String> acccess = httpHeaders.get("Access-Control-Request-Headers");
    log.info("acccess:{}",acccess);
    if (!CollectionUtils.isEmpty(acccess)) {
      headerKeyList.addAll(acccess);
    }

    StringBuilder stringBuilder = new StringBuilder();
    if (!CollectionUtils.isEmpty(headerKeyList)) {
      for (int i = 0, len = headerKeyList.size(); i < len; i++) {
        if (i != 0) {
          stringBuilder.append(",");
        }
        stringBuilder.append(headerKeyList.get(i));
      }
    }
    return stringBuilder.toString();
  }

}