#!/usr/bin/env python

import Image, ImageDraw, ImageFont
import Config
import shutil
import os
import pwd

class Writer():
    img = None;
    draw = None;
    font = None;
    
    ## Change this!!!!!!!
    BGCOLOR = "#73d216"
    FGCOLOR = (255, 255, 255)

    path = None
    cfg = None

    
    def __init__(self, bgcolor):
        self.cfg = Config.Config()
        self.BGCOLOR = bgcolor
        self.path = os.path.split(__file__)[0]


    """
        Create a copy of the image witha a rectangle and text on it
        * sting_user -> the string configured to show before the reward id
        * string_id -> autocreated id to control the rewards
        * orig_file -> the file without changes
        * save_file -> the new file create with the strings
    """
    def add_text(self, string_user, string_id, orig_file, save_file):
        img = Image.open(orig_file)
        draw = ImageDraw.Draw(img)
        font = ImageFont.truetype(self.cfg.get_font(), 13)

        # Get the image and text size
        text_width_user, text_height = font.getsize(string_user)
	text_width_id = font.getsize(string_id)[0]
        image_width, image_height = img.size

        # Get a portion of the image to make it translucid at the top of the new image
#        cropper = img.crop((0, 0, image_width, text_height*2)) 
        cropper = img.crop((0, 0, image_width, text_height*4)) 
        new_image = Image.new(img.mode, cropper.size, self.BGCOLOR)
        new_image = Image.blend(new_image, cropper, 0.5)

        img.paste(new_image, (0,0))

        # self.draw.rectangle((0, 0, image_height, text_height*2), fill = True)

        x_centered_user = image_width/2 - text_width_user/2
	x_centered_id = image_width/2 - text_width_id/2
        draw.text((x_centered_user, text_height/2), string_user, font = font, fill = self.FGCOLOR)
        draw.text((x_centered_id, text_height*2), string_id, font = font, fill = self.FGCOLOR)
        img.save(save_file)



    """
        Read the file stuff/awards_ids.txt to create the award image, and then, delete the first line in this file
    """
    def get_awarded_filename(self, filename):
        return_file = os.path.join('/tmp/', os.path.split(filename)[1])

        temp_file = open('/tmp/awards_ids.txt', 'w')
        
        # We will go to delte the first line in awards_ids.txt
        index = 0
        for line in file(os.path.join(self.path, '../stuff/awards_ids.txt')):
            index += 1

            if (index == 1): self.add_text(self.cfg.get_the_user_string(), line[0:-1], filename, return_file)
            else: temp_file.write(line)

        temp_file.close()
        # FIXME: Chown to root:www-data -> the user executing always must be root!
        #os.chown('/tmp/awards_ids.txt', 0, pwd.getpwnam("www-data").pw_uid)
        os.chmod('/tmp/awards_ids.txt', 0664)
        shutil.move('/tmp/awards_ids.txt', os.path.join(self.path, '../stuff/awards_ids.txt'))

        # If the execution is here, and index value is 1, we empty the file, so we return the file without award
        if (index == 0):
            return filename
                
        return return_file # Return the path to the awarded picture file
