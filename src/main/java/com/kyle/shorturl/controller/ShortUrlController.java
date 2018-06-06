package com.kyle.shorturl.controller;


import com.kyle.shorturl.dao.ShortUrl;
import com.kyle.shorturl.service.NoSuchShortKeyException;
import com.kyle.shorturl.service.ShortUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Controller
public class ShortUrlController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ShortUrlService service;


    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/{key:^[0-9|A-Z|a-z]{6}$}")
    public String redirect(@PathVariable String key) throws NoSuchShortKeyException {
        logger.info("Search for {}", key);
        return "redirect:"+ service.getOriginUrl(key);
    }

    @PostMapping("/shorturl/register")
    public String registerUrl(
            @RequestParam String url, Model model,
            @RequestHeader(value = "origin", required = false) final String origin
    ) throws UnknownHostException, ExecutionException, InterruptedException {
        if(!StringUtils.isEmpty(url)) {
            Future<String> f = service.registerUrl(url);
            String shortKey = f.get();
            logger.info("Successfully regiested {}",shortKey);
            model.addAttribute("shortUrl", new ShortUrl(shortKey, url));
            model.addAttribute("origin",  origin +"/");
        }
        return "index";
    }


}
