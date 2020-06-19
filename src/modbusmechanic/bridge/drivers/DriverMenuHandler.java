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
import modbusmechanic.bridge.*;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */

public class DriverMenuHandler implements ActionListener{
    JComboBox incomingDataSelector = null;
    JComboBox outgoingDataSelector = null;
    BridgeFrame parentFrame = null;
    BridgeEntryContainer parentEntryContainer = null;
    ArrayList<ProtocolHandler> driverList = new ArrayList();
    JPanel outgoingPanel = null;
    JPanel incomingDataSettings = null;
    public DriverMenuHandler(JComboBox aIncomingDataSelector, JComboBox aOutgoingDataSelector, BridgeFrame aParentFrame, BridgeEntryContainer aParentEntryContainer, JPanel aIncomingDataSettings, JPanel aOutgoingPanel)
    {
        incomingDataSelector = aIncomingDataSelector;
        outgoingDataSelector = aOutgoingDataSelector;
        parentFrame = aParentFrame;
        outgoingPanel = aOutgoingPanel;
        incomingDataSettings = aIncomingDataSettings;
        parentEntryContainer = aParentEntryContainer;
        outgoingDataSelector.addActionListener(this);
        loadDrivers();
    }
    public void loadDrivers()
    {
        driverList.add(new ModbusProtocolHandler(this, parentFrame, parentEntryContainer, incomingDataSettings, outgoingPanel));
        ArrayList<String> menuItems = new ArrayList();
        menuItems.add("Select");
        for (int i = 0; i < driverList.size(); i++)
        {
            String[] menuNames = driverList.get(i).getIncomingMenuNames();
            for (int j = 0; j < menuNames.length; j++)
            {
                menuItems.add(menuNames[j]);
            }
        }
        DefaultComboBoxModel model = new DefaultComboBoxModel(menuItems.toArray());
        incomingDataSelector.setModel(model);
    }
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == incomingDataSelector)
        {
            dispatchDriverEvent(ProtocolHandler.PANE_TYPE_INCOMING, incomingDataSelector.getSelectedItem().toString());
        }
        if (e.getSource() == outgoingDataSelector)
        {
            dispatchDriverEvent(ProtocolHandler.PANE_TYPE_OUTGOING, outgoingDataSelector.getSelectedItem().toString());
        }
    }
    public void dispatchDriverEvent(int paneType, String selectedItem)
    {
        boolean handlerFound = false;
        for (int i = 0; i < driverList.size() && !handlerFound; i++)
        {
            ProtocolHandler currentHandler = driverList.get(i);
            String[] menuNames = null;
            if (paneType == ProtocolHandler.PANE_TYPE_INCOMING)
            {
                menuNames = currentHandler.getIncomingMenuNames();
            }
            if (paneType == ProtocolHandler.PANE_TYPE_OUTGOING)
            {
                menuNames = currentHandler.getOutgoingMenuNames();
            }
            for (int j = 0; j < menuNames.length && !handlerFound; j++)
            {
                if (menuNames[j].equals(selectedItem))
                {
                    currentHandler.buildProtocolPane(paneType, selectedItem);
                    if (paneType == ProtocolHandler.PANE_TYPE_INCOMING)
                    {
                        parentEntryContainer.incomingHandler = currentHandler;
                    }
                    if (paneType == ProtocolHandler.PANE_TYPE_OUTGOING)
                    {
                        parentEntryContainer.outgoingHandler = currentHandler;
                    }
                    handlerFound = true;
                }
            }
        }
        if (!handlerFound && paneType == ProtocolHandler.PANE_TYPE_INCOMING)
        {
            incomingDataSettings.removeAll();
            outgoingPanel.removeAll();
            parentFrame.pack();
        }
        if (!handlerFound && paneType == ProtocolHandler.PANE_TYPE_OUTGOING)
        {
            outgoingPanel.removeAll();
            constructOutgoingDataMenu();
            parentFrame.pack();
        }
    }
    public void constructOutgoingDataMenu()
    {
        if (parentEntryContainer.outgoingSettings.size() < 1)
        {
            parentEntryContainer.outgoingSettings.add(new ArrayList());
        }
        ArrayList settings = parentEntryContainer.outgoingSettings.get(0);
        settings.clear();
        JPanel outgoingDataDest = new JPanel();
        outgoingDataDest.add(new JLabel("Outgoing Data Destination"));
        ArrayList incomingSettings = parentEntryContainer.incomingSettings.get(2);
        boolean runDriverList = true;
        ArrayList<String> driverNames = new ArrayList();
        driverNames.add("Select");
        if (incomingSettings.get(0) != null && incomingSettings.get(0) instanceof JComboBox)
        {
            JComboBox incomingSelector = (JComboBox)incomingSettings.get(0);
            if (incomingSelector.getSelectedItem().equals("Block Read"));
            {
                runDriverList = false;
                driverNames.add("To Modbus Slave");
            }
        }
        if (runDriverList)
        {
            for (int i = 0; i < driverList.size(); i++)
            {
                String[] menuNames = driverList.get(i).getOutgoingMenuNames();
                for (int j = 0; j < menuNames.length; j++)
                {
                    driverNames.add(menuNames[j]);
                }
            }
        }
        DefaultComboBoxModel model = new DefaultComboBoxModel(driverNames.toArray());
        outgoingDataSelector.setModel(model);
        JPanel outgoingDataSettings = new JPanel();
        outgoingDataSettings.setLayout(new BoxLayout(outgoingDataSettings, BoxLayout.Y_AXIS));
        
        settings.add(outgoingDataSelector);
        outgoingDataDest.add(outgoingDataSelector);
        outgoingPanel.add(outgoingDataDest);
        outgoingPanel.add(outgoingDataSettings);
        parentFrame.pack();
    }
}
