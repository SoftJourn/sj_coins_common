package com.softjourn.common.auth;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

public class OAuthHelper {

    private String authServerUrl;

    private RestTemplate restTemplate;

    private String base64;

    private AccessTokenDTO tokenDTO;

    private Long timestamp;

    public OAuthHelper(String clientId, String clientSecret, String authServerUrl, RestTemplate restTemplate) {
        this.authServerUrl = authServerUrl;
        this.restTemplate = restTemplate;
        this.base64 = OAuthHelper.generateBase64Code(clientId, clientSecret);
    }

    public <T> ResponseEntity<T> requestWithToken(String url, HttpMethod httpMethod, HttpEntity<?> entity
            , Class<T> responseType, Object... urlVariables) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "bearer " + getToken());
        httpHeaders.putAll(entity.getHeaders());
        HttpEntity<Object> newEntity = new HttpEntity<>(entity.getBody(), httpHeaders);
        return restTemplate.exchange(url, httpMethod, newEntity, responseType, urlVariables);
    }

    public <T> ResponseEntity<T> getForEntityWithToken(String url, Class<T> responseType, Object... urlVariables) {
        HttpEntity<String> newEntity = new HttpEntity<>("");
        return this.requestWithToken(url, HttpMethod.GET, newEntity, responseType, urlVariables);
    }

    public String getToken() {
        if (this.tokenDTO == null) {
            this.receiveTokenFromAuth();
        }
        Long expire_time = this.timestamp + this.tokenDTO.getExpiresIn();
        Long current = System.currentTimeMillis();
        if (current - expire_time > 0) {
            this.receiveTokenFromAuth();
        }
        return this.tokenDTO.getAccessToken();
    }

    static String generateBase64Code(String clientId, String clientSecret) {
        String base = clientId + ":" + clientSecret;
        byte[] encodedBytes = Base64.getEncoder().encode(base.getBytes());
        return new String(encodedBytes);
    }

    HttpHeaders getHeaders() {
        String authorization = "Basic " + this.base64;
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorization);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED.toString());
        return headers;
    }

    private AccessTokenDTO receiveTokenFromAuth() {
        String url = authServerUrl + "/oauth/token";
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, this.getHeaders());
        ResponseEntity<AccessTokenDTO> response = restTemplate.postForEntity(url, request, AccessTokenDTO.class);
        this.timestamp = System.currentTimeMillis();
        this.tokenDTO = response.getBody();
        return tokenDTO;
    }

}
