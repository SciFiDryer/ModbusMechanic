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
package modbusmechanic;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.intelligt.modbus.jlibmodbus.slave.*;
import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class ModbusSlaveGatewayTCP implements Runnable {
    int threadPoolSize = 10;
    ServerSocket ss = null;
    ExecutorService threadPool = null;
    boolean isRunning = true;
    GatewayManager manager = null;
    SerialPort port = null;
    public ModbusSlaveGatewayTCP(GatewayManager aManager) throws IOException
    {
        manager = aManager;
        ss = new ServerSocket(manager.tcpPort);
        threadPool = Executors.newFixedThreadPool(threadPoolSize);
        new Thread(this).start();
    }
    public void run()
    {
        try
        {
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(manager.comPort);
            if (portId.isCurrentlyOwned())
            {
                setRunning(false);
                manager.handleException(new Exception("Port in use"));
            }
            else
            {
                port = (SerialPort)portId.open(this.getClass().getName(), 1000);
                port.setSerialPortParams(manager.baud, manager.dataBits, manager.stopBits, manager.parity);
                port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                manager.handleGatewayReady();
                while (isRunning)
                {
                    Socket s = ss.accept();
                    threadPool.execute(new GatewayThreadTCP(s, port));
                }
            }
        }
        catch(Exception e)
        {
            if (isRunning)
            {
                manager.handleException(e);
            }
        }
    }
    public void setRunning(boolean runningTmp)
    {
        isRunning = runningTmp;
    }
    public void stop() throws IOException
    {
        setRunning(false);
        port.close();
        ss.close();
    }
}
