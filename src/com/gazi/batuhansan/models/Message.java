/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gazi.batuhansan.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


/**
 *
 * @author BatuPC
 */
public class Message implements Serializable{
    
    /**
     * Serializable implement ederek ağ üzerinde gönderdiğimiz verileri tekrar edilebilir olmasını ve birden fazla kez kullanabilmemizi sağlar.
     * Ayrıca bu model sunucu ve istemci arasında ki tüm işlemler de farklı şekiller de şekillendirilerek kullanılıyor.
     * Farklı tiplerde iletiler gönderilmesini sağlayan bu tip.
     * Kullanıcı girişlerini , çıkışlarını , mesaj gönderme eylemlerini , aktif kullanıcı listelerini taşıma gibi bir çok görevde kullanılmıştır.
     * 
     * Değişkenlerimiz iletişim sırasında çok yönlü olması için farklı tiplerde mesaj gönderilebilir olmasını sağladım.
     * 2 kullanıcı arasında gerçekleşen mesajlaşmalar farklı bir tür.
     * Sunucunun zoraki bir yöntemle bağlantıyı kesmesine kadar bir çok farklı işlem.
     * 
     */
    private String type;
    private String username;
    private String message;
    private Date date;
    private String target;
    private ArrayList<String> userList = new ArrayList<String>();
    
    /**
     * Giriş çıkış işlemlerinde bu constructoru kullandım , mesaj içeriğine gerek duymayan bir nesne.
     * 
     * @param type
     * @param username 
     */
    public Message(String type,String username){
        //Login Logout type
        this.type =type;
        this.username = username;
        this.date=new Date();
    }
    
    /**
     * Aktif kullanıcıları dinamik bir şekilde göstermek için yine bu nesne yapısını kullandım ve 
     * Aktif kullanıcılar yenilendiği an , aktif kullanıcıları tüm istemcilere dağıttım.
     * Bu amaç için kullanıldı.
     * 
     * @param type
     * @param username
     * @param userList 
     */
    public Message(String type,String username,ArrayList<String> userList){
        //Sync Active User list type
        this.type =type;
        this.username = username;
        this.userList = userList;
        this.date=new Date();
    }
    
    public Message(){}
    
    
    /**
     * İstemcilerin sunucuya yazdırmak , göstermek istedikleri mesajları bu nesne constructoru ile taşıdım.
     * 
     * @param type
     * @param username
     * @param message 
     */
    public Message(String type,String username,String message){
        //Default Message type
        this.type = type;
        this.username =username;
        this.date=new Date();
        this.message = message;
    }   
    
    
    /**
     * Özel mesajlar için ulaşması gereken bir hedefin olduğu mesajlar da bu nesne constructorunu kullandım.
     * Target özel mesaj yoluyla mesajın gidiceği kişinin ismini temsil ediyor.
     * 
     * 
     * @param type
     * @param username
     * @param target
     * @param message 
     */
    
    public Message(String type,String username,String target,String message){
        //Private Message type
        this.target=target;
        this.type = type;
        this.username =username;
        this.date=new Date();
        this.message = message;
    }   
    
    public Date getDate(){
        return this.date;
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public String getTarget(){
        return this.target;
    }
        
    public String getType(){
        return this.type;
    }
    
    public String getMessage(){
        return this.message;
    }
    
    public void syncList(ArrayList<String> userlist){       
        this.userList = userlist;
    }
    
    public ArrayList<String> getList(){
        return this.userList;
    }
    
}
