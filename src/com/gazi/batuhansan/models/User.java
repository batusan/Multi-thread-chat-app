package com.gazi.batuhansan.models;

import java.io.ObjectOutputStream;

/**
 *
 * @author BatuPC
 */
public class User {
    private ObjectOutputStream objOutputStream;
    private String username;
    
    public User(String username,ObjectOutputStream objOutputStream){
        this.objOutputStream = objOutputStream;
        this.username = username;       
    }
    
    public ObjectOutputStream getOutputObject(){
        return this.objOutputStream;
    }
    
    public String getUsername(){
        return this.username;
    }
    
}
