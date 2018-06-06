package com.kyle.shorturl.dao;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class ShortUrl {
    @Id
    String shortKey;
    @Column
    String originUrl;


    public ShortUrl() {
    }

    public ShortUrl(String shortKey, String originUrl) {
        this.shortKey = shortKey;
        this.originUrl = originUrl;
    }

    public String getShortKey() {
        return shortKey;
    }


    public String getOriginUrl() {
        return originUrl;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShortUrl shortUrl = (ShortUrl) o;

        if (shortKey != null ? !shortKey.equals(shortUrl.shortKey) : shortUrl.shortKey != null) return false;
        return originUrl != null ? originUrl.equals(shortUrl.originUrl) : shortUrl.originUrl == null;
    }

    @Override
    public int hashCode() {
        int result = shortKey != null ? shortKey.hashCode() : 0;
        result = 31 * result + (originUrl != null ? originUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ShortUrl{" +
                "shortKey='" + shortKey + '\'' +
                ", originUrl='" + originUrl + '\'' +
                '}';
    }
}
