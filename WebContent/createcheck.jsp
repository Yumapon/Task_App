<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@page import="application.beans.*" %>
<!DOCTYPE html>
<html>
<head>
<!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<meta charset="UTF-8">
<link rel="stylesheet" href="resources/css/createcheck.css">
<link rel="shortcut icon" href="resources/image/favicon.ico">
<title>Okask!-新規作成タスク確認画面-</title>
</head>

<%
	//30分に一回リロードする
 	response.setIntHeader("Refresh", 1800);
%>
<body>
<!-- ナビゲーションバー  -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark ">
  <a class="navbar-brand" href="list.jsp">Okask!</a>
  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
  </button>

  <div class="collapse navbar-collapse" id="navbarSupportedContent">
  <ul class="navbar-nav mr-auto">
  </ul>
    <form class="form-inline my-2 my-lg-0" action="/Web_TaskApp/LogoutServlet" method="POST">
      <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Logout</button>
      <input type="hidden" name="functionStr" onClick= "onClickLogoutButton();" />
    </form>
  </div>
</nav>

<%
	CreateTaskEntity task = (CreateTaskEntity)session.getAttribute("createTask");

	//タスク内容を取得
	String deadLine = task.getDeadline().toString();
	String taskName =  task.getTaskName();
	String content = task.getContent();
	String client = task.getClient();
%>
<form action="HogeHogeServlet" method="POST">
<ul class="taskcheckform">
	<li>
		<label for="deadline">期限</label><br>
		<%= deadLine%>
	</li>
	<li>
		<label for="taskname">タスク名</label><br>
		<%= taskName %>
	</li>
	<li>
		<label for="content">内容</label><br>
		<%= content %>
	</li>
	<li>
		<label for="client">依頼主</label><br>
		<%=client %>
	</li>
</ul>
	<input type="hidden" name="formName" value="createTask">
	<input type="hidden" name="actionName" value="action">
	<input type="hidden" name="actionMethodName" value="createCheck">
	<p class="registerButton">
	<input class="btn btn-primary" type="submit" name="button" value="登録">
	</p>
</form>

  <!-- フッタ -->
  <footer class="footer mt-auto py-3 bg-dark fixed-bottom">
    <div class="container text-right">
      <span class="text-muted"><small>©2020 Okask! Corporation(仮)</small></span>
    </div>
  </footer>


    <!-- Optional JavaScript -->
    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

</body>
</html>