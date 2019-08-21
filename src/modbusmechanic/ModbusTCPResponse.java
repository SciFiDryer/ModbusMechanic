/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modbusmechanic;

/**
 *
 * @author jamesma
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
