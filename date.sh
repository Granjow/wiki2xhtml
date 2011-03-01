#!/bin/bash
cd $(dirname $0)
echo $(git log -1 --pretty=tformat:%ad --date=short HEAD)