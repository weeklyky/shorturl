package com.kyle.shorturl.service;

import com.kyle.shorturl.dao.ShortUrl;
import com.kyle.shorturl.dao.ShortUrlRepository;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShortUrlServiceTest {

    @MockBean
    private ShortUrlRepository mockRepository;

    @Autowired
    ShortUrlService service;

    @Test
    public void getOriginUrl() throws Exception {
        given(mockRepository.findByShortKey("ABCDEF")).willReturn(new ShortUrl("ABCDEF","http://kakao.com"));

        assertEquals("http://kakao.com",service.getOriginUrl("ABCDEF"));
    }

    @Test(expected=NoSuchShortKeyException.class)
    public void getOriginUrlNoResult() throws Exception {
        service.getOriginUrl("ABCDEF");
    }



    @Test
    public void registerUrl() throws Exception {
        String ret1 = service.registerUrl("kakao1.com").get();
        String ret2 = service.registerUrl("kakao1.com").get();
        String ret3 = service.registerUrl("kakao3.com").get();

        assertEquals(ret1,ret2);
        assertNotEquals(ret3,ret2);

        given(mockRepository.findByOriginUrl("kakao3.com")).willReturn(new ShortUrl(ret3, "kakao3.com"));

        String ret4 = service.registerUrl("kakao3.com").get();
        assertEquals(ret3,ret4);

    }

}