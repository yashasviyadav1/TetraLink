package com.example.urlshortener.service;

import com.example.urlshortener.ExceptionHandler.UrlShortenerException;
import com.example.urlshortener.Utility.Base62Utility;
import com.example.urlshortener.model.LongUrl;
import com.example.urlshortener.model.ShortUrl;
import com.example.urlshortener.model.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class UrlShortenerService {

    private final UrlMappingRepository urlMappingRepository;
    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerService.class);
    private final ObjectMapper objectMapper;

    @Value("${shorturl.code.length.fixed}")
    private int fixedLengthForShortCode;

    @Value("${shorturl.custom.domain}")
    private String urlShortenerBackendDomain;

    @Value("${shorturl.collision.resolution.string}")
    private String collisionResolutionString;

    public UrlShortenerService(UrlMappingRepository urlMappingRepository,
                               ObjectMapper objectMapper
    ) {
        this.urlMappingRepository = urlMappingRepository;
        this.objectMapper = objectMapper;
    }

    public LongUrl getOriginalUrl(String shortUrlCode) throws UrlShortenerException {
        Optional<UrlMapping> urlMapping = urlMappingRepository.findByShortUrlCode(shortUrlCode);
        if (urlMapping.isEmpty()) {
            throw new UrlShortenerException("URL not found");
        }
        return LongUrl.builder()
                .url(urlMapping.get().getLongUrl())
                .build();
    }

    public ShortUrl generateShortUrl(LongUrl longUrlEntity) throws Exception {
        String originalUrl = longUrlEntity.getUrl();
        String initialEncodedCode = Base62Utility.encodeUrl(originalUrl);
        String encodedCode = initialEncodedCode.substring(0, Math.min(fixedLengthForShortCode,
                initialEncodedCode.length()));
        String shortCode = generateUniqueShortCode(originalUrl, encodedCode);
        UrlMapping urlMapping = UrlMapping.builder()
                .shortUrlCode(shortCode)
                .longUrl(longUrlEntity.getUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        logger.info("short code generated before saving: {}", objectMapper.writeValueAsString(urlMapping));
        try {
            UrlMapping response = urlMappingRepository.saveAndFlush(urlMapping);
            logger.info("shortUrl saved successfully: {}", objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            throw new UrlShortenerException("Error while saving URL mapping: " + e.getMessage());
        }
        String shortUrl = urlShortenerBackendDomain + "/" + urlMapping.getShortUrlCode();
        return ShortUrl.builder()
                .url(shortUrl)
                .code(urlMapping.getShortUrlCode())
                .build();
    }

    private String generateUniqueShortCode(String modifiedLongUrl, String shortCode) {
        if (urlMappingRepository.findByShortUrlCode(shortCode).isEmpty()) {
            return shortCode;
        }
        modifiedLongUrl = modifiedLongUrl + collisionResolutionString;
        String encodedString = Base62Utility.encodeUrl(modifiedLongUrl);
        return generateUniqueShortCode(modifiedLongUrl, encodedString.substring(0, Math.min(fixedLengthForShortCode,
                encodedString.length())));
    }


}
