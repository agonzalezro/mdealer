# -*- coding: utf-8 -*-

from pysqlite2 import dbapi2 as sqlite
import os
import calendar, time
import Config

class DataBase():
    db = None
    connection = None
    cursor = None


    def __init__(self):
        cfg = Config.Config()

        filename = cfg.get_the_db()

        # If the database file isn't provided, we use the default: $PWD/stuff/db.sqlite
        if (filename): self.db = filename
        else: self.db = os.path.join(os.path.split(__file__)[0], '../stuff/db.sqlite')
        
        # If the file doesn't exists we create it with the DB structure,
        # if it exists we don't initialize the structure
        self.initialize(os.path.exists(self.db))


    """
        Initialize the database attributes and the database structure if it isn't
    """
    def initialize(self, exists):
        self.connection = sqlite.connect(self.db)
        self.cursor = self.connection.cursor()

        if (exists == False):
            self.cursor.execute('CREATE TABLE devices (id INTEGER PRIMARY KEY, hwaddr CHAR(12), time DATETIME, channel INTEGER, file VARCHAR(1024))')
            self.connection.commit()


    """
        Save the mobile device in our database with the date and time when it has been seen
    """
    def save(self, hwaddr, channel, file):
        sql = 'INSERT INTO devices VALUES (NULL, ?, ?, ?, ?)'
        self.cursor.execute(sql, (self.getCleanAddress(hwaddr), self.get_actual_time(), channel, file)) # Erase the : at hardware address and save it
        self.connection.commit()


    """
        Update the mobile device information in our database with the date and time when it has been seen
    """
#    def update(self, hwaddr):
#        sql = 'UPDATE devices SET time = ? WHERE hwaddr = ?'
#        self.cursor.execute(sql, (self.get_actual_time(), self.getCleanAddress(hwaddr), )) # Erase the : at hardware address and save it
#        self.connection.commit()


    """
        Get the actual time in BD mode
    """
    def get_actual_time(self):
        return time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())

    
    """
        Ask to the DB if a mobile device is saved or no. If it find it, returns the UNIX time when it was saved
    """
    def get(self, hwaddr):
        sql = 'SELECT time FROM devices WHERE hwaddr = ? ORDER BY time DESC LIMIT 1'
        self.cursor.execute(sql, (self.getCleanAddress(hwaddr), ))
        self.connection.commit()

        timestamp = self.cursor.fetchone()

        #if time: return time[0]
        #else: return None # If this method returns None, the device isn't saved
        if timestamp:
            return self.unixtime(timestamp[0])
        else:
            return None


    """
        Get the actual time in timestamp mode
    """
    def unixtime(self, timestamp):
        return calendar.timegm(time.gmtime(time.mktime(time.strptime(timestamp, "%Y-%m-%d %H:%M:%S"))))


    """
        Get the channel saved in the BD
    """
    def get_channel(self, hwaddr):
        sql = 'SELECT channel FROM devices WHERE hwaddr = ? LIMIT 1'

        self.cursor.execute(sql, (self.getCleanAddress(hwaddr), ))
        self.connection.commit()

        channel = self.cursor.fetchone()

        if channel:
            return channel[0]
        else:
            return None


    """
        Return the hwaddress without :
    """
    def getCleanAddress(self, hwaddr):
        return hwaddr.replace(":", "")
