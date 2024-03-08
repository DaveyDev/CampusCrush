package com.example.campuscrush;

public class MarketUsers {

    String name, surname, dateOfBirth, email;
    Boolean sex;
    String profilePictureUrl;

    @Override
    public String toString() {
        return "MarketUsers{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", userAge='" + dateOfBirth + '\'' +
                '}'; //added just to test why it wasn't workin'
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSurname() {
        return surname;
    }


    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
    public void setSex(boolean sex) { // Setter for boolean sex
        this.sex = sex;
    }
    public boolean isSex() { // Getter for boolean sex
        return sex;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

}
