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
package modbusmechanic.bridge;
import modbusmechanic.bridge.drivers.*;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.master.*;
import com.intelligt.modbus.jlibmodbus.msg.base.*;
import com.intelligt.modbus.jlibmodbus.msg.response.*;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.*;
import javax.swing.*;
import static modbusmechanic.ModbusMechanic.generateModbusMessage;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class BridgeManager{
    public ArrayList<BridgeEntryContainer> bridgeMapList = new ArrayList();
    public ArrayList<BridgeMappingRecord> mappingRecords = new ArrayList();
    ArrayList<ProtocolDriver> driverList = new ArrayList();
    boolean firstRun = true;
    int restTime = 1000;
    boolean isRunning = false;
    BridgeThread bridgeThread = null;
    public DriverMenuHandler dmh = null;
    BridgeFrame bridgeFrame = null;
    public BridgeManager(boolean headless, String fileName)
    {
        if (!headless)
        {
            bridgeFrame = new BridgeFrame(this);
            bridgeFrame.setVisible(true);
        }
        driverList.add(new ModbusProtocolDriver(this));
        driverList.add(new CIPProtocolDriver(this));
        if (headless)
        {
            loadConfig(fileName);
            startBridge();
        }
    }
    public void loadConfig(String fileName)
    {
        try
        {
            XMLDecoder xmld = new XMLDecoder(new FileInputStream(fileName), null, null, new FilteredClassLoader());
            mappingRecords = (ArrayList<BridgeMappingRecord>)xmld.readObject();
            xmld.close();
        }
        catch (Exception e)
        {
            if (modbusmechanic.ModbusMechanic.debug)
            {
                e.printStackTrace();
            }
        }
    }
    public void setBridgeMapList(ArrayList aBridgeMapList)
    {
        bridgeMapList = aBridgeMapList;
    }
    public ArrayList<BridgeMappingRecord> getMappingRecords()
    {
        return mappingRecords;
    }
    public void setMappingRecords(ArrayList aMappingRecords)
    {
        mappingRecords = aMappingRecords;
    }
    public ArrayList<BridgeEntryContainer> getBridgeMapList()
    {
        return bridgeMapList;
    }
    public void setDriverList(ArrayList aDriverList)
    {
        driverList = aDriverList;
    }
    public ArrayList<ProtocolDriver> getDriverList()
    {
        return driverList;
    }
    public void runBridge()
    {
        if (firstRun)
        {
            for (int i = 0; i < driverList.size(); i++)
            {
                ProtocolDriver currentDriver = driverList.get(i);
                currentDriver.driverInit();
            }
        }
        //get incoming records
        for (int i = 0; i < driverList.size(); i++)
        {
            ProtocolDriver currentDriver = driverList.get(i);
            currentDriver.getIncomingRecords();
        }
        //get incoming records
        for (int i = 0; i < driverList.size(); i++)
        {
            ProtocolDriver currentDriver = driverList.get(i);
            currentDriver.mapIncomingValues();
        }
        //set outgoing records
        for (int i = 0; i < mappingRecords.size(); i++)
        {
            BridgeMappingRecord currentRecord = mappingRecords.get(i);
            currentRecord.outgoingRecord.setValue(currentRecord.incomingRecord.getValue());
        }
        //map records
        for (int i = 0; i < driverList.size(); i++)
        {
            ProtocolDriver currentDriver = driverList.get(i);
            currentDriver.sendOutgoingRecords();
        }
        firstRun = false;
    }
    public void restoreGuiFromFile()
    {
        for (int i = 0; i < mappingRecords.size(); i++)
        {
            if (mappingRecords.get(i).incomingRecord instanceof ModbusProtocolRecord)
            {
                bridgeFrame.addMapping();
                for (int j = 0; j < dmh.getDriverList().size(); j++)
                {
                    if (dmh.getDriverList().get(j) instanceof ModbusProtocolHandler)
                    {
                        ((ModbusProtocolHandler)(dmh.getDriverList().get(j))).setIncomingSettings((ModbusProtocolRecord)mappingRecords.get(i).incomingRecord);
                    }
                }
            }
            if (mappingRecords.get(i).incomingRecord instanceof CIPProtocolRecord)
            {
                bridgeFrame.addMapping();
                for (int j = 0; j < dmh.getDriverList().size(); j++)
                {
                    if (dmh.getDriverList().get(j) instanceof CIPProtocolHandler)
                    {
                        ((CIPProtocolHandler)(dmh.getDriverList().get(j))).setIncomingSettings((CIPProtocolRecord)mappingRecords.get(i).incomingRecord);
                    }
                }
            }
            if (mappingRecords.get(i).outgoingRecord instanceof ModbusProtocolRecord)
            {
                for (int j = 0; j < dmh.getDriverList().size(); j++)
                {
                    if (dmh.getDriverList().get(j) instanceof ModbusProtocolHandler)
                    {
                        ((ModbusProtocolHandler)(dmh.getDriverList().get(j))).setOutgoingSettings((ModbusProtocolRecord)mappingRecords.get(i).outgoingRecord);
                    }
                }
            }
            if (mappingRecords.get(i).outgoingRecord instanceof CIPProtocolRecord)
            {
                for (int j = 0; j < dmh.getDriverList().size(); j++)
                {
                    if (dmh.getDriverList().get(j) instanceof CIPProtocolHandler)
                    {
                        ((CIPProtocolHandler)(dmh.getDriverList().get(j))).setOutgoingSettings((CIPProtocolRecord)mappingRecords.get(i).outgoingRecord);
                    }
                }
            }
        }
    }
    public void constructSettingsFromGui()
    {
        mappingRecords.clear();
        for (int i = 0; i < bridgeMapList.size(); i++)
        {
            ProtocolRecord incomingRecord = bridgeMapList.get(i).incomingHandler.getIncomingProtocolRecord();
            ProtocolRecord outgoingRecord = bridgeMapList.get(i).outgoingHandler.getOutgoingProtocolRecord(incomingRecord);            
            BridgeMappingRecord bmr = new BridgeMappingRecord(incomingRecord, outgoingRecord);
            if (incomingRecord instanceof ModbusProtocolRecord && outgoingRecord instanceof ModbusProtocolRecord)
            {
                if (((ModbusProtocolRecord)(incomingRecord)).getFormatType() == ModbusProtocolRecord.FORMAT_TYPE_RAW && ((ModbusProtocolRecord)(outgoingRecord)).getFormatType() == ModbusProtocolRecord.FORMAT_TYPE_RAW)
                {
                    bmr.modbusBlockRemap = true;
                }
            }
            mappingRecords.add(bmr);
        }
    }
    public void startBridge()
    {
        bridgeThread = new BridgeThread(this);
        bridgeThread.start();
    }
    public void shutdown()
    {
        isRunning = false;
        firstRun = true;
        bridgeThread.interrupt();
        for (int i = 0; i < driverList.size(); i++)
        {
            driverList.get(i).shutdown();
        }
    }
    public class FilteredClassLoader extends ClassLoader
    {
        
        public Class<?> loadClass(String name) throws ClassNotFoundException
        {
            if (ModbusMechanic.debug)
            {
                System.out.println("Debug: got load class " + name);
            }
            if (name.equals("java.beans.XMLDecoder") || name.equals("java.util.ArrayList") || name.startsWith("modbusmechanic."))
            {
                return super.loadClass(name);
            }
            System.out.println("Insecure deserialiaztion attempt, exiting now");
            System.exit(0);
            return null;

        }
    }

    
}
