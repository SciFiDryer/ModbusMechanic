/*
 * Copyright 2021 Matt Jamesson <scifidryer@gmail.com>.
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
package modbusmechanic;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class UpdateFrame extends javax.swing.JFrame {

    /**
     * Creates new form UpdateFrame
     */
    public static void updateCheck(boolean atLaunch)
    {
        File f = new File(".updatecheckdisabled");
        if (!atLaunch || (!f.exists() && timeToCheck()))
        {
            try
            {
                URLConnection con = new URL("https://modbusmechanic.scifidryer.com/currentversion.txt").openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line = br.readLine();
                if (line != null)
                {
                    if (ModbusMechanic.debug)
                    {
                        System.out.println("Got current version string " + line);
                    }
                    if (versionCheck(line))
                    {
                        new UpdateFrame(atLaunch).setVisible(true);
                    }
                    else
                    {
                        if (!atLaunch)
                        {
                            JOptionPane.showMessageDialog(null, "No updates found.");
                        }
                    }
                }
            }
            catch (Exception e)
            {
                if (ModbusMechanic.debug)
                {
                    e.printStackTrace();
                }
            }
        }
        
    }
    public static boolean timeToCheck()
    {
        File f = new File(".nextupdatetime");
        boolean timeToCheck = false;
        if (f.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(f));
                long checkTime = Long.parseLong(br.readLine());
                if (checkTime < System.currentTimeMillis())
                {
                    timeToCheck = true;
                }
            }
            catch (Exception e)
            {
                if (ModbusMechanic.debug)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            timeToCheck = true;
        }
        if (timeToCheck)
        {
            try
            {
                f.createNewFile();
                PrintWriter pw = new PrintWriter(new FileWriter(f));
                pw.println(System.currentTimeMillis() + 86400000);
                pw.flush();
                pw.close();
            }
            catch (Exception e)
            {
                if (ModbusMechanic.debug)
                {
                    e.printStackTrace();
                }
            }
        }
        return timeToCheck;
    }
    public static boolean versionCheck(String version)
    {
        boolean checkError = false;
        try
        {
            if (version.indexOf(".") > 0)
            {
                String[] currentVerArr = version.split("\\.");
                if (Integer.parseInt(currentVerArr[0]) > ModbusMechanic.versionArray[0])
                {
                    return true;
                }
                if (currentVerArr.length > 1 && Integer.parseInt(currentVerArr[1]) > ModbusMechanic.versionArray[1])
                {
                    return true;
                }
                if (currentVerArr.length > 2 && Integer.parseInt(currentVerArr[2]) > ModbusMechanic.versionArray[2])
                {
                    return true;
                }
            }
            else
            {
                checkError = true;
            }
        }
        catch (Exception e)
        {
            if (ModbusMechanic.debug)
            {
                e.printStackTrace();
            }
            checkError = true;
        }
        if (checkError)
        {
            JOptionPane.showMessageDialog(null, "Failed to check for update.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    public UpdateFrame(boolean updateCheckButton) {
        initComponents();
        dontCheckButton.setVisible(updateCheckButton);
        try
        {
            java.net.URL url = new java.net.URL("https://modbusmechanic.scifidryer.com/currentversion.html");
            contentPane.setPage(url);
        }
        catch (Exception e)
        {
            if (ModbusMechanic.debug)
            {
                e.printStackTrace();
            }
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

        jScrollPane1 = new javax.swing.JScrollPane();
        contentPane = new javax.swing.JEditorPane();
        jPanel2 = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        dontCheckButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        contentPane.setEditable(false);
        contentPane.setContentType("text/html"); // NOI18N
        contentPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
                contentPaneHyperlinkUpdate(evt);
            }
        });
        jScrollPane1.setViewportView(contentPane);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        jPanel2.add(closeButton);

        dontCheckButton.setText("Don't check for updates");
        dontCheckButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dontCheckButtonActionPerformed(evt);
            }
        });
        jPanel2.add(dontCheckButton);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        setBounds(0, 0, 459, 374);
    }// </editor-fold>//GEN-END:initComponents

    private void contentPaneHyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {//GEN-FIRST:event_contentPaneHyperlinkUpdate
        if(evt.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED)
        {
            if(java.awt.Desktop.isDesktopSupported())
            {
                try
                {
                    java.awt.Desktop.getDesktop().browse(evt.getURL().toURI());
                }
                catch (Exception e)
                {
                    if (ModbusMechanic.debug)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }//GEN-LAST:event_contentPaneHyperlinkUpdate

    private void dontCheckButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dontCheckButtonActionPerformed
        try
        {
            File f = new File(".updatecheckdisabled");
            f.createNewFile();
        }
        catch (IOException e)
        {
            if (ModbusMechanic.debug)
            {
                e.printStackTrace();
            }
        }
        dispose();
    }//GEN-LAST:event_dontCheckButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JEditorPane contentPane;
    private javax.swing.JButton dontCheckButton;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
