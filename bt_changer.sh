#!/bin/bash

user="alex"
mdealer="python /home/alex/Escritorio/mdealer/py/Bluetooth.py&" 

function restart_mdealer() {
    pkill -f Bluetooth.py$
    su -c "$mdealer" $user
}

lines=`hciconfig|grep hci|wc -l`
if [ -e /tmp/bt_working ]; then
    old_lines=`cat /tmp/bt_working`
fi
echo $lines > /tmp/bt_working

if [ "$lines" != "$old_lines" ]; then
    case $lines in
        1)
            hciconfig hci0 up
        ;;
        2)
            hciconfig hci0 up
            hciconfig hci0 down
        ;;
        *)
            hciconfig hci0 up
        ;;
    esac

    restart_mdealer
fi

if [ -e /tmp/main.conf ]; then
    mv /tmp/main.conf /etc/bluetooth
    /etc/init.d/bluetooth restart
    sleep 5s
    restart_mdealer
    #restart bluetooths conf
    $0
fi
