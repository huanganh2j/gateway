package com.ywxk.bitell.gateway.consumer;

import com.ywxk.bitell.user.common.UserConstans;
import com.ywxk.bitell.user.remote.model.message.UserFreezeMessage;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author Hikaru
 * Created  on 2018/5/31.
 */
@Slf4j
@Component
public class UserFreezeConsumerListener {

  @Autowired
  private RedisTemplate redisTemplate;

  private static final String GATE_WAY_TOKEN_REFRESH = "gateway::token:%s";
  private static final long GATE_WAY_TOKEN_REFRESH_EXPIRE_TIME = 60 * 60 * 24 * 32;

  @KafkaListener(topics = UserConstans.TOPIC_USER_FREEZE)
  public void comment(UserFreezeMessage userFreezeMessage) {
    try {
      this.redisTemplate.opsForValue().set(
          String.format(GATE_WAY_TOKEN_REFRESH, String.valueOf(userFreezeMessage.getUserId())),
          userFreezeMessage, GATE_WAY_TOKEN_REFRESH_EXPIRE_TIME, TimeUnit.SECONDS
      );
      log.info("userFreezeMessage: {}", userFreezeMessage);
    } catch (Exception e) {
      log.error("userFreezeMessage:{}", userFreezeMessage, e);
    }

  }

  public UserFreezeMessage userFreezeMessage(String userId) {
    return (UserFreezeMessage) this.redisTemplate.opsForValue().get(String.format(GATE_WAY_TOKEN_REFRESH, userId));
  }

}
