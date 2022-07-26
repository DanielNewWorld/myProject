<?php
$otvet = 0;
if ((include 'nets') == FALSE) {
  $otvet=-90;
} else { 
 $text = "{\"ParsedResults\":[";
 $text2 = "{\"ParsedResults\":[";
 $count_user = 0;
 $count_user2 = 0;
 
$link = new mysqli($host, $user, $password, $db);
if ($link->connect_errno) {$otvet = -49;}
else {
  $otvet = -101;
  $link->set_charset("utf8");
 
 //spisok teh y kogo plus
  $line="SELECT * FROM users";
  $result = $link->query($line);
  while ($row = $result->fetch_assoc()) {
    $id_users = $row['id'];
    $balans = 0;
    $otvet = 10;

    $line2="SELECT * FROM pays WHERE mid=$id_users";
    $result2 = $link->query($line2);
    while ($row2 = $result2->fetch_assoc()) {$balans = $balans + $row2['cash'];};
    
    $line3 = "SELECT * FROM plans2";
    $result3 = $link->query($line3);
    while ($row3 = $result3->fetch_assoc()) {
      if ($row['paket'] == $row3['id']) {
        $paket_name = substr($row3['name'], 6);
        $paket_price = $row3['price'];}}
    
    $tarif = $row['paket'];
    $discount = $row['discount']; 
    $scidka = $discount*$paket_price/100;
    $balans_end = $balans - $paket_price + $scidka;
    if (date('d')<=10) $balans_end = $balans_end + $paket_price - $scidka;
    
    if ($balans_end < $row['limit_balance']) $tarif = 9;    
    if ($row['grp'] == 2) $tarif = 9;  //grupa udaleni
    $user_ip = $row['ip'];
    $user_ip = substr($user_ip, 12);
    if ($count_user < 169) {
        $text .= "{\"ip\":\"$user_ip\", \"tarif\":$tarif},";
        $count_user = $count_user + 1;
      } else {
      $text2 .= "{\"ip\":\"$user_ip\", \"tarif\":$tarif},";
      $count_user2 = $count_user2 + 1;
    }    
    
    //update balance
    //$line3="UPDATE users SET balance=$balans WHERE id=$id_users";
    //$result3 = $link->query($line3);
    //echo "ip: $user_ip, balans_end: $balans_end, balans: $balans, tarif: $tarif <br>";
    };
   
    $text  = substr($text, 0, strlen($text)-1);   
    $text2 = substr($text2, 0, strlen($text2)-1); 
    mysqli_close($link);
  };
};

//echo "otvet: ".$otvet."<br>";
//echo "text: ".$text."<br>";
//echo "text2: ".$text2."<br>";

$file = '/var/www/nets.in.ua/in_out/adress_list_bill.txt';
$count_user = $count_user - 1;
$count_user2 = $count_user2 - 1;
$text .= "], \"count\":$count_user}";

$file2 = '/var/www/nets.in.ua/in_out/adress_list_bill2.txt';
$text2 .= "], \"count\":$count_user2}";

$fOpen = fopen($file,'w');
fwrite($fOpen, $text);
fclose($fOpen);

$fOpen = fopen($file2,'w');
fwrite($fOpen, $text2);
fclose($fOpen);
?>