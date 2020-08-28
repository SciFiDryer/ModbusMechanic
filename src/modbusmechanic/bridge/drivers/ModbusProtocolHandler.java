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
    String[] incomingMenuNames = new String[] {"From Modbus Slave (act as master)", "From Modbus Master (act as slave)"};
    String[] outgoingMenuNames = new String[] {"To Modbus Slave (act as master)", "To Modbus Master (act as slave)"};
    public ModbusProtocolHandler()
    {
        
    }
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
        outgoingDataSelector = dmh.outgoingDataSelector;
        
    }
    public void setIncomingSettings(ProtocolRecord protocolRecord)
    {
        ModbusProtocolRecord modbusRecord = (ModbusProtocolRecord)protocolRecord;
        ArrayList currentLevel = null;
        JComboBox incomingMenu = (JComboBox)parentEntryContainer.incomingSettings.get(0).get(0);
        for (int i = 0; i < incomingMenu.getItemCount(); i++)
        {
            if (modbusRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_MASTER && incomingMenu.getItemAt(i).equals(incomingMenuNames[0]))
            {
                incomingMenu.setSelectedIndex(i);
            }
            if (modbusRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_SLAVE && incomingMenu.getItemAt(i).equals(incomingMenuNames[1]))
            {
                incomingMenu.setSelectedIndex(i);
            }
        }
        if (modbusRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_MASTER)
        {
             buildProtocolPane(PANE_TYPE_INCOMING, incomingMenuNames[0]);
             currentLevel = parentEntryContainer.incomingSettings.get(1);
             ((JTextField)(currentLevel.get(0))).setText(modbusRecord.slaveHost);
             ((JTextField)(currentLevel.get(1))).setText(modbusRecord.slavePort + "");
         }
         if (modbusRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_SLAVE)
         {
             buildProtocolPane(PANE_TYPE_INCOMING, incomingMenuNames[1]);
             currentLevel = parentEntryContainer.incomingSettings.get(1);
             JComboBox registerTypeSelector = (JComboBox)currentLevel.get(2);
             registerTypeSelector.setSelectedIndex(1);
         }
         if (modbusRecord.functionCode == 4)
         {
             ((JComboBox)(currentLevel.get(2))).setSelectedIndex(1);
         }
         if (modbusRecord.functionCode == 3)
         {
             ((JComboBox)(currentLevel.get(2))).setSelectedIndex(2);
         }
         currentLevel = parentEntryContainer.incomingSettings.get(2);
         if (modbusRecord.formatType == ModbusProtocolRecord.FORMAT_TYPE_RAW)
         {
             ((JComboBox)(currentLevel.get(0))).setSelectedIndex(1);
             currentLevel = parentEntryContainer.incomingSettings.get(3);
             ((JTextField)(currentLevel.get(0))).setText(modbusRecord.startingRegister + "");
             ((JTextField)(currentLevel.get(1))).setText(modbusRecord.quantity + "");
         }
         else if (modbusRecord.formatType == ModbusProtocolRecord.FORMAT_TYPE_FLOAT)
         {
             ((JComboBox)(currentLevel.get(0))).setSelectedIndex(2);
             currentLevel = parentEntryContainer.incomingSettings.get(3);
             ((JTextField)(currentLevel.get(0))).setText(modbusRecord.startingRegister + "");
             ((JCheckBox)(currentLevel.get(1))).setSelected(modbusRecord.wordSwap);
             ((JCheckBox)(currentLevel.get(2))).setSelected(modbusRecord.byteSwap);
         }
         else if (modbusRecord.formatType == ModbusProtocolRecord.FORMAT_TYPE_UINT_16)
         {
             ((JComboBox)(currentLevel.get(0))).setSelectedIndex(3);
             currentLevel = parentEntryContainer.incomingSettings.get(3);
             ((JTextField)(currentLevel.get(0))).setText(modbusRecord.startingRegister + "");
             ((JCheckBox)(currentLevel.get(2))).setSelected(modbusRecord.byteSwap);
         }
         else if (modbusRecord.formatType == ModbusProtocolRecord.FORMAT_TYPE_UINT_32)
         {
             ((JComboBox)(currentLevel.get(0))).setSelectedIndex(4);
             currentLevel = parentEntryContainer.incomingSettings.get(3);
             ((JTextField)(currentLevel.get(0))).setText(modbusRecord.startingRegister + "");
             ((JCheckBox)(currentLevel.get(1))).setSelected(modbusRecord.wordSwap);
             ((JCheckBox)(currentLevel.get(2))).setSelected(modbusRecord.byteSwap);
         }
    }
    public void setOutgoingSettings(ProtocolRecord protocolRecord)
    {
        ModbusProtocolRecord modbusRecord = (ModbusProtocolRecord)protocolRecord;
        ArrayList currentLevel = null;
        JComboBox outgoingMenu = (JComboBox)parentEntryContainer.outgoingSettings.get(0).get(0);
        for (int i = 0; i < outgoingMenu.getItemCount(); i++)
        {
            if (modbusRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_MASTER && outgoingMenu.getItemAt(i).equals(outgoingMenuNames[0]))
            {
                outgoingMenu.setSelectedIndex(i);
            }
            if (modbusRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_SLAVE && outgoingMenu.getItemAt(i).equals(outgoingMenuNames[1]))
            {
                outgoingMenu.setSelectedIndex(i);
            }
        }
        int functionSelectorIndex = 0;
        if (modbusRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_MASTER)
        {
             buildProtocolPane(PANE_TYPE_OUTGOING, outgoingMenuNames[0]);
             currentLevel = parentEntryContainer.outgoingSettings.get(1);
             ((JTextField)(currentLevel.get(0))).setText(modbusRecord.slaveHost);
             ((JTextField)(currentLevel.get(1))).setText(modbusRecord.slavePort + "");
             functionSelectorIndex = 2;
         }
         if (modbusRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_SLAVE)
         {
             buildProtocolPane(PANE_TYPE_OUTGOING, outgoingMenuNames[1]);
             currentLevel = parentEntryContainer.outgoingSettings.get(1);
             ((JTextField)(currentLevel.get(0))).setText(modbusRecord.slavePort + "");
             functionSelectorIndex = 1;
         }
         if (modbusRecord.functionCode == 4)
         {
             ((JComboBox)(currentLevel.get(functionSelectorIndex))).setSelectedIndex(1);
         }
         if (modbusRecord.functionCode == 3)
         {
             ((JComboBox)(currentLevel.get(functionSelectorIndex))).setSelectedIndex(2);
         }
         currentLevel = parentEntryContainer.outgoingSettings.get(2);
         if (modbusRecord.formatType == ModbusProtocolRecord.FORMAT_TYPE_RAW)
         {
             //((JComboBox)(currentLevel.get(0))).setSelectedIndex(1);
             //currentLevel = parentEntryContainer.outgoingSettings.get(3);
             ((JTextField)(currentLevel.get(0))).setText(modbusRecord.startingRegister + "");
         }
         else if (modbusRecord.formatType == ModbusProtocolRecord.FORMAT_TYPE_FLOAT)
         {
             ((JComboBox)(currentLevel.get(0))).setSelectedIndex(1);
             currentLevel = parentEntryContainer.outgoingSettings.get(3);
             ((JTextField)(currentLevel.get(0))).setText(modbusRecord.startingRegister + "");
             ((JCheckBox)(currentLevel.get(1))).setSelected(modbusRecord.wordSwap);
             ((JCheckBox)(currentLevel.get(2))).setSelected(modbusRecord.byteSwap);
         }
         else if (modbusRecord.formatType == ModbusProtocolRecord.FORMAT_TYPE_UINT_16)
         {
             ((JComboBox)(currentLevel.get(0))).setSelectedIndex(2);
             currentLevel = parentEntryContainer.outgoingSettings.get(3);
             ((JTextField)(currentLevel.get(0))).setText(modbusRecord.startingRegister + "");
             ((JCheckBox)(currentLevel.get(2))).setSelected(modbusRecord.byteSwap);
         }
         else if (modbusRecord.formatType == ModbusProtocolRecord.FORMAT_TYPE_UINT_32)
         {
             ((JComboBox)(currentLevel.get(0))).setSelectedIndex(3);
             currentLevel = parentEntryContainer.outgoingSettings.get(3);
             ((JTextField)(currentLevel.get(0))).setText(modbusRecord.startingRegister + "");
             ((JCheckBox)(currentLevel.get(1))).setSelected(modbusRecord.wordSwap);
             ((JCheckBox)(currentLevel.get(2))).setSelected(modbusRecord.byteSwap);
         }
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
        if (parentEntryContainer.incomingHandler != null && !parentEntryContainer.incomingHandler.getIncomingPanelReady())
        {
            resetOutgoingPanel();
        }
    }
    public void resetOutgoingPanel()
    {
        //outgoingDataSelector = new JComboBox();
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
        mainPanel.removeAll();
        //from modbus slave
        if (selectedItem.equals(incomingMenuNames[0]) || selectedItem.equals(incomingMenuNames[1]))
        {
            JPanel settingsPanel = new JPanel();
            ArrayList settings = parentEntryContainer.incomingSettings.get(1);
            if (selectedItem.equals(incomingMenuNames[0]))
            {
                settingsPanel.add(new JLabel("Slave address"));
                JTextField slaveHostField = new JTextField();
                slaveHostField.setColumns(15);
                settingsPanel.add(slaveHostField);
                settings.add(slaveHostField);
            }
            settingsPanel.add(new JLabel("Slave port"));
            JTextField slavePortField = new JTextField();
            slavePortField.setColumns(4);
            slavePortField.setText("502");
            settingsPanel.add(slavePortField);
            JComboBox registerTypeSelector = new JComboBox();
            DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Select register type", "Input registers", "Holding registers"});
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
            
            
            settings.add(slavePortField);
            settings.add(registerTypeSelector);
        }
        parentFrame.pack();
    }
    public void constructOutgoingDataSettings(JPanel mainPanel, String selectedItem)
    {
        resetOutgoingPanel();
        dmh.constructOutgoingDataMenu(true);
        if (parentEntryContainer.outgoingSettings.size() < 2)
        {
            parentEntryContainer.outgoingSettings.add(new ArrayList());
        }
        if (selectedItem.equals(outgoingMenuNames[0]) || selectedItem.equals(outgoingMenuNames[1]))
        {
            ArrayList settings = parentEntryContainer.outgoingSettings.get(1);
            settings.clear();
            JPanel settingsPanel = new JPanel();
            if (selectedItem.equals(outgoingMenuNames[0]))
            {
                settingsPanel.add(new JLabel("Slave address"));
                JTextField slaveHostField = new JTextField();
                settings.add(slaveHostField);
                slaveHostField.setColumns(15);
                settingsPanel.add(slaveHostField);
            }
            settingsPanel.add(new JLabel("Slave port"));
            JTextField slavePortField = new JTextField();
            settings.add(slavePortField);
            slavePortField.setColumns(4);
            slavePortField.setText("502");
            settingsPanel.add(slavePortField);
            JComboBox registerTypeSelector = new JComboBox();
            DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {"Select register type", "Input registers", "Holding registers"});
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
        if (selectedIndex == 1 || selectedIndex == 2)
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
            
            
            if (readTypeSelector != null && readTypeSelector.getSelectedIndex() == 1)
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
        if (selectedIndex == 2 || selectedIndex == 1)
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
        dmh.constructOutgoingDataMenu(false);
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
        int functionCode = 0;
        if (typeSelector.getSelectedItem().equals(incomingMenuNames[0]))
        {
            type = ModbusProtocolRecord.PROTOCOL_TYPE_MASTER;
            currentLevel = parentEntryContainer.incomingSettings.get(1);
            slaveHost = ((JTextField)(currentLevel.get(0))).getText();
            slavePort = Integer.parseInt(((JTextField)(currentLevel.get(1))).getText());
            if (((JComboBox)(currentLevel.get(2))).getSelectedIndex() == 1)
            {
                functionCode = 4;
            }
            if (((JComboBox)(currentLevel.get(2))).getSelectedIndex() == 2)
            {
                functionCode = 3;
            }
        }
        if (typeSelector.getSelectedItem().equals(incomingMenuNames[1]))
        {
            type = ModbusProtocolRecord.PROTOCOL_TYPE_SLAVE;
            currentLevel = parentEntryContainer.incomingSettings.get(1);
            slavePort = Integer.parseInt(((JTextField)(currentLevel.get(0))).getText());
            if (((JComboBox)(currentLevel.get(1))).getSelectedIndex() == 1)
            {
                functionCode = 3;
            }
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
        int functionCode = 0;
        if (typeSelector.getSelectedItem().equals(outgoingMenuNames[0]))
        {
            type = ModbusProtocolRecord.PROTOCOL_TYPE_MASTER;
            currentLevel = parentEntryContainer.outgoingSettings.get(1);
            slaveHost = ((JTextField)(currentLevel.get(0))).getText();
            slavePort = Integer.parseInt(((JTextField)(currentLevel.get(1))).getText());
            if (((JComboBox)(currentLevel.get(2))).getSelectedIndex() == 1)
            {
                functionCode = 4;
            }
            if (((JComboBox)(currentLevel.get(2))).getSelectedIndex() == 2)
            {
                functionCode = 3;
            }
        }
        else if (typeSelector.getSelectedItem().equals(outgoingMenuNames[1]))
        {
            type = ModbusProtocolRecord.PROTOCOL_TYPE_SLAVE;
            currentLevel = parentEntryContainer.outgoingSettings.get(1);
            slavePort = Integer.parseInt(((JTextField)(currentLevel.get(0))).getText());
            if (((JComboBox)(currentLevel.get(1))).getSelectedIndex() == 1)
            {
                functionCode = 4;
            }
            if (((JComboBox)(currentLevel.get(1))).getSelectedIndex() == 2)
            {
                functionCode = 3;
            }
        }
        
        currentLevel = parentEntryContainer.outgoingSettings.get(2);
        int format = 0;
        if (incomingProtocolRecord instanceof ModbusProtocolRecord && ((ModbusProtocolRecord)(incomingProtocolRecord)).formatType == ModbusProtocolRecord.FORMAT_TYPE_RAW)
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
}
