package com.ywxk.bitell.gateway.filter;

import com.hawkin.framework.autoconfigure.CommonStaticConstant;
import com.hawkin.framework.autoconfigure.auth.HawkinAuthProperties;
import com.hawkin.framework.autoconfigure.auth.HawkinJwtToken;
import com.hawkin.framework.autoconfigure.jackson.HawkinJson;
import com.hawkin.framework.autoconfigure.springmvc.BasicResult;
import com.hawkin.framework.autoconfigure.springmvc.BasicResultCode;
import com.ywxk.bitell.gateway.consumer.UserFreezeConsumerListener;
import com.ywxk.bitell.gateway.util.FilterUtils;
import com.ywxk.bitell.user.remote.model.message.UserFreezeMessage;
import io.jsonwebtoken.Claims;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Hikaru
 * Created  on 2018/4/12.
 * 解析jwt的token在头中加入B_UID字段
 *
 * 用户token校验
 */
@Component
@Slf4j
@Order(4)
public class GlobalJwtUserFilter implements GlobalFilter {

  @Autowired
  private UserFreezeConsumerListener userFreezeConsumerListener;

  @Autowired
  private HawkinJson hawkinJson;

  @Autowired
  private HawkinAuthProperties hawkinAuthProperties;
  @Autowired
  private HawkinJwtToken hawkinJwtToken;

  @Autowired
  private FilterUtils filterUtils;

  private static final String AUTH_PATTERN = "/auth";


  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest serverHttpRequest = exchange.getRequest();

    ServerHttpRequest.Builder mutate = exchange.getRequest().mutate()
        .headers(httpHeaders -> httpHeaders.remove(CommonStaticConstant.HEADER_USER_ID));

    String path = serverHttpRequest.getPath().pathWithinApplication().value();
    log.info("请求path:{}", path);
    if (path.contains("swagger-ui.html") || path.contains("/v2/api-docs")) {
      return Mono.empty();
    }

    List<String> tokens = serverHttpRequest.getHeaders()
        .get(this.hawkinAuthProperties.getHeaderName());
    //需要鉴权
    if (path.endsWith(AUTH_PATTERN)) {
      if (CollectionUtils.isEmpty(tokens)) {
        return this.filterUtils.getVoidMono(exchange,
            BasicResult.fail(BasicResultCode.SECURITY_ACCESS_DENIED.getCode(),
                BasicResultCode.SECURITY_ACCESS_DENIED.getMsg()));
      }
      String token = tokens.get(0);
      log.info("path:{}  token:{}", path, token);

      Claims claims = null;
      try {
        claims = this.hawkinJwtToken.getAllClaimsFromToken(token);
      } catch (Exception e) {

        return this.filterUtils.getVoidMono(exchange,
            BasicResult.fail(BasicResultCode.SECURITY_TOKEN_NOT_ACTIVE.getCode(),
                BasicResultCode.SECURITY_TOKEN_NOT_ACTIVE.getMsg()));
      }

      if (claims == null || !this.hawkinJwtToken.validateToken(token)) {
        return this.filterUtils.getVoidMono(exchange,
            BasicResult.fail(BasicResultCode.SECURITY_AUTHENTICATION.getCode(),
                BasicResultCode.SECURITY_AUTHENTICATION.getMsg()));
      }

      String userId = this.hawkinJwtToken.getUserIdFromToken(token);
      Date date = this.hawkinJwtToken.getIssuedAtDateFromToken(token);
      UserFreezeMessage userFreezeMessage = userFreezeConsumerListener.userFreezeMessage(userId);
      if (userFreezeMessage != null) {
        if (userFreezeMessage.getFreezeTime().after(date)) {
          log.warn("userId:{} 已冻结!", userId);
          return this.filterUtils.getVoidMono(exchange,
              BasicResult.fail(BasicResultCode.SECURITY_TOKEN_NOT_ACTIVE.getCode(),
                  BasicResultCode.SECURITY_TOKEN_NOT_ACTIVE.getMsg()));

        }
      }

      mutate.header(CommonStaticConstant.HEADER_USER_ID,
          userId);


    } else {
      try {
        if (!CollectionUtils.isEmpty(tokens)) {
          String token = tokens.get(0);
          log.info("无验证页面token:{}", token);
          if (StringUtils.isNotBlank(token)) {
            Claims claims = this.hawkinJwtToken.getAllClaimsFromToken(token);
            if (claims != null & this.hawkinJwtToken.validateToken(token)) {
              mutate.header(CommonStaticConstant.HEADER_USER_ID,
                  this.hawkinJwtToken.getUserIdFromToken(token));
            }
          }

        }
      } catch (Exception e) {
        log.error("无验证页面path: {} 解析token错误:", path, e);
      }
    }

    ServerHttpRequest build = mutate.build();
    return chain.filter(exchange.mutate().request(build).build());
  }


}
