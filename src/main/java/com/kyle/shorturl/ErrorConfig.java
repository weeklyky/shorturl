package com.kyle.shorturl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class ErrorConfig extends ServerProperties {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override public void customize(ConfigurableEmbeddedServletContainer container) {
        super.customize(container);
        container.addErrorPages(new ErrorPage("/error"));
    }
}
