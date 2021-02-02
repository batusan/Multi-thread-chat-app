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


/**
 * Parametrelerimiz 
 * Bağlanıcağımız ip adresi , port bağlanıcağımız port adresimiz , string username kullanıcı adımız , client bağlantı kurmamızı sağlayan socket nesnesini temsil eder.
 * FrmClient ise client classı üzerinden frame üzerinde yapmamız gereken değişiklikleri uygulamamız için kullancağımız referansın değişkeni.
 * İnput ve output streamler , classın farklı noktalarında bu değerleri kullanabilmemiz için metot dışına çıkararak bir global değişkene atıyoruz.
 * isClosed sunucu ile hala bağlantı içinde mi olduğumuzun durumunu kontrol ettiğimiz değişken
 * 
 * 
 * 
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

        /**
         * 
         * Socket ile sunucumuza bağlanıyoruz.
         * Kurduğumuz bağlantıların output streamlerini ve input streamlerini nesnelerin gönderilebilceği 
         * objectinput a çeviriyoruz.
         * 
         * ve yeni bir işlem parçacığı oluşturuyoruz. işlem parçacığı , bir metot veya bir iş sırasında farklı işlemlere paralel olarak
         * yürütülmesi gereken işlemleri kitlemeden yürütülmesini sağlar.Bu sayede Client metotlarından while döngüsü dönerken farklı metotlar çağırarak kullanabiliyoruz.
         * 
         * ardından bağlandığımız an join tipinde bir message objesi göndererek sunucuya katıldığımızı belirtiyoruz.
         * 
         * 
         */
        public void run() throws UnknownHostException, IOException {
		// connect client to server
		client = new Socket(host, port);
                OutputStream outputStream = client.getOutputStream();
                objOutStream = new ObjectOutputStream(outputStream);
		                
                InputStream inputStream = client.getInputStream();
                objInStream = new ObjectInputStream(inputStream);
		// create a new thread for server messages handling
		new Thread(new MessageListener(this,clientFrm,objInStream,isClosed)).start();

		// get nickname
                objOutStream.writeObject(new Message("join",nickname));
                System.out.println("Başarıyla bağlandı!");
                

	}
        
        /**
         * Durdurma işlemlerini yaptığımız metot, durdurma işlemini yaparken sunucununda bundan haberdar olması için
         * sunucuya left message objesi göndererek kullanıcının ayrıldığını belirtiyoruz ve socketleri streamleri kapatıyorz.
         * 
         * Ardından system.exit ile programı sonlandırıyoruz.
         * 
         */
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
        
        /**
         * 
         * Mesaj gönderme metotumuz , her türlü mesajı , özel mesajı gönderen metodumuz.
         * Output stream .write object sayesinde verileri sunucuya gönderdiğimiz metottur.
         * 
         * eğer /kapat yazarsınız sunucuya ,bunu okuyan client sunucuya mesaj göndermez ve stop metotu devreye girer.
         * 
         * eğer /pm *kullanıcı adı* *mesaj* şeklinde girerseniz girdiğiniz *kullanıcı adı* 'nı ve *mesaj* ı pm tipinde gönderir.
         * 
         * pm gönderirken kullanıcı adını ayrı bir şekilde almak için tüm mesaj satırını boşluklara göre ayırarak /pm den sonraki ilk kelimeyi username e karşılık
         * alıyorum ve bu sayede hedef kullanıcı adına karşılık bir kişi adı almış oluruz.
         * 
         * @param message 
         */
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
