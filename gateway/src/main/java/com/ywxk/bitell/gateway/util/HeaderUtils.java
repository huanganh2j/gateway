package com.ywxk.bitell.gateway.util;

import java.util.List;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;

/**
 * @author Hikaru
 * Created  on 2018/5/18.
 */
public class HeaderUtils {



  public static String getString(ServerHttpRequest serverHttpRequest, String headName) {
    List<String> list = serverHttpRequest.getHeaders()
        .get(headName);
    if (!CollectionUtils.isEmpty(list)) {
      return list.get(0);
    }
    return null;
  }

  public static Long getLong(ServerHttpRequest serverHttpRequest, String headName) {
    List<String> list = serverHttpRequest.getHeaders()
        .get(headName);
    if (!CollectionUtils.isEmpty(list)) {
      return Long.parseLong(list.get(0));
    }
    return null;
  }







}
