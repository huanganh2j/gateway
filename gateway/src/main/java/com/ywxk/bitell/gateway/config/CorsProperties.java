package com.ywxk.bitell.gateway.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Hikaru
 * Created  on 2018/5/5.
 */

@ConfigurationProperties(prefix = "gateway")
@Data
@Configuration
public class CorsProperties {

  private List<String> allowOrigins;

}
