package com.ywxk.bitell.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Hikaru
 * Created  on 2018/4/9.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class GateWayApplication {
  public static void main(String[] args) {
    SpringApplication.run(GateWayApplication.class, args);
  }
}
