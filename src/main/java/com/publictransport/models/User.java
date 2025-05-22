package com.publictransport.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    @Column(name = "firstname")
    private String firstname;

    @Size(max = 255)
    @Column(name = "lastname")
    private String lastname;

    @Size(max = 255)
    @Column(name = "email")
    @Email
    private String email;

    @Size(max = 255)
    @Column(name = "password")
    private String password;

    @Size(max = 255)
    @Column(name = "avatar")
    private String avatar;

    @Lob
    @Column(name = "role")
    private String role;

}