<?php
session_start();

// Constants
$domain = 'wiki2xhtml.sf.net';

	
// Only send if not done already
if ($_SESSION['last'] != getURI() || $_POST['backlink'] != $_SESSION['prevBacklink']) {
	if (send()) {
		$status = 'sent.';
	} else {
		$status = 'not sent.';
	}
} else {
	$status = 'F5.';
}
$_SESSION['prevBacklink'] = $_POST['backlink'];

$_SESSION['last'] = getURI();

function getURI() {
 $pageURL = 'http';
 if ($_SERVER["HTTPS"] == "on") {$pageURL .= "s";}
 $pageURL .= "://";
 if ($_SERVER["SERVER_PORT"] != "80") {
  $pageURL .= $_SERVER["SERVER_NAME"].":".$_SERVER["SERVER_PORT"].$_SERVER["REQUEST_URI"];
 } else {
  $pageURL .= $_SERVER["SERVER_NAME"].$_SERVER["REQUEST_URI"];
 }
 return $pageURL;
}


function send() {
	$mail_date = gmdate("D, d M Y H:i:s")." +0000";

	$subject = '('.$domain.') Document not found';

	
	$c = "Page not found: " . getURI();
	if ($_POST['backlink'] != '') {
		$content .= "\nBacklink: " . $_POST['backlink'];
	}
	
	// $content = wordwrap($content);
	
	$header = getHeader('mail@granjow.net', $domain);
	$mailTo = 'simon.eu@gmail.com';
	$content = $header."\n\n".$mail_date."\n\n".$c;
	
	// Send
	mail($mailTo, $subject, $content, $header);
	
	return true;
}

function getHeader($from, $name) {
	$header = 'From: '.$name.'<'.$from.">\n";
	$header.= 'Reply-To: '.$from."\n";
	$header.= "X-Mailer: Test\n";
	$header.= "Content-Type: text/plain; charset=\"utf-8\"\n";
	return $header;
}

?>
<!--endtop-->

<!--head-->

<script type="text/javascript">
function putBacklink() {
	var e = window.history;
	document.getElementById('backlink').value = window.history[0];
}
document.onload = putBacklink;
</script>

<!--/head-->

{{H1:File not Found}}
{{Title:Error 404 -- Means: File not found %s}}

$$
&#x203a; <a href="<?php echo (getURI()); ?> "><?php echo (getURI()); ?> </a> &#x2039;
$$

What links here? &#x2014; Was verlinkt hierhin? &#x2014; Quoi liens là?\\ 
<form action="<?php echo $_SERVER['self']; ?>" id="badlinksource" method="post">
	<input class="w1" type="text" name="backlink" onclick="putBacklink()" value="<?php echo($_POST['backlink']); ?>" /> <br />
	<input class="button" type="submit" id="send" value="OK" />
</form>
<?php 
if ($_POST['backlink'] != '') {
	echo('Thanks / Danke / Merci');
}
?>

== Document not found ==
The requested document was not found on this server. Please check the address or visit this page again in a few days.

== Dokument nicht gefunden ==
Das Dokument konnte nicht gefunden werden. Bitte prüfe deinen Link oder versuche es in ein paar Tagen nochmals.

== Document non trouvé ==
Le document ne peut pas être trouvé sur çe serveur. Verifie ton lien ou re-visite cette page en quelques jours.
----
<address>
Web Server at <?php echo($domain); ?>
</address>
