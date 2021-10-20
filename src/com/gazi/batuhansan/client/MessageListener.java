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

        

	@Override
	public void run() {
                
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
