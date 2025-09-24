package com.VTM.application.userAdministartion.dataOrModel;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserData {

    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "username")
    private String username;

    @JsonProperty(value = "email")
    private String email;

    @JsonProperty(value = "password")
    private String password;


    @JsonProperty(value = "contactNumber")
    private String contactNumber;

    @JsonProperty(value = "errorMessage")
    private String errorMessage;

    public UserData() {
    }

    public UserData(String username, String email, String password, String contactNumber) {
        this.username = username;
        this.email = email;
        this.password = password;

        this.contactNumber = contactNumber;
    }

    public UserData(Long id, String username, String email, String password, String contactNumber) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;

        this.contactNumber = contactNumber;
    }

    public UserData(String username, String email,   String contactNumber) {
        this.username = username;

        this.email = email;
        this.contactNumber = contactNumber;
    }

    public UserData(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
