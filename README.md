# Modbus Mechanic
Cross platform GUI tool for reading and testing MODBUS TCP and RTU instruments
![Screenshot](https://raw.githubusercontent.com/SciFiDryer/ModbusMechanic/master/docs/screenshot.png)

[More screenshots](https://modbusmechanic.scifidryer.com)
## Overview

Frustrated by the lack of GUI Modbus testing tools available that could interpret data types (float, unsigned int) I built this application on top of the JLibModbus library.

Modbus Mechanic aims to be a tool that can quickly read a float32, unsigned int16, unsigned int32, or raw hex from an instrument without the need for anything other than the dependant jar files, and the java runtime environment. It also aims to be 100% driverless. It is a work in progress.

This is a project that is evolving, so suggestions to improve coding conventions are welcome. I'm sure there are opportunities for improvement within my code.

My testing capabilities are limited, so if you notice a problem, please open an issue.

JlibModbus is a seperate, independant project that this application depends on.

## Quickstart

If you don't have Java installed, head to [adoptopenjdk.net](https://adoptopenjdk.net/) to download and install the Java runtime. Then, download & extract the [latest release](#latest-release) and double click ModbusMechanic.jar.

## Register scanner
![Register scanner](https://raw.githubusercontent.com/SciFiDryer/ModbusMechanic/master/docs/registerscanner.png)

The register scanner tool allows input and holding register ranges to be scanned for values. Nonexistant registers are excluded frm the results and there is an option to hide zero values. This tool may be able to help identify register ranges when they are not known. Note that full range scans can take some time as there are 65536 possible registers to scan in each of the holding register and input register groups.

## RTU scanner

The RTU scanner tool allows scanning of the entire RTU network for active nodes. This is useful if there is a slave device on the network for which the address is not known. For best results, the scanner should be the only master on the network.

## Gateway

The gateway function allows exposing an RTU network to a TCP network. TCP master devices can send Modbus transactions to the machine running ModbusMechanic, and ModbusMechanic will obtain a response from the RTU network if possible.

## Running Gateway in GUI

The gateway function is available from the GUI in the tools menu.

## Running the Gateway on a headless box

The gateway can be run on a headless machine by specifying a config file. A config file may be generated one of two ways.

1. Using the GUI - Go to the tools menu and select Gateway. Specify the settings and click Save Settings. The cfg file will be generated.
2. Manually - Create a file in the following format. comport must be specified. Other parameters may be omited if defaults are acceptable.
```
comport=COM1
tcpport=502
baud=19200
parity=0
databits=8
stopbits=1
allowmonitor=0
```
Attribute | Descrtiption | Default
--------- | ------------ | -------
comport | The name of the com port. On Linux this is the name of the serial port in the /dev directory, not the full path. | Nothing
tcpport | Port on which the TCP slave of the Gateway will be accessible. | 502
baud | Baud rate for the serial port. | 9600
parity | Parity for the serial port. 0=None 1=Odd 2=Even 3=Mark 4=Space | 0
databits | Data bits for serial port. 5-8 are acceptible. | 8
stopbits | Stop bits for serial port. 1=1 2=2 3=1.5 | 1
allowmonitor | Allow remote frame monitor via telnet 0=no 1=yes | 0
3. Launch ModbusMechanic with the -gateway argument followed by the path to the config file. Example on Linux for config file named "gateway.cfg".
  ```
  $ java -jar ModbusMechanic.jar -gateway gateway.cfg
  ```
## Monitoring frames on a headless gateway

Enable remote frame monitoring (see above) and connect to the gateway's IP address with a telnet client. You will be able to see frames as they would be displayed in the watch window.

## Bridging to other protocols

This functionality is still available in ModbusMechanic, but is being moved to a stand alone project [ProtocolWhisperer](https://github.com/SciFiDryer/ProtocolWhisperer)

## My language localizations are wrong, how can I report errors?

I am using automated translation to do localizations and am not a native speaker of all localizations. As a result, the terms in certain languages describing certain functions may be wrong. If you run across lingustic errors and would like to report them, please [open an issue](https://github.com/SciFiDryer/ModbusMechanic/issues).

## Dependencies

This project depends on the JLibModbus library and the PureJavaComm library for serial functionality. It depends on EtherIP for CIP functionality.

https://github.com/kochedykov/jlibmodbus  

https://github.com/nyholku/purejavacomm

https://github.com/EPICSTools/etherip

## Building from source

This is a NetBeans project. It can be built by pulling it in the NetBeans IDE or manually compiling. The project requires the JLibModbus jar. Serial functionality requires the PureJavaComm and JNA libraries.

## Guide

https://scifidryer.github.io/ModbusMechanic/

## Debugging

To obtain verbose debugging information start ModbusMechanic with the debug option.
  ```
  $ java -jar ModbusMechanic.jar -debug
  ```
## Latest release

[ModbusMechanic.v2.9.zip](https://github.com/SciFiDryer/ModbusMechanic/releases/download/v2.9/ModbusMechanic.v2.9.zip)

## Completed features and planned features in GUI

- [x] Read holding registers
- [x] Read input registers
- [x] Read coils
- [x] Read discretes
- [x] Write holding registers
- [x] Write coils
- [x] Slave stress test with custom packet crafter
- [x] Data interpreter
- [x] Word and byte swapper
- [x] RTU bus monitor
- [x] Gateway
- [x] Bookmarks
- [x] Master simulator
- [x] Slave simulator
