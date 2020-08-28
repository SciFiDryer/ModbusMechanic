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

/**
 *
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class CIPProtocolRecord implements ProtocolRecord {
    public static int PROTOCOL_TYPE_CIP_READ = 1;
    public static int PROTOCOL_TYPE_CIP_WRITE = 2;
    public int type = 0;
    public String host = null;
    public int port = 0;
    public String tag = null;
    double value = 0;
    public int slot = 0;
    public CIPProtocolRecord()
    {
        
    }
    public CIPProtocolRecord(int aType, String aHost, int aPort, int aSlot, String aTag)
    {
        type = aType;
        host = aHost;
        port = aPort;
        tag = aTag;
        slot = aSlot;
    }
    public void setValue(double aValue)
    {
        value = aValue;
    }
    public double getValue()
    {
        return value;
    }
}
