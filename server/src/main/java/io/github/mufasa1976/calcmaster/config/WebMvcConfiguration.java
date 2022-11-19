package io.github.mufasa1976.calcmaster.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
@EnableWebMvc
public class WebMvcConfiguration implements WebMvcConfigurer {
  private static final String SLASH = "/";
  private static final String ASTERISK = "*";
  private static final String[] ANGULAR_RESOURCES = {
      "/favicon.ico",
      "/main.*.js",
      "/main-*.*.js",
      "/polyfills.*.js",
      "/polyfills-*.*.js",
      "/runtime.*.js",
      "/runtime-*.*.js",
      "/styles.*.css",
      "/deeppurple-amber.css",
      "/indigo-pink.css",
      "/pink-bluegrey.css",
      "/purple-green.css",
      "/3rdpartylicenses.txt"
  };
  public static final String[] OFFERED_ANGULAR_LANGUAGES = {"de", "en"};

  private final String prefix;
  private final Collection<HttpMessageConverter<?>> messageConverters;
  private final AsyncTaskExecutor asyncTaskExecutor;

  public WebMvcConfiguration(
      @Value("${spring.thymeleaf.prefix:" + ThymeleafProperties.DEFAULT_PREFIX + "}") String prefix,
      Collection<HttpMessageConverter<?>> messageConverters,
      AsyncTaskExecutor asyncTaskExecutor) {
    this.prefix = StringUtils.appendIfMissing(prefix, "/");
    this.messageConverters = messageConverters;
    this.asyncTaskExecutor = asyncTaskExecutor;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.setOrder(1);
    registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
            .resourceChain(true);
    for (String offeredLanguage : OFFERED_ANGULAR_LANGUAGES) {
      final var relativeAngularResources = Stream.of(ANGULAR_RESOURCES)
                                                 .filter(resource -> StringUtils.contains(resource, ASTERISK))
                                                 .map(resource -> SLASH + offeredLanguage + resource)
                                                 .toArray(String[]::new);
      registry.addResourceHandler(relativeAngularResources)
              .addResourceLocations(prefix + offeredLanguage + SLASH);

      final var fixedAngularResources = Stream.of(ANGULAR_RESOURCES)
                                              .filter(resource -> !StringUtils.contains(resource, ASTERISK))
                                              .map(resource -> SLASH + offeredLanguage + resource)
                                              .toArray(String[]::new);
      registry.addResourceHandler(fixedAngularResources)
              .addResourceLocations(prefix);

      registry.addResourceHandler("/" + offeredLanguage + "/assets/**")
              .addResourceLocations(prefix + offeredLanguage + "/assets/");
    }
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.setOrder(2);
    for (String offeredLanguage : OFFERED_ANGULAR_LANGUAGES) {
      registry.addViewController("/" + offeredLanguage + "/**").setViewName(offeredLanguage + "/index");
    }
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(APPLICATION_JSON);
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.addAll(messageConverters);
  }

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    configurer.setDefaultTimeout(5000).setTaskExecutor(asyncTaskExecutor);
  }
}
