package com.VTM.application.userAdministartion.entityOrDomain;



import javax.persistence.*;


@Entity
@Table(name = "Sales_View")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 30)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;


    @Column(name = "contact_number", nullable = false, unique = true)
    private String contactNumber;

    public User() {
    }

    public User(Long id, String username, String email, String password, String contactNumber) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
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
