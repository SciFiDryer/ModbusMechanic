## Overview
Modbus Mechanic is a Modbus TCP and RTU master and slave simulator that interprets 32 bit numeric register values such as int32 and float32. It can also intrepret ASCII strings of any length.

Powered by Java, it can be run on Mac, Windows, or Linux with the OpenJDK.

## Requirements
OpenJDK
RS-485 serial adaptor (for RTU)

## Installing Java
The OpenJDK provides an open source version of the Java Runtime Environment. If you do not need an installer package, you can download the binaries from Oracle's site. The Java community maintains builds that have installers for Windows, Mac and Linux. They can be downloaded from [adoptopenjdk.net](https://adoptopenjdk.net/).

## Running
After the OpenJDK is installed, you should be able to launch Modbus Mechanic by double clicking ModbusMechanic.jar. If that doesn't work, you can always launch by typing "java -jar ModbusMechanic.jar" into a command line. The command for a Windows command line is "java.exe -jar ModbusMechanic.jar".

## Using Modbus

### Reading data from devices
In order to read data from devices, you need to know a few things.
- IP address of device (for TCP devices)
- Node id of device (often 1 for TCP devices)
- Function code
- Register number
- Length of data
- How to interpret the data
