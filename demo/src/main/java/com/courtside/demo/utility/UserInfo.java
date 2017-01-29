package com.courtside.demo.utility;

/**
 * Created by MELHAGEH on 11/21/2016.
 */

public class UserInfo {

    static String username = null;
    static String _id = null;
    static String displayName = null;
    static String phoneNumber = null;

    public void set_userId(String userId){
        username = userId;
    }
    public void set_id(String id){
        _id = id;
    }
    public void set_displayname(String displayname){
        displayName = displayname;
    }
    public void set_phonenumber(String phone){
        phoneNumber = phone;
    }

    public String get_userId(){
        return username;
    }
    public String get_id(){
        return _id;
    }
    public String get_displayname(){
        return displayName;
    }
    public String get_phonenumber(){
        return phoneNumber;
    }
}
