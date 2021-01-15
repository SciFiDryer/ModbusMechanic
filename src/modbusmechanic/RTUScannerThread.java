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
import java.util.Arrays;
import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class RTUScannerThread extends Thread {
    RTUScannerFrame parentFrame = null;
    boolean keepScanning = true;
    public RTUScannerThread(RTUScannerFrame aParentFrame)
    {
        parentFrame = aParentFrame;
    }
    public void run()
    {
        try
        {
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(parentFrame.comPortSelector.getSelectedItem().toString());
            SerialPort port = (SerialPort)portId.open(this.getClass().getName(), 1000);
            port.setSerialPortParams(Integer.parseInt(parentFrame.baudRateSelector.getSelectedItem().toString()), Integer.parseInt(parentFrame.dataBitsField.getText()), Integer.parseInt(parentFrame.stopBitsField.getText()), parentFrame.paritySelector.getSelectedIndex());
            port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            int timeout = 20;
            timeout = Integer.parseInt(parentFrame.timeoutField.getText());
            OutputStream out = port.getOutputStream();
            InputStream in = port.getInputStream();
            for (int node = 1; node < 255 && keepScanning; node++)
            {
                boolean nodeFound = false;
                byte[] pingBytes = new byte[] { (byte)node, 0x03, 0x00, 0x01, 0x00, 0x01};
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(pingBytes);

                baos.write(modbusmechanic.gateway.GatewayThreadTCP.getCRC(pingBytes));
                out.write(baos.toByteArray());
                out.flush();
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < timeout && in.available() == 0)
                {
                }
                if (in.available() > 0)
                {
                    byte[] buf = new byte[1024];

                    int len = in.read(buf);
                    byte[] resp = new byte[len];
                    resp = Arrays.copyOfRange(buf, 0, len);
                    if (modbusmechanic.gateway.GatewayThreadTCP.checkCRC(resp))
                    {

                        parentFrame.sr.setNodeColor((int)resp[0], Color.GREEN);
                        nodeFound = true;

                    }
                }
                if (!nodeFound)
                {
                    parentFrame.sr.setNodeColor(node, Color.RED);
                }
                parentFrame.repaint();
            }
            port.close();
            parentFrame.scanButton.setText("Scan");
            parentFrame.repaint();
            keepScanning = false;
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        parentFrame.repaint();
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
