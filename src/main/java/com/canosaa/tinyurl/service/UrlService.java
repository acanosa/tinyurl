package com.canosaa.tinyurl.service;

public interface UrlService {

    String shortenUrl(String url);

    String getOriginalUrl(String shortUrl);

}
