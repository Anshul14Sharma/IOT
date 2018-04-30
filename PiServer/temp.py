#!/usr/bin/python
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import pandas as pd
import urllib3
import requests
import os
import glob
import sys
from datetime import datetime
import time
from pandas.tools.plotting import table
import daemon
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

#REST API 
url = 'https://vegatechnoserv.co:8443/VegaWebApp/upload/iot'

os.system('modprobe w1-gpio')
os.system('modprobe w1-therm')
temp_sensor = '/sys/bus/w1/devices/28-0517c09a1bff/w1_slave' #temperature sensor id

#function to join dataframe
def combineFrame(df1,df2):
	df = df1.join(df2,lsuffix='_df1',rsuffix='_df2')
     	return df

#function that reads temperature from sensor
def tempRead():
        t = open(temp_sensor, 'r')
        lines = t.readlines()
        t.close()

	temp_output = lines[1].find('t=')
	if temp_output != -1:
	       temp_string = lines[1].strip()[temp_output+2:]
	       temp_c = float(temp_string)/1000.0
               temp_f = temp_c * 9.0/5.0 +  32.0
	return round(temp_c,1),round(temp_f)

c = [] #list for celcius 
f = [] #list for farenheit
date = [] #list for date
def start(key, acq_freq, upload_freq1):
    cnt = 0
    data_acq(key,int(acq_freq), int(upload_freq), cnt)

#function to acquire rows of temperature according to the acquire frequency
def data_acq(key,acq_freq,upload_freq, cnt):
    cnt = 0
    while cnt != acq_freq:
        cel,fah = tempRead()
        _date = datetime.now()
        _date =str( _date.strftime('%d-%m-%Y %H:%M:%S'))
        c.append(cel)
        date.append(_date)
        f.append(fah)

        df_cel = pd.DataFrame({'celsius':c})
        df_fah = pd.DataFrame({'fahrenheit':f})
        df_date = pd.DataFrame({'Date_Time':date})
        df = combineFrame(combineFrame(df_date,df_cel),df_fah) #dataframe with datetime and temperature 
        print df

        #df.to_html('df.html') # convert the dataframe to html file
        #df.plot(x='Date_Time',y='celsius')
        #filename = 'tempPlot.png'
        #plt.savefig(filename,dpi=150)
        ax = plt.subplot(111, frame_on=False)
        ax.xaxis.set_visible(False)
        ax.yaxis.set_visible(False)
        table(ax, df, loc='upper right')
        plt.savefig('datatemp.png', transparent=True) # save the dataframe as a png file.
        time.sleep(upload_freq)
        cnt = cnt+1
    data_upload(key, acq_freq, upload_freq) #upload the data recorded after the defined upload frequency

#function to upload the acquired data in defined frequency
def data_upload(key, acq_freq, upload_freq):
    payload ={'sessionKey':key}
    files = [('file', open('datatemp.png', 'rb'))]
    response = requests.post(url, data=payload, files=files)
    print response.text # Response from REST API 
    print "Do you want to stop or reset.. Press 'e' for stop and 'r' for reset"
    del c[:] 
    del f[:]
    del date[:]

key = str(sys.argv[2])
acq_freq = str(sys.argv[3])
upload_freq = str(sys.argv[4])

#class to run this script in background
class Test(daemon.Daemon):
    def run(self):
        while True:
            start(key, freq, freq1)
    def quit(self):
        sys.exit()

daemon = Test()
if 'start' == sys.argv[1]:
    daemon.start()
elif 'stop' == sys.argv[1]:
    daemon.stop()
