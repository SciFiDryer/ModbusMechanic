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

import etherip.EtherNetIP;
import etherip.types.CIPData;
import java.util.ArrayList;
import java.util.Arrays;
import modbusmechanic.bridge.*;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class CIPProtocolDriver implements ProtocolDriver{
    ArrayList<CIPHostRecord> incomingHostList = new ArrayList();
    ArrayList<CIPHostRecord> outgoingHostList = new ArrayList();
    BridgeManager manager = null;
    boolean enabled = true;
    public CIPProtocolDriver(BridgeManager aManager)
    {
        manager = aManager;
    }
    public void driverInit()
    {
        buildIncomingHostTable();
        buildOutgoingHostTable();
    }
    public void setEnabled(boolean aEnabled)
    {
        enabled = aEnabled;
    }
    public boolean getEnabled()
    {
        return enabled;
    }
    public void buildIncomingHostTable()
    {
        incomingHostList.clear();
        for (int i = 0; i < manager.mappingRecords.size(); i++)
        {
            if (manager.mappingRecords.get(i).incomingRecord instanceof CIPProtocolRecord)
            {
                CIPProtocolRecord currentRecord = (CIPProtocolRecord)manager.mappingRecords.get(i).incomingRecord;
                addToHostList(incomingHostList, currentRecord);
            }
        }
    }
    public void buildOutgoingHostTable()
    {
        outgoingHostList.clear();
        for (int i = 0; i < manager.mappingRecords.size(); i++)
        {
            if (manager.mappingRecords.get(i).outgoingRecord instanceof CIPProtocolRecord)
            {
                CIPProtocolRecord currentRecord = (CIPProtocolRecord)manager.mappingRecords.get(i).outgoingRecord;
                addToHostList(outgoingHostList, currentRecord);
            }
        }
    }
    public void addToHostList(ArrayList<CIPHostRecord> currentList, CIPProtocolRecord currentRecord)
    {
        boolean foundRecord = false;
        for (int i = 0; i < currentList.size() && !foundRecord; i++)
        {
            if (currentRecord.type == currentList.get(i).type && currentRecord.host.equals(currentList.get(i).host) && currentRecord.port == currentList.get(i).port && currentRecord.slot == currentList.get(i).slot)
            {
                foundRecord = true;
                currentList.get(i).tags.add(currentRecord.tag);
            }
        }
        if (!foundRecord)
        {
            CIPHostRecord chr = new CIPHostRecord(currentRecord.type, currentRecord.host, currentRecord.port, currentRecord.slot);
            chr.tags.add(currentRecord.tag);
            currentList.add(chr);
        }
    }
    public CIPData[] CIPRead(CIPHostRecord currentRecord)
    {
        try
        {
            EtherNetIP controller = new EtherNetIP(currentRecord.host, currentRecord.slot);
            controller.connectTcp();
            String[] tagArr = new String[currentRecord.tags.size()];
            for (int i = 0; i < currentRecord.tags.size(); i++)
            {
                tagArr[i] = currentRecord.tags.get(i);
            }
            CIPData[] values = controller.readTags(tagArr);
            controller.close();
            return values;
        }
        catch (Exception e)
        {
            if (modbusmechanic.ModbusMechanic.debug)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
    public double[] CIPReadTags(CIPHostRecord currentRecord)
    {
        
            try
            {
                CIPData[] values = CIPRead(currentRecord);
                if (values != null)
                {
                    double[] doubleValues = new double[values.length];
                    for (int i = 0; i < values.length; i++)
                    {
                        doubleValues[i] = values[i].getNumber(0).doubleValue();
                    }
                    return doubleValues;
                }
            }
            catch(Exception e)
            {
                if (modbusmechanic.ModbusMechanic.debug)
                {
                    e.printStackTrace();
                }
            }
            return new double[] {};
    }
    public void getIncomingRecords()
    {
        for (int i = 0; i < incomingHostList.size(); i++)
        {
            incomingHostList.get(i).values = CIPReadTags(incomingHostList.get(i));
        }
        for (int i = 0; i < manager.mappingRecords.size(); i++)
        {
            if (manager.mappingRecords.get(i).incomingRecord instanceof CIPProtocolRecord)
            {
                CIPProtocolRecord currentRecord = (CIPProtocolRecord)manager.mappingRecords.get(i).incomingRecord;
                for (int j = 0; j < incomingHostList.size(); j++)
                {
                    if (currentRecord.host.equals(incomingHostList.get(j).host) && currentRecord.port == incomingHostList.get(j).port && currentRecord.slot == incomingHostList.get(j).slot)
                    {
                        for (int k = 0; k < incomingHostList.get(j).tags.size(); k++)
                        {
                            if (incomingHostList.get(j).tags.get(k).equals(currentRecord.tag))
                            {
                                currentRecord.value = incomingHostList.get(j).values[k];
                            }
                        }
                    }
                }
            }
        }
    }
    public void mapIncomingValues()
    {
        
    }
    public void shutdown()
    {

    }
    public void sendOutgoingRecords()
    {
        
        for (int i = 0; i < manager.mappingRecords.size(); i++)
        {
            if (manager.mappingRecords.get(i).outgoingRecord instanceof CIPProtocolRecord)
            {
                CIPProtocolRecord currentRecord = (CIPProtocolRecord)manager.mappingRecords.get(i).outgoingRecord;
                for (int j = 0; j < outgoingHostList.size(); j++)
                {
                    if (currentRecord.host.equals(outgoingHostList.get(j).host) && currentRecord.port == outgoingHostList.get(j).port && currentRecord.slot == outgoingHostList.get(j).slot)
                    {
                        for (int k = 0; k < outgoingHostList.get(j).tags.size(); k++)
                        {
                            if (outgoingHostList.get(j).tags.get(k).equals(currentRecord.tag))
                            {
                                outgoingHostList.get(j).values = Arrays.copyOf(outgoingHostList.get(j).values, k+1);
                                outgoingHostList.get(j).values[k] = currentRecord.value;
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < outgoingHostList.size(); i++)
        {
            CIPWriteTags(outgoingHostList.get(i));
        }
    }
    public void CIPWriteTags(CIPHostRecord currentRecord)
    {
        try
        {
            CIPData[] values = CIPRead(currentRecord);
            for (int i = 0; i < values.length; i++)
            {
                values[i].set(0, currentRecord.values[i]);
            }
            EtherNetIP controller = new EtherNetIP(currentRecord.host, currentRecord.slot);
            controller.connectTcp();
            String[] tagArr = new String[currentRecord.tags.size()];
            for (int i = 0; i < currentRecord.tags.size(); i++)
            {
                tagArr[i] = currentRecord.tags.get(i);
            }
            controller.writeTags(tagArr, values);
            controller.close();
        }
        catch (Exception e)
        {
            if (modbusmechanic.ModbusMechanic.debug)
            {
                e.printStackTrace();
            }
        }
    }
}
