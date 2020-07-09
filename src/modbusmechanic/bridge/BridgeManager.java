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
import java.net.InetAddress;
import java.util.*;
import javax.swing.*;
import static modbusmechanic.ModbusMechanic.generateModbusMessage;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class BridgeManager {
    public ArrayList<BridgeEntryContainer> bridgeMapList = new ArrayList();
    public ArrayList<BridgeMappingRecord> mappingRecords = new ArrayList();
    ArrayList<ProtocolDriver> driverList = new ArrayList();
    boolean firstRun = true;
    public BridgeManager()
    {
        new BridgeFrame(this).setVisible(true);
        driverList.add(new ModbusProtocolDriver(this));
    }
    public void startBridge()
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
        for (int i = 0; i < bridgeMapList.size(); i++)
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

    public void constructSettingsFromGui()
    {
        mappingRecords.clear();
        for (int i = 0; i < bridgeMapList.size(); i++)
        {
            //to do call to protocol specific driver
            modbusmechanic.bridge.drivers.ProtocolHandler currentRecord = bridgeMapList.get(i).incomingHandler;
            BridgeMappingRecord bmr = currentRecord.getBridgeMappingRecord();
            mappingRecords.add(bmr);
        }
        startBridge();
    }
    public void shutdown()
    {
        for (int i = 0; i < driverList.size(); i++)
        {
            driverList.get(i).shutdown();
        }
    }
    
}
