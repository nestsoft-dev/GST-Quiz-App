package com.ikenna.gstquiz;

public class UserModel {
    private String name, email, pass,department;
    private long points = 50;

    public UserModel(String name, String email, String pass, String department, long points) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.department = department;
        this.points = points;
    }

    public UserModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }
}
