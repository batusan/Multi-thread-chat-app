package com.gazi.batuhansan.server;

import com.gazi.batuhansan.models.Message;
import com.gazi.batuhansan.models.User;
import com.gazi.batuhansan.views.FrmServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;




/**
 *
 * @author BatuPC
 */


public class Server {
    private int port;
    private Vector<User> clients;
    public ArrayList<String> userLists;
    private ServerSocket server;
    FrmServer serverFrame;


    public Server(FrmServer frmserver,int port){
        this.port = port;
        this.clients = new Vector<User>();
        this.userLists = new ArrayList<String>();
        serverFrame = frmserver;
    }
    
    
    public void run() throws IOException {
		server = new ServerSocket(port);
                
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                serverFrame.addLogToList("["+formatter.format(new Date())+"]"+"Port "+port+" aktif.");
		while (true) {
			
			Socket client = server.accept();
			
                        InputStream inputStream = client.getInputStream();
                        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			
			new Thread(new ClientListener(client,this, objectInputStream,this.serverFrame,this.userLists)).start();
		}
    }
    

    
    public void stop(){
        try{
            broadcastMessages((new Message("shutdown","server","")));
            server.close();
        }catch(Exception ex){
            System.out.println(ex.getLocalizedMessage()+" "+ex.getMessage());
        }  
    }
    

    public void broadcastMessages(Message msg) {
	for (User user : this.clients) {
            try {              
                user.getOutputObject().writeObject(msg);
                user.getOutputObject().reset();
            } catch (Exception ex) {
                System.out.println(ex.getLocalizedMessage()+" "+ex.getMessage());
            }
	}
    }     

    public void addUserTrackRecord(User user){
        clients.add(user);
    }
    

    public void addUser(String username){
        userLists.add(username);
    }
    

    public void removeUser(String username){
        userLists.remove(username);
    }
    

    
    public Vector<User> getUserlist(){
        return this.clients;
    }


    public void PrivateMessage(User user,Message message){ 
        try {
            user.getOutputObject().writeObject(message);
            user.getOutputObject().reset();
        } catch (IOException ex) {
           // Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public User findUserOutput(String username){
        User user=null;
        for (int i = 0; i < this.clients.size(); i++) {
            if (this.clients.get(i).getUsername().equals(username)) {
                user = this.clients.get(i);
            }
        }
        return user;
    }
}
