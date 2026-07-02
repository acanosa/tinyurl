package com.canosaa.tinyurl.service;

import org.springframework.stereotype.Service;

public interface EncoderService {

    String toBase62(long value);

    Long fromBase62(String shortUrl);
}
