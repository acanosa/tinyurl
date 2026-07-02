package com.canosaa.tinyurl.service;

public interface RedisService {

    void saveKey(String key, String value);

    String getValue(String key);

}
