<?php
 $otvet=-90;
 $comment='';
 $id=0;
 
 if ((include 'nets') == FALSE) {$otvet=-10;} else
{
 error_reporting(0);
// date_default_timezone_set('Europe/Kiev');

  $link = new mysqli($host, $user, $password, $db);
  if ($link->connect_errno) {$otvet=-49;} else
  {
   $link->set_charset("utf8");

 //proverka id klienta
 if ($_GET[cmd]=="verify")
 {
   $line="SELECT * FROM users WHERE name='$_GET[account]'";
   $result = $link->query($line);
   
   $otvet=-40;
     while ($row = $result->fetch_assoc()) {
     $id_users = $row['id'];
     $balans = $row['balance'];
     
     $line3 = "SELECT * FROM plans2";
     $result3 = $link->query($line3);
     while ($row3 = $result3->fetch_assoc()) {
       if ($row['paket'] == $row3['id']){
       $paket_name = substr($row3['name'], 6);
       $paket_price = $row3['price'];
     }}

     $discount = $row['discount'];
     $scidka = $discount*$paket_price/100;
     $balans_end = $balans - $paket_price + $scidka;

     if ($balans_end >= 0) {$do_splati = 0;} else
      {$do_splati = -1*$balans_end;};

     $otvet = 21;
     $comment = '<text>'.$row['fio'].', тариф: '.$paket_name.' '.$paket_price.' грн.'.
                ', баланс: '.$balans_end.' грн., к оплате: '.$do_splati.' грн.</text><comment>Account exist.</comment>';
 }; 
 };

 //proverka platega klienta
 if ($_GET[cmd]=="check")
 {
   $line="SELECT * FROM pays WHERE coment LIKE '$_GET[id]'";
   $result = $link->query($line);
   $otvet = -27;
   while ($row = $result->fetch_assoc())
   {
    if ($row['cash'] != 0)
    {
      $otvet=27;
      $coment = $row['coment'];
      $provider_id = $row['id'];
      $provider_time = date ("Y-m-d H:i:s", $row['time']);
      $comment = '<id>'.$coment.'</id><provider_id>'.$provider_id.'</provider_id><provider_time>'.$provider_time.
                 '</provider_time><comment>Transaction complete</comment>';
    };
   };
 };
 
 //popolnenie klienta
 if ($_GET[cmd]=="pay")
 {
   $otvet = -27;
   $line="SELECT * FROM pays WHERE coment LIKE '$_GET[id]'";
   $result = $link->query($line);
   while ($row = $result->fetch_assoc())
   {
    if ($row['cash'] != 0)
    {
     $otvet=27;
     $provider_id = $row['id'];
     $provider_time = date ("Y-m-d H:i:s", $row['time']);
     $comment = '<id>'.$_GET[id].'</id><provider_id>'.$provider_id.'</provider_id><provider_time>'.$provider_time.
                '</provider_time><comment>Transaction complete</comment>';
    };
   };
   if ($otvet!=27)
   {
     $line="SELECT * FROM users WHERE name LIKE '$_GET[account]'";
     $result = $link->query($line);
      while ($row = $result->fetch_assoc()) //esli est account
      {
        $id = $row['id'];
	$times = time();
	$reason = "'merchant_id = $_GET[merchantid]'";
        $line2="INSERT INTO pays (mid, cash, time, admin_id, admin_ip, office, reason, coment, type, category) VALUES 
                            ($id, $_GET[sum], $times, 5, 1295304594, 1, $reason, '$_GET[id]', 10, 600)";
        $result2 = $link->query($line2);
	
	$line3="SELECT * FROM pays WHERE coment LIKE '$_GET[id]'";
        $result3 = $link->query($line3);
        while ($row3 = $result3->fetch_assoc())
        {
	 if ($row3['cash'] == $_GET[sum])
	 {
          $otvet=27;
          $provider_id = $row3['id'];
          $provider_time = date ("Y-m-d H:i:s", $row3['time']);
          $comment = '<id>'.$_GET[id].'</id><provider_id>'.$provider_id.'</provider_id><provider_time>'.$provider_time.
                     '</provider_time><comment>Transaction complete</comment>';
          
          $line4="UPDATE users SET balance=balance+$_GET[sum] WHERE name LIKE '$_GET[account]'";
          $result4 = $link->query($line4);
         };
	};
      };
    };
 };
 
 //proverka balansa klienta
 if ($_GET[cmd]=="balance")
 {
   $otvet = -40;
   $line="SELECT * FROM users WHERE name LIKE '$_GET[id]'";
   $result = $link->query($line);
  
   while ($row = $result->fetch_assoc()){
     $id_users = $row['id'];
     $balans = $row['balance'];
     
     $line3 = "SELECT * FROM plans2";
     $result3 = $link->query($line3);
     while ($row3 = $result3->fetch_assoc()) {
       if ($row['paket'] == $row3['id']){
       $paket_name = substr($row3['name'], 6);
       $paket_price = $row3['price'];
     }}

     $discount = $row['discount'];
     $scidka = $discount*$paket_price/100;
     $balans_end = $balans - $paket_price + $scidka;
   
     $otvet=10;
     $comment = '<balance>'.$balans_end.'</balance>';
   };   
 };
 
 //otmena platega
// if ($_GET[cmd]=="cancel")
// {
//   $line="SELECT * FROM `pays` WHERE `coment` LIKE '".$_GET['id']."' and `id` LIKE '".$_GET['providerid']."'";
//   $result = mysql_query($line);
//   $otvet = -27;
//   while ($row = mysql_fetch_array($result, MYSQL_NUM))
//   {
//   if ($_GET[sum] == $row[2])
//   {
/*    $id = $row[1];
    $line2="SELECT * FROM `users` WHERE `id` LIKE '".$id."' and `name` LIKE '".$_GET['account']."'";
    $result2 = mysql_query($line2);
    $otvet = -40;
    while ($row2 = mysql_fetch_array($result2, MYSQL_NUM))
    {
     $line3="INSERT INTO `pays` (`mid`, `cash`, `time`, `admin_id`, `admin_ip`, `office`, `reason`, `coment`, `type`, `category`) VALUES 
                            (".$id.",0,".time().",5,1295304594,1,'".$_GET['providerid'].":10:600:".$_GET['sum']."::','".$_GET['id']."',50,501)";
     $result3 = mysql_query($line3);
     $line3="SELECT * FROM `pays` WHERE `reason` LIKE '".$_GET['providerid'].":10:600:".$_GET['sum']."::'";
     $result3 = mysql_query($line3);
     while ($row3 = mysql_fetch_array($result3, MYSQL_NUM))
     {
      $id3 = $row3[0];
     };
     $line5="INSERT INTO `pays` (`mid`, `cash`, `time`, `admin_id`, `admin_ip`, `office`, `reason`, `type`, `category`) VALUES 
                            (".$id.",0,".time().",5,1295304594,1,'".$id3.":0',50,502)";
     $result5 = mysql_query($line5);
     
     $line4="DELETE FROM `pays` WHERE id = ".$_GET['providerid'];
     $result4 = mysql_query($line4);
    
     $otvet=80;
     $comment = '<id>'.$_GET['id'].'</id><provider_id>'.$provider_id.'</provider_id>'.
                '<comment>Transaction canceled</comment>';
    };
   };
   };
 };
*/
mysqli_close($link);
};
};

header("Content-Type: application/xml"); 
$xml = <<<XML
<?xml version='1.0' encoding='utf-8'?>
<response>
  <result>$otvet</result>
  $comment
</response>
XML;

echo $xml;

?>