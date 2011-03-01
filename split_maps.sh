#!/bin/bash

function split {
    # $1 = the file to split
    # $2 = the dir to output the tiles

    # get the name without extension
    dir_name=`echo $1 | awk -F'/' '{ print $NF }'|cut -d. -f1`

    # file height and file width
    width=`file $1 | cut -d' ' -f4`
    height=`file $1 | cut -d' ' -f6 | cut -d ',' -f1`

    # create the directory to allow images
    if [ ! -d $2/$dir_name ]; then mkdir $2/$dir_name; fi

    # make the cropping!
    convert -crop 128x128 $1 $2/$dir_name/%d.png

    array_width=`echo $width/128 | bc`
    # if the division isn't exact increment the var
    # FIXME: Esto no debería pasar con los nuevos mapas que ya tienen en cuenta ser múltiplos de 128
    if [ `echo $width%128|bc` != "0" ]; then array_width=`expr $array_width + 1`; fi

    array_height=`echo $height/128 | bc`
    # if the division isn't exact increment the var
    if [ `echo $height%128|bc` != "0" ]; then array_height=`expr $array_height + 1`; fi

    array_dimension=`echo "$array_width * $array_height"|bc`
    array_dimension=`expr $array_dimension - 1`

    tiles_index=""
    for i in `seq 0 $array_dimension`; do 
        if [ "$i" == "0" ]; then
            tiles_index=$i
        else
            tiles_index="$tiles_index $i"
        fi
    done

    echo "$dir_name.png|$width|$height|$tiles_index" >> $2/index
}

if [ "$2" == "" ]; then
    echo "FIFOOOOO! que necesito el directorio de las imágenes y el directorio de salida ;)"
    exit -1
fi

if [ ! -d $2 ]; then mkdir $2; fi
# delete the splits info

rm -f "$2/index"

# do the same with all the png's file in the dir
for file in `ls $1/*.png`; do
    split $file $2
done
