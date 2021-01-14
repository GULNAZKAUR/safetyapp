package com.example.safetyapp;

public class user_details {
    String name,phoneno,password,gender,userpic="",emergency = "OFF";

    public user_details(String name, String phoneno, String password, String gender, String userpic) {
        this.name = name;
        this.phoneno = phoneno;
        this.password = password;
        this.gender = gender;
        this.userpic = userpic;
    }

    public user_details() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserpic() {
        return userpic;
    }

    public void setUserpic(String userpic) {
        this.userpic = userpic;
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }
}
