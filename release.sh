#!/bin/bash
# Creates a new release more or less automatically
# Usage: release.sh {NEW VERSION} (e.g. ./release.sh 0.7.4)

f=wiki2xhtml
dir=versions/$f-$1/

if [ -e versions/ ]
then
    :
else
    mkdir versions
fi

# Remove unnecessary files in the help dir
./clean.sh

# Remove old releases of the same version
cd versions
rm -rf $f-$1*
mkdir $f-$1
cd ..

# Copy all important files 
echo Copying ...

# Copy files for the webpage
mkdir ${dir}help
cp help/*.png help/*.jpg help/*.ico ${dir}help

# Copy Cheatsheets
cp doc/*.pdf help
cp help/*.pdf ${dir}help

# Copy Designs
cp -r style ${dir}

# Copy documentation
cp -r doc/ ${dir}
rm -f ${dir}doc/*.odt ${dir}doc/*.ott

# Copy .wx files
cp *.wx ${dir}

# Copy jar file
cp wiki2xhtml.jar ${dir}

# Copy start scripts
cp start.* helpfiles.* wiki2xhtml versions/$f-$1

# Change Unix line breaks to Windows line breaks for poor Windows users
echo Line breaks ...
cd ${dir}/doc/txt/
unix2dos *.txt 
cd -

# Remove .svn Directories
rm -rf `find ${dir} -type d -name .svn`

# Create the zip files 
cd versions
echo bz2 ...
tar -cjf $f-$1.tar.bz2 $f-$1/*
echo gz ...
tar -czf $f-$1.tar.gz $f-$1/*
echo zip ...
zip -9r $f-$1.zip $f-$1/* 1>/dev/null
echo 7z ...
7z a -t7z -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on $f-$1.7z $f-$1/* 1>/dev/null
echo 7z-exe ...
7z a -sfx -mhe=on -t7z -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on $f-$1.exe $f-$1/* 1>/dev/null
