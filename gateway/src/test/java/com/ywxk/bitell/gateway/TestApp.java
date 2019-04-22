package com.ywxk.bitell.gateway;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hikaru
 * Created  on 2018/5/21.
 */
@Slf4j
public class TestApp {

  public static void main(String[] args) {
    String uuid = UUID.randomUUID().toString();
    log.info("uuid:{}", uuid);
  }

}
