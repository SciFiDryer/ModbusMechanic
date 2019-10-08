## Overview
Modbus Mechanic is a Modbus TCP and RTU master and slave simulator that interprets 32 bit numeric register values such as int32 and float32. It can also intrepret ASCII strings of any length.

Powered by Java, it can be run on Mac, Windows, or Linux with the OpenJDK.

## Requirements
- OpenJDK
- RS-485 serial adaptor (for RTU)

## Installing Java
The OpenJDK provides an open source version of the Java Runtime Environment. If you do not need an installer package, you can download the binaries from Oracle's site. The Java community maintains builds that have installers for Windows, Mac and Linux. They can be downloaded from [adoptopenjdk.net](https://adoptopenjdk.net/).

## Running
After the OpenJDK is installed, you should be able to launch Modbus Mechanic by double clicking ModbusMechanic.jar. If that doesn't work, you can always launch by typing "java -jar ModbusMechanic.jar" into a command line. The command for a Windows command line is "java.exe -jar ModbusMechanic.jar".

## Using Modbus

### Reading and writing device data
In order to read or write data from devices, you need to know a few things.
- IP address of device (for TCP devices)
- Node id of device (often 1 for TCP devices)
- Function code
- Register number
- Length of data
- How to interpret the data

#### Registers
Data registers are organized into different groups. The groups of registers are:
- Discrete inputs - Single bit values that are read only. Similar to boolean values.
- Coils - Single bit values that can be changed with a write. Also similar to boolean values.
- Inputs - 16 bit values that are read only.
- Holding - 16 bit values that are read/write.
Some devices use an offset in their register numbering. Offset devices start labeling their registers at address 1. The actual register used in the register field of the packet is 1 lower. For devices that use an offset, subtract 1 from the register address, as Modbus Mechanic uses addressing starting from 0.

#### Quantity
Each 16 bit register is called a word. Some data types are longer than 16 bits, such as a float value. Modbus supports this by combining multiple registers. The length field is used to specify how many registers the read should be in 16 bit words.

#### Data interpretation
To read a Float, 16 Bit Integer, or 32 Bit Integer, use the appropriate radio buttons. The custom radio button is for reading raw words of data in hexidecimal format. The ASCII radio button interprets the recieved data as ASCII characters. You must specify length when reading ASCII.

#### Word and byte swapping
Some devices swap the words and/or bytes before converting the data type to Float, Int or ASCII. The checkboxes are to quickly determine what operation is needed in order to get good data.

### Writing values
Values can be written to devices by entering them into the text area. Select the appropriate radio buttons in order to convert the data.

## Using a simulator to test Modbus
If you do not have a Modbus device but want to test Modbus Mechanic, go to Tools>Start slave simulator and enter some register values. Then read them back using your local address (127.0.0.1). Writes will update values in the slave simulator window. Starting the slave simulator makes your PC Modbus accesible on any other network interfaces.
