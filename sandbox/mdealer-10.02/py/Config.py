# -*- coding: utf-8 -*-

import os
import ConfigParser
import sys

class Config():
    cfg = None
    filename = None

    def __init__(self):
        self.cfg = ConfigParser.ConfigParser()

        self.filename = '/etc/mdealer/config'

        self.cfg.readfp(file(self.filename))

    def get_filename(self):
        return self.filename
 
    def __getitem__(self, variable):
        return self.cfg.get('DEALER', variable)


    def get_the_db(self):
        if self.cfg.has_option('DEALER', 'db_file'): return self['db_file']
        else: return None


    """
        We use this methods to allow the program to reload the conf on-the-fly
    """
    def get_the_file_to_send(self):
        # if self['path_to_file'][-1] != '/': path = self['path_to_file'] + '/'
        # else: path = self['path_to_file']

        filename = os.path.join(self['path_to_file'], self['file_to_send'])

        if (not os.path.exists(filename)):
            print "The filename doesn't exist!"
            sys.exit(-1)
        else: return filename


    def get_the_user_string(self):
        return self['user_string']


    def get_font(self):
	return self['font']


    def get_the_time(self):
        time_to_next_send = float(self['time_to_next_send'])

        if (time_to_next_send == 0): print "Posible SPAM, acuerdate de cambiar la 'constante': TIME_TO_NEXT_SEND"

        return time_to_next_send

    def get_the_probability(self):
        return self['probability']

    def get_the_background_color(self):
        return self['background_color']
