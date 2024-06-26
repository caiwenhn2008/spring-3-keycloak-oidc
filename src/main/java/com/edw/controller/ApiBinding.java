package com.edw.controller;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.logging.Logger;

public abstract class ApiBinding {
  private Logger logger = Logger.getLogger(ApiBinding.class.getName());

  protected RestTemplate restTemplate;

  public ApiBinding(String accessToken) {
    logger.info("accessToken: " + accessToken);
    this.restTemplate = new RestTemplate();
    if (accessToken != null) {
      this.restTemplate.getInterceptors()
        .add(getBearerTokenInterceptor(accessToken));
    } else {
      this.restTemplate.getInterceptors().add(getNoTokenInterceptor());
    }
  }

  private ClientHttpRequestInterceptor
  getBearerTokenInterceptor(String accessToken) {
    ClientHttpRequestInterceptor interceptor =
      new ClientHttpRequestInterceptor() {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] bytes,
                                            ClientHttpRequestExecution execution) throws IOException {
          request.getHeaders().add("Authorization", "Bearer " + accessToken);
          return execution.execute(request, bytes);
        }
      };
    return interceptor;
  }

  private ClientHttpRequestInterceptor getNoTokenInterceptor() {
    return new ClientHttpRequestInterceptor() {
      @Override
      public ClientHttpResponse intercept(HttpRequest request, byte[] bytes,
                                          ClientHttpRequestExecution execution) throws IOException {
        throw new IllegalStateException(
          "Can't access the API without an access token");
      }
    };
  }

}