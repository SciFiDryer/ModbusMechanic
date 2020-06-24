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

package modbusmechanic.bridge.drivers;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import modbusmechanic.bridge.BridgeEntryContainer;
import modbusmechanic.bridge.BridgeMappingRecord;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */

public class ModbusProtocolHandler implements ProtocolHandler{
    modbusmechanic.bridge.BridgeFrame parentFrame = null;
    JComboBox incomingDataSelector = null;
    JComboBox outgoingDataSelector = null;
    boolean incomingPanelReady = false;
    JComboBox readTypeSelector = null;
    JPanel incomingDataSettings = null;
    JPanel outgoingPanel = null;
    DriverMenuHandler dmh = null;
    BridgeEntryContainer parentEntryContainer = null;
    String[] incomingMenuNames = new String[] {"From Modbus Slave"};
    String[] outgoingMenuNames = new String[] {"To Modbus Slave"};
    public String[] getIncomingMenuNames()
    {
        return incomingMenuNames;
    }
    public String[] getOutgoingMenuNames()
    {
        return outgoingMenuNames;
    }
    public ModbusProtocolHandler(DriverMenuHandler aDmh, modbusmechanic.bridge.BridgeFrame aParentFrame, BridgeEntryContainer aParentEntryContainer, JPanel aIncomingDataSettings, JPanel aOutgoingPanel)
    {
        parentFrame = aParentFrame;
        parentEntryContainer = aParentEntryContainer;
        outgoingPanel = aOutgoingPanel;
        incomingDataSettings = aIncomingDataSettings;
        dmh = aDmh;
        
    }
    public void buildProtocolPane(int paneType, String selectedItem)
    {
        if (paneType == PANE_TYPE_INCOMING)
        {
            constructDataSettings(incomingDataSettings, selectedItem);
        }
        if (paneType == PANE_TYPE_OUTGOING)
        {
            constructOutgoingDataSettings(outgoingPanel, selectedItem);
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
    public boolean getIncomingPanelReady()
    {
        return incomingPanelReady;
    }
    public void constructDataSettings(JPanel mainPanel, String selectedItem)
    {
        incomingPanelReady = false;
        resetOutgoingPanel();
        if (parentEntryContainer.incomingSettings.size() < 2)
        {
            parentEntryContainer.incomingSettings.add(new ArrayList());
        }
        parentEntryContainer.incomingSettings.get(1).clear();
        //from modbus slave
        if (selectedItem.equals(incomingMenuNames[0]))
        {
            JPanel settingsPanel = new JPanel();
            settingsPanel.add(new JLabel("Slave address"));
            JTextField slaveHostField = new JTextField();
            slaveHostField.setColumns(15);
            settingsPanel.add(slaveHostField);
            settingsPanel.add(new JLabel("Slave port"));
            JTextField slavePortField = new JTextField();
            slavePortField.setColumns(4);
            slavePortField.setText("502");
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
            ArrayList settings = parentEntryContainer.incomingSettings.get(1);
            settings.add(slaveHostField);
            settings.add(slavePortField);
            settings.add(registerTypeSelector);
        }
        parentFrame.pack();
    }
    public void constructOutgoingDataSettings(JPanel mainPanel, String selectedItem)
    {
        //to modbus slave
        if (selectedItem.equals(outgoingMenuNames[0]))
        {
            if (parentEntryContainer.outgoingSettings.size() < 2)
            {
                parentEntryContainer.outgoingSettings.add(new ArrayList());
            }
            ArrayList settings = parentEntryContainer.outgoingSettings.get(1);
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
            slavePortField.setText("502");
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
            if (parentEntryContainer.outgoingSettings.size() < 3)
            {
                parentEntryContainer.outgoingSettings.add(new ArrayList());
            }
            ArrayList settings = parentEntryContainer.outgoingSettings.get(2);
            settings.clear();
            JComboBox writeTypeSelector = new JComboBox();
            DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Select write type", "Single value Float", "Single value unsigned Int16", "Single value unsigned Int32"});
            writeTypeSelector.setModel(model);
            JPanel registerSettingsPanel = new JPanel();
            writeTypeSelector.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e)
                {
                    constructOutgoingRegisterSettings(registerSettingsPanel, writeTypeSelector.getSelectedIndex());
                }
            });
            
            
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
    public void constructOutgoingRegisterSettings(JPanel mainPanel, int selectedIndex)
    {
        if (parentEntryContainer.outgoingSettings.size() < 4)
        {
            parentEntryContainer.outgoingSettings.add(new ArrayList());
        }
        ArrayList settings = parentEntryContainer.outgoingSettings.get(3);
        settings.clear();
        mainPanel.removeAll();
        if (selectedIndex != 0)
        {
            mainPanel.add(new JLabel("Register"));
            JTextField registerField = new JTextField();
            settings.add(registerField);
            registerField.setColumns(5);
            mainPanel.add(registerField);
            if (selectedIndex != 2)
            {
                JCheckBox wordSwap = new JCheckBox();
                settings.add(wordSwap);
                mainPanel.add(wordSwap);
                mainPanel.add(new JLabel("Word swap"));
            }
            JCheckBox byteSwap = new JCheckBox();
            settings.add(byteSwap);
            mainPanel.add(byteSwap);
            mainPanel.add(new JLabel("Byte swap"));
        }
        parentFrame.pack();
    }
    public void constructSlaveSettings(JPanel mainPanel, int selectedIndex)
    {
        if (parentEntryContainer.incomingSettings.size() < 3)
        {
            parentEntryContainer.incomingSettings.add(new ArrayList());
        }
        ArrayList settings = parentEntryContainer.incomingSettings.get(2);
        settings.clear();
        resetOutgoingPanel();
        incomingPanelReady = false;
        mainPanel.removeAll();
        if (selectedIndex == 1)
        {
            
            DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Select read type", "Block read", "Read Float", "Read Unsigned Int16", "Read Unsigned Int32"});
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
        if (parentEntryContainer.incomingSettings.size() < 4)
        {
            parentEntryContainer.incomingSettings.add(new ArrayList());
        }
        ArrayList settings = parentEntryContainer.incomingSettings.get(3);
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
        if (selectedIndex == 2 || selectedIndex == 4)
        {
            JCheckBox wordSwap = new JCheckBox();
            settings.add(wordSwap);
            mainPanel.add(wordSwap);
            mainPanel.add(new JLabel("Word swap"));
        }
        if (selectedIndex > 1)
        {
            JCheckBox byteSwap = new JCheckBox();
            settings.add(byteSwap);
            mainPanel.add(byteSwap);
            mainPanel.add(new JLabel("Byte swap"));
        }
        parentFrame.pack();
        incomingPanelReady = true;
        dmh.constructOutgoingDataMenu();
    }
    public ProtocolRecord getIncomingProtocolRecord()
    {
        ProtocolRecord incomingProtocolRecord = null;
        //first container
        ArrayList currentLevel = parentEntryContainer.incomingSettings.get(0);
        JComboBox typeSelector = (JComboBox)(currentLevel.get(0));
        int type = 0;
        int format = 0;
        String slaveHost = null;
        int slavePort = 0;
        if (typeSelector.getSelectedIndex() == 1)
        {
            type = ModbusProtocolRecord.PROTOCOL_TYPE_MASTER;
            currentLevel = parentEntryContainer.incomingSettings.get(1);
            slaveHost = ((JTextField)(currentLevel.get(0))).getText();
            slavePort = Integer.parseInt(((JTextField)(currentLevel.get(1))).getText());
        }
        int functionCode = 0;
        if (((JComboBox)(currentLevel.get(2))).getSelectedIndex() == 1)
        {
            functionCode = 3;
        }
        currentLevel = parentEntryContainer.incomingSettings.get(2);
        if (((JComboBox)(currentLevel.get(0))).getSelectedIndex() == 1)
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_RAW;
            currentLevel = parentEntryContainer.incomingSettings.get(3);
            int register = Integer.parseInt(((JTextField)(currentLevel.get(0))).getText());
            int quantity = Integer.parseInt(((JTextField)(currentLevel.get(1))).getText());
            incomingProtocolRecord = new ModbusProtocolRecord(type, slaveHost, slavePort, format, functionCode, register, quantity, false, false);
        }
        else if (((JComboBox)(currentLevel.get(0))).getSelectedIndex() == 2)
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_FLOAT;
            currentLevel = parentEntryContainer.incomingSettings.get(3);
            int register = Integer.parseInt(((JTextField)(currentLevel.get(0))).getText());
            boolean wordSwap = ((JCheckBox)(currentLevel.get(1))).isSelected();
            boolean byteSwap = ((JCheckBox)(currentLevel.get(2))).isSelected();
            incomingProtocolRecord = new ModbusProtocolRecord(type, slaveHost, slavePort, format, functionCode, register, 2, wordSwap, byteSwap);
        }
        else if (((JComboBox)(currentLevel.get(0))).getSelectedIndex() == 3)
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_UINT_16;
            currentLevel = parentEntryContainer.incomingSettings.get(3);
            int register = Integer.parseInt(((JTextField)(currentLevel.get(0))).getText());
            //boolean wordSwap = ((JCheckBox)(currentLevel.get(1))).isSelected();
            boolean byteSwap = ((JCheckBox)(currentLevel.get(1))).isSelected();
            incomingProtocolRecord = new ModbusProtocolRecord(type, slaveHost, slavePort, format, functionCode, register, 1, false, byteSwap);
        }
        else if (((JComboBox)(currentLevel.get(0))).getSelectedIndex() == 4)
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_UINT_32;
            currentLevel = parentEntryContainer.incomingSettings.get(3);
            int register = Integer.parseInt(((JTextField)(currentLevel.get(0))).getText());
            boolean wordSwap = ((JCheckBox)(currentLevel.get(1))).isSelected();
            boolean byteSwap = ((JCheckBox)(currentLevel.get(2))).isSelected();
            incomingProtocolRecord = new ModbusProtocolRecord(type, slaveHost, slavePort, format, functionCode, register, 2, false, byteSwap);
        }
        return incomingProtocolRecord;
    }
    public ProtocolRecord getOutgoingProtocolRecord(ProtocolRecord incomingProtocolRecord)
    {
        ProtocolRecord outgoingProtocolRecord = null;
        //first container
        ArrayList currentLevel = parentEntryContainer.outgoingSettings.get(0);
        JComboBox typeSelector = (JComboBox)(currentLevel.get(0));
        int type = 0;
        String slaveHost = null;
        int slavePort = 0;
        if (typeSelector.getSelectedIndex() == 1)
        {
            type = ModbusProtocolRecord.PROTOCOL_TYPE_MASTER;
            currentLevel = parentEntryContainer.outgoingSettings.get(1);
            slaveHost = ((JTextField)(currentLevel.get(0))).getText();
            slavePort = Integer.parseInt(((JTextField)(currentLevel.get(1))).getText());
        }
        int functionCode = 0;
        if (((JComboBox)(currentLevel.get(2))).getSelectedIndex() == 1)
        {
            functionCode = 3;
        }
        currentLevel = parentEntryContainer.outgoingSettings.get(2);
        int format = 0;
        if (((ModbusProtocolRecord)(incomingProtocolRecord)).formatType == ModbusProtocolRecord.FORMAT_TYPE_RAW)
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_RAW;
        }
        else if (((JComboBox)(currentLevel.get(0))).getSelectedIndex() == 1)
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_FLOAT;
        }
        else if (((JComboBox)(currentLevel.get(0))).getSelectedIndex() == 2)
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_UINT_16;
        }
        else if (((JComboBox)(currentLevel.get(0))).getSelectedIndex() == 3)
        {
            format = ModbusProtocolRecord.FORMAT_TYPE_UINT_32;
        }
        if (format == ModbusProtocolRecord.FORMAT_TYPE_RAW)
        {
            currentLevel = parentEntryContainer.outgoingSettings.get(2);
            int register = Integer.parseInt(((JTextField)(currentLevel.get(0))).getText());
            int quantity = ((ModbusProtocolRecord)(incomingProtocolRecord)).quantity;
            outgoingProtocolRecord = new ModbusProtocolRecord(type, slaveHost, slavePort, format, functionCode, register, quantity, false, false);
        }
        if (format != ModbusProtocolRecord.FORMAT_TYPE_RAW && format != ModbusProtocolRecord.FORMAT_TYPE_UINT_16)
        {
            currentLevel = parentEntryContainer.outgoingSettings.get(3);
            int register = Integer.parseInt(((JTextField)(currentLevel.get(0))).getText());
            boolean wordSwap = ((JCheckBox)(currentLevel.get(1))).isSelected();
            boolean byteSwap = ((JCheckBox)(currentLevel.get(2))).isSelected();
            outgoingProtocolRecord = new ModbusProtocolRecord(type, slaveHost, slavePort, format, functionCode, register, 2, wordSwap, byteSwap);
        }
        if (format == ModbusProtocolRecord.FORMAT_TYPE_UINT_16)
        {
            currentLevel = parentEntryContainer.outgoingSettings.get(3);
            int register = Integer.parseInt(((JTextField)(currentLevel.get(0))).getText());
            boolean byteSwap = ((JCheckBox)(currentLevel.get(1))).isSelected();
            outgoingProtocolRecord = new ModbusProtocolRecord(type, slaveHost, slavePort, format, functionCode, register, 1, false, byteSwap);
        }
        return outgoingProtocolRecord;
    }
    public BridgeMappingRecord getBridgeMappingRecord()
    {
        ProtocolRecord incomingProtocolRecord = getIncomingProtocolRecord();
        ProtocolRecord outgoingProtocolRecord = getOutgoingProtocolRecord(incomingProtocolRecord);
        BridgeMappingRecord bmr = new BridgeMappingRecord(incomingProtocolRecord, outgoingProtocolRecord);
        if (incomingProtocolRecord instanceof ModbusProtocolRecord && outgoingProtocolRecord instanceof ModbusProtocolRecord)
        {
            if (((ModbusProtocolRecord)(incomingProtocolRecord)).formatType == ModbusProtocolRecord.FORMAT_TYPE_RAW && ((ModbusProtocolRecord)(outgoingProtocolRecord)).formatType == ModbusProtocolRecord.FORMAT_TYPE_RAW)
            {
                bmr.modbusBlockRemap = true;
            }
        }
        return bmr;
    }
}
