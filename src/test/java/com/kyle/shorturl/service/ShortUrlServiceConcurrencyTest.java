package com.kyle.shorturl.service;

import com.kyle.shorturl.dao.ShortUrl;
import com.kyle.shorturl.dao.ShortUrlRepository;
import org.jmock.lib.concurrent.Blitzer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StopWatch;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShortUrlServiceConcurrencyTest {
    AtomicInteger c;

    Blitzer blitzer = new Blitzer(10000,30);
    @Before
    public void setUp(){
        c= new AtomicInteger();
    }

    @Autowired
    ShortUrlService service;

    @MockBean
    private ShortUrlRepository mockRepository;


    @Test
    public void concurrency() throws Exception {

        given(mockRepository.findByOriginUrl("http://www.daum.net")).willReturn(new ShortUrl("ABCDEF","http://www.daum.net"));
        given(mockRepository.findByOriginUrl("http://kakao.com")).willReturn(new ShortUrl("ABCDEG","http://kakao.com"));

        given(mockRepository.exists("ABCDEF")).willReturn(true);
        given(mockRepository.exists("ABCDEG")).willReturn(true);

        StopWatch sw = new StopWatch();
        Random rand = new Random();
        sw.start();
        blitzer.blitz(() -> {
            //String url  = rand.nextBoolean() ? "http://www.daum.net" : "http://kakao.com";

            String url  = "www."+rand.nextInt(100000)+".com";
            String test = null;
            try {
                test = service.registerUrl(url).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println(url + " -> " + test);

            c.getAndIncrement();

        });

        sw.stop();
        System.out.println("execute time : " + sw.getTotalTimeSeconds());
        System.out.println("execute count : " + c.get());

    }
    @After
    public void tearDown() throws InterruptedException {
        blitzer.shutdown();
    }

}