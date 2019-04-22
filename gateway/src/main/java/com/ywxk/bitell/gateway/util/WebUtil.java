package com.ywxk.bitell.gateway.util;


import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class WebUtil {

  private static String[] IP_HEADS = new String[]{"x-forwarded-for", "proxy-client-ip",
      "wl-proxy-client-ip",
      "http_client_ip", "http_x_forwarded_for",};

  public static String getIp(ServerHttpRequest request) {
    for (String header : IP_HEADS) {
      String ip = request.getHeaders().getFirst(header);
      if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
        return ip;
      }
    }
    return request.getRemoteAddress().getHostString();
  }
}
