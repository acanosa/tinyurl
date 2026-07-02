package com.canosaa.tinyurl.controller;

import com.canosaa.tinyurl.dto.GetUrlRequest;
import com.canosaa.tinyurl.dto.ShortenUrlRequest;
import com.canosaa.tinyurl.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/urls")
@RequiredArgsConstructor
public class URLController {

    private final UrlService defaultUrlService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String shortenUrl(@RequestBody ShortenUrlRequest request) {
        return defaultUrlService.shortenUrl(request.getUrl());
    }

    @GetMapping("/{shortenedUrl}")
    public String getUrl(@PathVariable String shortenedUrl) {
        return defaultUrlService.getOriginalUrl(shortenedUrl);
    }

}
