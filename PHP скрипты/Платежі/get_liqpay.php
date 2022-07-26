<?php
 //$data_json = 'eyJhY3Rpb24iOiJwYXkiLCJwYXltZW50X2lkIjo5NDQxMzE3NTIsInN0YXR1cyI6InN1Y2Nlc3MiLCJ2ZXJzaW9uIjozLCJ0eXBlIjoiZG9uYXRlIiwicGF5dHlwZSI6ImNhcmQiLCJwdWJsaWNfa2V5IjoiaTIzMjEzOTk4MjI5IiwiYWNxX2lkIjo0MTQ5NjMsIm9yZGVyX2lkIjoiSjVUT01WUzIxNTQ5NTYxOTA1OTQxMDI4IiwibGlxcGF5X29yZGVyX2lkIjoiWjVIUlNZR1IxNTQ5NTYyMjIzODg3MDc0IiwiZGVzY3JpcHRpb24iOiIxMTExMTYwMyIsInNlbmRlcl9jYXJkX21hc2syIjoiNTE2OTMwKjYxIiwic2VuZGVyX2NhcmRfYmFuayI6IlBVQkxJQyBKT0lOVC1TVE9DSyBDT01QQU5ZIFwiVUsiLCJzZW5kZXJfY2FyZF90eXBlIjoibWMiLCJzZW5kZXJfY2FyZF9jb3VudHJ5Ijo4MDQsImlwIjoiNzcuNTIuMTk5LjE0NiIsImFtb3VudCI6MTUwLjAsImN1cnJlbmN5IjoiVUFIIiwic2VuZGVyX2NvbW1pc3Npb24iOjQuMTMsInJlY2VpdmVyX2NvbW1pc3Npb24iOjAuMCwiYWdlbnRfY29tbWlzc2lvbiI6MC4wLCJhbW91bnRfZGViaXQiOjE1MC4wLCJhbW91bnRfY3JlZGl0IjoxNTAuMCwiY29tbWlzc2lvbl9kZWJpdCI6NC4xMywiY29tbWlzc2lvbl9jcmVkaXQiOjAuMCwiY3VycmVuY3lfZGViaXQiOiJVQUgiLCJjdXJyZW5jeV9jcmVkaXQiOiJVQUgiLCJzZW5kZXJfYm9udXMiOjAuMCwiYW1vdW50X2JvbnVzIjowLjAsImF1dGhjb2RlX2RlYml0IjoiNTkyMTJQIiwicnJuX2RlYml0IjoiMDAxMTI1NjkyNzUzIiwibXBpX2VjaSI6IjUiLCJpc18zZHMiOnRydWUsImNyZWF0ZV9kYXRlIjoxNTQ5NTYyMTY5NTc2LCJlbmRfZGF0ZSI6MTU0OTU2MjIyNjUxOSwidHJhbnNhY3Rpb25faWQiOjk0NDEzMTc1Mn0=';
 $private_key = 'FlwZf4w09oCxпауппаSepRTxXi3nW3gEgb'; 

 $data_json = $_POST['data'];
 $signature = $_POST['signature'];
 $my_signature = base64_encode(sha1($private_key.$data_json.$private_key,1));
 
 $data_json_string = base64_decode($data_json); 
 $obj_json = json_decode($data_json_string);
 $data_json_acq_id = $obj_json->acq_id;
 $data_json_action = $obj_json->action;
 $data_json_amount = $obj_json->amount;
 $data_json_end_date = $obj_json->end_date;
 $data_json_currency = $obj_json->currency;
 $data_json_customer = $obj_json->customer;
 $data_json_description = $obj_json->description;
 $data_json_err_code = $obj_json->err_code;
 $data_json_err_description = $obj_json->err_description;
 $data_json_info = $obj_json->info;
 $data_json_ip = $obj_json->ip;
 $data_json_liqpay_order_id = $obj_json->liqpay_order_id;
 $data_json_order_id = $obj_json->order_id;
 $data_json_payment_id = $obj_json->payment_id;
 $data_json_paytype = $obj_json->paytype;
 $data_json_public_key = $obj_json->public_key;
 $data_json_sender_card_bank = $obj_json->sender_card_bank;
 $data_json_sender_first_name = $obj_json->sender_first_name;
 $data_json_sender_last_name = $obj_json->sender_last_name;
 $data_json_sender_phone = $obj_json->sender_phone;
 $data_json_status = $obj_json->status;
 $data_json_type = $obj_json->type;
 $data_json_err_erc = $obj_json->err_erc;
 $data_json_verifycode = $obj_json->verifycode;
 $data_json_transaction_id = $obj_json->transaction_id;

 $file = '/var/www/nets.in.ua/log/liqpay.log';

if ((include 'nets') == FALSE) {
  $otvet=-90;
} else { 
 $otvet=-49;
 $comment='';
 $id=0;
 
 //error_reporting(0);
 //date_default_timezone_set('Europe/Kiev');

$link = new mysqli($host, $user, $password, $db);
  if ($link->connect_errno) {
    $otvet=-80;
  } else
  {
   $link->set_charset("utf8");
 
 //popolnenie klienta
 if ($data_json_action == "pay" and $data_json_status == "success" and $my_signature == $signature)
 {
   $otvet = -27;
   $line="SELECT * FROM pays WHERE coment LIKE '$data_json_transaction_id'";
   $result = $link->query($line);
   while ($row = $result->fetch_assoc()) {
    if ($row['cash'] != 0)
    {
     $otvet=27;
     $provider_id = $row['mid'];
     $provider_time = date ("Y-m-d H:i:s", $row['time']);
     $comment .= 'plateg uge bil (27) '.$provider_id.' '.$provider_time.PHP_EOL;

     $text = '================='.PHP_EOL;    
     $text .= 'Plateg uge bil'.PHP_EOL;
     $text .= 'otvet = '.$otvet.PHP_EOL;
     $text .= 'my comment = '.$comment.PHP_EOL;
     $text .= 'acq_id = '.$data_json_acq_id.PHP_EOL;
     $text .= 'action = '.$data_json_action.PHP_EOL;
     $text .= 'amount = '.$data_json_amount.PHP_EOL;
     $text .= 'end date = '.$data_json_end_date.PHP_EOL;
     $text .= 'currency = '.$data_json_currency.PHP_EOL;
     $text .= 'description = '.$data_json_description.PHP_EOL;
     $text .= 'ip = '.$data_json_ip.PHP_EOL;
     $text .= 'liqpay_order_id = '.$data_json_liqpay_order_id.PHP_EOL;
     $text .= 'order_id = '.$data_json_order_id.PHP_EOL;
     $text .= 'payment_id = '.$data_json_payment_id.PHP_EOL;
     $text .= 'paytype = '.$data_json_paytype.PHP_EOL;
     $text .= 'public_key = '.$data_json_public_key.PHP_EOL;
     $text .= 'sender_card_bank = '.$data_json_sender_card_bank.PHP_EOL;
     $text .= 'sender_phone = '.$data_json_sender_phone.PHP_EOL;
     $text .= 'status = '.$data_json_status.PHP_EOL;
     $text .= 'type = '.$data_json_type.PHP_EOL;
     $text .= 'err_erc = '.$data_json_err_erc.PHP_EOL;
     $text .= 'verifycode = '.$data_json_verifycode.PHP_EOL;
     $text .= date('d-m-Y H:i:s').PHP_EOL;
     $text .= 'json: '.$data_json_string.PHP_EOL;
     
     //$fOpen = fopen($file,'a');
     //fwrite($fOpen, $text);
     //fclose($fOpen);
    
    };
   };

   if ($otvet!=27)
   {
     $line="SELECT * FROM users";
     $result = $link->query($line);
     $otvet = 100;
      while ($row = $result->fetch_assoc()) //esli est account
      {
        $login = $row['name'];
        $id = $row['id'];
	//$contract = $row['contract'];	
	$pos = strripos($data_json_description, $login);
	if ($pos === false) {
	  $pos2 = strripos($data_json_sender_phone, $login);
	  if ($pos2 === false) {
	  } else {
                   $reason = "'acq_id: ".$data_json_acq_id.PHP_EOL.
			    "completion_date: ".$data_json_end_date.PHP_EOL.
			    "currency: ".$data_json_currency.PHP_EOL.
			    "description: ".$data_json_description.PHP_EOL.
			    "ip: ".$data_json_ip.PHP_EOL.
			    "paytype: ".$data_json_paytype.PHP_EOL.
			    "public_key: ".$data_json_public_key.PHP_EOL.
			    "card bank: ".$data_json_sender_card_bank.PHP_EOL.
			    "phone: ".$data_json_sender_phone."'"; 
	           $line2="INSERT INTO pays (mid, cash, time, admin_id, admin_ip, office, reason, coment, type, category) VALUES 
                            ($id, $data_json_amount,".time().", 7, 1295304594,1,
                            $reason, '$data_json_transaction_id', 10, 600)";
	           $result2 = $link->query($line2);
		   $otvet=27;
		   $comment .= 'plateg sapisan v basu (27) po phone'.PHP_EOL;

                   $line3 = "UPDATE users SET balance=balance+$data_json_amount WHERE name LIKE '$login'";
	           $result3 = $link->query($line3);
		}
	} else {
    		    $line2="INSERT INTO pays (mid, cash, time, admin_id, admin_ip, office, reason, coment, type, category) VALUES 
                            ($id, $data_json_amount, ".time().", 7, 1295304594,1, ".
			    "'acq_id: ".$data_json_acq_id.PHP_EOL.
			    "completion_date: ".$data_json_end_date.PHP_EOL.
			    "currency: ".$data_json_currency.PHP_EOL.
			    "description: ".$data_json_description.PHP_EOL.
			    "ip: ".$data_json_ip.PHP_EOL.
			    "paytype: ".$data_json_paytype.PHP_EOL.
			    "public_key: ".$data_json_public_key.PHP_EOL.
			    "card bank: ".$data_json_sender_card_bank.PHP_EOL.
			    "phone: ".$data_json_sender_phone.
			    "', '$data_json_transaction_id', 10, 600)";
                    $result2 = $link->query($line2);
	            $otvet=27;
	            $comment .= 'plateg sapisan v basu (27) po description'.PHP_EOL;

                    $line3 = "UPDATE users SET balance=balance+$data_json_amount WHERE name LIKE '$login'";
                    $result3 = $link->query($line3);
      }}	

      if ($otvet == 100) {
        $error = 0;
        $line="SELECT * FROM find_pays WHERE num_doc='$data_json_transaction_id'";
        $result = $link->query($line);
        while ($row = $result->fetch_assoc()) {
          $error = 1;
          }
    
        if ($error == 0) {
          $description = "acq_id: $data_json_acq_id".PHP_EOL.
                    "completion_date: ".date("d.m.Y H:i", $data_json_end_date).PHP_EOL.
                    "currency: ".$data_json_currency.PHP_EOL.
			    "description: ".$data_json_description.PHP_EOL.
			    "ip: ".$data_json_ip.PHP_EOL.
			    "paytype: ".$data_json_paytype.PHP_EOL.
			    "public_key: ".$data_json_public_key.PHP_EOL.
			    "card bank: ".$data_json_sender_card_bank.PHP_EOL.
			    "phone: ".$data_json_sender_phone;
          $line2="INSERT INTO find_pays (dates, num_doc, type, amount, description) VALUES 
                            ('$data_json_end_date', '$data_json_transaction_id', 'liqpay', $data_json_amount, '$description')";
          $result2 = $link->query($line2);
        }
        
        $text = '================='.PHP_EOL;    
    	$text .= '!!!!!!!!Plateg ne najden'.PHP_EOL;
    	$text .= 'otvet = '.$otvet.PHP_EOL;
    	$text .= 'my comment = '.$comment.PHP_EOL;
    	$text .= 'acq_id = '.$data_json_acq_id.PHP_EOL;
    	$text .= 'action = '.$data_json_action.PHP_EOL;
    	$text .= 'amount = '.$data_json_amount.PHP_EOL;
    	$text .= 'end date = '.$data_json_end_date.PHP_EOL;
    	$text .= 'currency = '.$data_json_currency.PHP_EOL;
    	$text .= 'description = '.$data_json_description.PHP_EOL;
    	$text .= 'ip = '.$data_json_ip.PHP_EOL;
    	$text .= 'liqpay_order_id = '.$data_json_liqpay_order_id.PHP_EOL;
    	$text .= 'order_id = '.$data_json_order_id.PHP_EOL;
    	$text .= 'payment_id = '.$data_json_payment_id.PHP_EOL;
    	$text .= 'paytype = '.$data_json_paytype.PHP_EOL;
    	$text .= 'public_key = '.$data_json_public_key.PHP_EOL;
    	$text .= 'sender_card_bank = '.$data_json_sender_card_bank.PHP_EOL;
    	$text .= 'sender_phone = '.$data_json_sender_phone.PHP_EOL;
    	$text .= 'status = '.$data_json_status.PHP_EOL;
    	$text .= 'type = '.$data_json_type.PHP_EOL;
    	$text .= 'err_erc = '.$data_json_err_erc.PHP_EOL;
    	$text .= 'verifycode = '.$data_json_verifycode.PHP_EOL;
    	$text .= date('d-m-Y H:i:s').PHP_EOL;
    	$text .= 'json: '.$data_json_string.PHP_EOL;
    	
    	$fOpen = fopen($file,'a');
    	fwrite($fOpen, $text);
    	fclose($fOpen);
      }  
    };
 };

mysqli_close($link);
};
};

//$text .= 'obj json = '.$data_json_string.PHP_EOL;
//$text .= '   signature = '.$signature.PHP_EOL;
//$text .= 'my signature = '.$my_signature.PHP_EOL;

//     $text = '================='.PHP_EOL;    
//     $text .= 'nado'.PHP_EOL;
//     $text .= 'otvet = '.$otvet.PHP_EOL;
//     $text .= 'my comment = '.$comment.PHP_EOL;
//     $text .= 'acq_id = '.$data_json_acq_id.PHP_EOL;
//     $text .= 'action = '.$data_json_action.PHP_EOL;
//     $text .= 'amount = '.$data_json_amount.PHP_EOL;
//     $text .= 'end date = '.$data_json_end_date.PHP_EOL;
//     $text .= 'currency = '.$data_json_currency.PHP_EOL;
//     $text .= 'description = '.$data_json_description.PHP_EOL;
//     $text .= 'ip = '.$data_json_ip.PHP_EOL;
//     $text .= 'liqpay_order_id = '.$data_json_liqpay_order_id.PHP_EOL;
//     $text .= 'order_id = '.$data_json_order_id.PHP_EOL;
//     $text .= 'payment_id = '.$data_json_payment_id.PHP_EOL;
//     $text .= 'paytype = '.$data_json_paytype.PHP_EOL;
//     $text .= 'public_key = '.$data_json_public_key.PHP_EOL;
//     $text .= 'sender_card_bank = '.$data_json_sender_card_bank.PHP_EOL;
//     $text .= 'sender_phone = '.$data_json_sender_phone.PHP_EOL;
//     $text .= 'status = '.$data_json_status.PHP_EOL;
//     $text .= 'type = '.$data_json_type.PHP_EOL;
//     $text .= 'err_erc = '.$data_json_err_erc.PHP_EOL;
//     $text .= 'verifycode = '.$data_json_verifycode.PHP_EOL;
//     $text .= date('d-m-Y H:i:s').PHP_EOL;
     
//     $fOpen = fopen($file,'a');
//     fwrite($fOpen, $text);
//     fclose($fOpen);

?>