<?php
$file = '/var/www/nets.in.ua/log/privatbank.log';

 //date_default_timezone_set('Europe/Kiev');
 //error_reporting(0);
 
 $url = "https://acp.privatbank.ua/api/proxy/transactions/today?acc=UA33313399000405876";
 $opts = [
 'http' => [
 "method" => "GET",
     "header" => "User-Agent: klientskoe prilogenie\r\n".
                 "id: dc8431b8-96e44c2c03\r\n".
		 "token: lOUObkmZPeBLmWXsf+amHLru8lpF342G/n72Y2spLLmOfVIwpchHf7QprQTM1Tz1A0TqWcnZdhTw+/+nIos5L5fBHShKCV3Fjm8kT0n8vwW/st79YWkAxVRzWTMbPXt88qWB+upTbUbJ2hKaqPysNUTp71sXMj0LOEQTjmaJGbeiv8=\r\n".
		 "Content-Type: application/json;charset=cp1251\r\n"
         ]
	 , "ssl"=> [
           "verify_peer"=>false,
           "verify_peer_name"=>false]   
]; 
 $context = stream_context_create($opts);
 $stream = file_get_contents($url, false, $context);
 //$data=base64_encode($stream);
 //echo $data;
 //echo $stream;

// $private_key = 'njghbdfn,fyr';
// $signature = base64_decode(sha1($private_key.$data.$private_key,1));

// $url = "http://46.175.243.2/get_privatbank.php";
// $ch = curl_init();
// curl_setopt($ch, CURLOPT_URL, $url);
// curl_setopt($ch, CURLOPT_POST, 1); 
// curl_setopt($ch, CURLOPT_POSTFIELDS, //тут переменные которые будут переданы методом POST
//	array (
//		'data'=>$data,
//                'signature'=>$signature
//	));
// curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
// $data2 = curl_exec($ch);
// $otvet = curl_getinfo($ch, CURLINFO_HTTP_CODE); //200
// curl_close($ch);
//  if(!$data2) {$error = curl_error($curl).'('.curl_errno($curl).')';}
//curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));

//echo "vtoroj cset <br>";
// $url = "https://acp.privatbank.ua/api/proxy/transactions/today?acc=UA733133904055713169";
// $opts = [
// 'http' => [
// "method" => "GET",
//     "header" => "User-Agent: klientskoe prilogenie\r\n".
//                 "id: dc8431b8-4b47-4b9f-9e4a-c896e44c2c03\r\n".
//		 "token: lOUObkmZPeBLmWXsf+amHL6ig3Ff0Orim68/Mt7hlewPxQGDkPFBylC9ru8lpF342G/n72Y2spLLmOfVIwpchHf7QprQTM1Tz1A0TqWcnZdhTw+/+nIos5L5fBHShKCV3Fjm8kT0n8vwW/st79YWkAxVRzWT30QRUkWGT6JIrIdMbPXt88qWB+upTbUbJ2hKaqPysNUTp71sXMj0LOEQTjmaJGbeiv8=\r\n".
//		 "Content-Type: application/json;charset=cp1251\r\n"
//         ]
//	 , "ssl"=> [
//           "verify_peer"=>false,
//           "verify_peer_name"=>false]   
//];
// $context = stream_context_create($opts);
// $stream2 = file_get_contents($url, false, $context);
// echo $stream2;
//--------

 $stream = utf8_encode($stream);
 $obj_json = json_decode($stream);
 
 //print_r(json_last_error_msg());
 
if ((include 'nets') == FALSE) {
  $otvet=-90;
} else
{
 
 $id=0;
 
 //error_reporting(0);

  $link = new mysqli($host, $user, $password, $db);
  if ($link->connect_errno) {
    $otvet=-90;
  } else {
  $otvet=-101;
  $link->set_charset("utf8");

   for ($i = 0; $i <= count($obj_json->StatementsResponse->statements)-1; $i++)
   {
     $str = key($obj_json->StatementsResponse->statements[$i]);
     $ojd_trantype = $obj_json->StatementsResponse->statements[$i]->$str->TRANTYPE; //c
     $ojd_real = $obj_json->StatementsResponse->statements[$i]->$str->BPL_FL_REAL; //r
     $ojd_num_doc = $obj_json->StatementsResponse->statements[$i]->$str->BPL_NUM_DOC;
     $ojd_dat_od = $obj_json->StatementsResponse->statements[$i]->$str->BPL_DAT_OD;
     $ojd_tim_p = $obj_json->StatementsResponse->statements[$i]->$str->BPL_TIM_P;
     $ojd_sum = $obj_json->StatementsResponse->statements[$i]->$str->BPL_SUM_E;
     $ojd_ccy = $obj_json->StatementsResponse->statements[$i]->$str->BPL_CCY;
     $ojd_b_acc = $obj_json->StatementsResponse->statements[$i]->$str->BPL_B_ACC;
     $ojd_osnd = $obj_json->StatementsResponse->statements[$i]->$str->BPL_OSND;
     $ojd_pr_pr = $obj_json->StatementsResponse->statements[$i]->$str->BPL_PR_PR; //r
     //----
     
       
 //popolnenie klienta
 if ($ojd_trantype == "C" and $ojd_real == "r" and $ojd_pr_pr == "r")
 {
   $otvet = -27;
   $line="SELECT * FROM pays WHERE coment LIKE '$ojd_num_doc'";
   $result = $link->query($line);
   while ($row = $result->fetch_assoc()) {
    if ($row['cash'] != 0)
    {
     $otvet=27;
     $provider_id = $row['mid'];
     $provider_time = date ("Y-m-d H:i:s", $row['time']);
     $text .= "plateg uge bil (27) $provider_id, $provider_time".PHP_EOL;
    };
   };
   
   $text .= "otvet est li plateg= ".$otvet.PHP_EOL;
   if ($otvet!=27)
   {
     $line="SELECT * FROM users";
     $result = $link->query($line);
      while ($row = $result->fetch_assoc()) {
        $login = $row['name'];
	$id = $row['id'];
	
        $pos = strripos($ojd_osnd, $login);
        if ($pos === false) {
          //$text .= "net nomera".PHP_EOL;
          } else {
            $times = time();
            $reason = "date: ".$ojd_dat_od.PHP_EOL.
                      "time: ".$ojd_tim_p.PHP_EOL.
                      "bank account: *9876";
	    $line2="INSERT INTO pays (mid, cash, time, admin_id, admin_ip, office, reason, coment, type, category) VALUES
                            ($id, $ojd_sum, $times, 8, 1295304594, 1, '$reason', '$ojd_num_doc', 10, 600)";
            $result2 = $link->query($line2);
	    
            $line3 = "UPDATE users SET balance=balance+$ojd_sum WHERE name LIKE '$login'";
	    $result3 = $link->query($line3);

	    $text .= "yes login abonent: ".$login.PHP_EOL;
	    $text .= "plateg sapisan v basu".PHP_EOL;
	    $otvet = 10;
          }
      }; 
      
      if ($otvet != 10 ) {
        $pos = strripos($ojd_osnd, "LIQPAY");
        if ($pos === false) {
          //$text = "===============================".PHP_EOL;
          //$text .= "!!!!!!plateg ne najden";
          //$text .= date('d-m-Y H:i:s').PHP_EOL;
          //$text .= "trantype: ".$ojd_trantype.PHP_EOL;
          //$text .= "real: ".$ojd_real.PHP_EOL;
          //$text .= "date: ".$ojd_dat_od.PHP_EOL;
          //$text .= "time: ".$ojd_tim_p.PHP_EOL;
          //$text .= "ccy: ".$ojd_ccy.PHP_EOL;
          //$text .= "bank account: ".$ojd_b_acc.PHP_EOL;
          //$text .= "osnd: ".$ojd_osnd.PHP_EOL;
          //$text .= "pr_pr: ".$ojd_pr_pr.PHP_EOL;  
          //$text .= "num_doc = ".$ojd_num_doc.PHP_EOL;
          //$text .= "sum = ".$ojd_sum.PHP_EOL;
          //$text .= "otvet = ".$otvet.PHP_EOL;
          //$text .= "".PHP_EOL;
          //$text .= "json: ".$obj_json.PHP_EOL;

          //$fOpen = fopen($file,'a');
          //fwrite($fOpen, $text);
          //fclose($fOpen);
          
          //$osnd = utf8_decode($ojd_osnd);
          
          $error = 0;
          $line="SELECT * FROM find_pays WHERE num_doc = '$ojd_num_doc'";
          $result = $link->query($line);
          while ($row = $result->fetch_assoc()) {
            $error = 1;
          }
          
          if ($error == 0) {
            $description = "date: ".$ojd_dat_od." ".$ojd_tim_p.PHP_EOL.
                           "bank account: *9876".PHP_EOL.
                           $ojd_osnd;
            $description = "bank account: *9876";
            //$description = $ojd_osnd;
	    
	    $line="INSERT INTO find_pays (dates, num_doc, type, amount, description) VALUES
                            ($times, '$ojd_num_doc', 'Privatbank', $ojd_sum, $description)";
            $result = $link->query($line);
          }
        } else {};
      }
      
    };
 };
};

mysqli_close($link);
};
}

//vtoroj akkaunt
 $url = "https://acp.privatbank.ua/api/proxy/transactions/today?acc=UA733133990000026004055713169";
 $opts = [
 'http' => [
 "method" => "GET",
     "header" => "User-Agent: klientskoe prilogenie\r\n".
                 "id: dc8431b8-4b47-4b9f-9e4a-c896e44c2c03\r\n".
		 "token: lOUObkmZPeBLmWXsf+amHL6iCk5lG4xltNm5GstuRg60Iexg3Ff0Orim68/Mt7hlewPxQGDkPFBylC9ru8lpF342G/n72Y2spLLmOfVIwpchHf7QprQTM1Tz1A0TqWcnZdhTw+/+nIos5L5fBHShKCV3Fjm8kT0n8vwW/st79YWkAxVRzWT30QRUkWGT6JIrI6nEEG/PUtdMbPXt88qWB+upTbUbJ2hKaqPysNUTp71sXMj0LOEQTjmaJGbeiv8=\r\n".
		 "Content-Type: application/json;charset=cp1251\r\n"
         ]
	 , "ssl"=> [
           "verify_peer"=>false,
           "verify_peer_name"=>false]   
]; 
 $context = stream_context_create($opts);
 $stream = file_get_contents($url, false, $context);
 //$data=base64_encode($stream);
 //echo $data;
 //echo $stream;

// $private_key = 'njghbdfn,fyr';
// $signature = base64_decode(sha1($private_key.$data.$private_key,1));

// $url = "http://46.175.243.2/get_privatbank.php";
// $ch = curl_init();
// curl_setopt($ch, CURLOPT_URL, $url);
// curl_setopt($ch, CURLOPT_POST, 1); 
// curl_setopt($ch, CURLOPT_POSTFIELDS, //тут переменные которые будут переданы методом POST
//	array (
//		'data'=>$data,
//                'signature'=>$signature
//	));
// curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
// $data2 = curl_exec($ch);
// $otvet = curl_getinfo($ch, CURLINFO_HTTP_CODE); //200
// curl_close($ch);
//  if(!$data2) {$error = curl_error($curl).'('.curl_errno($curl).')';}
//curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));

//echo "vtoroj cset <br>";
// $url = "https://acp.privatbank.ua/api/proxy/transactions/today?acc=UA733133990000026004055713169";
// $opts = [
// 'http' => [
// "method" => "GET",
//     "header" => "User-Agent: klientskoe prilogenie\r\n".
//                 "id: dc8431b8-4b47-4b9f-9e4a-c896e44c2c03\r\n".
//		 "token: lOUObkmZPeBLmWXsf+amHL6iCk5lG4xltNm5GstuRg60Iexg3Ff0Orim68/Mt7hlewPxQGDkPFBylC9ru8lpF342G/n72Y2spLLmOfVIwpchHf7QprQTM1Tz1A0TqWcnZdhTw+/+nIos5L5fBHShKCV3Fjm8kT0n8vwW/st79YWkAxVRzWT30QRUkWGT6JIrI6nEEG/PUtdMbPXt88qWB+upTbUbJ2hKaqPysNUTp71sXMj0LOEQTjmaJGbeiv8=\r\n".
//		 "Content-Type: application/json;charset=cp1251\r\n"
//         ]
//	 , "ssl"=> [
//           "verify_peer"=>false,
//           "verify_peer_name"=>false]   
//];
// $context = stream_context_create($opts);
// $stream2 = file_get_contents($url, false, $context);
// echo $stream2;
//--------

 $stream = utf8_encode($stream);
 $obj_json = json_decode($stream);
 
 //print_r(json_last_error_msg());
 
if ((include 'nets') == FALSE) {
  $otvet=-902;
} else
{
 
 $id=0;
 
 //error_reporting(0);

  $link = new mysqli($host, $user, $password, $db);
  if ($link->connect_errno) {
    $otvet=-492;
  } else {
  $otvet=-1012;
  $link->set_charset("utf8");   

   for ($i = 0; $i <= count($obj_json->StatementsResponse->statements)-1; $i++)
   {
     $str = key($obj_json->StatementsResponse->statements[$i]);
     $ojd_trantype = $obj_json->StatementsResponse->statements[$i]->$str->TRANTYPE; //c
     $ojd_real = $obj_json->StatementsResponse->statements[$i]->$str->BPL_FL_REAL; //r
     $ojd_num_doc = $obj_json->StatementsResponse->statements[$i]->$str->BPL_NUM_DOC;
     $ojd_dat_od = $obj_json->StatementsResponse->statements[$i]->$str->BPL_DAT_OD;
     $ojd_tim_p = $obj_json->StatementsResponse->statements[$i]->$str->BPL_TIM_P;
     $ojd_sum = $obj_json->StatementsResponse->statements[$i]->$str->BPL_SUM_E;
     $ojd_ccy = $obj_json->StatementsResponse->statements[$i]->$str->BPL_CCY;
     $ojd_b_acc = $obj_json->StatementsResponse->statements[$i]->$str->BPL_B_ACC;
     $ojd_osnd = $obj_json->StatementsResponse->statements[$i]->$str->BPL_OSND;
     $ojd_pr_pr = $obj_json->StatementsResponse->statements[$i]->$str->BPL_PR_PR; //r
       
 //popolnenie klienta
 if ($ojd_trantype == "C" and $ojd_real == "r" and $ojd_pr_pr == "r")
 {
   $otvet = -272;
   $line="SELECT * FROM pays WHERE coment LIKE '$ojd_num_doc'";
   $result = $link->query($line);
   while ($row = $result->fetch_assoc()) {
    if ($row['cash'] != 0)
    {
     $otvet=272;
     $provider_id = $row['mid'];
     $provider_time = date ("Y-m-d H:i:s", $row['time']);
     $text .= "plateg uge bil (27) $provider_id, $provider_time".PHP_EOL;
    };
   };
   
   $text .= "otvet est li plateg= ".$otvet.PHP_EOL;
   if ($otvet!=272)
   {
     $line="SELECT * FROM users";
     $result = $link->query($line);
      while ($row = $result->fetch_assoc()) {
        $login = $row['name'];
	$id = $row['id'];
	
        $pos = strripos($ojd_osnd, $login);
        if ($pos === false) {
          //$text .= "net nomera".PHP_EOL;
          } else {
            $times = time();
            $reason = "date: ".$ojd_dat_od.PHP_EOL.
                      "time: ".$ojd_tim_p.PHP_EOL.
                      "bank account: *3169";
	    $line2="INSERT INTO pays (mid, cash, time, admin_id, admin_ip, office, reason, coment, type, category) VALUES
                            ($id, $ojd_sum, $times, 8, 1295304594, 1, '$reason', '$ojd_num_doc', 10, 600)";
            $result2 = $link->query($line2);
	    
	    $text .= "yes login abonent: ".$login.PHP_EOL;
	    $text .= "plateg sapisan v basu".PHP_EOL;
	    $otvet = 10;

            $line3 = "UPDATE users SET balance=balance+$ojd_sum WHERE name LIKE '$login'";
	    $result3 = $link->query($line3);
          }
      }; 
      
      if ($otvet != 10 ) {
        $pos = strripos($ojd_osnd, "LIQPAY");
        if ($pos === false) {

        //$text = "===============================".PHP_EOL;
        //$text .= "!!!!!!plateg ne najden";
        //$text .= date('d-m-Y H:i:s').PHP_EOL;
        //$text .= "trantype: ".$ojd_trantype.PHP_EOL;
        //$text .= "real: ".$ojd_real.PHP_EOL;
        //$text .= "date: ".$ojd_dat_od.PHP_EOL;
        //$text .= "time: ".$ojd_tim_p.PHP_EOL;
        //$text .= "ccy: ".$ojd_ccy.PHP_EOL;
        //$text .= "bank account: ".$ojd_b_acc.PHP_EOL;
        //$text .= "osnd: ".$ojd_osnd.PHP_EOL;
        //$text .= "pr_pr: ".$ojd_pr_pr.PHP_EOL;  
        //$text .= "num_doc = ".$ojd_num_doc.PHP_EOL;
        //$text .= "sum = ".$ojd_sum.PHP_EOL;
        //$text .= "otvet = ".$otvet.PHP_EOL;
        //$text .= "".PHP_EOL;
        //$text .= "json: ".$obj_json.PHP_EOL;

        //$fOpen = fopen($file,'a');
        //fwrite($fOpen, $text);
        //fclose($fOpen);
        
          $error = 0;
          $line="SELECT * FROM find_pays WHERE num_doc = '$ojd_num_doc'";
          $result = $link->query($line);
          while ($row = $result->fetch_assoc()) {
            $error = 1;
          }
          
          if ($error == 0) {
            $description = "date: ".$ojd_dat_od.PHP_EOL.
                           "time: ".$ojd_tim_p.PHP_EOL.
                           "bank account: *3169".PHP_EOL.
                           $ojd_osnd;
	    $times = time();
	    
	    $description = "*3169";
	    $line="INSERT INTO find_pays (dates, num_doc, type, amount, description) VALUES
                            ($times, '$ojd_num_doc', 'Privatbank', $ojd_sum, '$description')";
            $result = $link->query($line);
          }
        } 
      } 
    };
    $text .= "otvet popolnil li = ".$otvet.PHP_EOL;
    $text .= "".PHP_EOL;
 };
};

mysqli_close($link);
};
}
//$text .= 'stream = '.$stream.PHP_EOL;

        $text = "===============================".PHP_EOL;
        $text .= "nado";
        $text .= date('d-m-Y H:i:s').PHP_EOL;
        $text .= "trantype: ".$ojd_trantype.PHP_EOL;
        $text .= "real: ".$ojd_real.PHP_EOL;
        $text .= "date: ".$ojd_dat_od.PHP_EOL;
        $text .= "time: ".$ojd_tim_p.PHP_EOL;
        $text .= "ccy: ".$ojd_ccy.PHP_EOL;
        $text .= "bank account: $ojd_b_acc".PHP_EOL;
        $text .= "osnd: ".$ojd_osnd.PHP_EOL;
        $text .= "pr_pr: ".$ojd_pr_pr.PHP_EOL;  
        $text .= "num_doc = ".$ojd_num_doc.PHP_EOL;
        $text .= "sum = ".$ojd_sum.PHP_EOL;
        $text .= "otvet = ".$otvet.PHP_EOL;
        $text .= "".PHP_EOL;
        $text .= "json: ".$obj_json.PHP_EOL;

        //$fOpen = fopen($file,'a');
        //fwrite($fOpen, $text);
        //fclose($fOpen);
//echo "ok";

?>