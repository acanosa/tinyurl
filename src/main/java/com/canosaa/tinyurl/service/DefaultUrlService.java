package com.canosaa.tinyurl.service;

import com.canosaa.tinyurl.model.Url;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;

@Service
@Transactional
@RequiredArgsConstructor
public class DefaultUrlService implements UrlService {

    private static final String SHORT_URL_DOMAIN = "http://tiny.acanosa";
    private static final long OFFSET = 1_000_000;

    @Autowired
    private final UrlRepository urlRepository;
    @Autowired
    private final EncoderService defaultEncoderService;

    @Override
    public String shortenUrl(String url) {
        var exists = urlRepository.existsByUrl(url);
        if (exists) {
            return urlRepository.findByUrl(url).get().getShortUrl();
        }
        var urlToSave = Url.builder()
                .url(url)
                .build();
        var saved = urlRepository.save(urlToSave);

        var encoded = defaultEncoderService.toBase62(saved.getId() + OFFSET);
        saved.setShortUrl(encoded);
        return encoded;
    }

    @Override
    public String getOriginalUrl(String shortUrl) {
        String urlPath = shortUrl.replace(SHORT_URL_DOMAIN, "");
        var id = defaultEncoderService.fromBase62(shortUrl);
        var url = urlRepository.findById(id - OFFSET).orElseThrow(() -> new RuntimeException("Not found"));
        return url.getUrl();
    }
}
