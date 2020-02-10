package ru.rsatu.doAnimal;

import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class AnimalResponce {
    private String name;
    private Integer age;
    private String type;
    private String gender;

    public S3ObjectInputStream photo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public S3ObjectInputStream getPhoto() {
        return photo;
    }

    public void setPhoto(S3ObjectInputStream photo) {
        this.photo = photo;
    }
}
