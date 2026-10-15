package com.app.eventhive.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record GoogleAuthRequest(
        @JsonAlias({"credential", "token"})
        String idToken
) {
    public String getToken() {
        return idToken;
    }
}
