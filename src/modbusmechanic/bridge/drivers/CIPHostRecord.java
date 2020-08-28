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
public class CIPHostRecord {
    String host = null;
    int port = 0;
    int slot = 0;
    int type = 0;
    ArrayList<String> tags = new ArrayList();
    double[] values = new double[]{};
    public CIPHostRecord(int aType, String aHost, int aPort, int aSlot)
    {
        type = aType;
        host = aHost;
        port = aPort;
        slot = aSlot;
    }
    public ArrayList<String> getTags()
    {
        return tags;
    }
}
