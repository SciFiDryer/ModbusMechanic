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

import com.intelligt.modbus.jlibmodbus.master.*;
import com.intelligt.modbus.jlibmodbus.msg.base.ModbusResponse;
import com.intelligt.modbus.jlibmodbus.data.*;
import com.intelligt.modbus.jlibmodbus.msg.response.ReadHoldingRegistersResponse;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import com.intelligt.modbus.jlibmodbus.slave.*;
import java.net.InetAddress;
import java.util.*;
import modbusmechanic.*;
import static modbusmechanic.ModbusMechanic.generateModbusMessage;
import modbusmechanic.bridge.*;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class ModbusProtocolDriver implements ProtocolDriver{
    boolean enabled = false;
    ArrayList<ModbusHostRecord> incomingSlaveList = new ArrayList();
    ArrayList<ModbusHostRecord> outgoingSlaveList = new ArrayList();
    ArrayList<LocalModbusSlave> localRunningSlaves = new ArrayList();
    BridgeManager manager = null;
    public ModbusProtocolDriver(BridgeManager aManager)
    {
        manager = aManager;
    }
    public boolean getEnabled()
    {
        return enabled;
    }
    public void setEnabled(boolean aEnabled)
    {
        enabled = aEnabled;
    }
    public void driverInit()
    {
        buildIncomingSlaveTable();
        buildOutgoingSlaveTable();
        startLocalSlaves();
    }
    public void getIncomingRecords()
    {
        buildIncomingSlaveTable();
        fetchIncomingData();
    }
    public void sendOutgoingRecords()
    {
        buildOutgoingSlaveTable();
        sendOutgoingData();
    }
    public void startLocalSlaves()
    {
        for (int i = 0; i < incomingSlaveList.size(); i++)
        {
            if (incomingSlaveList.get(i).hostType == ModbusHostRecord.HOST_TYPE_LOCAL_SLAVE)
            {
                startLocalSlave(incomingSlaveList.get(i));
            }
        }
        for (int i = 0; i < outgoingSlaveList.size(); i++)
        {
            if (outgoingSlaveList.get(i).hostType == ModbusHostRecord.HOST_TYPE_LOCAL_SLAVE)
            {
                startLocalSlave(outgoingSlaveList.get(i));
            }
        }
    }
    public void startLocalSlave(ModbusHostRecord slaveRecord)
    {
        try
        {
            TcpParameters parameters = new TcpParameters();
            parameters.setHost(InetAddress.getLocalHost());
            parameters.setPort(slaveRecord.port);
            ModbusSlave slave = ModbusSlaveFactory.createModbusSlaveTCP(parameters);
            slave.setServerAddress(1);
            slave.setReadTimeout(30000);
            //slave.setDataHolder(new SimulatorDataHolder(registerList));
            slave.listen();
            byte[] buf = new byte[131070];
            for (int j = 0; j < buf.length; j++)
            {
                byte b = 0;
                buf[j] = b;
            }
            DataHolder dh = new DataHolder();
            ModbusHoldingRegisters hr = new ModbusHoldingRegisters();
            hr.setBytesBe(buf);
            dh.setHoldingRegisters(hr);
            slave.setDataHolder(dh);
            localRunningSlaves.add(new LocalModbusSlave(slave, slaveRecord.port));
            slaveRecord.localSlave = slave;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void buildIncomingSlaveTable()
    {
        incomingSlaveList.clear();
        for (int i = 0; i < manager.mappingRecords.size(); i++)
        {
            if (manager.mappingRecords.get(i).incomingRecord instanceof ModbusProtocolRecord)
            {
                ModbusProtocolRecord currentRecord = (ModbusProtocolRecord)manager.mappingRecords.get(i).incomingRecord;
                addToModbusList(incomingSlaveList, currentRecord);
            }
        }
    }
    public void addToModbusList(ArrayList<ModbusHostRecord> currentList, ModbusProtocolRecord currentRecord)
    {
        boolean foundRecord = false;
        for (int i = 0; i < currentList.size() && !foundRecord; i++)
        {
            if (currentRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_MASTER && currentRecord.slaveHost.equals(currentList.get(i).hostname) && currentRecord.slavePort == currentList.get(i).port)
            {
                foundRecord = true;
                for (int j = 0; j < currentRecord.quantity; j++)
                {
                    RegisterRecord rr = new RegisterRecord(currentRecord.functionCode, j+currentRecord.startingRegister);
                    if (currentRecord.rawValue != null)
                    {
                        rr.value = Arrays.copyOfRange(currentRecord.rawValue, j*2, (j*2)+2);
                    }
                    currentList.get(i).registerRecords.add(rr);
                }
            }
            else if (currentRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_SLAVE && currentRecord.slavePort == currentList.get(i).port)
            {
                for (int j = 0; j < currentRecord.quantity; j++)
                {
                    RegisterRecord rr = new RegisterRecord(currentRecord.functionCode, j+currentRecord.startingRegister);
                    if (currentRecord.rawValue != null)
                    {
                        rr.value = Arrays.copyOfRange(currentRecord.rawValue, j*2, (j*2)+2);
                    }
                    currentList.get(i).registerRecords.add(rr);
                    associateSlave(currentList.get(i));
                }
            }
        }
        if (!foundRecord)
        {
            ModbusHostRecord mhr = null;
            if (currentRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_MASTER)
            {
                mhr = new ModbusHostRecord(ModbusHostRecord.HOST_TYPE_REMOTE_SLAVE, currentRecord.slaveHost, currentRecord.slavePort);
            }
            if (currentRecord.protocolType == ModbusProtocolRecord.PROTOCOL_TYPE_SLAVE)
            {
                mhr = new ModbusHostRecord(ModbusHostRecord.HOST_TYPE_LOCAL_SLAVE, null, currentRecord.slavePort);
                associateSlave(mhr);
            }
            for (int j = 0; j < currentRecord.quantity; j++)
            {
                RegisterRecord rr = new RegisterRecord(currentRecord.functionCode, j+currentRecord.startingRegister);
                if (currentRecord.rawValue != null)
                {
                    rr.value = Arrays.copyOfRange(currentRecord.rawValue, j*2, j*2+2);
                }
                mhr.registerRecords.add(rr);
            }
            currentList.add(mhr);
        }
    }
    public void associateSlave(ModbusHostRecord currentRecord)
    {
        for (int i = 0; i < localRunningSlaves.size(); i++)
        {
            if (localRunningSlaves.get(i).port == currentRecord.port)
            {
                currentRecord.localSlave = localRunningSlaves.get(i).localSlave;
            }
        }
    }
    public void fetchIncomingData()
    {
        for (int i = 0; i < incomingSlaveList.size(); i++)
        {
            ModbusHostRecord currentSlave = incomingSlaveList.get(i);
            if (currentSlave.hostType == ModbusHostRecord.HOST_TYPE_REMOTE_SLAVE)
            {
                remoteSlaveFetch(currentSlave);
            }
            if (currentSlave.hostType == ModbusHostRecord.HOST_TYPE_LOCAL_SLAVE)
            {
                localSlaveFetch(currentSlave);
            }
        }
    }
    public void remoteSlaveFetch(ModbusHostRecord currentSlave)
    {
        byte[] buf = null;
        ModbusResponse response = null;
        Exception raisedException = null;
        ModbusMaster master = null;
        try
        {
            TcpParameters tcpParameters = new TcpParameters();
            tcpParameters.setHost(InetAddress.getByName(currentSlave.hostname));
            tcpParameters.setKeepAlive(true);
            tcpParameters.setPort(currentSlave.port);
            //this should work but somehow doesn't
            //tcpParameters.setConnectionTimeout(3000);

            master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
            master.setResponseTimeout(5000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //holding registers
        modbusBlockRead(3, master, currentSlave);
        //input registers
        modbusBlockRead(4, master, currentSlave);
        try
        {
            System.out.println("disconnecting");
            master.disconnect();
        }
        catch (Exception e)
        {
            if (ModbusMechanic.debug)
            {
                e.printStackTrace();
            }
        }
    }
    public void modbusBlockRead(int functionCode, ModbusMaster master, ModbusHostRecord currentSlave)
    {
        int[] registers = getSortedRegisters(functionCode, currentSlave);
        ModbusResponse response = null;
        int startingRegister = 0;
        int quantity = 0;
        for (int j = 0; j < registers.length; j++)
        {
            startingRegister = registers[j];
            quantity = 1;
            for (int k = 0; k < 1024 && k+j < registers.length; k++)
            {
                int calcDistance = registers[j+k]-registers[j] + 1;
                if (calcDistance < 1024)
                {
                    quantity = calcDistance;
                    j = j+k;
                }
            }
            try 
            {
                response = generateModbusMessage(master, 0, 1, 1, functionCode, startingRegister, quantity, null);
            }
            catch(Exception e)
            {
                if (ModbusMechanic.debug)
                {
                    e.printStackTrace();
                }
            }
            if (response != null)
            {
                byte[] buf = ((ReadHoldingRegistersResponse)(response)).getBytes();
                for (int k = 0; k < quantity; k++)
                {
                    byte[] value = Arrays.copyOfRange(buf, k*2, k*2+2);
                    currentSlave.insertRegisterValue(functionCode, k+startingRegister, value);
                }
            }
        }
    }
    public void localSlaveFetch(ModbusHostRecord currentSlave)
    {
        byte[] buf = null;
        ModbusResponse response = null;
        Exception raisedException = null;
        int[] registers = getSortedRegisters(3, currentSlave);
        
        int quantity = 0;
        for (int j = 0; j < registers.length; j++)
        {
            byte[] registerBuf = currentSlave.localSlave.getDataHolder().getHoldingRegisters().getBytes();
            buf = Arrays.copyOfRange(registerBuf, registers[j]*2, (registers[j]*2)+2);
            currentSlave.insertRegisterValue(3, registers[j], buf);
        }
    }
    public int[] getSortedRegisters(int functionCode, ModbusHostRecord mhr)
    {
        int[] returnArray = new int[] {};
        for (int i = 0; i < mhr.registerRecords.size(); i++)
        {
            if (mhr.registerRecords.get(i).functionCode == functionCode)
            {
                returnArray = Arrays.copyOf(returnArray, returnArray.length+1);
                returnArray[i] = mhr.registerRecords.get(i).register;
            }
        }
        Arrays.sort(returnArray);
        return returnArray;
    }
    public void mapIncomingValues()
    {
        for (int i = 0; i < manager.bridgeMapList.size(); i++)
        {
            if (manager.mappingRecords.get(i).incomingRecord instanceof ModbusProtocolRecord)
            {
                ModbusProtocolRecord incomingRecord = (ModbusProtocolRecord)manager.mappingRecords.get(i).incomingRecord;
                incomingRecord.rawValue = getModbusValue(incomingRecord.slaveHost, incomingRecord.slavePort, incomingRecord.functionCode, incomingRecord.startingRegister, incomingRecord.quantity);
            }
            if (manager.mappingRecords.get(i).modbusBlockRemap)
            {
                ModbusProtocolRecord outgoingRecord = (ModbusProtocolRecord)manager.mappingRecords.get(i).outgoingRecord;
                outgoingRecord.rawValue = ((ModbusProtocolRecord)manager.mappingRecords.get(i).incomingRecord).rawValue;
            }
        }
    }
    public byte[] getModbusValue(String host, int port, int functionCode, int startingRegister, int quantity)
    {
        for (int i = 0; i < incomingSlaveList.size(); i++)
        {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            ModbusHostRecord currentSlave = incomingSlaveList.get(i);
            if (currentSlave.port == port && (currentSlave.hostType == ModbusHostRecord.HOST_TYPE_LOCAL_SLAVE || (currentSlave.hostname.equals(host) && currentSlave.hostType == ModbusHostRecord.HOST_TYPE_REMOTE_SLAVE)) )
            {
                for (int j = 0; j < currentSlave.registerRecords.size(); j++)
                {
                    RegisterRecord rr = currentSlave.registerRecords.get(j);
                    if (rr.register >= startingRegister && rr.register < startingRegister + quantity)
                    {
                        try
                        {
                            baos.write(rr.value);
                            baos.flush();
                        }
                        catch (Exception e)
                        {
                            if (ModbusMechanic.debug)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return baos.toByteArray();
            }
        }
        return null;
    }
    public void buildOutgoingSlaveTable()
    {
        outgoingSlaveList.clear();
        for (int i = 0; i < manager.mappingRecords.size(); i++)
        {
            if (manager.mappingRecords.get(i).outgoingRecord instanceof ModbusProtocolRecord)
            {
                ModbusProtocolRecord currentRecord = (ModbusProtocolRecord)manager.mappingRecords.get(i).outgoingRecord;
                
                addToModbusList(outgoingSlaveList, currentRecord);
            }
        }
    }
    
    public void sendOutgoingData()
    {
        for (int i = 0; i < outgoingSlaveList.size(); i++)
        {
            ModbusHostRecord currentRecord = outgoingSlaveList.get(i);
            if (currentRecord.hostType == ModbusHostRecord.HOST_TYPE_REMOTE_SLAVE)
            {
                sendToRemoteSlave(currentRecord);
            }
            if (currentRecord.hostType == ModbusHostRecord.HOST_TYPE_LOCAL_SLAVE)
            {
                sendToLocalSlave(currentRecord);
            }
        }
    }
    public void sendToLocalSlave(ModbusHostRecord currentSlave)
    {
        
        for (int i = 0; i < currentSlave.registerRecords.size(); i++)
        {
            byte[] registerBuf = null;
            if (currentSlave.registerRecords.get(i).functionCode == 3)
            {
                registerBuf = currentSlave.localSlave.getDataHolder().getHoldingRegisters().getBytes();
            }
            setLocalSlaveRegister(currentSlave.registerRecords.get(i).register, currentSlave.registerRecords.get(i).value, registerBuf);
            currentSlave.localSlave.getDataHolder().getHoldingRegisters().setBytesBe(registerBuf);
        }
    }
    public void setLocalSlaveRegister(int register, byte[] value, byte[] buffer)
    {
        for (int i = 0; i < value.length; i++)
        {
            buffer[register*2+i] = value[i];
        }
    }
    public void sendToRemoteSlave(ModbusHostRecord currentSlave)
    {
        byte[] buf = null;
        ModbusResponse response = null;
        Exception raisedException = null;
        ModbusMaster master = null;
        try
        {
            TcpParameters tcpParameters = new TcpParameters();
            tcpParameters.setHost(InetAddress.getByName(currentSlave.hostname));
            tcpParameters.setKeepAlive(true);
            tcpParameters.setPort(currentSlave.port);
            //this should work but somehow doesn't
            //tcpParameters.setConnectionTimeout(3000);
            master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
            master.setResponseTimeout(5000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        int[] registers = getSortedRegisters(3, currentSlave);
        int startingRegister = 0;
        int quantity = 0;
        for (int j = 0; j < registers.length; j++)
        {
            startingRegister = registers[j];
            quantity = 1;
            for (int k = 0; k < 250 && j+k < registers.length; k++)
            {
                int calcDistance = registers[j+k]-registers[j] + 1;
                if (calcDistance < 1024)
                {
                    quantity = calcDistance;
                    j = j+k;
                }
            }
            byte[] values = getModbusValues(currentSlave, 3, startingRegister, quantity);

            try 
            {
                response = generateModbusMessage(master, 0, 1, 1, 16, startingRegister, quantity, values);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            try
            {
                master.disconnect();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public byte[] getModbusValues(ModbusHostRecord currentRecord, int functionCode, int register, int quantity)
    {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        for (int i = 0; i < quantity; i++)
        {
            for (int j = 0; j < currentRecord.registerRecords.size(); j++)
            {
                RegisterRecord rr = currentRecord.registerRecords.get(j);
                if (rr.functionCode == functionCode && rr.register == register+i)
                {
                    
                    if (rr.value != null)
                    {
                        try
                        {
                            baos.write(rr.value);
                            baos.flush();
                        }
                        catch(java.io.IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return baos.toByteArray();
    }
    public void shutdown()
    {
        for (int i = 0; i < localRunningSlaves.size(); i++)
        {
            try
            {
                localRunningSlaves.get(i).localSlave.shutdown();
            }
            catch(Exception e)
            {
                if (ModbusMechanic.debug)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
