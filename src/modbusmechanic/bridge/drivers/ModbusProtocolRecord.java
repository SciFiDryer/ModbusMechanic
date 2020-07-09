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
import com.intelligt.modbus.jlibmodbus.utils.DataUtils;
import modbusmechanic.ModbusMechanic;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class ModbusProtocolRecord implements ProtocolRecord {
    int formatType = 0;
    int protocolType = 0;
    public byte[] rawValue = null;
    double numericValue = 0;
    public int startingRegister = 0;
    public int quantity = 0;
    public int functionCode = 0;
    public String slaveHost = null;
    public int slavePort = 502;
    static int PROTOCOL_TYPE_SLAVE = 1;
    static int PROTOCOL_TYPE_MASTER = 2;
    static int FORMAT_TYPE_RAW = 1;
    static int FORMAT_TYPE_FLOAT = 2;
    static int FORMAT_TYPE_UINT_16 = 3;
    static int FORMAT_TYPE_UINT_32 = 4;
    static int HOLDING_REGISTER_FUNCTION = 3;
    public boolean wordSwap = false;
    public boolean byteSwap = false;
    public ModbusProtocolRecord(int protocol, String host, int port, int format, int aFunctionCode, int aStartingRegister, int aQuantity, boolean aWordSwap, boolean aByteSwap)
    {
        protocolType = protocol;
        slaveHost = host;
        slavePort = port;
        formatType = format;
        startingRegister = aStartingRegister;
        quantity = aQuantity;
        functionCode = aFunctionCode;
        byteSwap = aByteSwap;
        wordSwap = aWordSwap;
    }
    public double getValue()
    {
        byte[] workingValue = rawValue;
        if (wordSwap)
        {
            workingValue = ModbusMechanic.wordSwap(rawValue);
        }
        if (byteSwap)
        {
            workingValue = ModbusMechanic.byteSwap(rawValue);
        }
        if (formatType == FORMAT_TYPE_FLOAT)
        {
            return DataUtils.toFloat(workingValue);
        }
        if (formatType == FORMAT_TYPE_UINT_16)
        {
            return DataUtils.BeToIntArray(workingValue)[0];
        }
        if (formatType == FORMAT_TYPE_UINT_32)
        {
            return ModbusMechanic.bytesToInt32(workingValue);
        }
        return 0;
    }
    public void setValue(double value)
    {
        if (formatType != FORMAT_TYPE_RAW)
        {
            if (formatType == FORMAT_TYPE_FLOAT)
            {
                float floatValue = (float)value;
                rawValue = java.nio.ByteBuffer.allocate(4).putFloat(floatValue).array();
                rawValue = ModbusMechanic.wordSwap(rawValue);
            }
            if (formatType == FORMAT_TYPE_UINT_16)
            {
                rawValue = java.nio.ByteBuffer.allocate(4).putInt((int)value).array();
                rawValue = java.util.Arrays.copyOfRange(rawValue, 2, 4);  
            }
            if (formatType == FORMAT_TYPE_UINT_32)
            {
                byte[] buf = java.nio.ByteBuffer.allocate(8).putLong((long)value).array();
                rawValue = java.util.Arrays.copyOfRange(buf, 4, 8);
            }
            if (wordSwap)
            {
                rawValue = ModbusMechanic.wordSwap(rawValue);
            }
            if (byteSwap)
            {
                rawValue = ModbusMechanic.byteSwap(rawValue);
            }
        }
    }
}
