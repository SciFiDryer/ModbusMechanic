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

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */

import java.util.concurrent.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import modbusmechanic.ModbusMechanic;
public class RTUQueueManager extends Thread
{
    GatewayManager manager = null;
    int responseBytes = 0;
    byte[] resp = null;
    public RTUQueueManager(GatewayManager aManager)
    {
        manager = aManager;
    }
    LinkedBlockingQueue queue = null;
    public synchronized void run()
    {
        queue = new LinkedBlockingQueue();
        try
        {
            OutputStream outStream = manager.port.getOutputStream();
            boolean isRunning = true;
            byte[] buf = null;
            OutputStream out = null;
            try
            {
                QueuedRequest req = (QueuedRequest)queue.take();
                buf = req.buf;
                out = req.out;
            }
            catch (InterruptedException e)
            {
                if (ModbusMechanic.debug)
                {
                    e.printStackTrace();
                }
                isRunning = false;
            }
            while (isRunning)
            {
                manager.updateTrafficMonitor("TCP Slave <<: ", buf);
                byte[] transactionId = Arrays.copyOfRange(buf, 0, 2);
                int slaveNode = buf[6];
                int functionCode = buf[7];
                int register = (buf[8]*256) + buf[9];
                resp = Arrays.copyOfRange(buf, 6, buf.length);

                ByteBuffer bb = ByteBuffer.allocate(resp.length+2);
                bb.put(resp);
                bb.put(GatewayThreadTCP.getCRC(resp));
                resp = bb.array();
                responseBytes = 0;
                manager.updateTrafficMonitor("RTU Master >>: ", resp);
                outStream.write(resp);
                outStream.flush();
                
                try
                {
                    wait(1000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (responseBytes == 0)
                {
                    baos.write(transactionId);
                    //protocol id
                    baos.write(ModbusMechanic.toU16(0));
                    //length
                    baos.write(ModbusMechanic.toU16(3));
                    baos.write(slaveNode);
                    functionCode = 10000000 | functionCode;
                    baos.write(functionCode);
                    baos.write(11);
                    baos.flush();
                    manager.updateTrafficMonitor("TCP Slave >>: ", baos.toByteArray());
                    out.write(baos.toByteArray());
                    out.flush();
                }
                if (responseBytes > 0)
                {
                    resp = Arrays.copyOfRange(resp, 0, responseBytes);
                    if (GatewayThreadTCP.checkCRC(resp))
                    {
                        int recvSlave = resp[0];
                        int recvFunctionCode = resp[1];
                        
                        if (recvSlave == slaveNode)
                        {
                            //We need to see if the MSB is set. For some reason java seems to always pad an 8 bit number to a 32 bit number
                            if (((recvFunctionCode & 0b00000000000000001000000000000000) == 32768) && ((0b00000000000000000000000001111111 & recvFunctionCode) == functionCode))
                            {
                                if (ModbusMechanic.debug)
                                {
                                    System.out.println("got RTU exception");
                                }
                                
                                baos.write(transactionId);
                                //protocol id
                                baos.write(ModbusMechanic.toU16(0));
                                //length
                                baos.write(ModbusMechanic.toU16(resp.length-2));
                                baos.write(resp,0, resp.length-2);
                                baos.flush();
                                manager.updateTrafficMonitor("TCP Slave >>: ", baos.toByteArray());
                                out.write(baos.toByteArray());
                                out.flush();
                            }
                            if (recvFunctionCode == functionCode)
                            {
                                baos.write(transactionId);
                                //protocol id
                                baos.write(ModbusMechanic.toU16(0));
                                //length
                                baos.write(ModbusMechanic.toU16(resp.length-2));
                                //rtu response
                                baos.write(resp,0, resp.length-2);
                                baos.flush();
                                manager.updateTrafficMonitor("TCP Slave >>: ", baos.toByteArray());
                                out.write(baos.toByteArray());
                                out.flush();
                            }
                        }
                    }
                }
                try
                {
                    QueuedRequest req = (QueuedRequest)queue.take();
                    buf = req.buf;
                    out = req.out;
                }
                catch (InterruptedException e)
                {
                    if (ModbusMechanic.debug)
                    {
                        e.printStackTrace();
                    }
                    isRunning = false;
                }
            }
        }
        catch(Exception e)
        {
            if (ModbusMechanic.debug)
            {
                e.printStackTrace();
            }
        }
    }
    public void addToQueue(QueuedRequest req)
    {
        try
        {
            queue.put(req);
        }
        catch (InterruptedException e)
        {
            if (ModbusMechanic.debug)
            {
                e.printStackTrace();
            }
        }
    }
    public void putMessage(int aResponseBytes, byte[] buf)
    {
        if (getState() == Thread.State.TIMED_WAITING)
        {
            responseBytes = aResponseBytes;
            resp = buf;
            synchronized (this)
            {
                notify();
            }
        }
    }
}
