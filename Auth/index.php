<?php
session_start();

$CONFIG_FILE = "./config/config.json";

$config = get_config($CONFIG_FILE);
$alerts = array();

// Functions & Helpers
function get_config($f) {
  return json_decode(file_get_contents($f));
}

function do_auth($username, $password, $challenge_url, $challenge_file) {
  $ch = curl_init();
  curl_setopt($ch, CURLOPT_URL, $challenge_url);
  curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
  curl_setopt($ch, CURLOPT_VERBOSE, true); 
  curl_setopt($ch, CURLOPT_TIMEOUT, 5);
  curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 10);
  curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
  curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 0);
  curl_setopt($ch, CURLOPT_USERPWD, "$username:$password");
  curl_setopt($ch, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
  $output = trim(curl_exec($ch));
  $info = curl_getinfo($ch);
  curl_close($ch);
  error_log(print_r($info,true));
  if ($info["http_code"] == 403 || $info["http_code"] == 401) {
    return array("message" => "Invalid Username or Password.");
  } else if ($info["http_code"] != 200) {
    return array("message" => "Authentication service is experiencing problems at the moment. Please try again later.");
  }

  $challenge = trim(file_get_contents($challenge_file));

  if (empty($challenge) || empty($output)) {
    return array("message" => "Authentication service is not configured properly.");
  }
  
  if ($challenge != $output) {
    return array("message" => "Authentication service is not active at the moment.");
  }

  return true;
}

function validate_payload($raw_payload, $raw_signature, $shared_key) {
  $remote_payload = base64_decode($_REQUEST['payload']);
  $signature = hash_hmac('sha256', $_REQUEST['payload'], $shared_key);
  if (strtolower($signature) == strtolower($raw_signature)) { 
    parse_str($remote_payload, $payload_array);
    return $payload_array;
  } else {
    return false;
  }
}

function get_redirect_url($base_url, $nonce, $data, $shared_key ) {
  $data["nonce"] = $nonce;
  $raw_payload = http_build_query($data);
  $payload = base64_encode($raw_payload);
  $signature = strtolower(hash_hmac('sha256', $payload, $shared_key));
  $query_string = "payload=" . urlencode($payload) . "&signature=" . urlencode($signature);
  return $base_url . "?" . $query_string;
}

function get_full_name($username) {
  $system_name = exec('getent passwd ' . escapeshellarg($username) . ' | cut -d: -f5 | cut -d, -f1');
  return empty($system_name) ? "Unknown Name" : $system_name;
}
// End functions & helpers

if  (!empty($_REQUEST['submit'])) {
  $payload = validate_payload($_REQUEST['payload'], $_REQUEST['signature'], $config->sharedKey);
  $username = $_REQUEST['username'];
  $password = $_REQUEST['password'];
  
  if (! $payload === false) 
    $authenticate = do_auth($username,$password,$config->challengeURL, $config->challengeFile);
  else
    $authenticate = array("message" => "Login request is expired. Please go back to the site and click long again.");

  if ($authenticate === true) {
    http_response_code(200);
    $data = array();
    $data["username"] = $username;
    $data["fullname"] = get_full_name($username);

    //Testing
    $ALT_REDIRECT = empty($_SESSION["REDIRECT_TO"]) ? $config->redirectURL : $_SESSION["REDIRECT_TO"];
    //End testing
    error_log("Redirect To :   " . $ALT_REDIRECT);

    $url = get_redirect_url($ALT_REDIRECT, $payload["nonce"], $data, $config->sharedKey);
    header('Location: '. $url);
    die();
  } else {
    http_response_code(403);
    array_push($alerts, $authenticate["message"]);
  }
} else {
  if (empty($_REQUEST['payload']) || empty($_REQUEST['signature'])) {
    $error = "Please provide a valid payload and a signature to use the authentication service.";
  } else {
    $payload = validate_payload($_REQUEST['payload'], $_REQUEST['signature'], $config->sharedKey);
    if ($payload === false) {
      $error = "Login request is expired. Please go back to the site and click login again.";
    }
  }
  if (!empty($_SERVER["HTTP_REFERER"]) && !empty($_REQUEST["redirect"])) {
    $_SESSION["REDIRECT_TO"] = $_SERVER["HTTP_REFERER"] . $_REQUEST["redirect"];
  }
}
?>
<!doctype html>
<html class="no-js">
<head>
  <meta charset="utf-8">
  <title>Auth</title>
  <meta name="description" content="">
  <meta name="viewport" content="width=device-width">
  <link rel="shortcut icon" href="/favicon.ico">
  <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->
  <!-- build:css(.) styles/vendor.css -->
  <!-- bower:css -->
  <!-- endbower -->
  <!-- endbuild -->
  <!-- build:css(.tmp) styles/main.css -->
  <link rel="stylesheet" href="styles/main.css">
  <!-- endbuild -->
  <!-- build:js scripts/vendor/modernizr.js -->
  <!-- endbuild -->

</head>
<body>
    <!--[if lt IE 10]>
      <p class="browsehappy">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
      <![endif]-->


      <div class="container">
        <div class="header">
          <ul class="nav nav-pills pull-right">
            <li class="active"><a href="#">Authenticate</a></li>
            <li><a href="#">About</a></li>
          </ul>
          <h3 class="text-muted">Lassonde Auth</h3>
        </div>
        <div class="alert-container">
          <?php 
           if (!empty($alerts)) {
            foreach($alerts as $alert) {
              ?>
              <div class="alert alert-danger" role="alert">
                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
                <span class="sr-only">Error:</span>
                  <?php echo $alert ?>
              </div>
              <?php
            }
           }
          ?>
        </div>
        <div class="jumbotron">
        <?php if (empty($error)) { ?>
        <form role="form" id="login-form" action="" method="POST">
        <div class="form-group">
            <div class="input-group input-group-lg">
              <span class="input-group-addon">@</span>
              <input required type="text" name="username" class="form-control" placeholder="Username">
            </div>
        </div>  
        <div class="form-group">
            <div class="input-group input-group-lg">
              <span class="input-group-addon">@</span>
              <input required type="password" name="password" class="form-control" placeholder="Password">
            </div>
        </div>  
        <input type="hidden" name="submit" value="true"/>
        <input type="hidden" name="payload" value="<?php echo $_REQUEST["payload"] ?>"/>
        <input type="hidden" name="signature" value="<?php echo $_REQUEST["signature"] ?>"/>
        <p class="lead">You will be authenticated against EECS LDAP.</p>    
            <input type="submit" name="submit" value="Login" class="btn btn-lg btn-primary"/>
            <br/><br/>

          </form>
        <?php } else { ?>
          <p class="h2 text-center">Error</p>
          <p class="h4 text-center"><?php echo htmlspecialchars($error) ?></p>
        <?php } ?>
        </div>

        <div class="row marketing">
          <div class="col-lg-4">
            <h4>What is this?</h4>
            <p>This authenticates.</p>
          </div>
          <div class="col-lg-4">
            <h4>What is this?</h4>
            <p>This authenticates.</p>
          </div>
          <div class="col-lg-4">
            <h4>What is this?</h4>
            <p>This authenticates.</p>
          </div>
        </div>

        <div class="footer">
          <p><span class="glyphicon glyphicon-heart"></span> from the Yeoman team</p>
        </div>

      </div>


      <!-- build:js(.) scripts/vendor.js -->
      <!-- bower:js -->
      <!-- endbower -->
      <!-- endbuild -->

    </body>
    </html>
