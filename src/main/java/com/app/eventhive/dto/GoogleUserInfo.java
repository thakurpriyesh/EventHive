package com.app.eventhive.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleUserInfo(
        String email,
        @JsonProperty("email_verified") Object emailVerified,
        String name,
        String sub,
        String aud,
        String picture
) {
    public boolean isVerified() {
        if (emailVerified == null) return false;
        if (emailVerified instanceof Boolean b) return b;
        return "true".equalsIgnoreCase(String.valueOf(emailVerified));
    }
}
