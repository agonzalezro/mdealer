#!/usr/bin/env python
# -*- coding: utf-8 -*-

# I use this file only for debug and development

import sys
import time
from py.Applet import *
import os

def main():
    run_in_window()

if __name__ == "__main__":
    #Only for debug
    os.system("cd glade;make")
    sys.exit(main())
