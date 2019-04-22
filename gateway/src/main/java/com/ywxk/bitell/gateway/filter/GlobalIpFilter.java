package com.ywxk.bitell.gateway.filter;

import static com.hawkin.framework.autoconfigure.springmvc.BasicResultCode.BLACK_LIST_EXIST;

import com.hawkin.framework.autoconfigure.springmvc.BasicResult;
import com.ywxk.bitell.gateway.cache.CacheRedis;
import com.ywxk.bitell.gateway.config.GateWayConfig;
import com.ywxk.bitell.gateway.util.FilterUtils;
import com.ywxk.bitell.gateway.util.HeaderUtils;
import com.ywxk.bitell.gateway.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Hikaru
 * Created  on 2018/5/18.
 * ip过滤
 */

@Component
@Slf4j
@Order(2)
public class GlobalIpFilter implements GlobalFilter {

  @Autowired
  private GateWayConfig gateWayConfig;

  @Autowired
  private CacheRedis cacheRedis;

  @Autowired
  private FilterUtils filterUtils;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    if (gateWayConfig.isIpOpen()) {
      ServerHttpRequest serverHttpRequest = exchange.getRequest();
      String ip = WebUtil.getIp(serverHttpRequest);

      if (this.cacheRedis.sismember(ip)) {
        log.warn("ip:{} 已在黑名单列表中",ip);
        return this.filterUtils.getVoidMono(exchange,
            BasicResult.fail(BLACK_LIST_EXIST.getCode(),
                BLACK_LIST_EXIST.getMsg()));
      }

    }
    return chain.filter(exchange);

  }
}
