#!/usr/bin/env python
# -*- coding: utf-8 -*- 

import sys
sys.path.insert(0, '/usr/lib/mdealer/')

import subprocess
import os
import lightblue
import re
import time
import DataBase
import Config
import Writer
import random
import Parallel as p

class Bluetooth():
    TIME_TO_NEXT_SEND = None # Time in seconds, readed in the file config

    filename = None
    db = None
    cfg = None

    first_time = False


    """
        We always need a file to send with this object
        TODO: Send a exception is the file isn't provide
    """
    def __init__(self):
        self.first_time = True

        self.cfg = Config.Config()

        self.db = DataBase.DataBase()


    """
        Loop all the time to send to all the devices
    """
    def main_loop(self):
        while (True):
            # Only for debug
            # print lightblue.gethostaddr()
            print(time.ctime())

            self.cfg = Config.Config()

            self.filename = self.cfg.get_the_file_to_send()
            self.TIME_TO_NEXT_SEND = self.cfg.get_the_time()

            random_number = random.randint(1, 100)

            edit_is_possible = False
            # We can only edit the jpg and png images
            if (re.search("^.*(jpg|png|jpeg)$", self.filename.lower())): edit_is_possible = True

            # Make the contest
            if random_number <= self.cfg.get_the_probability() and edit_is_possible:
                # Create a new Writer to reload her conf
                self.writer = Writer.Writer(self.cfg.get_the_background_color())
                index = self.send_to_all_new(True)
            else:
                index = self.send_to_all_new(False)

            # Wait 5s to resend the content to the new found devices
            time.sleep(5)

            if index > 0:
                # THIS IS ONLY FOR DEBUG, WE MMMMMUUUUSSSSTTTTT FIX IT
                print 'Esperando...'
                time.sleep(15)
                print 'Matando...'
                subprocess.Popen('killall obexftp', shell = True, stdout = open('/dev/null', 'w'), stderr = subprocess.STDOUT)


    """
        Query to the DB and bluetooth device to know if we've yet sended the program to the device
        If price = True, we must send a promotional gift to one of the founded devices
    """
    def send_to_all_new(self, price = False):
        devices_and_channels = self.get_devices_and_channels()

        print devices_and_channels;

        awarded_device = random.randint(0, len(devices_and_channels)) + 1

        actual_unix_timestamp = time.time()

        index = 0

        for device in devices_and_channels:
            if device[1]: # We only can send if we have the channel to send, otherwise a exception will ocurr
                unix_timestamp = self.db.get(device[0])

                # The devices isn't present in the DB or the wait time was passed, so we resend the content
                # not unix_timestamp -> The device isn't in the DB
                # the other condition -> The wait time is passed
                #if (not unix_timestamp) or (unix_timestamp < (actual_unix_timestamp - self.TIME_TO_NEXT_SEND)):
                if ((not unix_timestamp) or (unix_timestamp < (actual_unix_timestamp - self.TIME_TO_NEXT_SEND)) or (self.first_time == True)) and (index < 7):
                    # We must control the bluetooth channels used, we do it with this var
                    index += 1 

                    # We only go to erase the price from the list if we can send it
                    if price and index == awarded_device:
                        old_filename = self.filename
                        self.filename = self.writer.get_awarded_filename(self.filename)
                   
                    self.db.save(device[0], device[1], self.filename)
                    print "Enviando a %s por canal %i" % (device[0], device[1])
                    p.run_in_parallel(self.send, device[0], device[1])
    
                    # Restore the normal image
                    if price and index == awarded_device: self.filename = old_filename

        #If in the local are more than 7 actived bluetooth we need deactive the first_time in the next loop
        if (index < 7): self.first_time = False

        return index

    """
        Get all the devices in the range and after it send the program
    """
    def get_devices_and_channels(self):
        while True:
            correct = False

            try:
                devices = lightblue.finddevices(getnames = False)
                correct = True
            except:
                print "ERROR finding devices!"
                time.sleep(5)
                os.popen("pkill -f obexftp")
                os.popen("pkill -f mdealer$")
                os.system("/usr/bin/mdealer&")

            if correct:
                break
            

        clean_devices = ()
        for device in devices:
            # We only need the hwaddr one time
            while devices.count(device) > 1: devices.remove(device)

            if clean_devices: clean_devices = clean_devices + [(device[0], self.get_channel(device[0]))]
            else: clean_devices = [(device[0], self.get_channel(device[0]))]

        return clean_devices # Return a list with all the devices


    """
        Get the OBEX Push channel and return it
    """
    def get_channel(self, device):
        # If we already have the device in the DB the bluetooth channel never change, so I use it from DB (no findservices required)
        channel = self.db.get_channel(device)
        if (channel): return channel
        else:
            services = lightblue.findservices(device)
    
            # Regular expression to get the obex push channel
            for service in services:
                if (service[2] != None):
                    service_name = service[2]
                    if (re.search("^obex.*push$|^.*objetos obex$", service_name.lower())): return service[1] # Return the channel
    
            # We didn't find the obex push channel, so I can't send to this device
            return None


    """
        Send the message via the OBEX channel provided
    """
    def send(self, hwaddr, channel):
        #debug: print "%s -> %s (%i)" % (time.ctime(), hwaddr, channel)
        command = 'obexftp --nopath --noconn --uuid none -b %s -B %d -p "%s"' % (hwaddr, channel, self.filename)
        # Call obexftp redirecting the output to /dev/null
        subprocess.Popen(command, shell = True, stdout = open('/dev/null', 'w'), stderr = subprocess.STDOUT)


if __name__ == "__main__":
    bt = Bluetooth()
    bt.main_loop()
