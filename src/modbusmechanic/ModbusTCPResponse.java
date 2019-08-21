/* 
 * Copyright 2019 Matt Jamesson <scifidryer@gmail.com>.
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

/**
 *
 * @author Matt Jamesson
 */
public class ModbusTCPResponse {
    int transactionId = 0;
    int protocolId = 0;
    int length = 0;
    int unitId = 0;
    int function = 0;
    int byteCount = 0;
    byte[] words;
    public ModbusTCPResponse(int aTransactionId, int aProtocolId, int aLength, int aUnitId, int aFunction, int aByteCount, byte[] aWords)
    {
        transactionId = aTransactionId;
        protocolId = aProtocolId;
        length = aLength;
        unitId = aUnitId;
        function = aFunction;
        byteCount = aByteCount;
        words = aWords;
    }
    public int getTransactionId()
    {
        return transactionId;
    }
    public int getProtocolId()
    {
        return protocolId;
    }
    public int getLength()
    {
        return length;
    }
    public int getUnitId()
    {
        return unitId;
    }
    public int getFunction()
    {
        return function;
    }
    public int getByteCount()
    {
        return byteCount;
    }
    public byte[] getWords()
    {
        return words;
    }
}
