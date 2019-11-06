package com.xyz.androideatit.Model;

public class User {

    private String Name;
    private String Password;
    private String Phone;
    private String IsStaff;
    private  String ResetPass;


    public User() {
    }


    public String getResetPass() {
        return ResetPass;
    }

    public void setResetPass(String resetPass) {
        ResetPass = resetPass;
    }

    public User(String name, String password, String resetPass) {
        Name = name;
        Password = password;
        ResetPass = resetPass;
        IsStaff = "false";

    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
