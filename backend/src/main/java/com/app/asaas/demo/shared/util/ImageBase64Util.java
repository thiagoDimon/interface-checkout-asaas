package com.app.asaas.demo.shared.util;

import java.util.Base64;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ImageBase64Util {

    private final RestClient imageClient = RestClient.create();

    public String downloadAsBase64(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("A URL da imagem não foi informada");
        }

        byte[] image = imageClient.get().uri(imageUrl).retrieve().body(byte[].class);
        return Base64.getEncoder().encodeToString(image);
    }

}
