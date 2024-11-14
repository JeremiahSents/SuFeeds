package com.sentomero.sufeeds.javasufeeds.Models;

public class User {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String studentNumber;
    private final String course;
    private final String yearModule;

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