# ModbusMechanic
Cross platform GUI program for reading and testing MODBUS TCP and RTU instruments
## Overview

Frustrated by the lack of GUI Modbus testing programs available that could interpret data types (float, unsigned int) I built this application on top of the JLibModbus library. It is a work in progress.

I am not a software engineer, so suggestions to improve coding conventions are welcome. I'm sure there are opportunities for improvement within my code.

JlibModbus is a seperate, independant project that this application depends on.

## Known Bugs
- No release jar file yet
- Only tested on Windows
- Modbus ASCII bus monitoring is not supported yet

## Dependencies

This project depends on the JLibModbus library and the JSSC library. Serial library selector coming soon.

https://github.com/kochedykov/jlibmodbus  
https://github.com/scream3r/java-simple-serial-connector

## Building from source

This is a NetBeans project. It can be built by pulling it in the NetBeans IDE or manually compiling. The project requires the JLibModbus jar. Serial functionality requires the JSSC jar.

## Guide

WIP

## Latest release

WIP

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
