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
import java.util.*;
/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class ModbusHostRecord {
    String hostname = null;
    int port = 0;
    ArrayList<RegisterRecord> registerRecords = new ArrayList();
    public ModbusHostRecord(String aHostname, int aPort)
    {
        hostname = aHostname;
        port = aPort;
    }
    public void insertRegisterValue(int functionCode, int register, byte[] value)
    {
        boolean foundRegister = false;
        for (int i = 0; i < registerRecords.size() && !foundRegister; i++)
        {
            RegisterRecord currentRecord = registerRecords.get(i);
            if (currentRecord.functionCode == functionCode && currentRecord.register == register)
            {
                currentRecord.value = value;
                foundRegister = true;
            }
        }
    }
}
