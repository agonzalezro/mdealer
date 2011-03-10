# -*- coding: utf-8 -*- 

import gtk
import Config, ConfigParser
import os
import random

class Event:

    def __init__(self, parent):
        self.parent = parent


    def on_configuration_window_destroy(self, widget):
        gtk.main_quit()


    def on_about_button_clicked(self, widget):
        self.parent['aboutdialog'].show()


    def on_aboutdialog_delete(self, widget, *args):
        widget.hide()
        return True

    
    def on_aboutdialog_response(self, widget, *args):
        # -6: close button and -4: x window button
        if (args[0] == -6 or args[0] == -4):
            widget.hide()

    
    def on_apply_clicked(self, widget):
        # Si se cambia el nombre del distribuidor... puedo hacerlo sin ser root?

        cfg = Config.Config()

        config = ConfigParser.ConfigParser()
        config.add_section('DEALER')

        config.set('DEALER', "time_to_next_send" , str(self.parent['time'].get_value()).split(".")[0])
    
        if self.parent['file'].get_filename() != None:
            config.set('DEALER', "path_to_file", os.path.dirname(self.parent['file'].get_uri()).split("file://")[1])
            config.set('DEALER', "file_to_send", self.parent['file'].get_filename().split("/")[-1])
        else:
            # Rescue the old configuration
            config.set('DEALER', "path_to_file", cfg['path_to_file'])
            config.set('DEALER', "file_to_send", cfg['file_to_send'])

        config.set('DEALER', "background_color", get_hex_color(self.parent['color'].get_color()))
        config.set('DEALER', "probability", str(self.parent['probability'].get_value()).split(".")[0])
        config.set('DEALER', "user_string", self.parent['caption'].get_text())
        config.set('DEALER', "font", "/usr/share/fonts/truetype/ttf-bitstream-vera/VeraBd.ttf")
        name = self.parent['name'].get_text()
        if not name: name = "mirblu dealer"
        config.set('DEALER', "dealer_name", name)

        config.write(file(cfg.get_filename(), "w"))

        f = file("/etc/mdealer/awards_ids.txt", "w")
        for i in range (0, int(self.parent['quantity'].get_value())):
            f.write(str(int(random.uniform(100000, 999998))))
            f.write("\n")
        f.close()

        #rename bluetooth device
        if cfg['dealer_name'] != name:
            config = ConfigParser.ConfigParser()
            config.read("/etc/bluetooth/main.conf")
            config.set("General", "Name", name)
            try:
                config.write(file("/tmp/main.conf", "w"))
            except:
                print "Error: imposible escribir en el fichero de configuración del bluetooth"

        #restart
        os.popen("pkill -f obexftp")
        os.popen("pkill -f mdealer$")
        #os.popen("/etc/init.d/bluetooth restart")
        os.system("/usr/bin/mdealer&")

        self.parent['configuration_window'].hide()


def get_hex_color(color):
    return '#%02x%02x%02x' % (color.red/0x101, color.green/0x101, color.blue/0x101)
