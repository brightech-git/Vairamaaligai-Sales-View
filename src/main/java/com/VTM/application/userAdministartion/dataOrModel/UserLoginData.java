package com.VTM.application.userAdministartion.dataOrModel;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginData {

    @JsonProperty(value = "usernameOrEmail")
    private String usernameOrEmail;

    @JsonProperty(value = "password")
    private String password;

    @JsonProperty(value = "errorMessage")
    private String errorMessage;

    public UserLoginData() {
    }

    public UserLoginData(String usernameOrEmail, String password
    ) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    public UserLoginData(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}