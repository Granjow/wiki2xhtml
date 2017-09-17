#!/bin/sh

echo "Generating .po files ..."
./lang-update.sh
echo "Generating l10n class files ..."
./create-l10n.sh
echo "Creating .jar file ..."
ant
