# Modbus Mechanic
Cross platform GUI tool for reading and testing MODBUS TCP and RTU instruments
## Overview

Frustrated by the lack of GUI Modbus testing tools available that could interpret data types (float, unsigned int) I built this application on top of the JLibModbus library.

Modbus Mechanic aims to be a tool that can quickly read a float32, unsigned int16, unsigned int32, or raw hex from an instrument without the need for anything other than the dependant jar files, and the java runtime environment. It also aims to be 100% driverless. It is a work in progress.

I am not a software engineer, so suggestions to improve coding conventions are welcome. I'm sure there are opportunities for improvement within my code.

JlibModbus is a seperate, independant project that this application depends on.

## Quickstart

Download & extract the [latest release](#latest-release) and double click ModbusMechanic.jar.

## Known Bugs
- Modbus ASCII bus monitoring is not supported yet
- USB serial adapters untested on MacOS
- Untested on Linux

## Dependencies

This project depends on the JLibModbus library and the PureJavaComm library for serial functionality. Serial library selector coming soon.

https://github.com/kochedykov/jlibmodbus  
https://github.com/nyholku/purejavacomm

## Building from source

This is a NetBeans project. It can be built by pulling it in the NetBeans IDE or manually compiling. The project requires the JLibModbus jar. Serial functionality requires the PureJavaComm and JNA libraries.

## Guide

https://scifidryer.github.io/ModbusMechanic/

## Latest release

[ModbusMechanic.v0.8.zip](https://github.com/SciFiDryer/ModbusMechanic/releases/download/v0.8/ModbusMechanic.v0.8.zip)

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
- [x] Bookmarks
- [x] Master simulator
- [x] Slave simulator
- [ ] ASCII bus monitor
- [ ] Data logger
