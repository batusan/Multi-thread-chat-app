/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gazi.batuhansan.models;

import java.io.ObjectOutputStream;

/**
 *
 * @author BatuPC
 */
public class User {
    /**
     * Bu classın kullanılma amacı , sunucuya bağlanan her bir socketin takibini yapmaktır.
     * her bağlanan socketin output streamini ve usernameini bir arada tutabilmek için bu model classı oluşturulmuştur.
     * 
     * Constructorunda output streamini ve usernameini alarak bağlanan her bir socketi etiketlemiş oluyoruz.
     * 
     * getOutputObject ve getUsername private fieldları return ediyorlar sonradan set edilme özelliklerine sahip değiller.
     */
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
