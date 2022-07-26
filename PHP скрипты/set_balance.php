<?php
 $otvet=-90;
 $comment='';
 $id=0;
 
 if ((include 'nets') == FALSE) {$otvet=-10;} else
{
 //error_reporting(0);
 date_default_timezone_set('Europe/Kiev');

  $link = new mysqli($host, $user, $password, $db);
  if ($link->connect_errno) {$otvet=-49;} else
  {
   $link->set_charset("utf8");

   $line="SELECT * FROM users";
   $result = $link->query($line);
   
   $otvet=-40;
   while ($row = $result->fetch_assoc()) {
   $id_users = $row['id'];
   $user_balance = $row['balance'];
   
   $balans = 0;     
   $line2="SELECT * FROM pays WHERE mid=$id_users";
   $result2 = $link->query($line2);
   while ($row2 = $result2->fetch_assoc()) {
     {$balans = $balans + $row2['cash'];};
   };
     
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
   $otvet = 21;
   echo "id=$id_users   balance: $user_balance         ($balans_end)   $paket_name<br>";
   
   $line3 = "UPDATE users SET balance=$balans WHERE id=$id_users";
   $result3 = $link->query($line3);
   }; 
   mysqli_close($link);
  };
};

  echo $otvet;
?>