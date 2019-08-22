# ModbusMechanic
Cross platform GUI tool for reading and testing MODBUS TCP and RTU instruments
## Overview

Frustrated by the lack of GUI Modbus testing tools available that could interpret data types (float, unsigned int) I built this application on top of the JLibModbus library. ModbusMechanic aims to be a tool that can quickly read a float32 int16, int32, or raw hex from an instrument without the need for anything other than the dependant jar files, and the java runtime environment. It is a work in progress.

I am not a software engineer, so suggestions to improve coding conventions are welcome. I'm sure there are opportunities for improvement within my code.

JlibModbus is a seperate, independant project that this application depends on.

## Quickstart

Download & extract the [latest release](#latest-release) and double click ModbusMechanic.jar.

## Known Bugs
- Only tested on Windows
- Modbus ASCII bus monitoring is not supported yet

## Dependencies

This project depends on the JLibModbus library and the JSSC library for serial functionality. Serial library selector coming soon.

https://github.com/kochedykov/jlibmodbus  
https://github.com/scream3r/java-simple-serial-connector

## Building from source

This is a NetBeans project. It can be built by pulling it in the NetBeans IDE or manually compiling. The project requires the JLibModbus jar. Serial functionality requires the JSSC jar.

## Guide

WIP

## Latest release

[ModbusMechanic.v0.1.zip](https://github.com/SciFiDryer/ModbusMechanic/releases/download/v0.1/ModbusMechanic.v0.1.zip)

## Completed features and planned features in GUI

- [x] Read holding registers
- [x] Read input registers
- [x] Slave stress test with custom packet crafter
- [x] Data interpreter
- [x] Word and byte swapper
- [x] RTU bus monitor
- [ ] ASCII bus monitor
- [ ] Read coils
- [ ] Write registers
- [ ] Write coils
- [ ] Master simulator
- [ ] Data logger
