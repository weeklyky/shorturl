package com.kyle.shorturl.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ShortUrlRepository extends CrudRepository<ShortUrl,String> {

    ShortUrl findByShortKey(String shortKey);

    ShortUrl findByOriginUrl(String originUrl);

    @Query("SELECT max(su.shortKey) FROM ShortUrl su")
    String findMaxId();
}
