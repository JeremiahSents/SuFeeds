package com.sentomero.sufeeds.javasufeeds.Models;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String studentNumber;
    private String course;
    private String yearModule;

    public User(int id, String firstName, String lastName, String studentNumber, String course, String yearModule) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentNumber = studentNumber;
        this.course = course;
        this.yearModule = yearModule;
    }

    // Getters
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getStudentNumber() { return studentNumber; }
    public String getCourse() { return course; }
    public String getYearModule() { return yearModule; }
}