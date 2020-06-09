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
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */

public class BridgeActionListener implements ActionListener{
    JFrame parentFrame = null;
    JPanel incomingPanel = null;
    JPanel outgoingPanel = null;
    JComboBox incomingDataSelector = null;
    JComboBox outgoingDataSelector = null;
    boolean incomingPanelReady = false;
    JComboBox readTypeSelector = null;
    JPanel incomingDataSettings = null;
    //index 0 for first level 1 for second and so on
    ArrayList<ArrayList> incomingSettings = new ArrayList();
    ArrayList<ArrayList> outgoingSettings = new ArrayList();
    public BridgeActionListener(JFrame aParentFrame, JPanel aIncomingPanel, JPanel aOutgoingPanel)
    {
        parentFrame = aParentFrame;
        incomingPanel = aIncomingPanel;
        outgoingPanel = aOutgoingPanel;
        incomingDataSelector = new JComboBox();
        outgoingDataSelector = new JComboBox();
        DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Select", "From Modbus Slave"});
        incomingDataSelector.setModel(model);
        incomingDataSelector.addActionListener(this);
        JPanel incomingDataSource = new JPanel();
        incomingDataSettings = new JPanel();
        incomingDataSettings.setLayout(new BoxLayout(incomingDataSettings, BoxLayout.Y_AXIS));
        incomingSettings.add(new ArrayList());
        incomingSettings.get(0).add(incomingDataSelector);
        JLabel incomingDataLabel = new JLabel("Incoming Data Source");
        incomingDataSource.add(incomingDataLabel);
        incomingDataSource.add(incomingDataSelector);
        incomingPanel.add(incomingDataSource);
        incomingPanel.add(incomingDataSettings);
        
        outgoingPanel.setLayout(new BoxLayout(outgoingPanel, BoxLayout.Y_AXIS));
    }
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == incomingDataSelector)
        {
            incomingDataSettings.removeAll();
            constructDataSettings(incomingDataSettings, incomingDataSelector.getSelectedIndex());
        }
        if (e.getSource() == outgoingDataSelector)
        {
            constructOutgoingDataSettings(outgoingPanel, outgoingDataSelector.getSelectedIndex());
        }
        if (!incomingPanelReady)
        {
            resetOutgoingPanel();
        }
    }
    public void resetOutgoingPanel()
    {
        outgoingDataSelector = new JComboBox();
        outgoingPanel.removeAll();
    }
    public void constructOutgoingDataMenu()
    {
        if (outgoingSettings.size() < 1)
        {
            outgoingSettings.add(new ArrayList());
        }
        ArrayList settings = outgoingSettings.get(0);
        settings.clear();
        JPanel outgoingDataDest = new JPanel();
        outgoingDataDest.add(new JLabel("Outgoing Data Destination"));
        DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Select", "To Modbus Slave"});
        outgoingDataSelector.setModel(model);
        JPanel outgoingDataSettings = new JPanel();
        outgoingDataSettings.setLayout(new BoxLayout(outgoingDataSettings, BoxLayout.Y_AXIS));
        outgoingDataSelector.addActionListener(this);
        settings.add(outgoingDataSelector);
        outgoingDataDest.add(outgoingDataSelector);
        outgoingPanel.add(outgoingDataDest);
        outgoingPanel.add(outgoingDataSettings);
        parentFrame.pack();
    }
    public void constructDataSettings(JPanel mainPanel, int selectedIndex)
    {
        incomingPanelReady = false;
        resetOutgoingPanel();
        if (incomingSettings.size() < 2)
        {
            incomingSettings.add(new ArrayList());
        }
        incomingSettings.get(1).clear();
        //from modbus slave
        if (selectedIndex == 1)
        {
            JPanel settingsPanel = new JPanel();
            settingsPanel.add(new JLabel("Slave address"));
            JTextField slaveHostField = new JTextField();
            slaveHostField.setColumns(15);
            settingsPanel.add(slaveHostField);
            settingsPanel.add(new JLabel("Slave port"));
            JTextField slavePortField = new JTextField();
            slavePortField.setColumns(4);
            settingsPanel.add(slavePortField);
            JComboBox registerTypeSelector = new JComboBox();
            DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Select register type", "Holding registers"});
            registerTypeSelector.setModel(model);
            JPanel slaveSettingsPanel = new JPanel();
            registerTypeSelector.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e)
                {
                    constructSlaveSettings(slaveSettingsPanel, registerTypeSelector.getSelectedIndex());
                }
            });
            settingsPanel.add(registerTypeSelector);
            mainPanel.add(settingsPanel);
            mainPanel.add(slaveSettingsPanel);
            ArrayList settings = incomingSettings.get(1);
            settings.add(slaveHostField);
            settings.add(slavePortField);
            settings.add(registerTypeSelector);
        }
        parentFrame.pack();
    }
    public void constructOutgoingDataSettings(JPanel mainPanel, int selectedIndex)
    {
        //to modbus slave
        if (selectedIndex == 1)
        {
            if (outgoingSettings.size() < 2)
            {
                outgoingSettings.add(new ArrayList());
            }
            ArrayList settings = outgoingSettings.get(1);
            settings.clear();
            JPanel settingsPanel = new JPanel();
            settingsPanel.add(new JLabel("Slave address"));
            JTextField slaveHostField = new JTextField();
            settings.add(slaveHostField);
            slaveHostField.setColumns(15);
            settingsPanel.add(slaveHostField);
            settingsPanel.add(new JLabel("Slave port"));
            JTextField slavePortField = new JTextField();
            settings.add(slavePortField);
            slavePortField.setColumns(4);
            settingsPanel.add(slavePortField);
            JComboBox registerTypeSelector = new JComboBox();
            DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Select register type", "Holding registers"});
            registerTypeSelector.setModel(model);
            JPanel slaveSettingsPanel = new JPanel();
            registerTypeSelector.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e)
                {
                    constructOutgoingSlaveSettings(slaveSettingsPanel, registerTypeSelector.getSelectedIndex());
                }
            });
            settings.add(registerTypeSelector);
            settingsPanel.add(registerTypeSelector);
            mainPanel.add(settingsPanel);
            mainPanel.add(slaveSettingsPanel);
        }
        parentFrame.pack();
    }
    public void constructOutgoingSlaveSettings(JPanel mainPanel, int selectedIndex)
    {
        mainPanel.removeAll();
        if (selectedIndex == 1)
        {
            if (outgoingSettings.size() < 3)
            {
                outgoingSettings.add(new ArrayList());
            }
            ArrayList settings = outgoingSettings.get(2);
            settings.clear();
            JComboBox writeTypeSelector = new JComboBox();
            DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Select write type", "Single value U16", "Single value U32", "Single value Float"});
            writeTypeSelector.setModel(model);
            
            
            JPanel registerSettingsPanel = new JPanel();
            if (readTypeSelector.getSelectedIndex() == 1)
            {
                mainPanel.add(new JLabel("Block write start address"));
                JTextField registerField = new JTextField();
                settings.add(registerField);
                registerField.setColumns(5);
                mainPanel.add(registerField);
            }
            else
            {
                mainPanel.add(writeTypeSelector);
                settings.add(writeTypeSelector);
            }
            mainPanel.add(registerSettingsPanel);
        }
        parentFrame.pack();
    }
    public void constructSlaveSettings(JPanel mainPanel, int selectedIndex)
    {
        if (incomingSettings.size() < 3)
        {
            incomingSettings.add(new ArrayList());
        }
        ArrayList settings = incomingSettings.get(2);
        settings.clear();
        resetOutgoingPanel();
        incomingPanelReady = false;
        mainPanel.removeAll();
        if (selectedIndex == 1)
        {
            
            DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Select read type", "Block read"});
            readTypeSelector = new JComboBox();
            readTypeSelector.setModel(model);
            JPanel registerSettingsPanel = new JPanel();
            readTypeSelector.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e)
                {
                    constructRegisterSettings(registerSettingsPanel, readTypeSelector.getSelectedIndex());
                }
            });
            settings.add(readTypeSelector);
            mainPanel.add(readTypeSelector);
            mainPanel.add(registerSettingsPanel);
        }
        parentFrame.pack();
    }
    public void constructRegisterSettings(JPanel mainPanel, int selectedIndex)
    {
        if (incomingSettings.size() < 4)
        {
            incomingSettings.add(new ArrayList());
        }
        ArrayList settings = incomingSettings.get(3);
        settings.clear();
        mainPanel.removeAll();
        resetOutgoingPanel();
        if (selectedIndex != 0)
        {
            mainPanel.add(new JLabel("Starting register"));
            JTextField registerField = new JTextField();
            settings.add(registerField);
            registerField.setColumns(5);
            mainPanel.add(registerField);
        }
        if (selectedIndex == 1)
        {
            mainPanel.add(new JLabel("Quantity"));
            JTextField quantityField = new JTextField();
            settings.add(quantityField);
            quantityField.setColumns(5);
            mainPanel.add(quantityField);
        }
        parentFrame.pack();
        incomingPanelReady = true;
        constructOutgoingDataMenu();
    }
}
