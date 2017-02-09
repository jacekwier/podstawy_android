package com.example.yggdrasil.peoplephotos;

/**
 * Created by Yggdrasil on 2016-11-26.
 */

public class Person {

    private int id;
    private String name;
    private String surname;
    private String birthDate;
    private String photoPath;

    public Person(int id,String name, String surname,String birthDate, String photoPath){
        this.id=id;
        this.name=name;
        this.surname=surname;
        this.birthDate=birthDate;
        this.photoPath=photoPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}
