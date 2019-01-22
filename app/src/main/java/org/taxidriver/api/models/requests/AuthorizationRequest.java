package org.taxidriver.api.models.requests;

public class AuthorizationRequest {
    String login;
    String password;

    public AuthorizationRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
