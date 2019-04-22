package com.ywxk.bitell.gateway.config;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author Hikaru
 * Created  on 2018/5/22.
 */

@RefreshScope
@Data
@Configuration
@ConfigurationProperties(prefix = "hawkin")
public class HawkinGatewayAppProperties {

  private Map<String, String> appInfos;

  /**
   * 需要验证签名的路径
   */
  private List<String> includePath;

  /**
   * 排序签名的appId
   */
  private List<String> excludeAppId;



}
