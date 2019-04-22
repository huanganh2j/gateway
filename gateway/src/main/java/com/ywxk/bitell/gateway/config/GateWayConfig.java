package com.ywxk.bitell.gateway.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author Hikaru
 * Created  on 2018/5/18.
 */

@RefreshScope
@Configuration
@Data
@ConfigurationProperties(prefix = "gateway")
public class GateWayConfig {

  /**
   * 5分钟
   */
  private  static final long INVOKE_INTERVAL_TIME = 1000*60*5;

  /**
   * 最大访问间隔时间
   */
  private long invokeIntervalTime = INVOKE_INTERVAL_TIME;

  /**
   * ip检查开关
   */
  private boolean ipOpen;
  /**
   * 重复提交开关
   */
  private boolean resubmitOpen;


  /**
   * 黑名单用户
   */
  private boolean blackListOpen;








}
