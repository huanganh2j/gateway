package com.ywxk.bitell.gateway.filter;

import static com.hawkin.framework.autoconfigure.CommonStaticConstant.APPID_HEAD_NAME;
import static com.hawkin.framework.autoconfigure.CommonStaticConstant.SECRET;
import static com.hawkin.framework.autoconfigure.springmvc.BasicResultCode.SUBMIT_EXIST;
import static com.hawkin.framework.autoconfigure.springmvc.BasicResultCode.TIME_ERROR;

import com.hawkin.framework.autoconfigure.springmvc.BasicResult;
import com.hawkin.framework.autoconfigure.springmvc.BasicResultCode;
import com.ywxk.bitell.gateway.cache.CacheRedis;
import com.ywxk.bitell.gateway.config.GateWayConfig;
import com.ywxk.bitell.gateway.config.HawkinGatewayAppProperties;
import com.ywxk.bitell.gateway.util.FilterUtils;
import com.ywxk.bitell.gateway.util.HeaderUtils;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Hikaru
 * Created  on 2018/5/18.
 * 重复提交
 */
@Component
@Slf4j
@Order(3)
public class GlobalResubmiFilter implements GlobalFilter {

  private final static String TIME_STAMP_HEAD_NAME = "timeStamp";
  private final static String UUID_HEAD_NAME = "uuid";

  @Autowired
  private GateWayConfig gateWayConfig;

  @Autowired
  private FilterUtils filterUtils;

  @Autowired
  private CacheRedis cacheRedis;

  @Autowired
  private HawkinGatewayAppProperties hawkinGatewayAppProperties;


  /**
   * 是否匹配配置路径
   */
  private boolean match(String path) {
    boolean flag = false;
    List<String> list = hawkinGatewayAppProperties.getIncludePath();
    if(!CollectionUtils.isEmpty(list)){
      for (String string : list) {
        if (string.matches(path)) {
          flag = true;
          break;
        }
      }
    }
    return flag;

  }

  /**
   * 判断是否包含在排除的app列表中
   */
  private boolean isExistExluceApp(String appId, String secret) {
    boolean flag = false;
    if (!CollectionUtils.isEmpty(this.hawkinGatewayAppProperties.getExcludeAppId())) {
      if (isContain(appId) && this.hawkinGatewayAppProperties.getAppInfos().get(appId)
          .equals(secret)) {
        flag = true;
      }
    }
    return flag;

  }

  /**
   * 判断是否包含在排除签名校验的app列表中
   */
  private boolean isContain(String appId) {
    boolean flag = false;
    for (String string : this.hawkinGatewayAppProperties.getExcludeAppId()) {
      if (string.equals(appId)) {
        flag = true;
        break;
      }

    }
    return flag;
  }


  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest serverHttpRequest = exchange.getRequest();

    if (this.gateWayConfig.isResubmitOpen()) {
      String path = serverHttpRequest.getPath().pathWithinApplication().value();
      String appId = HeaderUtils.getString(serverHttpRequest, APPID_HEAD_NAME);
      String sercet = HeaderUtils.getString(serverHttpRequest, SECRET);
      log.info("path:{},appId:{},sercet:{}",path,appId,sercet);
      if (!isExistExluceApp(appId, sercet) && match(path)) {
        log.info("路径"+path+"匹配上了，并且也不在排除之列");
        Long timeStamp = HeaderUtils.getLong(serverHttpRequest, TIME_STAMP_HEAD_NAME);
        String uuid = HeaderUtils.getString(serverHttpRequest, UUID_HEAD_NAME);

        Long now = System.currentTimeMillis();
        log.info("timeStamp:{}",timeStamp);
        //时间戳不能为空
        if (timeStamp == null) {
          return this.filterUtils.getVoidMono(exchange,
              BasicResult.fail(BasicResultCode.PARAM_MISS_ERROR.getCode(),
                  BasicResultCode.PARAM_MISS_ERROR.getMsg()));
        }
        //随机串不能为空
        if (StringUtils.isBlank(uuid)) {
          return this.filterUtils.getVoidMono(exchange,
              BasicResult.fail(BasicResultCode.PARAM_MISS_ERROR.getCode(),
                  BasicResultCode.PARAM_MISS_ERROR.getMsg()));
        }

        //校验时间是否正确
        if (Math.abs(now - timeStamp) > this.gateWayConfig.getInvokeIntervalTime()) {
          return this.filterUtils.getVoidMono(exchange,
              BasicResult.fail(TIME_ERROR.getCode(),
                  TIME_ERROR.getMsg()));
        }

        //是否存在uuid
        if (this.cacheRedis.exists(uuid)) {
          return this.filterUtils.getVoidMono(exchange,
              BasicResult.fail(SUBMIT_EXIST.getCode(),
                  SUBMIT_EXIST.getMsg()));
        }

        this.cacheRedis.addInvokeRecord(uuid);
      }


    }

    return chain.filter(exchange);
  }




}
