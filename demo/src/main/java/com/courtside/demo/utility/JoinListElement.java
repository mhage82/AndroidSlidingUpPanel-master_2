package com.courtside.demo.utility;

/**
 * Created by mohammadhage82 on 12/29/2016.
 */

public class JoinListElement {
    static String username;
    static String _id;
    static String displayName;
    // image TODO:mohammad


    public JoinListElement(String un, String id, String dn){
        username = un;
        _id = id;
        displayName = dn;
    }

    public String getUserName(){
        return username;
    }
    public String getID(){
        return _id;
    }
    public String getDisplayName(){
        return displayName;
    }

    public void setUserName(String value){
        username = value;
    }
    public void setID(String value){
        _id = value;
    }
    public void setDisplayName(String value){
        displayName = value;
    }
}
