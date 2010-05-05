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
{{H1:PHP: Reload count}}
{{Keywords:php, reload counter}}
You've (re-)loaded this page <?php echo $_SESSION['counter']; ?> times. Press F5 to reload it again.

The code for this example is:
$$((style="margin-left: 100px;"))
&lt;?php
session_start();
?&gt;
&lt;!--endtop--&gt;
&#123;{H1:PHP: Reload count}}<br />
&lt;nowiki&gt;
&lt;?php
if (empty($_SESSION['counter']))
	$_SESSION['counter'] = 1;
else
	$_SESSION['counter']++;
?&gt;
&lt;/nowiki&gt;<br />
You've (re-)loaded this page 
&lt;?php echo $_SESSION['counter']; ?&gt; times. 
Press F5 to reload it again.
$$

The rest like the menu will be inserted by wiki2xhtml.

[[examples.html « back]]