/*
 * Copyright 2019 Matt Jamesson <scifidryer@gmail.com>.
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

/**
 *
 * @author matthew
 */
public class BitsFrame extends javax.swing.JFrame {

    javax.swing.JTextField targetField = null;
    byte[] responseBytes = null;
    javax.swing.JCheckBox[] bitCheckbox = null;
    public BitsFrame() {
        initComponents();
    }
    public BitsFrame(javax.swing.JTextField aTargetField, int numBits)
    {
        targetField = aTargetField;
        initComponents();
        constructBits(numBits);
        pack();
    }
    public BitsFrame(byte[] aResponseBytes)
    {
        responseBytes = aResponseBytes;
        initComponents();
        constructBits(responseBytes.length*8);
        pack();
        setAllBitsEnabled(false);
    }
    public void calculateBytes()
    {
        uncheckAllBits();
        int bitNum = 0;
        for (int byteNum = responseBytes.length-1; byteNum >= 0; byteNum = byteNum - 1)
        {
            for (int i = 0; i < 8; i++)
            {
                if ((responseBytes[byteNum] >> i & 1) == 1)
                {
                    bitCheckbox[bitNum].setSelected(true);
                }
                bitNum++;
            }
        }
    }
    public void uncheckAllBits()
    {
        for (int i = 0; i < bitCheckbox.length; i++)
        {
            bitCheckbox[i].setSelected(false);
        }
    }
    public void setAllBitsEnabled(boolean enabled)
    {
        for (int i = 0; i < bitCheckbox.length; i++)
        {
            bitCheckbox[i].setEnabled(enabled);
        }
    }
    public void calculateInt16()
    {
        int int16Value = 0;
        for (int i = 0; i < bitCheckbox.length; i++)
        {
            if (bitCheckbox[i].isSelected())
            {
                int16Value = (int)(int16Value + Math.pow(2, i));
            }
        }
        targetField.setText(int16Value + "");
    }
    public void calculateInt32()
    {
        long int32Value = 0;
        for (int i = 0; i < bitCheckbox.length; i++)
        {
            if (bitCheckbox[i].isSelected())
            {
                int32Value = (long)(int32Value + Math.pow(2, i));
            }
        }
        targetField.setText(int32Value + "");
    }
    public void displayInt16(int int16Value)
    {
        uncheckAllBits();
        for (int i = 0; i < bitCheckbox.length; i++)
        {
            if ((int16Value >> i & 1) == 1)
            {
                bitCheckbox[i].setSelected(true);
            }
        }
    }
    public void displayInt32(long int32Value)
    {
        uncheckAllBits();
        for (int i = 0; i < bitCheckbox.length; i++)
        {
            if ((int32Value >> i & 1) == 1)
            {
                bitCheckbox[i].setSelected(true);
            }
        }
    }
    public void constructBits(int numBits)
    {
        
        bitCheckbox = new javax.swing.JCheckBox[numBits];
        for (int wordBits = 0; wordBits < numBits; wordBits = wordBits + 16)
        {
            javax.swing.JPanel parentPanel = new javax.swing.JPanel();
            javax.swing.JLabel wordLabel = new javax.swing.JLabel("Word " + ((wordBits/16)+1));
            parentPanel.setLayout(new javax.swing.BoxLayout(parentPanel, javax.swing.BoxLayout.X_AXIS));
            parentPanel.add(wordLabel);
            for (int i = 15; i >= 0 ; i = i - 1)
            {
                javax.swing.JPanel bitPanel = new javax.swing.JPanel();
                bitPanel.setLayout(new javax.swing.BoxLayout(bitPanel, javax.swing.BoxLayout.Y_AXIS));
                javax.swing.JLabel bitLabel = new javax.swing.JLabel("Bit " + (i+1));
                bitCheckbox[i+wordBits] = new javax.swing.JCheckBox();
                bitCheckbox[i+wordBits].addActionListener(new java.awt.event.ActionListener(){
                    public void actionPerformed(java.awt.event.ActionEvent e)
                    {
                        if (numBits == 16)
                        {
                            calculateInt16();
                        }
                        if (numBits == 32)
                        {
                            calculateInt32();
                        }
                    }
                });
                javax.swing.JSeparator seperator = new javax.swing.JSeparator(javax.swing.SwingConstants.VERTICAL);
                
                bitPanel.add(bitLabel);
                bitPanel.add(bitCheckbox[i+wordBits]);
                parentPanel.add(seperator);
                parentPanel.add(bitPanel);
            }
            jPanel1.add(parentPanel);
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

        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bit Inspector");
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));
        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(BitsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BitsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BitsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BitsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BitsFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
