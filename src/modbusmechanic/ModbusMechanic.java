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
import com.intelligt.modbus.jlibmodbus.data.*;
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
    public static int RESPONSE_TYPE_BOOLEAN = 6;
    public static int READ_HOLDING_REGISTER_CODE = 3;
    public static int READ_COILS_CODE = 1;
    public static int READ_DI_CODE = 2;
    public static int READ_INPUT_REGISTER_CODE = 4;
    public static int WRITE_COILS_CODE = 15;
    public static int WRITE_HOLDING_REGISTERS_CODE = 16;
    public static int SLAVE_SIMULATOR_TCP = 1;
    public static int SLAVE_SIMULATOR_RTU = 2;
    public static int DATA_TYPE_FLOAT = 1;
    public static int DATA_TYPE_INT_16 = 2;
    public static int DATA_TYPE_INT_32 = 3;
    public static ModbusSlave slave = null;
    public static ByteBuffer hrBuf = null;
    public static ByteBuffer irBuf = null;
    public static ModbusCoils mc = new ModbusCoils(65535);
    public static ModbusCoils di = new ModbusCoils(65535);
    public static SlaveWatchWindow watchWindow = null;
    public static void main(String[] args)
    {
        new PacketFrame().setVisible(true);
        //Modbus.setLogLevel(Modbus.LogLevel.LEVEL_DEBUG);
        
    }
    public static String[] getPortNames()
    {
        String[] portStrings = new String[] { "N/A" };
        try
        {
            if (isSerialAvailable())
            {
                SerialUtils.setSerialPortFactory(new SerialPortFactoryPJC());
                Object[] ports = SerialUtils.getPortIdentifiers().toArray();
                portStrings = new String[ports.length];
                for (int i = 0; i < ports.length; i++)
                {
                    portStrings[i] = ports[i].toString();
                }
            }
        }
        catch (SerialPortException e)
        {
            e.printStackTrace();
        }
        
        return portStrings;
    }
    public static boolean isSerialAvailable()
    {
        try
        {
            Class.forName("purejavacomm.SerialPortEventListener");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static void startSerialMonitorFrame(String comPort, int baud, int dataBits, int stopBits, int parity)
    {
        SerialParameters serialParameters = new SerialParameters(comPort, castToBaud(baud), dataBits, stopBits, castToParity(parity));
        SerialUtils.setSerialPortFactory(new SerialPortFactoryPJC());
        SerialPort sp = null;
        ModbusConnection connection = null;
        try
        {
            sp = SerialUtils.createSerial(serialParameters);
            connection = ModbusConnectionFactory.getRTU(sp);
            connection.open();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        new SerialMonitorFrame(connection);
    }
    public static ModbusResponse generateModbusTCPRequest(String host, int port, int protocolId, int transactionId, int slaveNode, int functionCode, int register, int quantity, byte[] values) throws Exception
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
            //this should work but somehow doesn't
            //tcpParameters.setConnectionTimeout(3000);
            
            ModbusMaster master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
            master.setResponseTimeout(3000);
            if (transactionId == -1)
            {
                
            }
            
            try 
            {
                response = generateModbusMessage(master, protocolId, transactionId, slaveNode, functionCode, register, quantity, values);
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
    public static SerialPort.BaudRate castToBaud(int baudRate)
    {
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
            return baud;
    }
    public static SerialPort.Parity castToParity(int parityTmp)
    {
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
        return parity;
    }
    public static ModbusResponse generateModbusRTURequest(String comPort, int baudRate, int dataBits, int stopBits, int parityTmp, int slaveNode, int functionCode, int register, int quantity, byte[] values) throws Exception
    {
        ModbusResponse response = null;
        Exception raisedException = null;
        try {
            SerialPort.BaudRate baud = castToBaud(baudRate);
            SerialPort.Parity parity = castToParity(parityTmp);
            SerialParameters serialParameters = new SerialParameters(comPort, baud, dataBits, stopBits, parity);
            SerialUtils.setSerialPortFactory(new SerialPortFactoryPJC());
            ModbusMaster master = ModbusMasterFactory.createModbusMasterRTU(serialParameters);
            master.setResponseTimeout(3000);
            
            try 
            {
                response = generateModbusMessage(master, 0, 0, slaveNode, functionCode, register, quantity, values);
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
    public static ModbusResponse generateModbusMessage(ModbusMaster master, int protocolId, int transactionId, int slaveNode, int functionCode, int register, int quantity, byte[] values) throws Exception
    {
        
        ModbusResponse response = null;
        if (!master.isConnected())
        {
            master.connect();
        }
        AbstractMultipleRequest request = null;
        if (functionCode == 1)
        {
            request = new ReadCoilsRequest();
        }
        if (functionCode == 2)
        {
            request = new ReadDiscreteInputsRequest();
        }
        if (functionCode == 3)
        {
            request = new ReadHoldingRegistersRequest();
        }
        else if (functionCode == 4)
        {
            request = new ReadInputRegistersRequest();
        }
        else if (functionCode == 15)
        {
            request = new WriteMultipleCoilsRequest();
        }
        else if (functionCode == 16)
        {
            request = new WriteMultipleRegistersRequest();
        }
        request.setServerAddress(slaveNode);
        request.setStartAddress(register);
        request.setQuantity(quantity);
        if (request instanceof AbstractWriteMultipleRequest)
        {
            ((AbstractWriteMultipleRequest)(request)).setBytes(values);
        }
        if (master instanceof ModbusMasterTCP)
        {
            master.setTransactionId(transactionId);
            request.setProtocolId(protocolId);
        }
        response = master.processRequest(request);
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
    public static String byteToHex(byte[] buf)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++)
        {
            sb.append(String.format("%02X", buf[i]));
        }
        return sb.toString();
    }
    public static void startSlaveSimulatorTCP(int port, java.util.ArrayList registerList)
    {
         
        try
        {
            TcpParameters parameters = new TcpParameters();
            parameters.setHost(InetAddress.getLocalHost());
            parameters.setPort(port);
            slave = ModbusSlaveFactory.createModbusSlaveTCP(parameters);
            slave.setServerAddress(1);
            slave.setReadTimeout(30000);
            slave.setDataHolder(new SimulatorDataHolder(registerList));
            slave.listen();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void startSlaveSimulatorRTU(int serverAddress, String comPort, int baud, int dataBits, int stopBits, int parity)
    {
        try
        {
            SerialParameters parameters = new SerialParameters();
            parameters.setDevice(comPort);
            parameters.setBaudRate(castToBaud(baud));
            parameters.setDataBits(dataBits);
            parameters.setStopBits(stopBits);
            parameters.setParity(castToParity(parity));
            slave = ModbusSlaveFactory.createModbusSlaveRTU(parameters);
            slave.setServerAddress(serverAddress);
            slave.listen();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void setSimulatorRegisterValue(int function, int register, byte[] bytes)
    {
        try
        {
            if (function == 3)
            {
                if (hrBuf == null)
                {
                    hrBuf = ByteBuffer.allocate(131070);
                    for (int i = 0; i < hrBuf.capacity(); i++)
                    {
                        byte b = 0;
                        hrBuf.put(b);
                    }
                }
                ((Buffer)(hrBuf)).position(register*2);
                hrBuf.put(bytes);
                ModbusHoldingRegisters hr = new ModbusHoldingRegisters(65535);
                hr.setBytesBe(hrBuf.array());
                slave.getDataHolder().setHoldingRegisters(hr);
            }
            if (function == 4)
            {
                if (irBuf == null)
                {
                    irBuf = ByteBuffer.allocate(131070);
                    for (int i = 0; i < irBuf.capacity(); i++)
                    {
                        byte b = 0;
                        irBuf.put(b);
                    }
                }
                ((Buffer)(irBuf)).position(register*2);
                irBuf.put(bytes);
                ModbusHoldingRegisters ir = new ModbusHoldingRegisters(65535);
                ir.setBytesBe(irBuf.array());
                slave.getDataHolder().setInputRegisters(ir);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void setSimulatorCoilValue(int function, int register, boolean coil)
    {
        try
        {
            if (function == 1)
            {
                mc.setImpl(register, coil);
                slave.getDataHolder().setCoils(mc);
            }
            if (function == 2)
            {
                di.setImpl(register, coil);
                slave.getDataHolder().setDiscreteInputs(di);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void stopSlaveSimulator()
    {
        try
        {
            slave.shutdown();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static class SimulatorDataHolder extends DataHolder {
        
        java.util.ArrayList<SimulatorRegisterHolder> registerList = null;
        public SimulatorDataHolder(java.util.ArrayList aRegisterList) {
            registerList = aRegisterList;
        }
        
        @Override
        public void writeHoldingRegister(int offset, int value) throws IllegalDataAddressException, IllegalDataValueException {
            super.writeHoldingRegister(offset, value);

        }
        @Override
        public int readHoldingRegister(int offset) throws IllegalDataAddressException
        {
            printToWatchWindow("Holding register read request at offset " + offset + " single");
            return super.readHoldingRegister(offset);
        }
        @Override
        public int[] readHoldingRegisterRange(int offset, int quantity) throws IllegalDataAddressException
        {
            printToWatchWindow("Holding register read request at offset " + offset + " length " + quantity);
            return super.readHoldingRegisterRange(offset, quantity);
        }
        @Override
        public int[] readInputRegisterRange(int offset, int quantity) throws IllegalDataAddressException
        {
            printToWatchWindow("Input register read request at offset " + offset + " length " + quantity);
            return super.readInputRegisterRange(offset, quantity);
        }
        @Override
        public boolean[] readDiscreteInputRange(int offset, int quantity) throws IllegalDataAddressException, IllegalDataValueException
        {
            printToWatchWindow("Discrete input read request at offset " + offset + " length " + quantity);
            return super.readDiscreteInputRange(offset, quantity);
        }
        @Override
        public boolean[] readCoilRange(int offset, int quantity) throws IllegalDataAddressException, IllegalDataValueException
        {
            printToWatchWindow("Coil read request at offset " + offset + " length " + quantity);
            return super.readCoilRange(offset, quantity);
        }
        @Override
        public void writeHoldingRegisterRange(int offset, int[] range) throws IllegalDataAddressException, IllegalDataValueException {
            
            super.writeHoldingRegisterRange(offset, range);
            SimulatorRegisterHolder holder = ModbusMechanic.findRegisterHolderByRegister(registerList, 3, offset);
            ModbusMechanic.refreshRegisterHolder(holder);
            printToWatchWindow("Holding register write request at offset " + offset + " length " + range.length);
        }

        @Override
        public void writeCoil(int offset, boolean value) throws IllegalDataAddressException, IllegalDataValueException {
            super.writeCoil(offset, value);
        }

        @Override
        public void writeCoilRange(int offset, boolean[] range) throws IllegalDataAddressException, IllegalDataValueException {
            super.writeCoilRange(offset, range);
            SimulatorRegisterHolder holder = ModbusMechanic.findRegisterHolderByRegister(registerList, 1, offset);
            ModbusMechanic.refreshRegisterHolder(holder);
            printToWatchWindow("Coil write request at offset " + offset + " length " + range.length);
        }
    }
    public static SimulatorRegisterHolder findRegisterHolderByRegister(java.util.ArrayList<SimulatorRegisterHolder> registerList, int functionCode,  int registerNumber)
    {
        for (int i = 0; i < registerList.size(); i++)
        {
            SimulatorRegisterHolder currentHolder = registerList.get(i);
            if (currentHolder.getRegisterNumber() == registerNumber && currentHolder.getFunctionCode() == functionCode)
            {
                return currentHolder;
            }
        }
        return null;
    }
    public static void refreshRegisterHolder(SimulatorRegisterHolder holder)
    {
        int functionCode = holder.getFunctionCode();
        if (functionCode == ModbusMechanic.READ_HOLDING_REGISTER_CODE)
        {
            int dataType = holder.getDataType();
            if (dataType == ModbusMechanic.DATA_TYPE_FLOAT)
            {
                int registerNumber = holder.getRegisterNumber();
                byte[] registers = Arrays.copyOfRange(slave.getDataHolder().getHoldingRegisters().getBytes(), registerNumber*2, (registerNumber*2)+4);
                if (holder.getByteSwap())
                {
                    registers = ModbusMechanic.byteSwap(registers);
                }
                if (holder.getWordSwap())
                {
                    registers = ModbusMechanic.wordSwap(registers);
                }
                holder.getValueField().setText(DataUtils.toFloat(registers) + "");
            }
            if (dataType == ModbusMechanic.DATA_TYPE_INT_16)
            {
                int registerNumber = holder.getRegisterNumber();
                byte[] registers = Arrays.copyOfRange(slave.getDataHolder().getHoldingRegisters().getBytes(), registerNumber*2, (registerNumber*2)+2);
                if (holder.getByteSwap())
                {
                    registers = ModbusMechanic.byteSwap(registers);
                }
                holder.getValueField().setText(DataUtils.BeToIntArray(registers)[0] + "");
            }
            if (dataType == ModbusMechanic.DATA_TYPE_INT_32)
            {
                int registerNumber = holder.getRegisterNumber();
                byte[] registers = Arrays.copyOfRange(slave.getDataHolder().getHoldingRegisters().getBytes(), registerNumber*2, (registerNumber*2)+4);
                if (holder.getByteSwap())
                {
                    registers = ModbusMechanic.byteSwap(registers);
                }
                if (holder.getWordSwap())
                {
                    registers = ModbusMechanic.wordSwap(registers);
                }
                holder.getValueField().setText(ModbusMechanic.bytesToInt32(registers) + "");
            }
        }
        if (functionCode == ModbusMechanic.READ_COILS_CODE)
        {
            int registerNumber = holder.getRegisterNumber();
            try
            {
                boolean coilValue = mc.get(registerNumber);
                holder.getTrueButton().setSelected(coilValue);
                holder.getFalseButton().setSelected(!coilValue);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public static void printToWatchWindow(String msg)
    {
        if (watchWindow != null)
        {
            watchWindow.msgTextArea.setText(watchWindow.msgTextArea.getText() + msg + "\n");
            if (watchWindow.autoScrollCheckbox.isSelected())
            {
                watchWindow.msgTextArea.setCaretPosition(watchWindow.msgTextArea.getText().length());
            }
        }
    }
    public static byte[] floatToBytes(String floatValue)     
    {
        float theFloat = Float.parseFloat(floatValue);
        byte[] buf = java.nio.ByteBuffer.allocate(4).putFloat(theFloat).array();
        buf = ModbusMechanic.wordSwap(buf);
        return buf;
    }
    public static byte[] int32ToBytes(String int32Value)
    {
        long intValue = Long.parseLong(int32Value);
        byte[] buf = java.nio.ByteBuffer.allocate(8).putLong(intValue).array();
        buf = java.util.Arrays.copyOfRange(buf, 4, 8);
        return buf;
    }
    public static byte[] int16ToBytes(String int16Value)
    {
        int intValue = Integer.parseInt(int16Value);
        byte[] buf = java.nio.ByteBuffer.allocate(4).putInt(intValue).array();
        buf = java.util.Arrays.copyOfRange(buf, 2, 4);
        return buf;
    }
    public static long bytesToInt32(byte[] buf)
    {
        int[] regs = DataUtils.BeToIntArray(buf);
        return ((long)regs[0]*65536) + (long)regs[1];
    }
}
