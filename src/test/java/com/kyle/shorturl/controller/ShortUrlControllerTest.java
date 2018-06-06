package com.kyle.shorturl.controller;

import com.google.gson.JsonObject;
import com.kyle.shorturl.dao.ShortUrl;
import com.kyle.shorturl.service.NoSuchShortKeyException;
import com.kyle.shorturl.service.ShortUrlService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ShortUrlController.class)
public class ShortUrlControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ShortUrlService service;


    @Test
    public void redirectRegisteredUrl() throws Exception {
        given(service.getOriginUrl("ABCDEF")).willReturn("http://naver.com");

        mvc.perform(get("/ABCDEF"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://naver.com"));
    }


    @Test
    public void redirectUnregisteredUrl() throws Exception {
        given(service.getOriginUrl(eq("ABCDEF"))).willThrow(NoSuchShortKeyException.class);

        mvc.perform(get("/ABCDEF"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void registerUrl() throws Exception {
        given(service.registerUrl(eq("http://naver.com"))).willReturn(new AsyncResult<String>("ABCDEF"));

        JsonObject expectResult = new JsonObject();
        expectResult.addProperty("originUrl", "http://naver.com");
        expectResult.addProperty("shortKey", "ABCDEF");

        mvc.perform(post("/shorturl/register").param("url","http://naver.com"))
                .andExpect(status().isOk());
    }


    @Test
    public void index() throws Exception {
        mvc.perform(post("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

}