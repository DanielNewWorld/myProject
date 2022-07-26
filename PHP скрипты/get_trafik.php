<?php
if ((include 'nets') == FALSE) {$otvet=-90;} else
{
 $otvet=0;

$link = new mysqli($host, $user, $password, $db);
if ($link->connect_errno) {$otvet=-49;} else
{
  $link->set_charset("utf8");
 
  $date_m = date('m');
  if ($date_m == "01") $date_m = 1;
  if ($date_m == "02") $date_m = 2;
  if ($date_m == "03") $date_m = 3;
  if ($date_m == "04") $date_m = 4;
  if ($date_m == "05") $date_m = 5;
  if ($date_m == "06") $date_m = 6;
  if ($date_m == "07") $date_m = 7;
  if ($date_m == "08") $date_m = 8;
  if ($date_m == "09") $date_m = 9;
  
  $date_d = date('d');
  if ($date_d == "01") $date_d = 1;
  if ($date_d == "02") $date_d = 2;
  if ($date_d == "03") $date_d = 3;
  if ($date_d == "04") $date_d = 4;
  if ($date_d == "05") $date_d = 5;
  if ($date_d == "06") $date_d = 6;
  if ($date_d == "07") $date_d = 7;
  if ($date_d == "08") $date_d = 8;
  if ($date_d == "09") $date_d = 9;
  
  $table = "x".date('Y')."x".$date_m."x".$date_d;

  $table_real = 0;
  $line = "SHOW TABLES";
  $result = $link->query($line);
  while ($row = $result->fetch_assoc()) {
    if ($row['Tables_in_nets'] == $table) $table_real = 1;}; 
  //echo "real: ".$table_real."<br>";
 
 //if ($table_real == 0) {
 //  $line="CREATE TABLE $table (mid mediumint(9) NOT NULL DEFAULT 0,
 //                               time int(11) NOT NULL DEFAULT 0,
 //                               class tinyint(4) NOT NULL DEFAULT 0,
 //                               in bigint(20) unsigned NOT NULL DEFAULT 0,
 //                               out bigint(20) unsigned NOT NULL DEFAULT 0,
 //                               KEY mid (mid),
 //                               KEY time (time)
 //                              ) ENGINE=MyISAM DEFAULT CHARSET=cp1251;";
 //  $result = $link->query($line);
 //}
 
 // table 1 mikrotik
 $lines = file('/var/www/nets.in.ua/in_out/data.txt'); 
 foreach($lines as $line) {
   //echo ($line."<br>");
 }
 
  $json_data = json_decode($line);
  //var_dump($json_data);
 
 foreach($json_data as $json_value)
 {
   $json_ip = $json_value -> ip;
   $json_ip = "110.110.140.".$json_ip;
   $json_in = $json_value -> in;
   $json_out = $json_value -> out;
   //echo $json_ip."<br>";
   //echo $json_in."<br>";
   //echo $json_out."<br>";
 
   //if ($otvet == 27)
   {
     $id=0;
     $line="SELECT * FROM users WHERE ip='$json_ip'";
     $result = $link->query($line);
     while ($row = $result->fetch_assoc()) {
        $login = $row['name'];
	$id = $row['id'];
	$user_ip = $row['ip'];
	//echo $user_ip;
	
        //$pos = strripos($json_ip, $user_ip);
        //if ($pos === false) {
          //$text .= "net nomera".PHP_EOL;
          //} else {
            $times = time();
	    $line2="INSERT $table VALUES ($id, $times, 1, $json_in, $json_out)";
            $result2 = $link->query($line2);
	    
	    $otvet = 1;
	    $text .= "ip: $json_ip; id: $id; in: $json_in, out: $json_out".PHP_EOL;
	    //$text .= 'trafik sapisan v basu'.PHP_EOL."<br>";
          //};
      }; 
    };
 };

// table 2 mikrotik
 $lines = file('/var/www/nets.in.ua/in_out/data2.txt'); 
 foreach($lines as $line) {
   //echo ($line."<br>");
 }
 
  $json_data = json_decode($line);
  //var_dump($json_data);
 
 foreach($json_data as $json_value)
 {
   $json_ip = $json_value -> ip;
   $json_ip = "110.110.140.".$json_ip;
   $json_in = $json_value -> in;
   $json_out = $json_value -> out;
   //echo $json_ip."<br>";
   //echo $json_in."<br>";
   //echo $json_out."<br>";
 
   //if ($otvet == 27)
   {
     $id=0;
     $line="SELECT * FROM users WHERE ip='$json_ip'";
     $result = $link->query($line);
     while ($row = $result->fetch_assoc()) {
        $login = $row['name'];
	$id = $row['id'];
	$user_ip = $row['ip'];
	//echo $user_ip;
	
        //$pos = strripos($json_ip, $user_ip);
        //if ($pos === false) {
          //$text .= "net nomera".PHP_EOL;
          //} else {
            $times = time();
	    $line2="INSERT $table VALUES ($id, $times, 1, $json_in, $json_out)";
            $result2 = $link->query($line2);
	    
	    $otvet = 1;
	    $text .= "ip: $json_ip; id: $id; in: $json_in, out: $json_out".PHP_EOL."<br>";
	    //$text .= 'trafik sapisan v basu'.PHP_EOL."<br>";
          //};
      }; 
    };
 };

mysqli_close($link);
};
};

//echo $text;
$file = '/var/www/nets.in.ua/log/trafik.log';
$text .= date('d-m-Y H:i:s').PHP_EOL;
$text .= 'otvet = '.$otvet.PHP_EOL;
$text .= 'table real = '.$table_real.PHP_EOL;
$text .= '================='.PHP_EOL;

//$fOpen = fopen($file,'a');
//fwrite($fOpen, $text);
//fclose($fOpen);
?>