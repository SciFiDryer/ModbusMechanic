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
import com.intelligt.modbus.jlibmodbus.msg.response.ReadHoldingRegistersResponse;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import java.net.InetAddress;
import java.util.*;
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
            if (currentRecord.slaveHost.equals(currentList.get(i).hostname) && currentRecord.slavePort == currentList.get(i).port)
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
        }
        if (!foundRecord)
        {
            ModbusHostRecord mhr = new ModbusHostRecord(currentRecord.slaveHost, currentRecord.slavePort);
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
    public void fetchIncomingData()
    {
        for (int i = 0; i < incomingSlaveList.size(); i++)
        {
            ModbusHostRecord currentSlave = incomingSlaveList.get(i);
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
                    response = generateModbusMessage(master, 0, 1, 1, 3, startingRegister, quantity, null);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                buf = ((ReadHoldingRegistersResponse)(response)).getBytes();
                for (int k = 0; k < quantity; k++)
                {
                    byte[] value = Arrays.copyOfRange(buf, k*2, k*2+2);
                    currentSlave.insertRegisterValue(3, k+startingRegister, value);
                }
            }
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
            else
            {
                BridgeMappingRecord currentRecord = manager.mappingRecords.get(i);
                currentRecord.outgoingRecord.setValue(currentRecord.incomingRecord.getValue());
            }
        }
    }
    public byte[] getModbusValue(String host, int port, int functionCode, int startingRegister, int quantity)
    {
        
        for (int i = 0; i < incomingSlaveList.size(); i++)
        {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            ModbusHostRecord currentSlave = incomingSlaveList.get(i);
            if (currentSlave.hostname.equals(host) && currentSlave.port == port)
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
                        catch (java.io.IOException e)
                        {
                            e.printStackTrace();
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
            ModbusHostRecord currentSlave = outgoingSlaveList.get(i);
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
                for (int k = 0; k < 1024 && j+k < registers.length; k++)
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
}
