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
package modbusmechanic.gateway;

import modbusmechanic.gateway.GatewayFrame;
import java.awt.Color;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import modbusmechanic.ModbusMechanic;
import purejavacomm.*;
import java.util.concurrent.*;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class GatewayManager {
    GatewayFrame parentFrame = null;
    boolean isCommandLine = false;
    ModbusSlaveGatewayTCP slave = null;
    String comPort = null;
    int baud = 0;
    int dataBits = 0;
    int stopBits = 0;
    int parity = 0;
    int tcpPort = 0;
    SerialPort port = null;
    RTUQueueManager queueManager = null;

    public GatewayManager(File propFile) throws Exception
    {
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream(propFile);
        prop.load(fis);
        isCommandLine = true;
        comPort = prop.getProperty("comport", "");
        tcpPort = Integer.parseInt(prop.getProperty("tcpport", "502"));
        baud = Integer.parseInt(prop.getProperty("baud", "9600"));
        dataBits = Integer.parseInt(prop.getProperty("databits", "8"));
        stopBits = Integer.parseInt(prop.getProperty("stopbits", "1"));
        parity = Integer.parseInt(prop.getProperty("parity", "0"));
    }
    public GatewayManager(GatewayFrame aParentFrame)
    {
        parentFrame = aParentFrame;
    }
    public void handleException(Exception e)
    {
        if (isCommandLine || ModbusMechanic.debug)
        {
            e.printStackTrace();
        }
        if (!isCommandLine)
        {
            JOptionPane.showMessageDialog(parentFrame, e.getMessage(), "Gateway Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void handleGatewayReady()
    {
        queueManager.start();
        RTUPacketListener rtuListener = new RTUPacketListener(this);
        rtuListener.start();
        if (!isCommandLine)
        {
            parentFrame.statusLabel.setForeground(new Color(66, 189, 0));
            parentFrame.statusLabel.setText("Gateway is running");
            parentFrame.startStopButton.setText("Stop Gateway");
            parentFrame.startStopButton.setEnabled(true);
        }
    }
    public void handleStartStop()
    {
        if (slave == null)
        {
            try
            {
                if (!isCommandLine)
                {
                    parentFrame.startStopButton.setEnabled(false);
                    parentFrame.startStopButton.setText("Starting Gateway");
                    parentFrame.statusLabel.setForeground(Color.black);
                    parentFrame.statusLabel.setText("Gateway is starting");
                    tcpPort = Integer.parseInt(parentFrame.tcpPortField.getText());
                    comPort = parentFrame.comPortSelector.getSelectedItem().toString();
                    baud = Integer.parseInt(parentFrame.baudRateSelector.getSelectedItem().toString());
                    dataBits = Integer.parseInt(parentFrame.dataBitsField.getText());
                    stopBits = Integer.parseInt(parentFrame.stopBitsField.getText());
                    parity = parentFrame.paritySelector.getSelectedIndex();
                }
                queueManager = new RTUQueueManager(this);
                slave = new ModbusSlaveGatewayTCP(this);
            }
            catch (Exception e)
            {
                handleException(e);
            }
        }
        else
        {
            stopGateway();
        }
    }
    public void stopGateway()
    {
        if (slave != null)
        {
            try
            {
                slave.stop();
                queueManager.interrupt();
                slave = null;
                if (!isCommandLine)
                {
                    parentFrame.startStopButton.setText("Start Gateway");
                    parentFrame.statusLabel.setForeground(Color.red);
                    parentFrame.statusLabel.setText("Gateway is stopped");
                }
            }
            catch(java.io.IOException e)
            {
                handleException(e);
            }
        }
    }
    public void updateTrafficMonitor(String msg, byte[] buf)
    {
        updateTrafficMonitor(msg, buf, -1);
    }
    public void updateTrafficMonitor(String msg, byte[] buf, int length)
    {
        if (ModbusMechanic.debug || (!isCommandLine && parentFrame.gatewayMonitorCheckbox.isSelected()))
        {
            String hexMsg = "";
            int endIndex = buf.length;
            if (length != -1)
            {
                endIndex = length;
            }
            for (int i = 0; i<endIndex; i++)
            {
                hexMsg = hexMsg + String.format("%02X", buf[i]);
            }
            if ((!isCommandLine && parentFrame.gatewayMonitorCheckbox.isSelected()))
            {
                parentFrame.gatewayMonitorTextArea.setText(parentFrame.gatewayMonitorTextArea.getText() + msg + hexMsg + "\n");
                parentFrame.gatewayMonitorTextArea.setCaretPosition(parentFrame.gatewayMonitorTextArea.getText().length());
            }
            if (ModbusMechanic.debug)
            {
                System.out.println(msg + hexMsg);
            }
        }
    }
}
