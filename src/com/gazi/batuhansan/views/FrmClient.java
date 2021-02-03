package com.gazi.batuhansan.views;

import com.gazi.batuhansan.client.Client;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author BatuPC
 */
public class FrmClient extends javax.swing.JFrame {

    /**
     * Tanımladığım değişkenler Styled document , client classı nesnesi , Kullanıcı adı , myColor kullanıcının yazdığı yazıların renkleri
     * otherColor ise karşıdan gelen mesajların renklerini tutmaktadır.
     * Frame üzerinde işlem yaparken client frameinin client classı tarafında ki metotlara ve verilere ulaşabilmesi için nesneleri oluşturdum.
     */
    StyledDocument doc;
    Client client;
    String myUsername;
    Color myColor=Color.GRAY;
    Color otherColor=Color.GRAY;
    /**
     * Constructorumuz da mesaj gönderme butonunu devre dışı bırakarak kullanıcının bağlantı kurmadan mesaj atmasını engelliyorum.
     * add window listener ile kullanıcının sağlıklı bir şekilde sunucudan ayrılmasını ve sunucunun da bundan haberdar olmasını sağlıyoruz.
     * ve programı güvenli bir şekilde kapatıyoruz.
     */
    public FrmClient() {
        initComponents();
        btn_submit.setEnabled(false);
        
        this.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent evt) {
          disconnect("kapat");
        }
       });
    }
    /**
     * Bu metotta programımızı kapatmak için ve üstte açıkladığım gibi (bakınız : 33.satır) sağlıklı bir çıkış yapmak için bu metoda ihtiyacımız var.
     *  EKSTRA : Ekstra bir durum var bu metot için , Client sunucuya girerken bir hatayla karşılaşırsa örneğin aynı isime sahip kullanıcılar aynı anda
      sunucu da bulunamıyor ve bunu engellemek için sunucuya girilirken bir kontrol gerçekleştiriyorum.
     *  Bu kontrol sunucu tarafından clientın sunucuya girmesinin mümkün olmadığını ve programı kapatmasını tetikliyor. Error parametresi burdan geliyor eğer ki
      kullanıcı zorla kapattırılmak zorunda kalırsa "kapat" stringi harici bir değer geldiği zaman kullanıcıya neden giremediğinin sebebini vermiş bulunuyor.
      * 
      * Ayrıca eğer sunucuyla bağlantı kurmadıysak , programdan çıkış yapmaya kalktığımızda bağlantımız olmayan bir sunucudan çıktığımızı haber veremiceğimiz için
       bağlantı kurmadan önce çıkış yaparsak program sadece system.exit olarak çıkıyor.
     * @param error 
     */
    public void disconnect(String error) {
        if(!error.equals("kapat")){
            JOptionPane.showMessageDialog(null,error, "Error", JOptionPane.ERROR_MESSAGE);
        }
        if(btn_submit.isEnabled()){
            client.stop();
            
        } 
        System.exit(0);
    }
    
    /**
     * Eğer ki sohbet anında sunucuda bir aksaklık olursa veya sunucu uygulaması üzerinden kapatılma işlemi başlatılırsa 
     * istemcilerin sohbet ekranına bir şey göndermeye çalışmamaları için sohbet butonunu ve text fieldını devre dışı bırakıyoruz.
     */
    public void disableChatFeatures(){
        txtArea_MessageBox.setEnabled(false);
        btn_submit.setEnabled(false);
    }   
    
    /**
     * Kullanıcının giriş yapmak istediği ismi get text ile aldıktan sonra connect server metoduyla , client nesnemizi oluşturarak 
     * bu clientı run ettikten sonra sunucuya bağlantı kurmuş oluruz. Client nesnesini thread dışında tutmamızın sebebi olası bir den fazla
     * thread başlatarak sunucu ile istemci arasında birden fazla veri yolu oluşturarak mesajların gelmesi gereken rotalardan sapmasını engellemek.
     * 
     * Ayrıca bağlan tuşunu bağlandıktan sonra kapalı ve textini bağlanıldı hale getiriyoruz.
     * Ve nihayet kullanıcının mesaj gönderebilmesi için gönder butonunu aktif hale getiriyoruz.
     * 
     * 
     * @param username 
     */
    public void connectServer(String username){
        client = new Client(this,username,"127.0.0.1", 1453);
        new Thread(){
            @Override
            public void run() {
                try {
                    client.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }
        }.start();
        button_connect.setEnabled(false);
        button_connect.setText("Bağlanıldı");
        btn_submit.setEnabled(true);
    }
    
    /**
     * Sync users metodu parametresinde görüldüğü gibi bir arraylist alıyor ve bu alınan arraylisti önce
     * users text panesini temizleyerek ardından aldığımız userlist arraylistini tek teker yazdırıyor.
     * 
     * kullanıcıları dinamik bir şekilde giriş ve çıkışlarını görmemizi sağlayan metot.
     * 
     * @param userlist 
     */
    public void SyncUsers(ArrayList<String> userlist){
        txtArea_users.setText(null);
        for (int i = 0; i < userlist.size(); i++) {       
             txtArea_users.append(userlist.get(i)+"\n");
        }      
    }
    
    /**
     * Bu uygulamanın ana metotlarından biri olan , gelen mesajları gönderdiği kişiye bağlı olarak,
     * sağ veya mesajın sol da gözükmesini sağlayan , text pane üzerinde stil olarak düzenlemeleri yaptığımız
     * bir metottur.
     * 
     * Date Format sayesinde new Date ile oluşturduğum date nesnelerini düzgün bir formatta yazdırabiliyorum.
     * Gelen username ile global parametremizi kontrol ederek , sunucudan gelen mesajın bizim gönderdiğimiz bir mesaj olup olmadığını kontrol ederek
     * sağ veya solda yazdırır. Bizim göndermeye çalıştığımız mesajlar direkt olarak bizim ekranımıza yazdırılmaması , sunucu tarafından kontrol edildikten sonra
     * bizim ekranımıza yazdırılması , bu konuda işi kolaylaştırmıştır.
     * 
     * Not : Date Format sayesinde new Date ile oluşturduğum date nesnelerini düzgün bir formatta yazdırabiliyorum.
     * 
     * @param username
     * @param message 
     */
    public void displayMessage(String username,String message,Date date){
        try {
            StyledDocument doc = txtArea_Chat.getStyledDocument();
            
            SimpleAttributeSet left = new SimpleAttributeSet();
            StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
            StyleConstants.setForeground(left, otherColor);

            SimpleAttributeSet right = new SimpleAttributeSet();
            StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
            StyleConstants.setForeground(right, myColor);
            
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            
            if(!username.equals(myUsername)){
                doc.setParagraphAttributes(doc.getLength(), 1, left, false);
                
                doc.insertString(doc.getLength(), username+" "+formatter.format(date)+":\n"+message+"\n", left );

            }else{
                doc.setParagraphAttributes(doc.getLength(), 1, right, false);
                
                doc.insertString(doc.getLength(), username+" "+formatter.format(date)+":\n"+message+"\n", right );
            }
            
            //StyleConstants.setBackground(join, Color.GRAY);
            
            
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(null,ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        btn_colorPick2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btn_colorPick1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        input_username = new javax.swing.JTextField();
        lbl_username = new javax.swing.JLabel();
        button_connect = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtArea_Chat = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtArea_MessageBox = new javax.swing.JTextArea();
        btn_submit = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtArea_users = new javax.swing.JTextArea();
        lbl__users = new javax.swing.JLabel();
        btn_exit = new javax.swing.JButton();
        btn_colors = new javax.swing.JButton();

        jDialog1.setResizable(false);

        btn_colorPick2.setText("Renk Seç");
        btn_colorPick2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_colorPick2ActionPerformed(evt);
            }
        });

        jLabel2.setText("Gelen Mesaj renkleri : ");

        jLabel3.setText("Gönderilen Mesaj renkleri : ");

        btn_colorPick1.setText("Renk Seç");
        btn_colorPick1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_colorPick1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Default renkleri gridir.");

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog1Layout.createSequentialGroup()
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_colorPick1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jDialog1Layout.createSequentialGroup()
                                .addGap(71, 71, 71)
                                .addComponent(jLabel4))))
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(btn_colorPick2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(33, 33, 33))
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(btn_colorPick1))
                .addGap(18, 18, 18)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_colorPick2)
                    .addComponent(jLabel2))
                .addGap(58, 58, 58))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(" Mesaj/Sohbet Uygulamasına Hoşgeldiniz");

        input_username.setText("Kullanıcı Adınız");

        lbl_username.setText("Kullanıcı Adı :");

        button_connect.setText("Bağlan");
        button_connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_connectActionPerformed(evt);
            }
        });

        txtArea_Chat.setEditable(false);
        jScrollPane1.setViewportView(txtArea_Chat);

        txtArea_MessageBox.setColumns(20);
        txtArea_MessageBox.setRows(5);
        jScrollPane2.setViewportView(txtArea_MessageBox);

        btn_submit.setText("Gonder");
        btn_submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_submitActionPerformed(evt);
            }
        });

        txtArea_users.setEditable(false);
        txtArea_users.setColumns(20);
        txtArea_users.setRows(5);
        jScrollPane3.setViewportView(txtArea_users);

        lbl__users.setText("Aktif Kullanıcılar");

        btn_exit.setText("Çıkış");
        btn_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_exitActionPerformed(evt);
            }
        });

        btn_colors.setText("Chat renkleri");
        btn_colors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_colorsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_colors, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbl_username)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(input_username, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(button_connect, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_submit)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(lbl__users)
                                        .addGap(86, 86, 86))))
                            .addComponent(btn_exit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_colors))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(input_username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_username)
                    .addComponent(button_connect)
                    .addComponent(lbl__users))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_submit, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                        .addComponent(btn_exit, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Textfield üzerinden aldığımız kullanıcı adı ile sunucu başlatma metodunu çalıştırdığımız bir actiondur.
     * 
     * @param evt 
     */
    private void button_connectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_connectActionPerformed
        myUsername = input_username.getText();
        connectServer(myUsername);
    }//GEN-LAST:event_button_connectActionPerformed

    /**
     * Mesaj gönderme işlemini yaptığımız , text areadan aldığımız text ile sunucuya mesajı gönderdiğimiz metotu çalıştıran actiondur.
     * ayrıca her mesajdan sonra mesajlaşma uygulamalarına daha çok benzemesi için , bir önceki attığımız mesaj text areamızdan silinir.
     * 
     * @param evt 
     */
    private void btn_submitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_submitActionPerformed
        client.SendMessage(txtArea_MessageBox.getText());
        txtArea_MessageBox.setText(null);
    }//GEN-LAST:event_btn_submitActionPerformed

    /**
     * Çıkış yap butonumuz , sağlıklı bir çıkış yapabilmek için disconnect metotu ile uygulamamızı kapatıyoruz ve bu
     * işlemi çalıştıran metotumuzdur.
     * 
     * @param evt 
     */
    private void btn_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_exitActionPerformed
        disconnect("kapat");       
    }//GEN-LAST:event_btn_exitActionPerformed

    /**
     * Chati kontrol eden renkleri seçebilmek için bir popup ekrandır , üzerinde 2 tane buton olan ve
     * bu butonların 2 farklı chat rengini ayarladığı ekrandır.
     * 
     * @param evt 
     */
    private void btn_colorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_colorsActionPerformed
        jDialog1.setPreferredSize(new Dimension(281, 200));
        jDialog1.pack();
        jDialog1.setVisible(true);
    }//GEN-LAST:event_btn_colorsActionPerformed

    /**
     * Bu buton bir renk seçme ekranı getirerek , bir renk seçmemize olanak sağlar
     * bu seçtiğimiz renk bizim chat ekranımızda yazdıklarımız bu renge sahip olur.
     *
     * @param evt 
     */
    private void btn_colorPick1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_colorPick1ActionPerformed
        Color initialcolor = Color.BLACK;
        Color colorPick = (Color) JColorChooser.showDialog(this, "Renk Seç", initialcolor);
        btn_colorPick1.setBackground(colorPick);
        myColor = colorPick;
    }//GEN-LAST:event_btn_colorPick1ActionPerformed

     /**
     * Bu buton bir renk seçme ekranı getirerek , bir renk seçmemize olanak sağlar
     * bu seçtiğimiz renk karşıdan gelen mesajlar için chat ekranımızda yazılanlar bu renge sahip olur.
     *
     * @param evt 
     */
    private void btn_colorPick2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_colorPick2ActionPerformed
        Color initialcolor = Color.BLACK;
        Color colorPick = (Color) JColorChooser.showDialog(this, "Renk Seç", initialcolor);
        btn_colorPick2.setBackground(colorPick);
        otherColor = colorPick;
    }//GEN-LAST:event_btn_colorPick2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmClient().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_colorPick1;
    private javax.swing.JButton btn_colorPick2;
    private javax.swing.JButton btn_colors;
    private javax.swing.JButton btn_exit;
    private javax.swing.JButton btn_submit;
    private javax.swing.JButton button_connect;
    private javax.swing.JTextField input_username;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lbl__users;
    private javax.swing.JLabel lbl_username;
    private javax.swing.JTextPane txtArea_Chat;
    private javax.swing.JTextArea txtArea_MessageBox;
    private javax.swing.JTextArea txtArea_users;
    // End of variables declaration//GEN-END:variables
}
