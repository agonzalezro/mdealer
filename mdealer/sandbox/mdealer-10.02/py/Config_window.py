#!/usr/bin/env python
# -*- coding: utf-8 -*-

# general imports
import gtk
import os
import Config

# my stuff
from Event_config_window import *


class Config_window:
    def __init__(self, file, window):
        self.builder = gtk.Builder()
        self.builder.add_from_file(file)
        self.window = self.builder.get_object(window)
        self.window.show()

        e = Event(self)
        self.builder.connect_signals(e)

        self.load()

    def __getitem__(self, key):
        return self.builder.get_object(key)

    def load(self):
        cfg = Config.Config()

        self['file'].set_uri("file://" + os.path.join(cfg['path_to_file'], cfg['file_to_send']))
        self['name'].set_text(cfg['dealer_name'])
        self['time'].set_value(eval(cfg['time_to_next_send']))
        self['caption'].set_text(cfg['user_string'])
        
        self['color'].set_color(gtk.gdk.color_parse(cfg['background_color']))


#
# EOC
#


if __name__ == '__main__':
    show()

def show():
    #glade-2 exported to XML file
    gbf = '/usr/lib/mdealer/config_window.xml'

    #Only for debug
    widgets = Config_window(gbf, 'configuration_window')
    gtk.main()
