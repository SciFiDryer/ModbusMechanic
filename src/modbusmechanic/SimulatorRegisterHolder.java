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
 * @author Matt Jamesson <scifidryer@gmail.com>
 */
public class SimulatorRegisterHolder {
    int functionCode = 0;
    int registerNumber = 0;
    int dataType = 0;
    javax.swing.JTextField valueField = null;
    javax.swing.JRadioButton trueButton = null;
    javax.swing.JRadioButton falseButton = null;
    boolean wordSwap = false;
    boolean byteSwap = false;
    public SimulatorRegisterHolder(int aFunctionCode, int aRegisterNumber, javax.swing.JTextField aValueField, boolean aWordSwap, boolean aByteSwap, int aDataType)
    {
        functionCode = aFunctionCode;
        registerNumber = aRegisterNumber;
        valueField = aValueField;
        dataType = aDataType;
        wordSwap = aWordSwap;
        byteSwap = aByteSwap;
    }
    public SimulatorRegisterHolder(int aFunctionCode, int aRegisterNumber, javax.swing.JRadioButton aTrueButton, javax.swing.JRadioButton aFalseButton)
    {
        functionCode = aFunctionCode;
        registerNumber = aRegisterNumber;
        trueButton = aTrueButton;
        falseButton = aFalseButton;
    }
    public javax.swing.JRadioButton getTrueButton()
    {
        return trueButton;
    }
    public javax.swing.JRadioButton getFalseButton()
    {
        return falseButton;
    }
    public javax.swing.JTextField getValueField()
    {
        return valueField;
    }
    public int getRegisterNumber()
    {
        return registerNumber;
    }
    public int getFunctionCode()
    {
        return functionCode;
    }
    public int getDataType()
    {
        return dataType;
    }
    public boolean getWordSwap()
    {
        return wordSwap;
    }
    public boolean getByteSwap()
    {
        return byteSwap;
    }
}
