package com.gazi.batuhansan.server;

import com.gazi.batuhansan.models.Message;
import com.gazi.batuhansan.models.User;
import com.gazi.batuhansan.views.FrmServer;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;


/**
 *
 * @author BatuPC
 */

public class ClientListener implements Runnable{
        private Server server;
	private ObjectInputStream client;
        private FrmServer serverFrame;
        private Socket socket;
        private ArrayList<String> userlist;
        
	public ClientListener(Socket socket,Server server, ObjectInputStream client,FrmServer serverFrame,ArrayList<String> userList) {
		this.server = server;
		this.client = client;
                this.serverFrame = serverFrame;
                this.socket = socket;
                this.userlist = new ArrayList<String>();
                this.userlist = userList;
	}
        
        
	@Override
	public void run() {
            try {
                String message;
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                while(true){
                   Message messageObj = (Message) client.readObject(); 
                   //System.out.println("Veri okundu");                          
                    if(messageObj.getType().equals("join")){
                        User user = new User(messageObj.getUsername(),out);
                        if(uniqueNameCheck(messageObj.getUsername(), server.getUserlist())){
                            this.server.addUser(messageObj.getUsername());
                            
                            this.server.addUserTrackRecord(user);
                            serverFrame.addLogToList("["+formatter.format(messageObj.getDate())+"]"+"[KATILDI]"+socket.getRemoteSocketAddress()+" "+messageObj.getUsername()+" adlı kullanıcı bağlandı.");                                              

                            Message syncList = new Message("syncUser", "sync", this.userlist);

                            this.server.broadcastMessages(syncList);

                            serverFrame.addUserToList(this.userlist);
                        }else{
                            Message nameChangeRequest = new Message("namechange", "change");
                            this.server.PrivateMessage(user,nameChangeRequest);
                        }            
                    }else if(messageObj.getType().equals("message")){
                        serverFrame.addLogToList("["+formatter.format(messageObj.getDate())+"]"+"[LOG]"+messageObj.getUsername()+" mesaj gönderdi.Içerik : ("+messageObj.getMessage()+")");
                    }else if(messageObj.getType().equals("left")){
                        this.server.removeUser(messageObj.getUsername());
                        Message syncList = new Message("syncUser", "sync", this.userlist);    
                        this.server.broadcastMessages(syncList);
                        serverFrame.addLogToList("["+formatter.format(messageObj.getDate())+"]"+"[AYRILDI]"+socket.getRemoteSocketAddress()+" "+messageObj.getUsername()+" adlı kullanıcı ayrıldı.");
                        serverFrame.addUserToList(this.userlist);
                    }else if(messageObj.getType().equals("pm")){
                        serverFrame.addLogToList("["+formatter.format(messageObj.getDate())+"]"+"[PM]"+messageObj.getUsername()+"-->"+messageObj.getTarget()+" özel mesaj gönderdi.Içerik : ("+messageObj.getMessage()+")");
                        this.server.PrivateMessage(this.server.findUserOutput(messageObj.getTarget()), messageObj);
                    }
                    
                    if(!messageObj.getType().equals("pm")){
                        server.broadcastMessages(messageObj);
                    }
                    
                    
                }
            } catch (Exception ex) {
                System.out.println(ex.getLocalizedMessage()+" "+ex.getMessage());
            }    
	}


        
        public boolean uniqueNameCheck(String username,Vector<User> userListClone){
            boolean control =true;
            for (int i = 0; i < userListClone.size(); i++) {
                if (userListClone.get(i).getUsername().equals(username)) {
                    control=false;
                }
            }
            return control;
        }
}
