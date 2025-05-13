package com.example.urlshortener.service;

import com.example.urlshortener.model.LongUrl;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

@Service
public class UrlValidationService {

    private static final Logger logger = LoggerFactory.getLogger(UrlValidationService.class);
    private final UrlMappingRepository urlMappingRepository;

    public UrlValidationService(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }

    public UrlMapping validate(LongUrl longUrl) throws IllegalArgumentException {
        logger.info("Validating URL: {}", longUrl.getUrl());
        validateUrlSyntax(longUrl);
        // Todo : check if this below function actually works or not.
//        verifyUrlReachable(longUrl);
        Optional<UrlMapping> urlMapping = urlMappingRepository.findByLongUrl(longUrl.getUrl());
        if(!urlMapping.isEmpty()) {
            logger.info("URL already exists in the database");
            return urlMapping.get();
        }
        logger.info("URL validation successfull");
        return null;
    }

    public boolean validateUrlSyntax(LongUrl url) throws IllegalArgumentException {
        String originalUrl = url.getUrl();
        if (Objects.isNull(url)) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
            originalUrl = "https://" + originalUrl;
            url.setUrl(originalUrl);
        }
        return true;
    }

    public boolean verifyUrlReachable(LongUrl url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url.getUrl()).openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode < 400);
        } catch (IOException e) {
            return false;
        }

    }

}
