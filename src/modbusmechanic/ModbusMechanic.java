/* 
 * Copyright 2019 Matt Jamesson <scifidryer@gmail.com>.
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
import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.Arrays;
import com.intelligt.modbus.jlibmodbus.*;
import com.intelligt.modbus.jlibmodbus.exception.*;
import com.intelligt.modbus.jlibmodbus.master.*;
import com.intelligt.modbus.jlibmodbus.slave.*;
import com.intelligt.modbus.jlibmodbus.msg.request.*;
import com.intelligt.modbus.jlibmodbus.msg.*;
import com.intelligt.modbus.jlibmodbus.msg.base.*;
import com.intelligt.modbus.jlibmodbus.msg.response.*;
import com.intelligt.modbus.jlibmodbus.net.ModbusConnectionFactory;
import com.intelligt.modbus.jlibmodbus.tcp.*;
import com.intelligt.modbus.jlibmodbus.serial.*;
import com.intelligt.modbus.jlibmodbus.utils.DataUtils;
import com.intelligt.modbus.jlibmodbus.utils.*;
import com.intelligt.modbus.jlibmodbus.net.*;
import com.intelligt.modbus.jlibmodbus.net.stream.*;
import com.intelligt.modbus.jlibmodbus.net.stream.base.*;
import javax.swing.JOptionPane;
import javax.xml.bind.DatatypeConverter;
/**
 *
 * @author Matt Jamesson
 */
public class ModbusMechanic {

    /**
     * @param args the command line arguments
     */
    public static int RESPONSE_TYPE_ASCII = 1;
    public static int RESPONSE_TYPE_FLOAT = 2;
    public static int RESPONSE_TYPE_UINT16 = 3;
    public static int RESPONSE_TYPE_UINT32 = 4;
    public static int RESPONSE_TYPE_RAW = 5;
    public static int HOLDING_REGISTER_CODE = 3;
    public static int INPUT_REGISTER_CODE = 4;
    public static void main(String[] args)
    {
        new PacketFrame().setVisible(true);
        //Modbus.setLogLevel(Modbus.LogLevel.LEVEL_DEBUG);
        
    }
    
    public static ModbusResponse generateModbusTCPRequest(String host, int port, int protocolId, int transactionId, int slaveNode, int functionCode, int register, int quantity) throws Exception
    {
        byte[] buf = null;
        ModbusResponse response = null;
        Exception raisedException = null;
        try
        {
            TcpParameters tcpParameters = new TcpParameters();
            tcpParameters.setHost(InetAddress.getByName(host));
            tcpParameters.setKeepAlive(true);
            tcpParameters.setPort(Modbus.TCP_PORT);
            tcpParameters.setConnectionTimeout(3000);
            
            ModbusMaster master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
            master.setResponseTimeout(3000);
            if (transactionId == -1)
            {
                
            }
            
            try 
            {
                response = generateModbusMessage(master, protocolId, transactionId, slaveNode, functionCode, register, quantity);
            }
            catch(Exception e)
            {
                raisedException = e;
            }
            finally
            {
                try
                {
                    master.disconnect();
                }
                catch (ModbusIOException e)
                {
                    e.printStackTrace();
                }
                
            }
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (raisedException != null)
        {
            throw raisedException;
        }
        return response;
        
    }
    public static ModbusResponse generateModbusRTURequest(String comPort, int baudRate, int dataBits, int stopBits, int parityTmp, int slaveNode, int functionCode, int register, int quantity) throws Exception
    {
        ModbusResponse response = null;
        Exception raisedException = null;
        try {
            SerialPort.BaudRate baud = null;
            if (baudRate == 4800)
            {
                baud = SerialPort.BaudRate.BAUD_RATE_4800;
            }
            if (baudRate == 9600)
            {
                baud = SerialPort.BaudRate.BAUD_RATE_9600;
            }
            if (baudRate == 14400)
            {
                baud = SerialPort.BaudRate.BAUD_RATE_14400;
            }
            if (baudRate == 19200)
            {
                baud = SerialPort.BaudRate.BAUD_RATE_19200;
            }
            if (baudRate == 38400)
            {
                baud = SerialPort.BaudRate.BAUD_RATE_38400;
            }
            if (baudRate == 57600)
            {
                baud = SerialPort.BaudRate.BAUD_RATE_57600;
            }
            SerialPort.Parity parity = null;
            if (parityTmp == 0)
            {
                parity = SerialPort.Parity.NONE;
            }
            if (parityTmp == 1)
            {
                parity = SerialPort.Parity.ODD;
            }
            if (parityTmp == 2)
            {
                parity = SerialPort.Parity.EVEN;
            }
            if (parityTmp == 3)
            {
                parity = SerialPort.Parity.MARK;
            }
            if (parityTmp == 4)
            {
                parity = SerialPort.Parity.SPACE;
            }
            SerialParameters serialParameters = new SerialParameters(comPort, baud, dataBits, stopBits, parity);
            SerialUtils.setSerialPortFactory(new SerialPortFactoryJSSC());
            ModbusMaster master = ModbusMasterFactory.createModbusMasterRTU(serialParameters);
            master.setResponseTimeout(3000);
            
            try 
            {
                response = generateModbusMessage(master, 0, 0, slaveNode, functionCode, register, quantity);
            }
            catch (Exception e)
            {
                raisedException = e;
            }
            finally
            {
                try
                {
                    master.disconnect();
                }
                catch (ModbusIOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (raisedException != null)
        {
            throw raisedException;
        }
        return response;
    }
    //todo ideally this should be wrapped into an abstraction in case a different library is desired
    public static ModbusResponse generateModbusMessage(ModbusMaster master, int protocolId, int transactionId, int slaveNode, int functionCode, int register, int quantity) throws Exception
    {
        
        ModbusResponse response = null;
        if (!master.isConnected())
        {
            master.connect();
        }
        if (functionCode == 3)
        {
            ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest();
            request.setServerAddress(slaveNode);
            request.setStartAddress(register);
            request.setQuantity(quantity);
            if (master instanceof ModbusMasterTCP)
            {
                master.setTransactionId(transactionId);
                request.setProtocolId(protocolId);
            }
            response = master.processRequest(request);
        }
        else if (functionCode == 4)
        {
            ReadInputRegistersRequest request = new ReadInputRegistersRequest();
            request.setServerAddress(slaveNode);
            request.setStartAddress(register);
            request.setQuantity(quantity);
            if (master instanceof ModbusMasterTCP)
            {
                master.setTransactionId(transactionId);
                request.setProtocolId(protocolId);
            }
            response = master.processRequest(request);
        }
        return response;
    }
    public static byte[] toU16(int i)
    {
        byte[] b = new byte[2];
        b[0] = (byte) ((i >> 8) & 0xFF);
        b[1] = (byte) (i & 0xFF);
        return b;
    }
    public static byte[] byteSwap(byte[] buf)
    {
        byte[] returnBuf = new byte[buf.length];
        for (int i = 0; i < buf.length; i = i + 2)
        {
            returnBuf[i] = buf[i+1];
            returnBuf[i+1] = buf[i];
        }
        return returnBuf;
    }
    public static byte[] wordSwap(byte[] buf)
    {
        byte[] returnBuf = new byte[buf.length];
        for (int i = 0; i < buf.length; i = i + 4)
        {
            returnBuf[i] = buf[i+2];
            returnBuf[i+1] = buf[i+3];
            returnBuf[i+2] = buf[i];
            returnBuf[i+3] = buf[i+1];
        }
        return returnBuf;
    }
}
