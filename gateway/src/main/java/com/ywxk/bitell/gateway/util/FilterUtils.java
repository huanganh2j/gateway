package com.ywxk.bitell.gateway.util;

import com.hawkin.framework.autoconfigure.jackson.HawkinJson;
import com.hawkin.framework.autoconfigure.springmvc.BasicResult;
import java.nio.charset.StandardCharsets;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Hikaru
 * Created  on 2018/5/21.
 */
@Service
public class FilterUtils {
  @Autowired
  private HawkinJson hawkinJson;

  @NotNull
  public Mono<Void> getVoidMono(ServerWebExchange serverWebExchange, BasicResult basicResult) {
    serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
    serverWebExchange.getResponse().setStatusCode(HttpStatus.OK);
    byte[] bytes = this.hawkinJson.obj2string(basicResult).getBytes(StandardCharsets.UTF_8);
    DataBuffer buffer = serverWebExchange.getResponse().bufferFactory().wrap(bytes);
    return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
  }


}
