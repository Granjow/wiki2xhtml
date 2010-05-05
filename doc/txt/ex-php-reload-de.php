<?php
session_start();
?>
<!--endtop-->
<nowiki>
<?php
if (empty($_SESSION['counter']))
	$_SESSION['counter'] = 1;
else
	$_SESSION['counter']++;
?>
</nowiki>
{{H1:PHP: Reload-Zähler}}
{{Keywords:php, reload counter}}
Du hast diese Seite <?php echo $_SESSION['counter']; ?> mal aktualisiert. Drücke F5, um sie ein weiteres Mal zu aktualisieren. 

Der Code für dieses Beispiel ist:
$$((style="margin-left: 100px;"))
&lt;?php
session_start();
?&gt;
&lt;!--endtop--&gt;
&#123;{H1:PHP: Reload-Zähler}}<br />
&lt;nowiki&gt;
&lt;?php
if (empty($_SESSION['counter']))
	$_SESSION['counter'] = 1;
else
	$_SESSION['counter']++;
?&gt;
&lt;/nowiki&gt;<br />
Du hast diese Seite 
&lt;?php echo $_SESSION['counter']; ?&gt; times. 
aktualisiert. Drücke F5, um sie nochmals zu aktualisieren.
$$

Der Rest, wie das Menu, wird von wiki2xhtml eingefügt.

[[examples-de.html « zurück]]