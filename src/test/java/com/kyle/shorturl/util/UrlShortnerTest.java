package com.kyle.shorturl.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UrlShortnerTest {
    UrlShortner shortner;

    @Before
    public void init(){
        shortner = new UrlShortner();
    }

    @Test
    public void encode16() throws Exception {

        UrlShortner urlShortner16  = new UrlShortner(16);
        Random random = new Random();

        for(int i=0;i<100;i++){
            int testNum = random.nextInt(100000000);
            String hex = String.format("%06X",testNum);
            assertEquals(hex,urlShortner16.encode(testNum));
        }
    }

    @Test
    public void decode16() throws Exception {

        UrlShortner urlShortner16  = new UrlShortner(16);
        Random random = new Random();
        for(int i=0;i<100;i++){
            int testNum = random.nextInt(100000000);
            String hex = String.format("%06X",testNum);
            assertEquals(testNum,urlShortner16.decode(hex));

        }
    }


    @Test
    public void encode() throws Exception{
        assertEquals("000000", shortner.encode(0));
        assertEquals("00000z", shortner.encode(61));
        assertEquals("00001z", shortner.encode(62 + 61));
    }
    @Test
    public void decode() throws Exception{
        assertEquals(0, shortner.decode("000000"));
        assertEquals(61, shortner.decode("00000z"));
        assertEquals(62+61, shortner.decode("000001z"));
    }

}