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

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class ModbusProtocolRecord implements ProtocolRecord {
    int formatType = 0;
    int protocolType = 0;
    byte[] rawValue = null;
    double numericValue = 0;
    int startingRegister = 0;
    int quantity = 0;
    int functionCode = 0;
    String slaveHost = null;
    int slavePort = 502;
    static int PROTOCOL_TYPE_SLAVE = 1;
    static int PROTOCOL_TYPE_MASTER = 2;
    static int FORMAT_TYPE_RAW = 1;
    static int HOLDING_REGISTER_FUNCTION = 3;
    public ModbusProtocolRecord(int protocol, String host, int port, int format, int aFunctionCode, int aStartingRegister, int aQuantity)
    {
        protocolType = protocol;
        slaveHost = host;
        slavePort = port;
        formatType = format;
        startingRegister = aStartingRegister;
        quantity = aQuantity;
        functionCode = aFunctionCode;
    }
}
