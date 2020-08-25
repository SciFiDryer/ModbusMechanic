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

import modbusmechanic.bridge.drivers.ProtocolRecord;

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class BridgeMappingRecord {
    public boolean modbusBlockRemap = false;
    public ProtocolRecord incomingRecord;
    public ProtocolRecord outgoingRecord;
    public BridgeMappingRecord(ProtocolRecord aIncomingRecord, ProtocolRecord aOutgoingRecord)
    {
        incomingRecord = aIncomingRecord;
        outgoingRecord = aOutgoingRecord;
    }
    public BridgeMappingRecord()
    {
        
    }
}
