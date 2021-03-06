/*
 * FrmTerminal.java
 *
 * Created on 2008年5月25日, 上午3:23
 */
package org.joy.starter;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author  Lamfeeling
 */
public class FrmNode extends javax.swing.JFrame {

    /** Creates new form FrmTerminal */
    public FrmNode() {
        initComponents();
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToggleButton1 = new javax.swing.JToggleButton();
        jToggleButton2 = new javax.swing.JToggleButton();
        jToggleButton3 = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jToggleButton5 = new javax.swing.JToggleButton();
        jToggleButton6 = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("节点启动器");
        setLocationByPlatform(true);
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jToggleButton1.setText("分析服务器");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jToggleButton2.setText("蜘蛛");
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });

        jToggleButton3.setText("检索服务器");
        jToggleButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton3ActionPerformed(evt);
            }
        });

        jLabel1.setText("请选择一个节点启动：");

        jLabel2.setText("或者是一个Single服务器：");

        jToggleButton5.setText("PageRank服务器");
        jToggleButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton5ActionPerformed(evt);
            }
        });

        jToggleButton6.setText("查询请求服务器");
        jToggleButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jToggleButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jToggleButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToggleButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jToggleButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButton1)
                    .addComponent(jToggleButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToggleButton5)
                    .addComponent(jToggleButton6)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    Process pS;
private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
    try {
        if (pS == null) {
            try {
                pS = Runner.run("AnalysisNode");
            } catch (AlreadyRunException ex) {
                Logger.getLogger(FrmNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            jToggleButton1.setEnabled(false);
        } else {
            pS.destroy();
            pS = null;
        }
    } catch (RunnerException ex) {//GEN-LAST:event_jToggleButton1ActionPerformed
            Logger.getLogger(FrmNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    Process pV;
private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed
    try {
        if (pV == null) {
            try {
//            ArrayList<String> arg = new ArrayList<String>();
//            arg.add("rmi://"+JOptionPane.showInputDialog("输入蜘蛛中心服务器IP:")+"/1");
                pV = Runner.run("SpiderNode");
            } catch (AlreadyRunException ex) {
                Logger.getLogger(FrmNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            jToggleButton2.setEnabled(false);
        } else {
        }
    } catch (RunnerException ex) {
        Logger.getLogger(FrmNode.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jToggleButton2ActionPerformed
    Process pB;
private void jToggleButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton3ActionPerformed
    try {
        if (pB == null) {
            try {
//            ArrayList<String> arg = new ArrayList<String>();
//            arg.add("rmi://"+JOptionPane.showInputDialog("输入索引中心服务器IP:")+"/1");
                pB = Runner.run("DataNode");
            } catch (AlreadyRunException ex) {
                JOptionPane.showMessageDialog(rootPane, "Already started");
                jToggleButton3.setSelected(false);
                return;
            }
            jToggleButton3.setEnabled(false);
        } else {
        }
    } catch (RunnerException ex) {
        Logger.getLogger(FrmNode.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jToggleButton3ActionPerformed
    Process pQ;
    Process pO;
private void jToggleButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton5ActionPerformed
    // TODO add your handling code here:
    try {
        if (pO == null) {
            try {
                pO = Runner.run("RankServer");
            } catch (AlreadyRunException ex) {
                JOptionPane.showMessageDialog(rootPane, "Already started");
                jToggleButton5.setSelected(false);
                return;
            }
            jToggleButton5.setEnabled(false);
        } else {
        }
    } catch (RunnerException ex) {
        Logger.getLogger(FrmNode.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jToggleButton5ActionPerformed
    Process pQu;
private void jToggleButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton6ActionPerformed
    // TODO add your handling code here:
    try {
        if (pQu == null) {
            try {
                pQu = Runner.run("QueryServer");
            } catch (AlreadyRunException ex) {
                JOptionPane.showMessageDialog(rootPane, "Already started");
                jToggleButton6.setSelected(false);
                return;
            }
            jToggleButton6.setEnabled(false);
        } else {
        }
    } catch (RunnerException ex) {
        Logger.getLogger(FrmNode.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jToggleButton6ActionPerformed

private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
    // TODO add your handling code here:
}//GEN-LAST:event_formWindowLostFocus

private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
    // TODO add your handling code here:\
    File f = new File("c:\\DataNode.DONE");
    if(f.exists()){
        jToggleButton3.setEnabled(false);
    }
    f = new File("c:\\RankServer.DONE");
    if(f.exists()){
        jToggleButton5.setEnabled(false);
    }
    f = new File("c:\\QueryServer.DONE");
    if(f.exists()){
        jToggleButton6.setEnabled(false);
    }
}//GEN-LAST:event_formWindowOpened
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FrmNode().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton3;
    private javax.swing.JToggleButton jToggleButton5;
    private javax.swing.JToggleButton jToggleButton6;
    // End of variables declaration//GEN-END:variables
}
