package com.sentomero.sufeeds.javasufeeds.Models;

public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String studentNumber;
    private String course;
    private String yearModule;

    public User(int userId, String firstName, String lastName, String studentNumber,
                String course, String yearModule) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentNumber = studentNumber;
        this.course = course;
        this.yearModule = yearModule;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getStudentNumber() { return studentNumber; }
    public String getCourse() { return course; }
    public String getYearModule() { return yearModule; }
}
