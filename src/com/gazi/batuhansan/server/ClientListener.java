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

/**
 * 
 * Thread yapısını kullanabilmek için thread sınıfını kalıtım yoluyla veya runnable interfaceini implement yoluyla kullanmamız gerekir.
 * 
 * Değişkenlerim , Server classına referans gösterebilmek için server , gelen mesajları ve bu socket ile aramızda ki iletişim ağını dinleyebilmek için Object İnput stream değişkeni oluşturdum.
 * Servera yardımcı bir classın server framei üzerinde işlem yapabilmesi için server frame e erişebilceği bir referans olması gerekir o yüzden ekledim.
 * userlist ise aktif bir şekilde kullanıcıları tek bir veri kaynağından tüm kullanıcılara iletmek için tek bir yerden yönetilmesi gerekir , bir kullanıcı giriş yaptığında bu listeyi güncelleyerek
 * hangi kullanıcının girdiğini tüm istemcilere göndermek için arraylist içinde tüm aktif kullanıcıları tutuyorum.
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
        
        /**
         * Sunucuya bağlanan socketlerin bir thread yardımıyla dinlenmesi gerekir ki kesintisiz 
         * ve diğer işlemleri engellemeden bir dizi eylemler gerçekleşsin.
         * run metotu bir object output stream oluşturarak server socketa bağlanan 
         * her socketin geri dönüş alabileceği bir yol olması için , o socketlere tekrardan mesaj atabilmemiz için
         * output streamleri kullanıyoruz.
         * 
         * While döngüsü her nesne geldiğinde karşılayabilmemiz için döngü içerisinde sürekli dönüyor.
         * Döngü içerisinde bir nesne okuyarak ardından o nesneyi cast ederek bir message nesnesi haline getiriyor.
         * Message nesnesi bir tipe , mesaj içeriğine ve bir sürü fielde sahiptir.
         * 
         * Tipe sahip olması gelen mesajların hangi işlemlere tabi tutulcağını kolaylaştırmıştır.
         * Bir kullanıcı sisteme giriş yaptığı an sunucuya "join" tipinde bir obje yolluyor ve bu kullanıcı ismini 
         * aktif kullanıcılar arasına eklemeden önce bu isim şu anda aktif bir kullanıcı ismine sahip mi
         * diye kontrol ediyoruz.
         * 
         * Eğer sahipse o message nesnesini gönderen sockete bir geri dönüş mesajı yollayarak sunucudan isim değişikliği nedeniyle
         * atıldığını bildiriyoruz ve uzaktan bağlantısını kopararak uygulamaya yeniden bağlanmasını istiyoruz.
         * 
         * Eğer sahip değilse , ismini aktif kullanıcılar listesine ekleyerek mesajları alması için gönderen kişinin output streamini saklayarak sonradan o kullanıcıya
         * ulaşabiliyoruz.
         * 
         * eğer bir mesaj tipi geldiyse, bu mesajı loglarımıza biri mesaj attı diye logluyoruz.
         * 
         * eğer (left) ayrıldı mesaj tipi geldiyse , kullanıcıyı aktif kullanıcılar arasından kaldırıyoruz ve yeni listeyi tekrardan istemcilere gönderiyoruz ki 
         * aktif kullanıcı sayısını dinamik olarak görsünler.
         * 
         * eğer pm tipi geldiyse , bu privatemessage metotuyla spesifik bir output streame gönderilerek özel mesajla birbirlerine ulaşmalarını sağlıyoruz.
         * 
         * en son tüm kullanıcıdan gelen mesajları kullanıcılara da tekrar yayınlıyoruz.
         */

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
        
        /**
         * https://stackoverflow.com/questions/2986296/what-are-the-differences-between-arraylist-and-vector
         * Üstte ki link üzerine arraylist yerine bazı noktalarda vector kullandım.
         * Vector kullanmamın sebebi aynı anda 2,3 farklı threadin aynı collection üzerinde değişiklik yapmalarına açık olmasıydı.
         * 
         * Bu metot üzerinde bir kullanıcı adı ve bir vector alarak , aldığımız vector içerisinde ki kullanıcı adlarını kontrol ederek
         * sunucuya katılmaya çalışan bir kullanıcının ismiyle , aktif kullanıcılar arasında ki birinin isminin aynı olmasını engelleyerek
         * sunucuda isimlerin unique olmasını sağlamış oluyoruz.
         * 
         * 
         * @param username
         * @param userListClone
         * @return 
         */
        
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
