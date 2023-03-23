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
import com.intelligt.modbus.jlibmodbus.serial.SerialParameters;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import com.intelligt.modbus.jlibmodbus.serial.SerialPortFactoryPJC;
import com.intelligt.modbus.jlibmodbus.serial.SerialUtils;
import static modbusmechanic.ModbusMechanic.castToBaud;
import static modbusmechanic.ModbusMechanic.castToParity;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class ModbusRTUMaster extends AbstractModbusMaster {
    public ModbusRTUMaster(String comPort, int baudRate, int dataBits, int stopBits, int parityTmp)
    {
        try {
            SerialPort.BaudRate baud = castToBaud(baudRate);
            SerialPort.Parity parity = castToParity(parityTmp);
            SerialParameters serialParameters = new SerialParameters(comPort, baud, dataBits, stopBits, parity);
            SerialUtils.setSerialPortFactory(new SerialPortFactoryPJC());
            master = ModbusMasterFactory.createModbusMasterRTU(serialParameters);
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
