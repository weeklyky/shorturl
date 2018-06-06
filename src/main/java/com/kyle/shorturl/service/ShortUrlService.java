package com.kyle.shorturl.service;

import com.kyle.shorturl.dao.ShortUrl;
import com.kyle.shorturl.dao.ShortUrlRepository;
import com.kyle.shorturl.util.UrlShortner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ShortUrlService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ShortUrlRepository repository;

    private UrlShortner shortner;

    private ConcurrentHashMap<String, FutureWithTimer<String>> urlMap;

    private AtomicLong idx;


    @PostConstruct
    private void initialize(){
        urlMap = new ConcurrentHashMap<>();

        shortner = new UrlShortner();

        String maxKey = repository.findMaxId();

        if(maxKey==null){ // nothing in DB
            idx = new AtomicLong(0);
        }
        else{ // set MaxID From DB
            idx = new AtomicLong(shortner.decode(maxKey));
        }


        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(()-> evictUrlMap(), 10, 10, TimeUnit.SECONDS);

    }

    private void evictUrlMap(){
        final Date date = new Date();
        List<String> victim = urlMap.keySet().stream().filter(v -> urlMap.get(v).isExpired(date)).collect(Collectors.toList());
        logger.info("{} are removed",victim.size());
        victim.forEach(x ->urlMap.remove(x));

    }

    @Transactional
    @Cacheable("URLS")
    public String getOriginUrl(String shortKey) throws NoSuchShortKeyException {
        ShortUrl shortUrl = repository.findByShortKey(shortKey);
        logger.info("{} is found",shortUrl);

        if(shortUrl!=null){
            return shortUrl.getOriginUrl();
        }else{
            throw new NoSuchShortKeyException();
        }
    }

    @Async
    @Transactional
    public Future<String> registerUrl(String url) {
        if(!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://"+url;
        }
        final String origin = url;
        logger.info("Origin url is {}",origin);



        FutureWithTimer<String> f = urlMap.get(origin);
        if(f == null){
            FutureTask ft = new FutureTask(() -> findKeyAndInsert(origin));
            FutureWithTimer ftt = new FutureWithTimer(ft);
            f = urlMap.putIfAbsent(origin,ftt) ;
            if(f == null) {
                f = ftt;
                ft.run();
            }
        }
        return f.getFuture();
    }

    private String findKeyAndInsert(String origin){
        ShortUrl shortUrl = repository.findByOriginUrl(origin);
        String newKey;
        if(shortUrl != null) {
            newKey = shortUrl.getShortKey();
        }
        else {
            do {
                newKey = shortner.encode(idx.incrementAndGet());
            } while(repository.exists(newKey));
            repository.save(new ShortUrl(newKey, origin));
        }
        return newKey;

    }

    static class FutureWithTimer<T>{
        Future<T> future;
        Date time;

        public FutureWithTimer(Future<T> future) {
            this.future = future;
            this.time = new Date();
        }

        public Future<T> getFuture() {
            return future;
        }

        public Date getTime() {
            return time;
        }

        public boolean isExpired(Date now) {
            return time.getTime() < (now.getTime() - 5 * 1000l);
        }
    }


}
