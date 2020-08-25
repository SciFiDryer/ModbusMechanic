/*
 * Copyright 2020 Matt Jamesson <scifidryer@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package modbusmechanic.bridge;
import modbusmechanic.bridge.drivers.ModbusProtocolHandler;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;
import javax.swing.filechooser.*;
import java.beans.*;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class BridgeFrame extends javax.swing.JFrame {

    /**
     * Creates new form BridgeFrame
     */
    BridgeManager manager = null;
    
    public BridgeFrame(BridgeManager aManager) {
        manager = aManager;
        initComponents();
    }
    public BridgeFrame()
    {
    }
    public void setManager(BridgeManager aManager)
    {
        manager = aManager;
    }
    public BridgeManager getManager()
    {
        return manager;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        addMappingButton = new javax.swing.JButton();
        startBridgeButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        restIntervalField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        saveConfig = new javax.swing.JMenuItem();
        loadConfig = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanel1.add(jSeparator1);

        addMappingButton.setText("Add mapping");
        addMappingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMappingButtonActionPerformed(evt);
            }
        });
        jPanel1.add(addMappingButton);

        startBridgeButton.setText("Start Bridge");
        startBridgeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startBridgeButtonActionPerformed(evt);
            }
        });
        jPanel1.add(startBridgeButton);

        getContentPane().add(jPanel1);

        jLabel2.setText("Rest interval");
        jPanel3.add(jLabel2);

        restIntervalField.setColumns(4);
        restIntervalField.setText("1000");
        jPanel3.add(restIntervalField);

        jLabel1.setText("ms");
        jPanel3.add(jLabel1);

        getContentPane().add(jPanel3);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));
        getContentPane().add(jPanel2);

        jMenu1.setText("File");

        saveConfig.setText("Save config to file");
        saveConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveConfigActionPerformed(evt);
            }
        });
        jMenu1.add(saveConfig);

        loadConfig.setText("Load config from file");
        loadConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadConfigActionPerformed(evt);
            }
        });
        jMenu1.add(loadConfig);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addMappingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMappingButtonActionPerformed
        addMapping();
    }//GEN-LAST:event_addMappingButtonActionPerformed

    public void addMapping()
    {
        JPanel mainPanel = new JPanel();
        JPanel incomingPanel = new JPanel();
        JPanel incomingDataSettings = new JPanel();
        JPanel outgoingPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        
        incomingPanel.setLayout(new BoxLayout(incomingPanel, BoxLayout.Y_AXIS));
        JComboBox incomingDataSelector = new JComboBox();
        JComboBox outgoingDataSelector = new JComboBox();
        
        
        JPanel incomingDataSource = new JPanel();
        
        incomingDataSettings.setLayout(new BoxLayout(incomingDataSettings, BoxLayout.Y_AXIS));
        
        JLabel incomingDataLabel = new JLabel("Incoming Data Source");
        incomingDataSource.add(incomingDataLabel);
        incomingDataSource.add(incomingDataSelector);
        incomingPanel.add(incomingDataSource);
        incomingPanel.add(incomingDataSettings);
        
        outgoingPanel.setLayout(new BoxLayout(outgoingPanel, BoxLayout.Y_AXIS));
        BridgeEntryContainer entryContainer = new BridgeEntryContainer();
        entryContainer.incomingSettings.add(new ArrayList());
        entryContainer.incomingSettings.get(0).add(incomingDataSelector);
        modbusmechanic.bridge.drivers.DriverMenuHandler dmh = new modbusmechanic.bridge.drivers.DriverMenuHandler(incomingDataSelector, outgoingDataSelector, this, entryContainer, incomingDataSettings, outgoingPanel);
        incomingDataSelector.addActionListener(dmh);
        manager.bridgeMapList.add(entryContainer);
        manager.dmh = dmh;
        
        
        mainPanel.add(incomingPanel);
        mainPanel.add(new JSeparator(JSeparator.VERTICAL));
        mainPanel.add(outgoingPanel);
        mainPanel.add(new JSeparator(JSeparator.VERTICAL));
        JButton deleteButton = new JButton("Delete mapping");
        mainPanel.add(deleteButton);
        deleteButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                jPanel2.remove(mainPanel);
                manager.bridgeMapList.remove(entryContainer);
                pack();
            }
        });
        jPanel2.add(mainPanel);
        pack();
    }
    private void startBridgeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startBridgeButtonActionPerformed
        if (!manager.isRunning)
        {
            try
            {
                manager.restTime = Integer.parseInt(restIntervalField.getText());
            }
            catch (Exception e)
            {
                if (modbusmechanic.ModbusMechanic.debug)
                {
                    e.printStackTrace();
                }
                manager.restTime = 1000;
            }
            manager.constructSettingsFromGui();
            manager.startBridge();
            startBridgeButton.setText("Stop bridge");
        }
        else
        {
            manager.shutdown();
            startBridgeButton.setText("Start bridge");
        }
    }//GEN-LAST:event_startBridgeButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (manager.isRunning)
        {
            manager.shutdown();
        }
    }//GEN-LAST:event_formWindowClosing

    private void saveConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveConfigActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CFG file", "cfg");
        chooser.setFileFilter(filter);
        int status = chooser.showSaveDialog(this);
        if (status == JFileChooser.APPROVE_OPTION)
        {
            java.io.File f = chooser.getSelectedFile();
            String filename = f.getName();
            if (!filename.substring(filename.length()-4).equalsIgnoreCase(".cfg"))
            {
                f = new File(f.getParent() + File.separator + f.getName() + ".cfg");
            }
            try
            {
                XMLEncoder xmle = new XMLEncoder(new FileOutputStream(f));
                xmle.writeObject(manager.mappingRecords);
                xmle.close();
            }
            catch (Exception e)
            {
                if (modbusmechanic.ModbusMechanic.debug)
                {
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_saveConfigActionPerformed

    private void loadConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadConfigActionPerformed
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CFG file", "cfg");
        chooser.setFileFilter(filter);
        int status = chooser.showOpenDialog(this);
        if (status == JFileChooser.APPROVE_OPTION)
        {
            java.io.File f = chooser.getSelectedFile();
            String filename = f.getName();
            if (!filename.substring(filename.length()-4).equalsIgnoreCase(".cfg"))
            {
                f = new File(f.getParent() + File.separator + f.getName() + ".cfg");
            }
            try
            {
                XMLDecoder xmld = new XMLDecoder(new FileInputStream(f));
                manager.mappingRecords = (ArrayList<BridgeMappingRecord>)xmld.readObject();
                xmld.close();
                manager.restoreGuiFromFile();
            }
            catch (Exception e)
            {
                if (modbusmechanic.ModbusMechanic.debug)
                {
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_loadConfigActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addMappingButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JMenuItem loadConfig;
    private javax.swing.JTextField restIntervalField;
    private javax.swing.JMenuItem saveConfig;
    private javax.swing.JButton startBridgeButton;
    // End of variables declaration//GEN-END:variables
}
