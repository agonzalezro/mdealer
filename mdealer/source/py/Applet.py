#!/usr/bin/env python
# -*- coding: utf-8 -*-

import pygtk
import sys
pygtk.require('2.0')


import gtk
import gnomeapplet
import Config_window
import os
from Bluetooth import *
import Config_window

class Applet(gnomeapplet.Applet):

    running = False
    bt = None

    def __init__(self, applet, iid):
        event_box = gtk.EventBox()
        applet.add(event_box)
        event_box.show()

        image = gtk.Image()
        image.set_from_file(os.path.join(os.path.split(__file__)[0], "../stuff/mdealer.svg"))
        event_box.add(image)
        image.show()

        event_box.connect("button-press-event", self.button_clicked, applet)
        event_box.connect("destroy-event", self.destroy_event, applet)

        applet.show_all()

        #DEBUG
        #self.show_configuration(None, None);


    def menu (self, applet, runnning):
        propxml="""
            <popup name="button3">
            <menuitem name="Item 3" verb="conf" label="_Configurar" pixtype="stock" pixname="gtk-preferences"/>
            """
        if self.running: propxml=propxml + '<menuitem name="Item 4" verb="stop" label="_Stop mdealer" pixtype="stock" pixname="gtk-media-stop"/>'
        else: propxml=propxml + '<menuitem name="Item 4" verb="start" label="_Start mdealer" pixtype="stock" pixname="gtk-media-play"/>'

        propxml=propxml + '</popup>'

        verbs = [("conf", self.show_configuration),("start", self.start),("stop", self.stop)]
        applet.setup_menu(propxml, verbs, None)


    def show_configuration(self, *arguments, **keywords):
        Config_window.show();


    def start(self, *arguments, **keywords):
        self.running = True
        try:
            bt = Bluetooth()
            #gobject.idle_add(bt.main_loop, ())
            os.popen("pkill -f obexftp")
            os.popen("pkill -f Bluetooth.py$")
            bt_sender = os.path.join(os.path.split(__file__)[0], 'Bluetooth.py')
            os.system("/usr/bin/env python " + bt_sender + "&")
        except:
            self.running = False


    def stop(self, *arguments, **keywords):
        self.running = False
        os.popen("pkill -f obexftp")
        os.popen("pkill -f Bluetooth.py$")


    def destroy_event(self, button, event, applet):
        self.stop(None, None)


    def button_clicked(self, button, event, applet):
        print event.button
        if event.type == gtk.gdk.BUTTON_PRESS and event.button == 3:
            self.menu(applet, self.running)

def run_in_window():
    main_window = gtk.Window(gtk.WINDOW_TOPLEVEL)
    main_window.set_title("mdealer Applet")
    main_window.connect("destroy", gtk.main_quit)
    app = gnomeapplet.Applet()
    applet = Applet(app, None)
    app.reparent(main_window)
    main_window.show_all()

    applet.start()

    gtk.main()
    sys.exit()


def factory(applet, iid):
    applet = Applet(applet, iid)
    applet.start()
    return gtk.TRUE
    

if len(sys.argv) == 2:
    if sys.argv[1] == "run-in-window":
        run_in_window()
else:
    gnomeapplet.bonobo_factory('OAFIID:GNOME_MdealerApplet_Factory',
    gnomeapplet.Applet.__gtype__,
    'Configuración de mirblu dealer', '0.9',
    factory)
