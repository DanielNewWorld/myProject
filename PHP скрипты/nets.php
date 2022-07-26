<?php
$file = '/var/www/nets.in.ua/log/nets.log';
$text = date ('d-m-Y H:i:s').PHP_EOL;
date_default_timezone_set('Europe/Kiev');
//error_reporting(0);

if ((include 'nets') == FALSE) {
 $arr = array('dbChekError' => '-10');
 echo json_encode($arr);
} else
{

$link = new mysqli($host, $user, $password, $db);
if ($link->connect_errno) {
 $descriptionError = $link->connect_errno. ': '.$link->connect_error;
 $arr = array('dbChekError' => '-10');
 echo json_encode($arr);
} else
{
 $link->set_charset("utf8");

if ($_GET[service] == "user_list") {
 $chekError='user_list_NO';
 
 $line="SELECT * FROM users LIMIT 0, 10";
 $result = $link->query($line);
 while ($row = $result->fetch_assoc()) {
   $chekError='user_list_YES';
   $line2="SELECT * FROM user_grp WHERE grp_id = $row[grp]";
   $result2 = $link->query($line2);
   while ($row2 = $result2->fetch_assoc()) {
       $grp_name = $row2['grp_name'];
       $grp_id = $row2['grp_id'];}
 
   $line3="SELECT * FROM plans2 WHERE id = $row[paket]";
   $result3 = $link->query($line3);
   while ($row3 = $result3->fetch_assoc()) {
     $paket_name = substr($row3['name'], 6);
     $paket_price = $row3['price'];}
     
 $dateContract = date ("d.m.Y", $row['contract_date']);
 $dateModify = date ("d.m.Y H:i:s", $row['modify_time']);
 
 $data_json[] = array('userLogin' => $row['name'],
                'userID' => $row['id'],
                'userIP' => $row['ip'],
                'userContract' => $row['contract'],
                'userContractDate' => $dateContract,
                'userGRP' => $grp_name,
                'userGRP_id' => $grp_id,
                'userSTATE' => $row['state'],
                'userBALANCE' => $row['balance'],
                'userLimitBalance' => $row['limit_balance'],
                'userModifyTime' => $dateModify,
                'userPaket' => $paket_name,
                'userPaketPrice' => $paket_price,
                'userNextPaket' => $row['next_paket'],
                'userDiscount' => $row['discount'],
                'userComment' => $row['comment'],
                'userFIO' => $row['fio']);
    }
     $data_string = json_encode($data_json);
     $data_basa = base64_encode($data_string);
     $arr = array('dbChekError' => $chekError,
                         'dbDATA' => $data_basa,
                        'service' => $_GET[service]);
     echo json_encode($arr);
  }
  
if ($_GET[service] == "pays_list_noname") {
 $chekError='pays_list_NO';
 $where = "WHERE type = 'Privatbank'";
 $where = "WHERE type = 'liqpay'";
 $where = "";
 
 $line="SELECT * FROM find_pays $where";
 $result = $link->query($line);
 while ($row = $result->fetch_assoc()) {
   $chekError='pays_list_YES';
   $data_json[] = array(
                'paysID' => $row['id'],
                'paysDates' => date ("d.m.Y H:i:s", $row['dates']),
                'paysNumDoc' => $row['num_doc'],
                'paysType' => $row['type'],
                'paysAmount' => $row['amount'],
                'paysDescription' => $row['description']
                );
    }
     $data_string = json_encode($data_json);
     $data_basa = base64_encode($data_string);
     $arr = array('dbChekError' => $chekError,
                         'dbDATA' => $data_basa,
                        'service' => $_GET[service]);
     echo json_encode($arr);
}

if ($_GET[service] == "pays_delete_noname") {
 $data_base_decode = base64_decode($_GET[data]);
 $data_json_in = json_decode($data_base_decode);
 $data_json_paysID = $data_json_in->pays_noname_ID;

 $line="DELETE FROM find_pays WHERE id = $data_json_paysID";
 $result = $link->query($line);
 
 $chekError='pays_list_NO';
 $line="SELECT * FROM find_pays";
 $result = $link->query($line);
 while ($row = $result->fetch_assoc()) {
   $chekError='pays_list_YES';
   $data_json[] = array(
                'paysID' => $row['id'],
                'paysDates' => $row['dates'],
                'paysNumDoc' => $row['num_doc'],
                'paysType' => $row['type'],
                'paysAmount' => $row['amount'],
                'paysDescription' => $row['description']
                );
    }
     $data_string = json_encode($data_json);
     $data_basa = base64_encode($data_string);
     $arr = array('dbChekError' => $chekError,
                         'dbDATA' => $data_basa,
                        'service' => $_GET[service]);
     echo json_encode($arr);
}

  if ($_GET[act] == 2) {
         $times = time();
         $line2="insert into users (ip, name, passwd, contract, contract_date, state, limit_balance, block_if_limit, fio, paket, next_paket3,
        			    cstate_time, comment) values
                                   ('110.110.140.41', $times, '', 'uliza', $times, 'off', -1.00, 1, 'Сергей например', 5, 0, $times, 'свободный')";
         $result2 = $link->query($line2);
         echo json_encode($arr);
  }
  
  if ($_GET[act] == 3) {
    $line="SELECT * FROM pays WHERE type = 10";
    $result = $link->query($line);
    $date_year = date ("Y");
    $date_month = date ("m") - 1;
    $date_day = cal_days_in_month(CAL_GREGORIAN, $date_month, $date_year);
    
      while ($row = $result->fetch_assoc()) {
        $timesDB = date ("d.m.Y H:i", $row['time']);
        if ($date_month <= 9) {
            $timesLink = "$date_day.0$date_month.$date_year 23:59";
          } else {
            $timesLink = "$date_day.$date_month.$date_year 23:59";
          }
        
        if ($timesDB == $timesLink and $row['cash'] == 0.00) {
        $midPAYS = $row['mid'];
        
        $line2 = "SELECT * FROM users WHERE id = $midPAYS";
        $result2 = $link->query($line2);
        while ($row2 = $result2->fetch_assoc()) {
          //echo $row2['id'];
        
        
        $arr[] = array('userChekError' => $chekError,
                       'credit' => $row['cash'],
                       'ip = ' => $row2['ip'],
                       'idUSER' => $row['mid']);
        }
        }
      }
    echo json_encode($arr)."<br>";
    echo $timesLink;
  }
  
    if ($_GET[act] == 4) {
    $line="SELECT p2.coment FROM pays AS p1, pays AS p2 WHERE p1.coment = p2.coment AND p1.id != p2.id";
    $result = $link->query($line);
    
      //while ($row = $result->fetch_assoc()) {
        //$arg1 = $row[coment];
        //$line2="SELECT * FROM pays WHERE coment = $arg1";
        //$result2 = $link->query($line2);
        //while ($row2 = $result2->fetch_assoc()) {
        //  $arg2 = "2PL7897";
        //  $pos = strripos($arg1, $arg2);
        //  if ($pos === false) {
        //  } else {
	//    echo $row[coment]."<br>";
        //  }
        //}
    //}
  }
  
  if ($_GET[act] == 5) {
  $line="SELECT * FROM users WHERE start_day = -1";
  $result = $link->query($line);
    while ($row = $result->fetch_assoc()) {
        $arr[] = array('userChekError' => $chekError,
                       'credit' => $row[''],
                       'ip = ' => $row['ip'],
                       'idUSER' => $row['id']);
    
    }
    echo json_encode($arr);
  }

  if ($_GET[act] == 6) {
    $line="SELECT * FROM pays WHERE cash = ''";
    $result = $link->query($line);
    $date_year = date ("Y");
    $date_month = date ("m") - 1;
    $date_day = cal_days_in_month(CAL_GREGORIAN, $date_month, $date_year);
    
      while ($row = $result->fetch_assoc()) {
        $timesDB = date ("d.m.Y H:i", $row['time']);
        
        if ($timesDB == "$date_day.0$date_month.$date_year 23:59" and $row['cash'] == 0.00) {
        $midPAYS = $row['mid'];
      
        $line2 = "SELECT * FROM users WHERE id = $midPAYS";
        $result2 = $link->query($line2);
        while ($row2 = $result2->fetch_assoc()) {
          //echo $row2['id'];
        
        
        $arr[] = array('userChekError' => $chekError,
                       'credit' => $row['cash'],
                       'ip = ' => $row2['ip'],
                       'idUSER' => $row['mid']);
        }
        }
      }
    echo json_encode($arr)."<br>";
    echo "$date_day.0$date_month.$date_year 23:59";
  }
    
  mysqli_close($link);
};
}
?>