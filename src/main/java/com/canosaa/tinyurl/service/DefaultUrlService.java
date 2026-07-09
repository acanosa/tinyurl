package com.canosaa.tinyurl.service;

import com.canosaa.tinyurl.model.Url;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;

@Service
@Transactional
@RequiredArgsConstructor
public class DefaultUrlService implements UrlService {

    private static final String LONG_URL_KEY = "longUrl:";
    private static final String SHORT_URL_KEY = "shortUrl:";
    private static final long OFFSET = 1_000_000;

    @Autowired
    private final UrlRepository urlRepository;
    @Autowired
    private final EncoderService defaultEncoderService;
    @Autowired
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String shortenUrl(String url) {
        var redisKey = LONG_URL_KEY + url;
        var cached =  redisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            System.out.println("Cache match in redis for key: " + redisKey);
            return cached;
        }

        var exists = urlRepository.existsByUrl(url);
        if (exists) {
            return urlRepository.findByUrl(url).get().getShortUrl();
        }
        var urlToSave = Url.builder()
                .url(url)
                .build();
        var saved = urlRepository.save(urlToSave);
        System.out.println(saved.getId());

        var encoded = defaultEncoderService.toBase62(saved.getId() + OFFSET);
        saved.setShortUrl(encoded);
        redisTemplate.opsForValue().set(redisKey, saved.getShortUrl());
        return encoded;
    }

    @Override
    public String getOriginalUrl(String shortUrl) {
        var redisKey = SHORT_URL_KEY + shortUrl;
        var cachedValue = redisTemplate.opsForValue().get(redisKey);
        if (cachedValue != null) {
            System.out.println("Cache hit for redis with key: " + redisKey);
            return cachedValue;
        }

        var id = defaultEncoderService.fromBase62(shortUrl);
        var url = urlRepository.findById(id - OFFSET).orElseThrow(() -> new RuntimeException("Not found"));
        redisTemplate.opsForValue().set(SHORT_URL_KEY + shortUrl, url.getUrl());
        return url.getUrl();
    }
}
