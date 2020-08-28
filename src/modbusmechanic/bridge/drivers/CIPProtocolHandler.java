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
import modbusmechanic.bridge.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class CIPProtocolHandler implements ProtocolHandler{
    JFrame parentFrame = null;
    BridgeEntryContainer parentEntryContainer = null;
    JPanel outgoingPanel = null;
    JPanel incomingDataSettings = null;
    DriverMenuHandler dmh = null;
    JComboBox outgoingDataSelector = null;
    String[] incomingMenuNames = new String[] {"CIP Read Tag"};
    String[] outgoingMenuNames = new String[] {"CIP Write Tag"};
    boolean incomingPanelReady = false;
    public CIPProtocolHandler(DriverMenuHandler aDmh, modbusmechanic.bridge.BridgeFrame aParentFrame, BridgeEntryContainer aParentEntryContainer, JPanel aIncomingDataSettings, JPanel aOutgoingPanel)
    {
        parentFrame = aParentFrame;
        parentEntryContainer = aParentEntryContainer;
        outgoingPanel = aOutgoingPanel;
        incomingDataSettings = aIncomingDataSettings;
        dmh = aDmh;
        outgoingDataSelector = dmh.outgoingDataSelector;
        
    }
    public String[] getIncomingMenuNames()
    {
        return incomingMenuNames;
    }
    public String[] getOutgoingMenuNames()
    {
        return outgoingMenuNames;
    }
    public boolean getIncomingPanelReady()
    {
        return incomingPanelReady;
    }
    public void buildProtocolPane(int paneType, String selectedItem)
    {
        if (paneType == PANE_TYPE_INCOMING)
        {
            constructIncomingDataSettings(incomingDataSettings, selectedItem);
        }
        if (paneType == PANE_TYPE_OUTGOING)
        {
            constructOutgoingDataSettings(outgoingPanel, selectedItem);
        }
        /*if (!incomingPanelReady)
        {
            resetOutgoingPanel();
        }*/
    }
    public void constructIncomingDataSettings(JPanel mainPanel, String selectedItem)
    {
        if (parentEntryContainer.incomingSettings.size() < 2)
        {
            parentEntryContainer.incomingSettings.add(new ArrayList());
        }
        parentEntryContainer.incomingSettings.get(1).clear();
        mainPanel.removeAll();
        //CIP read tag
        if (selectedItem.equals(incomingMenuNames[0]))
        {
            JPanel settingsPanel = new JPanel();
            ArrayList settings = parentEntryContainer.incomingSettings.get(1);
            settingsPanel.add(new JLabel("CIP host address"));
            JTextField hostField = new JTextField();
            hostField.setColumns(15);
            settingsPanel.add(hostField);
            settings.add(hostField);
            settingsPanel.add(new JLabel("CIP port"));
            JTextField portField = new JTextField();
            portField.setColumns(4);
            portField.setText("44818");
            settingsPanel.add(portField);
            JPanel readTagPanel = new JPanel();
            settingsPanel.add(readTagPanel);
            mainPanel.add(settingsPanel);
            mainPanel.add(readTagPanel);
            readTagPanel.add(new JLabel("Tag name"));
            JTextField readTagField = new JTextField();
            readTagField.setColumns(15);
            readTagPanel.add(readTagField);
            readTagPanel.add(new JLabel("Controller slot"));
            JTextField slotField = new JTextField();
            slotField.setColumns(3);
            slotField.setText("0");
            readTagPanel.add(slotField);
            settings.add(portField);
            settings.add(readTagField);
            settings.add(slotField);
        }
        
        incomingPanelReady = true;
        dmh.constructOutgoingDataMenu(false);
        parentFrame.pack();
    }
    public void constructOutgoingDataSettings(JPanel mainPanel, String selectedItem)
    {
        mainPanel.removeAll();
        dmh.constructOutgoingDataMenu(true);
        if (parentEntryContainer.outgoingSettings.size() < 2)
        {
            parentEntryContainer.outgoingSettings.add(new ArrayList());
        }
        parentEntryContainer.outgoingSettings.get(1).clear();
        //CIP write tag
        if (selectedItem.equals(outgoingMenuNames[0]))
        {
            JPanel settingsPanel = new JPanel();
            ArrayList settings = parentEntryContainer.outgoingSettings.get(1);
            settingsPanel.add(new JLabel("CIP host address"));
            JTextField hostField = new JTextField();
            hostField.setColumns(15);
            settingsPanel.add(hostField);
            settings.add(hostField);
            settingsPanel.add(new JLabel("CIP port"));
            JTextField portField = new JTextField();
            portField.setColumns(4);
            portField.setText("44818");
            settingsPanel.add(portField);
            JPanel readTagPanel = new JPanel();
            settingsPanel.add(readTagPanel);
            mainPanel.add(settingsPanel);
            mainPanel.add(readTagPanel);
            readTagPanel.add(new JLabel("Tag name"));
            JTextField writeTagField = new JTextField();
            writeTagField.setColumns(15);
            readTagPanel.add(writeTagField);
            readTagPanel.add(new JLabel("Controller slot"));
            JTextField slotField = new JTextField();
            slotField.setColumns(3);
            slotField.setText("0");
            readTagPanel.add(slotField);
            settings.add(portField);
            settings.add(writeTagField);
            settings.add(slotField);
        }
        parentFrame.pack();
    }
    public void setIncomingSettings(ProtocolRecord protocolRecord)
    {
        CIPProtocolRecord currentRecord = (CIPProtocolRecord)protocolRecord;
        ArrayList currentLevel = null;
        JComboBox incomingMenu = (JComboBox)parentEntryContainer.incomingSettings.get(0).get(0);
        for (int i = 0; i < incomingMenu.getItemCount(); i++)
        {
            if (currentRecord.type == CIPProtocolRecord.PROTOCOL_TYPE_CIP_READ && incomingMenu.getItemAt(i).equals(incomingMenuNames[0]))
            {
                incomingMenu.setSelectedIndex(i);
                buildProtocolPane(PANE_TYPE_INCOMING, incomingMenuNames[0]);
            }
        }
        currentLevel = parentEntryContainer.incomingSettings.get(1);
        ((JTextField)(currentLevel.get(0))).setText(currentRecord.host);
        ((JTextField)(currentLevel.get(1))).setText(currentRecord.port + "");
        ((JTextField)(currentLevel.get(2))).setText(currentRecord.tag);
        ((JTextField)(currentLevel.get(3))).setText(currentRecord.slot + "");
    }
    public void setOutgoingSettings(ProtocolRecord protocolRecord)
    {
        CIPProtocolRecord currentRecord = (CIPProtocolRecord)protocolRecord;
        ArrayList currentLevel = null;
        JComboBox outgoingMenu = (JComboBox)parentEntryContainer.outgoingSettings.get(0).get(0);
        for (int i = 0; i < outgoingMenu.getItemCount(); i++)
        {
            if (currentRecord.type == CIPProtocolRecord.PROTOCOL_TYPE_CIP_WRITE && outgoingMenu.getItemAt(i).equals(outgoingMenuNames[0]))
            {
                outgoingMenu.setSelectedIndex(i);
                buildProtocolPane(PANE_TYPE_OUTGOING, outgoingMenuNames[0]);
            }
        }
        currentLevel = parentEntryContainer.outgoingSettings.get(1);
        ((JTextField)(currentLevel.get(0))).setText(currentRecord.host);
        ((JTextField)(currentLevel.get(1))).setText(currentRecord.port + "");
        ((JTextField)(currentLevel.get(2))).setText(currentRecord.tag);
        ((JTextField)(currentLevel.get(3))).setText(currentRecord.slot + "");
    }
    public ProtocolRecord getIncomingProtocolRecord()
    {
        ProtocolRecord incomingProtocolRecord = null;
        //first container
        ArrayList currentLevel = parentEntryContainer.incomingSettings.get(0);
        JComboBox typeSelector = (JComboBox)(currentLevel.get(0));
        int type = 0;
        String host = null;
        int port = 0;
        String tag = null;
        int slot = 0;
        if (typeSelector.getSelectedItem().equals(incomingMenuNames[0]))
        {
            type = CIPProtocolRecord.PROTOCOL_TYPE_CIP_READ;
            currentLevel = parentEntryContainer.incomingSettings.get(1);
            host = ((JTextField)(currentLevel.get(0))).getText();
            port = Integer.parseInt(((JTextField)(currentLevel.get(1))).getText());
            tag = ((JTextField)(currentLevel.get(2))).getText();
            slot = Integer.parseInt(((JTextField)(currentLevel.get(3))).getText());
        }
        incomingProtocolRecord = new CIPProtocolRecord(type, host, port, slot, tag);
        return incomingProtocolRecord;
    }
    public ProtocolRecord getOutgoingProtocolRecord(ProtocolRecord incomingRecord)
    {
        ProtocolRecord outgoingProtocolRecord = null;
        //first container
        ArrayList currentLevel = parentEntryContainer.outgoingSettings.get(0);
        JComboBox typeSelector = (JComboBox)(currentLevel.get(0));
        int type = 0;
        String host = null;
        int port = 0;
        String tag = null;
        int slot = 0;
        if (typeSelector.getSelectedItem().equals(outgoingMenuNames[0]))
        {
            type = CIPProtocolRecord.PROTOCOL_TYPE_CIP_WRITE;
            currentLevel = parentEntryContainer.outgoingSettings.get(1);
            host = ((JTextField)(currentLevel.get(0))).getText();
            port = Integer.parseInt(((JTextField)(currentLevel.get(1))).getText());
            tag = ((JTextField)(currentLevel.get(2))).getText();
            slot = Integer.parseInt(((JTextField)(currentLevel.get(3))).getText());
        }
        outgoingProtocolRecord = new CIPProtocolRecord(type, host, port, slot, tag);
        return outgoingProtocolRecord;
    }
}
