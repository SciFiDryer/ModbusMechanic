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

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;
import javax.swing.*;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class RTUScannerThread extends Thread {
    RTUScannerFrame parentFrame = null;
    boolean keepScanning = true;
    int functionCode = 3;
    String scanRegister = "";
    InputStream in = null;
    OutputStream out = null;
    ArrayList<Integer> secondAttempts = new ArrayList();
    public RTUScannerThread(RTUScannerFrame aParentFrame)
    {
        parentFrame = aParentFrame;
    }
    public void run()
    {
        try
        {
            functionCode = parentFrame.functionCodeSelector.getSelectedIndex()+1;
            scanRegister = parentFrame.scanRegisterField.getText();
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(parentFrame.comPortSelector.getSelectedItem().toString());
            SerialPort port = (SerialPort)portId.open(this.getClass().getName(), 1000);
            port.setSerialPortParams(Integer.parseInt(parentFrame.baudRateSelector.getSelectedItem().toString()), Integer.parseInt(parentFrame.dataBitsField.getText()), Integer.parseInt(parentFrame.stopBitsField.getText()), parentFrame.paritySelector.getSelectedIndex());
            port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            long timeout = 50;
            timeout = Integer.parseInt(parentFrame.timeoutField.getText());
            out = port.getOutputStream();
            in = port.getInputStream();
            for (int node = 1; node < 248 && keepScanning; node++)
            {
                scanNode(node, functionCode, scanRegister, timeout, false);
                parentFrame.repaint();
            }
            for (int i = 0; i < secondAttempts.size(); i++)
            {
                scanNode(secondAttempts.get(i).intValue(), functionCode, scanRegister, timeout, true);
                parentFrame.repaint();
            }
            port.close();
            parentFrame.scanButton.setText("Scan");
            parentFrame.repaint();
            keepScanning = false; 
        }
        catch (Exception e)
        {
            if (e instanceof purejavacomm.PortInUseException)    
            {
                JOptionPane.showMessageDialog(null, "Port in use or insufficient permissions. If the port is not in use running as administrator or root can elevate permissions.\nOn Linux adding the user to the dialout group may elevate permissions.", "Serial port error", JOptionPane.ERROR_MESSAGE);
            }
            if (ModbusMechanic.debug)
            {
                e.printStackTrace();
            }
            parentFrame.scanButton.setText("Scan");
            keepScanning = false; 
        }
        parentFrame.repaint();
    }
    public void scanNode(int node, int functionCode, String scanRegister, long timeout, boolean secondAttempt) throws Exception
    {
        if (secondAttempt)
        {
            if (timeout < 100)
            {
                timeout = 100 + timeout;
            }
            else
            {
                timeout = timeout * 2;
            }
        }
        if (ModbusMechanic.debug)
        {
            System.out.println("Node: " + node);
        }
        boolean nodeFound = false;
        byte[] regBytes = ModbusMechanic.int16ToBytes(scanRegister);
        byte[] pingBytes = new byte[] { (byte)node, (byte)functionCode, regBytes[0], regBytes[1], 0x00, 0x01};
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(pingBytes);
        baos.write(modbusmechanic.gateway.GatewayThreadTCP.getCRC(pingBytes));
        long nanoStart = System.nanoTime();
        if (ModbusMechanic.debug)
        {
            System.out.println("Preparing write");
        }
        out.write(baos.toByteArray());
        out.flush();
        if (ModbusMechanic.debug)
        {
            System.out.println("Flushed output buffer");
        }
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeout && in.available() == 0)
        {
        }
        long pingTime = System.nanoTime() - nanoStart;
        boolean bytesRecvd = false;
        if (in.available() > 0)
        {
            byte[] buf = new byte[1024];

            int len = in.read(buf);
            if (ModbusMechanic.debug)
            {
                System.out.println("Got " + len + " bytes on wire");
            }
            bytesRecvd = true;
            if (len > 3)
            {
                byte[] resp = new byte[len];
                resp = Arrays.copyOfRange(buf, 0, len);
                if (modbusmechanic.gateway.GatewayThreadTCP.checkCRC(resp))
                {
                    if (ModbusMechanic.debug)
                    {
                        System.out.println("Checksum passed, valid response from node " + (int)resp[0]);
                    }
                    parentFrame.sr.setCellAttribs((int)resp[0], Color.GREEN, Math.floor(pingTime/100000) / 10 + "ms");
                    if ((int)resp[0] == node)
                    {
                        nodeFound = true;
                    }

                }
            }
        }
        if (bytesRecvd && !nodeFound && !secondAttempt)
        {
            addToSecondAttempts(node);
            if (node != 1 && parentFrame.sr.getNodeColor(node-1) != Color.GREEN)
            {
                addToSecondAttempts(node-1);
            }
        }
        if (!nodeFound)
        {
            parentFrame.sr.setCellAttribs(node, Color.RED, "Node not active");
        }
    }
    public void addToSecondAttempts(int node)
    {
        boolean nodeExists = false;
        for (int i = 0; i < secondAttempts.size(); i++)
        {
            if (secondAttempts.get(i).intValue() == node)
            {
                nodeExists = true;
            }
        }
        if (!nodeExists)
        {
            secondAttempts.add(node);
        }
    }
    public void stopRunning()
    {
        keepScanning = false;
    }
    public boolean isRunning()
    {
        return keepScanning;
    }
}
