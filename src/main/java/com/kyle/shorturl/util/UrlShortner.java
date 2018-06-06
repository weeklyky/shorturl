package com.kyle.shorturl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UrlShortner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final int MIN_SIZE = 6;
    private static final char [] TABLE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static Map<Character, Integer> DICTIONARY;
    static {
        Map<Character, Integer> map = new HashMap();
        for (int i=0;i<TABLE.length;i++){
            map.put(TABLE[i], i);
        }
        DICTIONARY = Collections.unmodifiableMap(map);
    }

    private final int BASE;

    public UrlShortner(){
        this.BASE = 62;
    }
    public UrlShortner(int base) {
        this.BASE = base;
    }

    public String encode(long decimal) {
        StringBuilder builder = new StringBuilder();

        while(builder.length() < MIN_SIZE || decimal != decimal/BASE){
            int mod = (int) (decimal % BASE);
            builder.append(TABLE[mod]);
            decimal = decimal / BASE;
        }
        return builder.reverse().toString();
    }

    public long decode(String base62) {
        char[] base62Arr = base62.toCharArray();
        long decimal = 0;
        long mul = 1;
        int pos;

        for(int i = base62.length()-1; i >=0; i--)
        {
            pos = DICTIONARY.get(base62Arr[i]);
            decimal += pos * mul;
            mul *= BASE;
        }
        return decimal;
    }

}
