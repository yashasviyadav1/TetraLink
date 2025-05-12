package com.example.urlshortener.controller;

import com.example.urlshortener.ExceptionHandler.UrlShortenerException;
import com.example.urlshortener.model.LongUrl;
import com.example.urlshortener.model.ShortUrl;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.service.UrlShortenerService;
import com.example.urlshortener.service.UrlValidationService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


import java.util.Objects;


@RestController
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;
    private final UrlValidationService urlValidationService;
    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);

    @Value("${internal.access.token}")
    private String internalAccessToken;

    @Value("${shorturl.custom.domain}")
    private String urlShortenerBackendDomain;

    public UrlShortenerController(UrlShortenerService urlShortenerService,
                                  UrlValidationService urlValidationService) {
        this.urlShortenerService = urlShortenerService;
        this.urlValidationService = urlValidationService;
    }

    @PostMapping("/url")
    public ShortUrl generateShortUrl(@RequestBody LongUrl longUrlRequest,
                                     @RequestParam String accessToken) throws Exception {
        System.out.println("Access Token: " + accessToken);
        logger.info("inside generateShortUrl()");

        if (!accessToken.equals(internalAccessToken)) {
            throw new UrlShortenerException("Invalid access token");
        }
        if (Objects.isNull(longUrlRequest.getUrl())) {
            throw new UrlShortenerException("long_url is required field");
        }
        LongUrl longUrlEntity = LongUrl.builder()
                .url(longUrlRequest.getUrl())
                .build();
        UrlMapping urlMapping = urlValidationService.validate(longUrlEntity);
        if (Objects.nonNull(urlMapping)) {
            return ShortUrl.builder()
                    .url(urlShortenerBackendDomain + "/" + urlMapping.getShortUrlCode())
                    .code(urlMapping.getShortUrlCode())
                    .build();
        }
        try {
            return urlShortenerService.generateShortUrl(longUrlEntity);
        } catch (Exception e) {
            throw new UrlShortenerException("Error while generating short URL: " + e.getMessage());
        }
    }

    @GetMapping("/{shortUrlCode}")
    public void redirect(@PathVariable String shortUrlCode, HttpServletResponse response) throws UrlShortenerException {
        logger.info("inside redirect() shortUrlCode: {}", shortUrlCode);
        LongUrl originalUrl = urlShortenerService.getOriginalUrl(shortUrlCode);
        logger.info("response from getOriginalUrl: {}", originalUrl);
        if (Objects.isNull(originalUrl)) {
            throw new UrlShortenerException("URL not found");
        }
        try {
            response.sendRedirect(originalUrl.getUrl());
        } catch (Exception e) {
            throw new UrlShortenerException("Error while redirecting: " + e.getMessage());
        }

    }


}
