package com.edutec.deportes.user.models;

public class User {

    private String nombre;
    private String username;
    private String email;
    private String birthday;

    public User() {
    }

    public User(String nombre, String username, String email, String birthday) {
        this.nombre = nombre;
        this.username = username;
        this.email = email;
        this.birthday = birthday;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
