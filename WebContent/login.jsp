<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">

<link rel="shortcut icon" href="resources/image/favicon.ico">
<title>Okask!-ログイン画面-</title>

</head>
<body>
<!-- ナビゲーションバー  -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark ">
  <a class="navbar-brand" href="#">Okask!</a>
  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
  </button>

  <div class="collapse navbar-collapse" id="navbarSupportedContent">
  <ul class="navbar-nav mr-auto">
  </ul>
    <form class="form-inline my-2 my-lg-0" action="register.jsp">
      <button class="btn btn-outline-success my-2 my-sm-0" type="submit">新規登録</button>
    </form>
  </div>
</nav>

<div class="container my-5" >
	<!-- ログインフォーム -->
	<h3>ログインしましょう！</h3>
	<div class="container col-4 mx-auto bg-light py-4" >
   	<form action="HogeHogeServlet" method="POST"onsubmit="return check();">
   		<div class="form-group">
   			<h4>ログイン情報を入力して下さい</h4>
   		</div>
   		<span class="label label-danger" ><font color="red">${message}</font></span>
  		<div class="form-group">
    		<label for="UserID">ユーザID</label>
    		<input type="text" name="user_id" class="form-control" id="UserID" aria-describedby="userHelp" placeholder="Enter userID" required autofocus maxlength='11' />
  		</div>
  		<div class="form-group">
    		<label for="exampleInputPassword1">Password</label>
    		<input type="password" name="password" class="form-control" id="exampleInputPassword1" placeholder="Password" required maxlength='20'>
  		</div>
  		<button type="submit" class="btn btn-primary" name = "button">ログイン</button>

  	<input type="hidden" name="formName" value="userInfo">
	<input type="hidden" name="actionName" value="loginAction">
	<input type="hidden" name="actionMethodName" value="login">
	</form>
	</div>
</div>

    <!-- Optional JavaScript -->
    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

  <!-- フッタ -->
  <footer class="footer mt-auto py-3 bg-dark fixed-bottom">
    <div class="container text-right">
      <span class="text-muted"><small>©2020 Okask! Corporation(仮)</small></span>
    </div>
  </footer>

</body>
</html>