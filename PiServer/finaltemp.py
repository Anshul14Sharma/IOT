# SDP bluetooth and tcp server for rasperry pi 3

#!/usr/bin/python
import threading 
from bluetooth import *
import requests
import time
import socket
import struct
import fcntl
import random as r
import select
import subprocess

#*************Bluetooth connection starts*******************************************
server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1) #listen only 1 connection
#port = server_sock.getsockname()[1]

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

advertise_service( server_sock, "AquaPiServer",
                   service_id = uuid,
                   service_classes = [ uuid, SERIAL_PORT_CLASS ],
                   profiles = [ SERIAL_PORT_PROFILE ],
#                   protocols = [ OBEX_UUID]
                    )

#Get ip address of wlan0 interface
def get_ip_address(ifname):
     s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
     return socket.inet_ntoa(fcntl.ioctl(
        s.fileno(),
        0x8915,
        struct.pack('256s', ifname[:15])
     )[20:24])

def bluetooth_conn():
    print "Waiting for connection on RFCOMM channel "
    client_sock,address = server_sock.accept()
    print "Accepted connection from ",address
    input = client_sock.recv(1024) # receives the Wifi SSID and password 

    k = []
    d = input.split(',')
    for x in d:
        k.append('"{0}"'.format(x))
    reps = {'ssid':'ssid='+k[0],
            'psk':'psk='+k[1]
    }
    # Configuration file of raspberry file 
    # Change the Wifi SSID and password with the received inputs
    f = open('/etc/wpa_supplicant/wpa_supplicant.conf','r').read()
    lines = f.split("\n")
    newConf = ""
    flag = 0
    for line in lines:
        REPLACED = False
        for key in reps.keys():
            if key in line:
                if reps[key] not in line:
                   count = line.index(key[0])
                   l = "%s%s\n"%(" "*count,reps[key])
                   REPLACED = True
                   flag = 1

        if REPLACED == True:
            newConf += l

        else:
            newConf += "%s\n"%line

    new_conf_file = open('/etc/wpa_supplicant/wpa_supplicant.conf','w')
    new_conf_file.write(newConf)
    new_conf_file.close()
    print flag
    
    if flag == 1:
       os.system('sudo wpa_cli -i wlan0 reconfigure') # Restart the Wifi service to update the changes in conf file
    time.sleep(8)
    ip = get_ip_address('wlan0')
    print ip
    client_sock.send(ip+'!')
    client_sock.close()
    server_sock.close()
#*********************Bluetooth connection ends here************************************

#function to generate random integer string
def get_random_number(length):
    random_string = ''
    random_str_seq = "1234567890"
    for ii in range(0,length):
        if ii % length == 0 and ii != 0:
            random_string += '-'
        random_string += str(random_str_seq[r.randint(0, len(random_str_seq) - 1)])
    return random_string

#*********************Wifi Connection start**********************************************
def wifi_conn():
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_address = (get_ip_address('wlan0'), 8081)
    print 'starting up on %s port %s' % server_address
    try:
        s.bind(server_address)
    except:
        print("Bind failed. Error : " + str(sys.exc_info()))
        sys.exit()
    return s

def setupconnection():
    s.listen(2)
    conn, add = s.accept()
    print 'Connection from', add
    return conn

#function to split data from client 
def split_data(conn):
    reply = get_random_number(6)
    while True:
        data = conn.recv(1024)
        if not data:
            break
        print "received data from client " + data
        d = data.split(",")
        tid = str(d[0])
        if tid == 't':  #if header is 't' temperature is recored and upload in Virgo Web App session space
            print 'starting...'
     	    d1 = str(d[1])
    	    d2 = str(d[2])
     	    d3 = str(d[3])
            os.system("sudo python /home/pi/Downloads/temp.py start "+ d1+' '+d2+' '+d3)
            print 'started!'
        elif str(data) == 'e': # Stop the temperature acquire and upload process
	        os.system('sudo python /home/pi/Downloads/temp.py stop d1 d2 d3')
            print 'stopping'
            reply = 'stopped!'
        elif str(data) == 'r': # reset the acquire and upload frequency
            reply = 'reset!'
            os.system('sudo python /home/pi/Downloads/temp.py stop d1 d2 d3')
        else:
            reply = 'bad'
        conn.send(str(reply))
    conn.close()

def main():
    bluetooth_conn()
    s = wifi_conn()
    while True:
        try:
            conn = setupconnection()
            split_data(conn)
    except:
        break

if __name__ == "__main__":
    main()
