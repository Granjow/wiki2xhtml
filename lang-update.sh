xgettext -ktrc:1c,2 -ktrnc:1c,2,3 -ktr -kmarktr -ktrn:1,2 --from-code=UTF-8 -o l10n/po/keys.pot `find . -name "*.java" |grep "src/"`
msgmerge -U l10n/po/de.po l10n/po/keys.pot
msgmerge -U l10n/po/de_CH.po l10n/po/keys.pot
msgmerge -U l10n/po/en.po l10n/po/keys.pot
msgmerge -U l10n/po/en_GB.po l10n/po/keys.pot
msgmerge -U l10n/po/it.po l10n/po/keys.pot
msgmerge -U l10n/po/it_CH.po l10n/po/keys.pot
msgmerge -U l10n/po/fr.po l10n/po/keys.pot
msgmerge -U l10n/po/fr_CH.po l10n/po/keys.pot
msgmerge -U l10n/po/ru.po l10n/po/keys.pot
msgmerge -U l10n/po/es.po l10n/po/keys.pot
msgmerge -U l10n/po/hr.po l10n/po/keys.pot
