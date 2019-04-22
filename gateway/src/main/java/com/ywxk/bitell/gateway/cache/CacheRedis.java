package com.ywxk.bitell.gateway.cache;

import com.ywxk.bitell.gateway.config.GateWayConfig;
import com.ywxk.bitell.gateway.config.HawkinGatewayAppProperties;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Hikaru
 * Created  on 2018/5/21.
 */
@Service
public class CacheRedis {

  @Autowired
  private RedisTemplate redisTemplate;

  private static final String IP_BLACK_LIST_KEY = "gataway::ip";

  @Autowired
  private GateWayConfig gateWayConfig;

  private static final String RE_SUBMIT_KEY = "gateway::submit:%s";

  public boolean sismember(String ip) {
    return this.redisTemplate.opsForSet().isMember(IP_BLACK_LIST_KEY, ip);
  }

  public void addInvokeRecord(String uuid) {
    this.redisTemplate.opsForValue().set(String.format(RE_SUBMIT_KEY, uuid),
        "1", gateWayConfig.getInvokeIntervalTime(), TimeUnit.MILLISECONDS);
  }

  /**
   * 是否存在key记录
   */
  public boolean exists(String uuid) {
    return this.redisTemplate.hasKey(String.format(RE_SUBMIT_KEY, uuid));
  }


}
