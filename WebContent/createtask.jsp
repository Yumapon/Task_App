<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="application.entity.*"%>
<!DOCTYPE html>
<html>
<head>
<!-- Required meta tags -->
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">

<!-- Bootstrap CSS -->
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
	integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
	crossorigin="anonymous">

<!-- bootstrap-datepickerを読み込む -->
<link rel="stylesheet" type="text/css"
	href="../bootstrap-datepicker-1.6.4-dist/css/bootstrap-datepicker.min.css">
<script type="text/javascript"
	src="../bootstrap-datepicker-1.6.4-dist/js/bootstrap-datepicker.min.js"></script>
<script type="text/javascript"
	src="../bootstrap-datepicker-1.6.4-dist/locales/bootstrap-datepicker.ja.min.js"></script>

<meta charset="UTF-8">
<link rel="shortcut icon" href="resources/image/favicon.ico">
<title>Okask!-新規作成画面-</title>
</head>

<%
	//30分に一回リロードする
	response.setIntHeader("Refresh", 1800);
%>
<body>
	<!-- ナビゲーションバー  -->
	<nav class="navbar navbar-expand-lg navbar-dark bg-dark ">
		<a class="navbar-brand" href="list.jsp">Okask!</a>
		<button class="navbar-toggler" type="button" data-toggle="collapse"
			data-target="#navbarSupportedContent"
			aria-controls="navbarSupportedContent" aria-expanded="false"
			aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>

		<div class="collapse navbar-collapse" id="navbarSupportedContent">
			<ul class="navbar-nav mr-auto">
				<li class="nav-item active"><a class="nav-link"
					href="createtask.jsp">新規タスク <span class="sr-only">(current)</span></a>
				</li>
			</ul>
			<form class="form-inline my-2 my-lg-0"
				action="/Web_TaskApp/LogoutServlet" method="POST">
				<button class="btn btn-outline-success my-2 my-sm-0" type="submit">Logout</button>
				<input type="hidden" name="functionStr"
					onClick="onClickLogoutButton();" />
			</form>
		</div>
	</nav>

	<!-- タスク入力ホーム -->
	<h3>タスク新規作成</h3>

	<div class="container my-5">
		<form action="HogeHogeServlet" method="POST">
			<div class="form-group">
				<label for="deadline">期限</label>
				<!-- ここにカレンダー表示用のテキストボックスを追加 -->
				<input type="date" class="form-control" id="deadline"
					name="deadline" required>
			</div>
			<div class="form-group">
				<label for="taskname">タスク名</label>
				<input type="text" class="form-control" name="taskName" id="task" size="50" required maxlength='200'>
			</div>
			<div class="form-group">
				<label for="content">内容</label>
				<textarea name="content" class="form-control" id="content" rows="3" maxlength='2000'></textarea>
			</div>
			<div class="form-group">
				<label for="client">依頼主</label> <input type="text" class="form-control" name="client" id="client" required maxlength='100'>
			</div>

			<input type="hidden" name="formName" value="createTask">
			<input type="hidden" name="actionName" value="action">
			<input type="hidden" name="actionMethodName" value="create">
			<button type="submit" class="btn btn-primary" name="button">新規作成</button>
		</form>
	</div>

	<!-- フッタ -->
	<footer class="footer mt-auto py-3 bg-dark fixed-bottom">
		<div class="container text-right">
			<span class="text-muted">
			<small>©2020 Okask! Corporation(仮)</small></span>
		</div>
	</footer>

	<!-- スクリプト -->
	<!--
	<script>
  	//新規作成ボタンが押された際は、functionStrに'gocreatetaskui'をセット
  	function onClickCreateButton(){
		document.getElementById("functionStr").value = "createtask";
  	}

  	$('#deadline').datepicker();
	</script>
	-->

	<!-- Optional JavaScript -->
	<!-- jQuery first, then Popper.js, then Bootstrap JS -->
	<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
	<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

</body>
</html>