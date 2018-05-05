# IOT
This repo aims to get the temperature from the sensor attached to Pi using Android Client and python server on Raspberry Pi and send the temperature reading to a web application using RESTful services.  
On Pi I am using Bluetooth and tcp/ip connection to connect with Android.  
* Programming for Bluetooth in Python follows the socket programming model and communications between the Bluetooth devices is done through RFCOMM socket. RFCOMM (Radio Frequency Communication) is a Bluetooth Protocol which provided emulated RS-232 serial ports and also called as Serial Port Emulation.  
* TCP/IP Client and Server - Sockets can be configured to act as a server and listen for incoming messages, or connect to other applications as a client. After both ends of a TCP/IP socket are connected, communication is bi-directional.  

#### Installing Required Packages for Bluetooth Communication: ####  
``sudo apt-get update``  
``sudo apt-get upgrade``  
* Then install few Bluetooth related packages:-  
``sudo apt-get install bluetooth blueman bluez``  
* Then reboot the Raspberry Pi:  
``sudo reboot``  

* BlueZ is a open source project and official Linux Bluetooth protocol stack. It supports all the core Bluetooth protocols and now become part of official Linux Kernel.  
* Blueman provides the Desktop interface to manage and control the Bluetooth devices.  
* Finally we need python Library for Bluetooth communication so that we can send and receive data through RFCOMM using Python language:-  
``sudo apt-get install python-bluetooth``  
* Also install the GPIO support libraries for Raspberry Pi:-  
``sudo apt-get install python-rpi.gpio``  
Now we are done with installing required packages for Bluetooth communication in Raspberry Pi.  

#### Installling Required Python 2.7.14 Packages on Pi: ####
* pip install:  
``sudo apt-get install -y python-pip``  
* Dependencies install:  
``pip install -r requirements.txt``  






