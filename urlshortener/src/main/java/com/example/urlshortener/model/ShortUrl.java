package com.example.urlshortener.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortUrl {

    private String url;
    private String code;

}
