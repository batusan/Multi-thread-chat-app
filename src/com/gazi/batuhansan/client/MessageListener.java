/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gazi.batuhansan.client;

import com.gazi.batuhansan.models.Message;
import com.gazi.batuhansan.views.FrmClient;
import java.io.ObjectInputStream;

/**
 *
 * @author BatuPC
 */




/**
 * Thread yapısını kullanabilmek için thread sınıfını kalıtım yoluyla veya runnable interfaceini implement yoluyla kullanmamız gerekir.
 * 
 *  Değişkenlerim Objectinput streami her yerde kullanabilmek için , client frameinde değişiklikler yapabilmek için , isClosed sunucu ile bağlantı kapanmış mı diye kontrol etmek için 
 * Client ise client classına erişebilmek için kullanıldı.
 */
public class MessageListener implements Runnable{
        private ObjectInputStream server;
        private FrmClient frmClient;
        private boolean isClosed;
        private Client client;

	public MessageListener(Client client,FrmClient frmClient,ObjectInputStream server,boolean isClosed) {
		this.client = client;
                this.server = server;
                this.frmClient = frmClient;
                this.isClosed = isClosed;
        }

        
        /**
         * 
         * Sunucu tarafından gelen nesneleri dinleyerek ona göre farklı işlemler yapabilmek için kullandığım metottur.
         * Bir threaddir o yüzden while döngüsü dönerken bir işleme engel olmamaktadır.
         * 
         * read object ile dinlediğimiz nesneleri tiplerine göre farklı işlemler yaptırırız.
         * 
         * eğer message ise chat ekranını yazdırır.
         * 
         * eğer join ise bir kullanıcının geldiğini ve katıldığını chat ekranına yazar.
         * 
         * eğer syncUser ise aktif kullanıcı listesinin senkronize edilmesi gerekir ve gelen arraylist ile client aktif kullanıcı listesi
         * yenilenir bu sayede dinamik bir yapı oluşur.
         * 
         * eğer shutdown ise sunucu ile bağlantı kopmasının ardından mesajlaşma butonlarının ve textboxları deaktif hale getirilir.
         * 
         * eğer namechange ise aynı isimde birinin olduğu ve bu yüzden sunucuya giremediğimizi gösterir. Ve sunucu bizi uzaklaştırır tekrar bağlanmamız gerekir.
         * 
         * eğer pm ise biri özel mesaj yoluyla clienta mesaj göndermiş demektir.
         * 
         */
	@Override
	public void run() {
                //veri geldi
                
		while (!(isClosed)) {
                    try {
                        Message messageObj= new Message();
                        messageObj = (Message) server.readObject();
                        if (messageObj.getType().equals("message")) {
                            frmClient.displayMessage(messageObj.getUsername(),messageObj.getMessage(),messageObj.getDate());
                        }else if(messageObj.getType().equals("join")){
                            frmClient.displayMessage("Server",messageObj.getUsername()+" Adlı kullanıcı bağlandı.",messageObj.getDate());                           
                        }else if(messageObj.getType().equals("syncUser")){
                            frmClient.SyncUsers(messageObj.getList());
                        }else if(messageObj.getType().equals("shutdown")){
                            frmClient.displayMessage("Server","Sunucu kapanıyor.",messageObj.getDate());
                            frmClient.disableChatFeatures();
                        }else if(messageObj.getType().equals("namechange")){
                            frmClient.disconnect("Change your name ERROR : Same name already defined");                           
                        }else if(messageObj.getType().equals("pm")){
                            frmClient.displayMessage(messageObj.getUsername()+" adlı kullanıcıdan :",messageObj.getMessage(),messageObj.getDate());    
                        }
                    } catch (Exception ex) {
                        System.out.println(ex.getLocalizedMessage()+" "+ex.getMessage());
                    }
		}
	}
}
