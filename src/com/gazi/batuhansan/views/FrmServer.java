/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gazi.batuhansan.views;


import com.gazi.batuhansan.server.Server;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author BatuPC
 */
public class FrmServer extends javax.swing.JFrame {

    /**
     * Tanımladığım değişkenler Styled document ve server classı nesnesi
     * Frame üzerinde işlem yaparken server frameinin server tarafında ki metotlara ve verilere ulaşabilmesi için nesneleri oluşturdum.
     */
    StyledDocument doc;
    Server server;
    /**
     *   Constructorımızda server nesnemizi oluşturarak sunucumuzun açılma ve diğer tüm işlevlerini yapabilmek için kullanabilmek için gerektiği classı
       nesnesini oluşturuyoruz.
     *  Serverin aldığı değerler bulunduğumuz frame'e ulaşması için bu frame'i referanslıyoruz ve 2.olarak portu gönderiyoruz.
     *  Altında bir thread yani işlem parçacığı oluşturarak sunucuyu çalıştırdığımız an da GUI nesnelerimizin ve frame'in kendi işlevlerine devam edip
      tıkanmaması için bir paralel yolda giden işlem oluşturuyoruz. Bu sayede server nesne run edildiği anda çalışan while döngüsü yüzünden uygulamamaız donmamış 
      oluyor.
      * Sunucu açılış anında bir hata alırsak onu option pane ile kullanıcıya hatayı yansıtıyoruz.
     */
    public FrmServer() {
        initComponents();
        server = new Server(FrmServer.this,1453);
        new Thread(){
            @Override
            public void run() {
                try {
                     server.run();
                } catch (Exception ex) {
                    // TODO Auto-generated catch block
                    JOptionPane.showMessageDialog(null,ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.start();
        /**
         *  Add window listener ile eğer programı kapatmaya kalkarsak tüm kullanıcılarına sunucunun kapanıyor olduğunu ve 
          istemcilerin konuşma hizmetlerini butonlarını ve text arealarını dondurarak işlem yapamamalarını sağladığım bir metotu devreye sokuyorum.
          Bu sayede kullanıcılar sunucu devre dışı kaldıktan sonra bir mesaj gönderme yetisine sahip olmuyacaklar.
         */
        this.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent evt) {
          stopServer();
        }
       });
    }
    
    /**
     * 
     * Bu metot string parametresi alarak log mesajlarını ekrana yansıtılmasını sağlıyor.
     * Bu metot txtArea_log adlı nesneyi düzenleyerek mesajların gri renkli gelmesini sağlıyor
     * 
     */
    public void addLogToList(String log){    
        try {
            StyledDocument doc = txtArea_log.getStyledDocument();
            
            SimpleAttributeSet join = new SimpleAttributeSet();
            StyleConstants.setAlignment(join, StyleConstants.ALIGN_LEFT);
            StyleConstants.setForeground(join, Color.GRAY);
            //StyleConstants.setBackground(join, Color.GRAY);            
            doc.insertString(doc.getLength(),log+"\n",join);
            doc.setParagraphAttributes(doc.getLength(), 1, join, false);
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(null,ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Parametresi users adlı bir arraylist olan bu metot , dinamik bir şekilde sunucu üzerinde aktif kullanıcılar listesini görülmesini sağlıyorum
     * Her giriş çıkış eylemi yapıldığı an aktif kullanıcılar listesini temizleyerek üstüne yeniden şu an aktif olarak bulunan kullanıcıları sıralıyorum.
     * 
     * @param users 
     */
    public void addUserToList(ArrayList<String> users){
        txtArea_userList.setText(null);
        try {
            StyledDocument doc = txtArea_userList.getStyledDocument();           
            SimpleAttributeSet join = new SimpleAttributeSet();
            StyleConstants.setAlignment(join, StyleConstants.ALIGN_LEFT);
            StyleConstants.setForeground(join, Color.RED);
            for (int i = 0; i < users.size(); i++) {
                doc.insertString(doc.getLength(),users.get(i)+"\n",join);  
            }
            doc.setParagraphAttributes(doc.getLength(), 1, join, false);
            
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtArea_userList = new javax.swing.JTextPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtArea_log = new javax.swing.JTextPane();
        btn_stopServer = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel1.setText("Logs");

        jLabel2.setText("Online Users");

        txtArea_userList.setEditable(false);
        jScrollPane3.setViewportView(txtArea_userList);

        txtArea_log.setEditable(false);
        txtArea_log.setForeground(new java.awt.Color(0, 0, 0));
        txtArea_log.setCaretColor(new java.awt.Color(0, 0, 0));
        jScrollPane1.setViewportView(txtArea_log);

        btn_stopServer.setText("Stop Server");
        btn_stopServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_stopServerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btn_stopServer, javax.swing.GroupLayout.PREFERRED_SIZE, 642, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 642, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel1)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 642, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(81, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_stopServer)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    /**
     * Sunucuyu durdurmak için kullanılan metot , window listener üzerinde ki metot ile aynı metottur.
     * Ve ardından system.exit ile sunucunun çalıştığı programı kapatır.
     * @param evt 
     */
    private void btn_stopServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_stopServerActionPerformed
        stopServer();
        System.exit(0);
    }//GEN-LAST:event_btn_stopServerActionPerformed

    /**
     * Server classı içinde ki stop metotunu çalıştırır ve amacı sunucuyu istemcilere haber vererek durdurmaktır.
     */
    public void stopServer(){
        server.stop();
    }
    
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
            java.util.logging.Logger.getLogger(FrmServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmServer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_stopServer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextPane txtArea_log;
    private javax.swing.JTextPane txtArea_userList;
    // End of variables declaration//GEN-END:variables
}
