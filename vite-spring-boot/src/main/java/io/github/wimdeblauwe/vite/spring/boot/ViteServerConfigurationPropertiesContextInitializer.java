package io.github.wimdeblauwe.vite.spring.boot;


import static io.github.wimdeblauwe.vite.spring.boot.ViteDevServerConfigurationProperties.PREFIX;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

/**
 * This ApplicationContextInializer will automatically set the property values of {@link ViteDevServerConfigurationProperties}
 * by reading the `server-config.json` file that is generated by the vite-plugin-spring-boot npm package.
 * Due to that, Spring Boot knows where the Vite live reload server is hosting the assets.
 */
public class ViteServerConfigurationPropertiesContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final String PROPERTY_FILE_PREFX = PREFIX + ".";

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    try {
      Path path = Path.of("target/vite-plugin-spring-boot/server-config.json");
      ObjectMapper objectMapper = new ObjectMapper();
      Map map = objectMapper.readValue(path.toFile(), Map.class);

      MapPropertySource hostPropertySource = new MapPropertySource(PROPERTY_FILE_PREFX + "host",
          Map.of(PROPERTY_FILE_PREFX + "host", map.get("host")));
      MapPropertySource portPropertySource = new MapPropertySource(PROPERTY_FILE_PREFX + "port",
          Map.of(PROPERTY_FILE_PREFX + "port", map.get("port")));
      MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
      propertySources.addFirst(hostPropertySource);
      propertySources.addFirst(portPropertySource);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
