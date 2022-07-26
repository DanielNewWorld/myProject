<?php
function unicodeString($str, $encoding=null) {
    if (is_null($encoding)) $encoding = ini_get('mbstring.internal_encoding');
    return preg_replace_callback('/\\\\u([0-9a-fA-F]{4})/u', create_function('$match', 'return mb_convert_encoding(pack("H*", $match[1]), '.var_export($encoding, true).', "UTF-16BE");'), $str);
}

if ((include 'nets') == FALSE) {
  $otvet=-90;
} else
{
  $text  = "[";
  $count_user = 0;
  $recipient_3 = '';
  $recipient_4 = '';
  $recipient_5 = '';
  $log = time().PHP_EOL;
 
  $link = new mysqli($host, $user, $password, $db);
  if ($link->connect_errno) {$otvet=-49;} else
  {
   $link->set_charset("utf8");

 //spisok teh y kogo minus
   $line="SELECT * FROM users";
   $result = $link->query($line);
   while ($row = $result->fetch_assoc()) {
    $id_users = $row['id'];

    $balans = 0;    
    $line2="SELECT * FROM pays WHERE mid=$id_users";
    $result2 = $link->query($line2);
    while ($row2 = $result2->fetch_assoc())
      {$balans = $balans + $row2['cash'];};    
    
   $line3 = "SELECT * FROM plans2";
   $result3 = $link->query($line3);
   while ($row3 = $result3->fetch_assoc()) {
   if ($row['paket'] == $row3['id']){
     $paket_name = substr($row3['name'], 6);
     $paket_price = $row3['price'];
    }}
    
    $tarif = $row['paket'];
    if ($row['grp'] == 2) $tarif = 9;  //grupa udaleni
    if ($row['grp'] == 3) $tarif = 9;  //grupa nereguljarni
    
    $discount = $row['discount'];
    $scidka = $discount*$paket_price/100;
    $balans_end = $balans - $paket_price + $scidka;

    if ($balans_end > $row['limit_balance']) $tarif = 9;

    $user_login = $row['name'];

    if ($tarif != 9) {    
      $text .= "{\"login\":\"$user_login\", \"tarif\":$tarif},";
      $count_user = $count_user + 1;
      if ($tarif == 3) {$recipient_3 .= "<recipient>".$user_login."</recipient>";};
      if ($tarif == 4) {$recipient_4 .= "<recipient>".$user_login."</recipient>";};
      if ($tarif == 5) {$recipient_5 .= "<recipient>".$user_login."</recipient>";};
      //echo "ip: $user_ip, balans_end: $balans_end, balans: $balans, tarif: $tarif <br>";
    };
   };
   
   $text  = substr($text, 0, strlen($text)-1);   
   mysqli_close($link);
  };
};

$file = '/var/www/nets.in.ua/in_out/sms_list.txt';
$count_user = $count_user - 1;
$text .= "]";

$fOpen = fopen($file,'w');
fwrite($fOpen, $text);
fclose($fOpen);

$month = date('m');
$dates = "11.$month.21";

//__________________Ekonom
$text = htmlspecialchars(unicodeString("$dates \u0434\u043E\u0441\u0442\u0443\u043F \u0434\u043E \u0456\u043D\u0442\u0435\u0440\u043D\u0435\u0442\u0443 \u0431\u0443\u0434\u0435 \u0437\u0430\u0431\u043b\u043e\u043a\u043e\u0432\u0430\u043d\u0438\u0439. \u0411\u0443\u0434\u044C-\u043B\u0430\u0441\u043A\u0430, \u043F\u043E\u043F\u043E\u0432\u043D\u0456\u0442\u044c \u0440\u0430\u0445\u0443\u043D\u043E\u043A \u043D\u0430 150 \u0433\u0440\u043D."));
$description = iconv('windows-1251', 'utf-8', htmlspecialchars('Info new month'));
$start_time = 'AUTO';
$end_time = 'AUTO';
$rate = 120;
$lifetime = 4;
$source = 'NETS'; // Alfaname
//$recipient = '380990567443';
$user = '380990567443';
$password = 'p19';
$myXML 	 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
$myXML 	.= "<request>";
$myXML 	.= "<operation>SENDSMS</operation>";
$myXML 	.= '		<message start_time="'.$start_time.'" end_time="'.$end_time.'" lifetime="'.$lifetime.'" rate="'.$rate.'" desc="'.$description.'" source="'.$source.'">'."\n";
$myXML 	.= "		<body>".$text."</body>";
$myXML 	.= "		".$recipient_3;
$myXML 	.=  "</message>";
$myXML 	.= "</request>";
$ch = curl_init();
curl_setopt($ch, CURLOPT_USERPWD , $user.':'.$password);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);
curl_setopt($ch, CURLOPT_POST, 1);
curl_setopt($ch, CURLOPT_URL, 'http://sms-fly.com/api/api.php');
curl_setopt($ch, CURLOPT_HTTPHEADER, array("Content-Type: text/xml", "Accept: text/xml"));
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_POSTFIELDS, $myXML);
$response = curl_exec($ch);
curl_close($ch);
//echo '<textarea spellcheck="false" name="111" rows="25" cols="150">';
//echo $myXML;
//echo $response;
//echo '</textarea>';
$log .= $myXML.PHP_EOL.$response.PHP_EOL;

//Standart
$text = htmlspecialchars(unicodeString("$dates \u0434\u043E\u0441\u0442\u0443\u043F \u0434\u043E \u0456\u043D\u0442\u0435\u0440\u043D\u0435\u0442\u0443 \u0431\u0443\u0434\u0435 \u0437\u0430\u0431\u043b\u043e\u043a\u043e\u0432\u0430\u043d\u0438\u0439. \u0411\u0443\u0434\u044C-\u043B\u0430\u0441\u043A\u0430, \u043F\u043E\u043F\u043E\u0432\u043D\u0456\u0442\u044c \u0440\u0430\u0445\u0443\u043D\u043E\u043A \u043D\u0430 190 \u0433\u0440\u043D."));
$myXML 	 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
$myXML 	.= "<request>";
$myXML 	.= "<operation>SENDSMS</operation>";
$myXML 	.= '		<message start_time="'.$start_time.'" end_time="'.$end_time.'" lifetime="'.$lifetime.'" rate="'.$rate.'" desc="'.$description.'" source="'.$source.'">'."\n";
$myXML 	.= "		<body>".$text."</body>";
$myXML 	.= "		".$recipient_4;
$myXML 	.=  "</message>";
$myXML 	.= "</request>";
$ch = curl_init();
curl_setopt($ch, CURLOPT_USERPWD , $user.':'.$password);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);
curl_setopt($ch, CURLOPT_POST, 1);
curl_setopt($ch, CURLOPT_URL, 'http://sms-fly.com/api/api.php');
curl_setopt($ch, CURLOPT_HTTPHEADER, array("Content-Type: text/xml", "Accept: text/xml"));
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_POSTFIELDS, $myXML);
$response = curl_exec($ch);
curl_close($ch);

//echo '<textarea spellcheck="false" name="111" rows="25" cols="150">';
//echo $myXML;
//echo $response;
//echo '</textarea>';
$log .= $myXML.PHP_EOL.$response.PHP_EOL;

//Premium
$text = htmlspecialchars(unicodeString("$dates \u0434\u043E\u0441\u0442\u0443\u043F \u0434\u043E \u0456\u043D\u0442\u0435\u0440\u043D\u0435\u0442\u0443 \u0431\u0443\u0434\u0435 \u0437\u0430\u0431\u043b\u043e\u043a\u043e\u0432\u0430\u043d\u0438\u0439. \u0411\u0443\u0434\u044C-\u043B\u0430\u0441\u043A\u0430, \u043F\u043E\u043F\u043E\u0432\u043D\u0456\u0442\u044c \u0440\u0430\u0445\u0443\u043D\u043E\u043A \u043D\u0430 220 \u0433\u0440\u043D."));
$myXML 	 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
$myXML 	.= "<request>";
$myXML 	.= "<operation>SENDSMS</operation>";
$myXML 	.= '		<message start_time="'.$start_time.'" end_time="'.$end_time.'" lifetime="'.$lifetime.'" rate="'.$rate.'" desc="'.$description.'" source="'.$source.'">'."\n";
$myXML 	.= "		<body>".$text."</body>";
$myXML 	.= "		".$recipient_5;
$myXML 	.=  "</message>";
$myXML 	.= "</request>";
$ch = curl_init();
curl_setopt($ch, CURLOPT_USERPWD , $user.':'.$password);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);
curl_setopt($ch, CURLOPT_POST, 1);
curl_setopt($ch, CURLOPT_URL, 'http://sms-fly.com/api/api.php');
curl_setopt($ch, CURLOPT_HTTPHEADER, array("Content-Type: text/xml", "Accept: text/xml"));
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_POSTFIELDS, $myXML);
$response = curl_exec($ch);
curl_close($ch);

//echo '<textarea spellcheck="false" name="111" rows="25" cols="150">';
//echo $myXML;
//echo $response;
//echo '</textarea>';
$log .= $myXML.PHP_EOL.$response.PHP_EOL;

$file = '/var/www/nets.in.ua/log/sms_list.log';
$fOpen = fopen($file,'w');
fwrite($fOpen, $log);
fclose($fOpen);
?>