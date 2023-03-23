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
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
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
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class ModbusTCPMaster extends AbstractModbusMaster{
    
    public ModbusTCPMaster(String host, int port) throws Exception
    {
        TcpParameters tcpParameters = new TcpParameters();
        tcpParameters.setHost(InetAddress.getByName(host));
        tcpParameters.setKeepAlive(true);
        tcpParameters.setPort(port);
        master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
    }
}
