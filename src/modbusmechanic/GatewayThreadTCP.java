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
import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.data.DataHolder;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.msg.base.ModbusMessage;
import com.intelligt.modbus.jlibmodbus.msg.base.ModbusRequest;
import com.intelligt.modbus.jlibmodbus.net.ModbusConnectionFactory;
import com.intelligt.modbus.jlibmodbus.net.transport.ModbusTransport;
import com.intelligt.modbus.jlibmodbus.slave.RequestHandler;
import com.intelligt.modbus.jlibmodbus.msg.base.*;
import com.intelligt.modbus.jlibmodbus.utils.CRC16;
import purejavacomm.*;
import java.nio.*;

import java.io.IOException;
import java.net.Socket;
import java.io.*;
import java.util.Arrays;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class GatewayThreadTCP implements Runnable {
    Socket s = null;
    SerialPort sp = null;
    
    GatewayThreadTCP(Socket aS, SerialPort aSp) {
        s = aS;
        sp = aSp;
    }
    public void run()
    {
        try 
        {
            byte[] buf = new byte[1024];
            InputStream in = s.getInputStream();
            OutputStream out = s.getOutputStream();
            int bytesRead = in.read(buf);
            while (bytesRead > 0)
            {
                //transaction id bytes 0-1
                byte[] transactionId = Arrays.copyOfRange(buf, 0, 2);
                //protocol id bytes 2-3
                //length bytes 4-5
                int length = (buf[4] *256) + buf[5];
                if (length == bytesRead-6)
                {
                    
                    //good packet length wise
                    if (ModbusMechanic.debug)
                    {
                        System.out.println("TCP frame recieved");
                    }
                    int slaveNode = buf[6];
                    int functionCode = buf[7];
                    int register = (buf[8]*256) + buf[9];
                    //int quantity = (buf[10]*65536) + buf[11];
                    
                    OutputStream outStream = sp.getOutputStream();
                    InputStream inStream = sp.getInputStream();
                    sp.enableReceiveTimeout(1000);
                    byte[] resp = Arrays.copyOfRange(buf, 6, bytesRead);
                    
                    ByteBuffer bb = ByteBuffer.allocate(resp.length+2);
                    bb.put(resp);
                    bb.put(getCRC(resp));
                    resp = bb.array();
                    outStream.write(resp);
                    outStream.flush();
                    int responseBytes = 0;
                    try
                    {
                        responseBytes = inStream.read(buf);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    
                    if (responseBytes == 0)
                    {
                        out.write(transactionId);
                        //protocol id
                        out.write(ModbusMechanic.toU16(0));
                        //length
                        out.write(ModbusMechanic.toU16(3));
                        out.write(slaveNode);
                        functionCode = 10000000 | functionCode;
                        out.write(functionCode);
                        out.write(11);
                        out.flush();
                    }
                    if (responseBytes > 0)
                    {
                        resp = Arrays.copyOfRange(buf, 0, responseBytes);

                        if (checkCRC(resp))
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
                                    out.write(transactionId);
                                    //protocol id
                                    out.write(ModbusMechanic.toU16(0));
                                    //length
                                    out.write(ModbusMechanic.toU16(resp.length-2));
                                    out.write(resp,0, resp.length-2);
                                }
                                if (recvFunctionCode == functionCode)
                                {
                                    out.write(transactionId);
                                    //protocol id
                                    out.write(ModbusMechanic.toU16(0));
                                    //length
                                    out.write(ModbusMechanic.toU16(resp.length-2));
                                    //rtu response
                                    out.write(resp,0, resp.length-2);
                                    out.flush();
                                }
                            }
                        }
                    }
                }
                bytesRead = in.read(buf);
            }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static byte[] getCRC(byte[] b)
    {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.putShort((short)CRC16.calc(b));
        byte[] crc = bb.array();
        return new byte[] {crc[1], crc[0]};
    }
    public static boolean checkCRC(byte[] b)
    {
        byte[] crc = getCRC(Arrays.copyOfRange(b, 0, b.length-2));
        byte[] frameCrc = new byte[] {b[b.length-2], b[b.length-1]};
        if (crc[0] == frameCrc[0] && crc[1] == frameCrc[1])
        {
            return true;
        }
        return false;
    }
}
