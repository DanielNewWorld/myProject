<?php
$file = '/var/www/nets.in.ua/log/family.log';
$text .= date('d-m-Y H:i:s').PHP_EOL;
$text .= 'begin...'.PHP_EOL;
$text .= 'MY_SYGN = '.$MY_SIGN.PHP_EOL.'GET_SIGN = '.$_GET[SIGN].PHP_EOL;
$text .= 'otvet = '.$otvet.PHP_EOL;
$text .= "RECEIPT_NUM = $_GET[RECEIPT_NUM],".PHP_EOL."SERVICE_ID = $_GET[SERVICE_ID],".PHP_EOL."TRADE_POINT = $_GET[TRADE_POINT]".PHP_EOL;
$text .= "PAY_AMOUNT: ".$_GET[PAY_AMOUNT].PHP_EOL;
$text .= "PAY_ID: ".$_GET[PAY_ID].PHP_EOL;
$text .= "PAY_ACCOUNT: ".$_GET[PAY_ACCOUNT].PHP_EOL;
//$text .= '================='.PHP_EOL;
//echo $text;
$fOpen = fopen($file,'a');
fwrite($fOpen, $text);
fclose($fOpen);

 $otvet=-90;
 $comment='';
 $id=0;

if ((include 'nets') == FALSE) {$otvet=-12;} else
{
 error_reporting(0);
 date_default_timezone_set('Europe/Kiev');

  $link = new mysqli($host, $user, $password, $db);
  if ($link->connect_errno) {$otvet=-49;} else
  {
    $MY_SIGN = MD5($_GET[ACT]."_".$_GET[PAY_ACCOUNT]."_".$_GET[SERVICE_ID]."_".$_GET[PAY_ID]."_".PegFb);
    $MY_SIGN = strtoupper($MY_SIGN);
  $otvet=-101;
  $link->set_charset("utf8");
  
 //proverka id klienta
 if ($_GET[ACT] == 1 and $_GET[SIGN] == $MY_SIGN)
 { 
   $otvet = -40;
   $line="SELECT * FROM users WHERE name='$_GET[PAY_ACCOUNT]'";
   $result = $link->query($line);
   
   while ($row = $result->fetch_assoc()) {
    $id_users = $row['id'];
    $balans = 0;
    $plan = 0.0;
    $balans = $row['balance'];
    
   $line3 = "SELECT * FROM plans2";
   $result3 = $link->query($line3);
   while ($row3 = $result3->fetch_assoc()) {
   if ($row['paket'] == $row3['id']){
     $paket_name = substr($row3['name'], 6);
     $paket_price = $row3['price'];
    }}

    $discount = $row['discount'];
    //$comment = $discount;
    $scidka = $discount*$paket_price/100;
    $balans_end = $balans - $paket_price + $scidka;
    if ($balans_end >= 0) {$do_splati = 0;} else
      {$do_splati = -1*$balans_end;};
    
    $otvet = 21;
    $comment = '<balance>'.$do_splati.'</balance>'.'<name>'.$row['fio'].', баланс: '.$balans_end.' грн. </name><account>'.$_GET[PAY_ACCOUNT].'</account><service_id>'.
               $_GET[SERVICE_ID].'</service_id><abonplata>'.$paket_price.'</abonplata>';
    };
 };

 //proverka platega klienta
 if ($_GET['ACT']==7 and $_GET[SIGN] == $MY_SIGN)
 {
   $line="SELECT * FROM pays WHERE coment LIKE '$_GET[PAY_ID]'";
   $result = $link->query($line);
   $otvet = -10;
   while ($row = $result->fetch_assoc()) {
    if ($row['cash'] != 0)
    {
      $otvet=11;
      $provider_id = $row['id'];
      $tr_time = date ("d.m.Y H:i:s",$row['time']);
      $comment = '<transaction><pay_id>'.$row['coment'].'</pay_id><service_id>'.$_GET[SERVICE_ID].'</service_id><amount>'.
                 $row['cash'].'</amount><status>111</status><time_stamp>'.$tr_time.'</time_stamp></transaction>';
    };
   };
 };
 
 //popolnenie klienta
 if ($_GET[ACT]==4 and $MY_SIGN == $_GET[SIGN])
 {
   $line="SELECT * FROM pays WHERE coment LIKE '$_GET[PAY_ID]'";
   $result = $link->query($line);
   while ($row = $result->fetch_assoc()) 
     {$otvet=-100;};
   
   if ($otvet!=-100)
   {
     $line="SELECT * FROM users WHERE name LIKE '$_GET[PAY_ACCOUNT]'";
     $result = $link->query($line);
       while ($row = $result->fetch_assoc()) //esli est account
       {
         $id = $row['id'];
         $times = time();
         $reason = "'RECEIPT_NUM = $_GET[RECEIPT_NUM],".PHP_EOL."SERVICE_ID = $_GET[SERVICE_ID],".PHP_EOL."TRADE_POINT = $_GET[TRADE_POINT]'";
         $coment = $_GET[PAY_ID];
         $line2="INSERT INTO pays (mid, cash, time, admin_id, admin_ip, office, reason, coment, type, category) VALUES
                                  ($id, $_GET[PAY_AMOUNT], $times, 6, 1295304594, 1, $reason, '$coment', 10, 600)";
 	 $result2 = $link->query($line2);
	
	$otvet = -14;
	$line3="SELECT * FROM pays WHERE coment LIKE '$_GET[PAY_ID]'";
        $result3 = $link->query($line3);
        while ($row3 = $result3->fetch_assoc()) {
	if ($row3['cash'] == $_GET[PAY_AMOUNT]) {
          $otvet=22;
          $amount = $row3['cash'];
          $comment = '<pay_id>'.$row3['coment'].'</pay_id><service_id>'.$_GET[SERVICE_ID].'</service_id>'.
                     '<amount>'.$amount.'</amount><description></description>';         
	  $line4 = "UPDATE users SET balance=balance+$_GET[PAY_AMOUNT] WHERE name LIKE '$_GET[PAY_ACCOUNT]'";
	  $result4 = $link->query($line4);
         };
	};
     };
   };
};
mysqli_close($link);
};
}

//$file = '/var/www/nets.in.ua/log/family.log';
//$text .= date('d-m-Y H:i:s').PHP_EOL;
//$text .= 'MY_SYGN = '.$MY_SIGN.PHP_EOL.'GET_SIGN = '.$_GET[SIGN].PHP_EOL;
$text .= 'otvet = '.$otvet.PHP_EOL;
//$text .= "RECEIPT_NUM = $_GET[RECEIPT_NUM],".PHP_EOL."SERVICE_ID = $_GET[SERVICE_ID],".PHP_EOL."TRADE_POINT = $_GET[TRADE_POINT]".PHP_EOL;
//$text .= "PAY_AMOUNT: ".$_GET[PAY_AMOUNT].PHP_EOL;
//$text .= "PAY_ID: ".$_GET[PAY_ID].PHP_EOL;
//$text .= "PAY_ACCOUNT: ".$_GET[PAY_ACCOUNT].PHP_EOL;
$text .= '================='.PHP_EOL;
//echo $text;
$fOpen = fopen($file,'a');
fwrite($fOpen, $text);
fclose($fOpen);

header("Content-Type: application/xml"); 
$provider_time = date ("d.m.Y H:i:s");
$xml = <<<XML
<?xml version='1.0' encoding='utf-8'?>
<pay-response>
  $comment
  <status_code>$otvet</status_code>
  <time_stamp>$provider_time</time_stamp>
</pay-response>
XML;

echo $xml;
?>