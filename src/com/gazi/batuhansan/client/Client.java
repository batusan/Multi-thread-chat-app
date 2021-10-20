/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gazi.batuhansan.client;

import com.gazi.batuhansan.models.Message;
import com.gazi.batuhansan.views.FrmClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


/**
 *
 * @author BatuPC
 */


public class Client {
        private String host;
	private int port;
	private String nickname;
        private Socket client;
        private FrmClient clientFrm;
        private ObjectOutputStream objOutStream;
        private ObjectInputStream objInStream;
        private boolean isClosed=false;

	public Client(FrmClient clientFrm,String nickname,String host, int port) {
                this.clientFrm = clientFrm;
		this.host = host;
		this.port = port;
                this.nickname = nickname;
	}

   
        public void run() throws UnknownHostException, IOException {
		// connect client to server
		client = new Socket(host, port);
                OutputStream outputStream = client.getOutputStream();
                objOutStream = new ObjectOutputStream(outputStream);
		                
                InputStream inputStream = client.getInputStream();
                objInStream = new ObjectInputStream(inputStream);
		
		new Thread(new MessageListener(this,clientFrm,objInStream,isClosed)).start();

		// get nickname
                objOutStream.writeObject(new Message("join",nickname));
                System.out.println("Başarıyla bağlandı!");
                

	}
        

        public void stop(){
            try{
                objOutStream.writeObject(new Message("left",nickname,""));
                client.close();
                objOutStream.close();
                objInStream.close();
                isClosed = client.isClosed();
                System.out.println("İstemci sunucudan ayrıldı +"+client.isClosed());   
                System.exit(0);
            }catch(Exception ex){
                System.out.println(ex.getLocalizedMessage()+" "+ex.getMessage());
            }
                    
        }
        
     

        public void SendMessage(String message) {
            try {
                if(message.equals("/kapat")){
                    stop();
                }else if(message.startsWith("/pm")){
                    ArrayList<String> wordArrayList = new ArrayList<String>();
                    for (String word : message.split(" ")) {
                        wordArrayList.add(word);
                    }
                    String newMessage = message;
                    newMessage = newMessage.replace("/pm", "");
                    System.out.println(wordArrayList.get(1));
                    objOutStream.writeObject(new Message("pm",nickname,wordArrayList.get(1),newMessage));
                }else{
                    objOutStream.writeObject(new Message("message",nickname,message));
                }                 
            } catch (Exception ex) {
                System.out.println(ex.getLocalizedMessage()+" "+ex.getMessage());
            }
        }
}
