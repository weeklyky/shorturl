package com.kyle.shorturl.dao;


import com.kyle.shorturl.dao.ShortUrl;
import com.kyle.shorturl.dao.ShortUrlRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
;import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(SpringRunner.class)
@DataJpaTest
@Rollback(value=true)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class ShortUrlRepositoryTest {

    @Autowired
    private ShortUrlRepository repository;


    @Test
    public void findMaxIdEmptyTable() throws Exception {
        repository.deleteAll();
        String maxId = repository.findMaxId();
        assertNull(maxId);
    }



    @Test
    public void findMaxId() throws Exception {
        repository.save(new ShortUrl("0","http://www.naver.com"));
        repository.save(new ShortUrl("ABCDEFG","http://www.naver.com"));
        repository.save(new ShortUrl("ABCDEFZ","http://www.naver.com"));


        String maxId = repository.findMaxId();
        assertEquals(maxId, "ABCDEFZ");
    }
    @Test
    public void findByURLTest() {
        repository.save(new ShortUrl("ABCDEFG","http://www.naver.com"));
        ShortUrl url = repository.findByShortKey("ABCDEFG");
        assertEquals(url.getOriginUrl(),"http://www.naver.com");

    }

}