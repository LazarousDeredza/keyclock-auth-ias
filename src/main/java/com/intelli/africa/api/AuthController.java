package com.intelli.africa.api;

import okhttp3.*;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class AuthController {



    @Value("${keycloak.urls.auth}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    private final OkHttpClient client = new OkHttpClient().newBuilder().build();

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "client_id="+realm+"-app&grant_type=password&username=" + username + "&password=" + password);
        Request request = new Request.Builder()
                .url(authServerUrl+"/realms/"+realm+"/protocol/openid-connect/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return ResponseEntity.ok(response.body().string());
            } else {
                return ResponseEntity.status(response.code()).body(response.body().string());
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during authentication: " + e.getMessage());
        }
    }
}

