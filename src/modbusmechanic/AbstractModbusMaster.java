/*
 * Copyright 2023 Matt Jamesson <scifidryer@gmail.com>.
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

import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterTCP;
import com.intelligt.modbus.jlibmodbus.msg.base.AbstractDataRequest;
import com.intelligt.modbus.jlibmodbus.msg.base.AbstractMultipleRequest;
import com.intelligt.modbus.jlibmodbus.msg.base.AbstractWriteMultipleRequest;
import com.intelligt.modbus.jlibmodbus.msg.base.ModbusResponse;
import com.intelligt.modbus.jlibmodbus.msg.request.ReadCoilsRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.ReadDiscreteInputsRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.ReadHoldingRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.ReadInputRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.WriteMultipleCoilsRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.WriteMultipleRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.WriteSingleRegisterRequest;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import java.nio.ByteBuffer;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public abstract class AbstractModbusMaster {
    protected ModbusMaster master;
    public void setResponseTimeout(int timeout)
    {
        master.setResponseTimeout(timeout);
    }
    public ModbusResponse generateModbusMessage(int protocolId, int transactionId, int slaveNode, int functionCode, int register, int quantity, byte[] values) throws Exception
    {
        ModbusResponse response = null;
        if (!master.isConnected())
        {
            master.connect();
        }
        AbstractDataRequest request = null;
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
        else if (functionCode == 6)
        {
            request = new WriteSingleRegisterRequest();
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
        if (request instanceof AbstractMultipleRequest)
        {
            ((AbstractMultipleRequest)(request)).setQuantity(quantity);
        }
        if (request instanceof AbstractWriteMultipleRequest)
        {
            ((AbstractWriteMultipleRequest)(request)).setBytes(values);
        }
        if (request instanceof WriteSingleRegisterRequest)
        {
            //cast the two bytes here to a short, because the only accepted input is a short
            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.put(values[0]);
            bb.put(values[1]);
            ((WriteSingleRegisterRequest)(request)).setValue(bb.getShort(0));
        }
        if (master instanceof ModbusMasterTCP)
        {
            master.setTransactionId(transactionId);
            request.setProtocolId(protocolId);
        }
        response = master.processRequest(request);
        return response;
    }
    public void disconnect() throws ModbusIOException
    {
        master.disconnect();
    }
}
