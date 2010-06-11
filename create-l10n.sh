if [ -e /usr/lib/jvm/java-gcj-4.4/bin/javac ]
then
    export JAVAC=/usr/lib/jvm/java-gcj-4.4/bin/javac
else
    echo Attention: msgfmt might need gcj.
fi

echo -n "en  "
msgfmt --java2 -d . -r bin.l10n.Messages -l en l10n/po/en.po &
echo -n "en_GB  "
msgfmt --java2 -d . -r bin.l10n.Messages -l en_GB l10n/po/en_GB.po

# Check whether command succeeded
if (( $? ))
then
    echo Failed. msgfmt seems to rely on gcj only. Perhaps try something like «export JAVAC=/usr/lib/jvm/java-gcj-4.4/bin/javac» before running this script.
    exit
fi
echo -n "de  "
msgfmt --java2 -d . -r bin.l10n.Messages -l de l10n/po/de.po &
echo -n "de_CH  "
msgfmt --java2 -d . -r bin.l10n.Messages -l de_CH l10n/po/de_CH.po
echo -n "fr  "
msgfmt --java2 -d . -r bin.l10n.Messages -l fr l10n/po/fr.po &
echo -n "fr_CH  "
msgfmt --java2 -d . -r bin.l10n.Messages -l fr_CH l10n/po/fr_CH.po
echo -n "it  "
msgfmt --java2 -d . -r bin.l10n.Messages -l it l10n/po/it.po &
echo -n "it_CH  "
msgfmt --java2 -d . -r bin.l10n.Messages -l it_CH l10n/po/it_CH.po
echo -n "ru  "
msgfmt --java2 -d . -r bin.l10n.Messages -l ru l10n/po/ru.po &
echo -n "es  "
msgfmt --java2 -d . -r bin.l10n.Messages -l es l10n/po/es.po
echo -n "hr  "
msgfmt --java2 -d . -r bin.l10n.Messages -l hr l10n/po/hr.po &
echo

mkdir bin/bin >/dev/null 2>&1
cp -r bin/l10n bin/bin/