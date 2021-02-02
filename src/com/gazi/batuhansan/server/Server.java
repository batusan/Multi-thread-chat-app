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

/**
 * 
 * Değişkenlerimiz , port , output stream ve username bilgilerini tuttuğumuz bir User nesnesi tutan vector , string arraylist aktif kullanıcıların isimlerini tutmak için
 * serversocket metotlar arasında farklı yerlerde kullanabilmek için , Frame server ise server uygulaması üzerinden sunucuyu kapatma gibi özelliklerini kullanabilmek için
 * 
 * 
 */
public class Server {
    private int port;
    private Vector<User> clients;
    public ArrayList<String> userLists;
    private ServerSocket server;
    FrmServer serverFrame;

    /**
     * 
     * Constructorumuz da frame tarafından metotları yürütebilmek için frame server referansı ve port alıyoruz.
     * 2 ayrı listemizi de newliyoruz ki kullanabilelim.
     * 
     * @param frmserver
     * @param port 
     */
    public Server(FrmServer frmserver,int port){
        this.port = port;
        this.clients = new Vector<User>();
        this.userLists = new ArrayList<String>();
        serverFrame = frmserver;
    }
    
    /**
     * Run metotu serveri başlatılmasını ve clientların bağlanılmayı açık hale geldiği metottur.
     * 
     * new ServerSocket diyerek sunucumuzu başlatıyoruz ve altında simple date format sayesinde Date tipi verileri düzgün bir şekilde formatlayabiliyoruz.
     * addlogtolist metotum server frame tarafında log listesine log ekleyerek serverin verileri tutmasını sağlıyor.
     * Örneğin ilk log sunucunun hangi portta başlatıldığıdır.
     * 
     * while true içerisine aldığımız ve ardından server.accept dememiz while döndüğü sürece farklı kullanıcılarla iletişime açık olcağımız anlamına geliyor.
     * input streamlerini alıp , object input nesnesi oluşturarak , objeler ile veri alışverişine açık olmamızı sağlıyor.
     * Ardından açtığımız thread bir işlem parçacığı gelen tek bir kullanıcı ile meşgul olmamamızı ve her gelen kullanıcıyla ayrı bir şekilde iletişim kurmamızı sağlayarak
     * multi-thread kavramını kullanmış oluyoruz.
     * 
     * Client listener gelen kullanıcıların mesajlarını dinlediğimiz thread.
     * 
     * 
     * @throws IOException 
     */
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
    
    /**
     * Stop metotu sağlıklı bir şekilde sunucuyu kapatırken aktif kullanıcılara bu sunucunun kapatılıyor olduğunu ve konuşma işlemlerini kesmelerini
     * bildirir. Bu sayede burda ki mesaj ile kullanıcılara ulaşan veri text pane ve gönder butonlarını deaktif ederek boşa mesaj göndermelerini engeller.
     * 
     * server.close diyerek socketimizi kapatmış bulunuyoruz.
     */
    
    public void stop(){
        try{
            broadcastMessages((new Message("shutdown","server","")));
            server.close();
        }catch(Exception ex){
            System.out.println(ex.getLocalizedMessage()+" "+ex.getMessage());
        }  
    }
    
    /**
     * Broadcast message , mesajları aktif clients listesinde bulunan output streamlere tüm mesajları göndermemizi sağlar.
     * for döngüsüyle socket ile bağlı olan her kullanıcıya mesaj gönderebiliriz ve bu sayede tüm kullanıcılar tek bir çatıda haberleşebilirler.
     * 
     * writeObject metodu object gönderir , reset metotu ise write object ile giden veriyi resetleyerek bir sonraki veri gönderme işlemin de eski verilerin veya
     * yanlış verilerin gitmesini engeller.
     * 
     * 
     * @param msg 
     */
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
    
    /**
     * User nesnesi alarak clients vectorü içerisine nesne ekler.
     * 
     * @param user 
     */
    public void addUserTrackRecord(User user){
        clients.add(user);
    }
    
    /**
     * String alarak aktif kullanıcılar listesine bir kullanıcı ekler. Aktif kullanıcıları güncel tutar.
     * 
     * @param username 
     */
    public void addUser(String username){
        userLists.add(username);
    }
    
    /**
     * aktif kullanıcı listesinden bir kullanıcı çıkış yaptığında silinmesini sağlar. Aktif kullanıcıları güncel tutar.
     * @param username 
     */
    public void removeUser(String username){
        userLists.remove(username);
    }
    
    /**
     * Private olan clients vectorunu farklı class içerisinde çağırılmasını sağlar.
     * @return 
     */
    
    public Vector<User> getUserlist(){
        return this.clients;
    }
    
    /**
     * Özel mesajın gitmesini istediğimiz user nesnesini alarak o nesnenin outputstream adresine
     * write object metotuyla gitmesini istediğimiz mesajı gönderiyoruz.
     * 
     *  /pm *kullanıcı adı*
     * @param user
     * @param message 
     */
    public void PrivateMessage(User user,Message message){ 
        try {
            user.getOutputObject().writeObject(message);
            user.getOutputObject().reset();
        } catch (IOException ex) {
           // Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * usernamelerimiz tek olduğu için unique bir halde oldukları için özel mesaj atarken kullandığımız usernamelere sahip
     * user nesnelerini bulabilmek ve o nesneleri return edebilmek için client vectorunden usernamelerimizi aratıyoruz ve eğer bulursak return ediyoruz.
     * 
     * @param username
     * @return 
     */
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
