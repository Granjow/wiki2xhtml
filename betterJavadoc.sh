#!/bin/bash
echo $0
echo $1
cd $1
if (( $? == 0 ))
then
    echo "Changed to directory $1."
    for i in $(find . -name '*.html'); do perl -pi -e 's/border="\d" //i' $i; done
    for i in $(find . -name '*.html'); do perl -pi -e 's/cellpadding="\d" //i' $i; done
    for i in $(find . -name '*.html'); do perl -pi -e 's/cellspacing="\d" //i' $i; done
    for i in $(find . -name '*.html'); do perl -pi -e 's/bgcolor="[^"]+" //i' $i; done
else
    echo "Could not change to directory $1!"
fi