package jisaku_servlet.servlet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import jisaku_dicontainer.annotation.ActionMethod;
import jisaku_dicontainer.container.ApplicationContainer;
import jisaku_dicontainer.container.ApplicationContainerImpl;
import jisaku_servlet.annotation.Login;
import jisaku_servlet.annotation.LoginCheck;
import jisaku_servlet.annotation.RequestScoped;
import jisaku_servlet.annotation.SessionObj;
import jisaku_servlet.annotation.SessionScoped;
import jisaku_servlet.exception.IlligalMethodNameException;

/**
 * Servlet implementation class HogeHogeServlet
 * TODO: このクラスに色々かきすぎ。分割した方が良さげ
 *
 * @author okamotoyuuma
 * @version 1.0.0
 * @version 2021.04.20
 */
@WebServlet("/HogeHogeServlet")
public class HogeHogeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	final Logger logger = Logger.getLogger(HogeHogeServlet.class.getName());// Loggerクラスのインスタンスを生成する

	ApplicationContainer ac = new ApplicationContainerImpl();//コンテナ生成
	Object actionObj;//Action格納用Object
	ArrayList<String> paraNameList = new ArrayList<>();//RequestのParameterName格納用配列

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HogeHogeServlet() {super();}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		//バインディングするBean名(Form名)を取得 formは空でもOK
		Optional<String> formNameOpt = Optional.ofNullable(request.getParameter("formName"));
		//Actionクラス名を取得
		String actionName = request.getParameter("actionName");
		//実行するメソッド名を取得
		String actionMethodName = request.getParameter("actionMethodName");

		//Requestから送られてきたParameterNameを全て取得する
		paraNameList.clear();
		Enumeration<?> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			paraNameList.add(name);
		}

		//formName.actionName.actionMethodは必要ないので削除
		//formNameは格納されていない可能性があるので、格納されている場合のみ削除
		int index = paraNameList.indexOf("formName");
		if (index != (-1)) {
			paraNameList.remove(index);
		}
		paraNameList.remove(paraNameList.indexOf("actionName"));
		paraNameList.remove(paraNameList.indexOf("actionMethodName"));
		paraNameList.remove(paraNameList.indexOf("button"));

		//Actionクラスを取得
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Actionクラスを取得します。");
		actionObj = ac.getAction(actionName);

		//formNameが指定されている場合のみ、Formの確認を行う
		formNameOpt.ifPresent(formName -> setFormBean(formName, request));

		//ActionClass内の指定されたMethodを取得し、実行
		Class<?> clazz = actionObj.getClass();
		Method[] methods = clazz.getMethods();

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "実行するメソッドを探しています。");
		for (Method m2 : methods) {
			if (m2.isAnnotationPresent(ActionMethod.class)) {
				ActionMethod aMethod = m2.getAnnotation(ActionMethod.class);
				if (!(aMethod.value().equals(actionMethodName))) {
					continue;
				} else {
					try {
						logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "実行するメソッドが見つかりました。メソッド名：" + m2.getName());

						if (m2.isAnnotationPresent(LoginCheck.class) || clazz.isAnnotationPresent(LoginCheck.class)) {
							//ログイン確認
							logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "ログインチェックを行います");
							//session,Cookieを取得、新規作成は行わない
							HttpSession session = request.getSession(false);
							Cookie[] cookies = request.getCookies();
							Cookie sessionIdCookie = null;
							String sessionId = null;
							for (int i = 0; i < cookies.length; i++) {
								if (cookies[i].getName().equals("sessionId")) {
									sessionIdCookie = cookies[i];
									sessionId = sessionIdCookie.getValue();
									if (sessionId != session.getId()) {
										logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "セッションIDが一致しました。同一クライアントからのリクエストです。");
									}
								}
							}
							//ログインされていない場合
							if (session == null || sessionIdCookie == null || !(sessionId.equals(session.getId()))) {
								//エラーメッセージを返却
								ObjectMapper mapper = new ObjectMapper();
								String json = mapper.writeValueAsString(new ErrorObj());

								//JSONの出力
								response.getWriter().write(json);
								return;
							}
							/*
							 * ログインチェックができたら、先へ進む
							 */
						}

						Model model = null;

						//実行するActionMethodに引数があるかどうかを確認
						if (m2.getParameterCount() > 0) {
							//複数のParameterをセットする用の配列
							Object[] parameters = new Object[m2.getParameterCount()];
							int paramsConut = 0;
							//Parameterクラスのインスタンスを格納する
							Object obj = null;

							for (Parameter p : m2.getParameters()) {
								//@SessionObjが付与されている場合
								if (p.isAnnotationPresent(SessionObj.class)) {
									//Valueの値を取得
									String attributeName = p.getAnnotation(SessionObj.class).value();
									//sessionを取得
									HttpSession session = request.getSession(false);
									//sessionから値を取得
									obj = session.getAttribute(attributeName);
									//parametersにセット
									parameters[paramsConut] = obj;
									//Countを増やす
									paramsConut++;
								}
							}
							//遷移先URLとメソッド（forword or redirect）などを取得
							model = (Model) m2.invoke(actionObj, parameters);

						} else {
							//遷移先URLとメソッド（forword or redirect）などを取得
							model = (Model) m2.invoke(actionObj);
						}
						logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "LoginMethodかどうか確認");
						if (m2.isAnnotationPresent(Login.class)) {
							if (model.isLoginCheckerFlag()) {
								logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "ログイン初期処理を開始します。");
								InitProcess(request);

								//cookie作成して、セッションIDを格納し、リクエストにクッキーを格納
								String sessionId = request.getSession().getId();
								Cookie sessionIdCookie = new Cookie("sessionId", sessionId);
								response.addCookie(sessionIdCookie);

								logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "ログイン処理が完了しました。");
							}
							logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "ログイン処理は実行されませんでした");
						}

						//Sessionスコープが指定されているものを格納する
						logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "ModelにSessionとして格納されているものをsessionに格納します");
						ArrayList<Value> list = model.getSessionObj();
						int size = list.size();
						HttpSession session = request.getSession(true);
						Value value;
						for (int i = 0; i < size; i++) {
							value = list.get(i);
							session.setAttribute(value.getName(), value.getObj());
						}

						//Requestスコープが指定されているものを格納する
						logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "ModelにRequestとして格納されているものをRequestに格納します");
						ArrayList<Value> list2 = model.getRequestObj();
						int size2 = list2.size();
						Value value2;
						for (int i = 0; i < size2; i++) {
							value2 = list2.get(i);
							request.setAttribute(value2.getName(), value2.getObj());
						}

						//WEBの場合
						if (model.getType().equalsIgnoreCase("WEB")) {

							//遷移先・遷移方法を判断し、遷移する
							if (model.getMethod().equals("forword")) {
								request.getRequestDispatcher(model.getNextPage()).forward(request, response);
							} else if (model.getMethod().equals("redirect")) {
								response.sendRedirect(model.getNextPage());
							} else {
								throw new IlligalMethodNameException();
							}
						}

						//APIの場合
						if (model.getType().equalsIgnoreCase("JSON")) {
							//JSONにするデータを取得
							Object jsonObj = model.getJsonObj();

							//JavaObjectからJSONに変換
							ObjectMapper mapper = new ObjectMapper();
							String json = mapper.writeValueAsString(jsonObj);
							//JSONの出力
							response.getWriter().write(json);
						}

						//GUIの場合
						if (model.getType().equalsIgnoreCase("GUI")) {

						} else {
							logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "WEB,JSON,GUIのいずれかをModelクラスのTypeFieldにセットしてください");
						}

					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {

						e1.printStackTrace();
					} catch (IlligalMethodNameException e1) {
						logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "メソッド名が間違っています メソッド名はforwordもしくはredirectで指定してください");
						e1.printStackTrace();
					}
				}
			}
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * Beanにリクエストパラメータをセットするメソッド
	 * @author okamotoyuuma
	 * @version 1.0.0:2020.12.10
	 */
	private void setFormBean(String formName, HttpServletRequest request) {
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + formName + "に値をセットします。");

		//リクエストの値をセットするBeanのインスタンスを取得
		//Actionクラス
		Class<?> clazz = actionObj.getClass();

		//Actionクラス内のFormクラスを取得
		Field formField = null;
		try {
			formField = clazz.getDeclaredField(formName);
		} catch (NoSuchFieldException | SecurityException e2) {
			e2.printStackTrace();
		}

		Object form = null;
		formField.setAccessible(true);
		try {
			form = formField.get(actionObj);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		formField.setAccessible(false);

		//Fieldにリクエストの値をセット
		String paraName;
		for (int i = 0; i < paraNameList.size(); i++) {
			//リクエストパラメータの属性値を取得
			paraName = paraNameList.get(i);

			//リクエストパラメータと合致するFieldを取得
			Field f = null;
			try {
				f = form.getClass().getDeclaredField(paraName);
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}

			//Fieldに値をセット
			f.setAccessible(true);//無理やり書き込む。
			System.out.println(request.getParameter(paraName));

			//Fieldの方を取得
			Class<?> type = f.getType();
			String typeName = type.toString();

			try {
				if (typeName.contains("[Ljava.lang.String")) {
					f.set(form, request.getParameterValues(paraName));
				} else if (typeName.contains("String")) {
					f.set(form, request.getParameter(paraName));
				} else if (typeName.contains("int")) {
					f.set(form, Integer.parseInt(request.getParameter(paraName)));
				} else if (typeName.contains("boolean")) {
					f.set(form, Boolean.parseBoolean(request.getParameter(paraName)));
				} else if (typeName.contains("byte")) {
					f.set(form, Byte.parseByte(request.getParameter(paraName)));
				} else if (typeName.contains("short")) {
					f.set(form, Short.parseShort(request.getParameter(paraName)));
				} else if (typeName.contains("long")) {
					f.set(form, Long.parseLong(request.getParameter(paraName)));
				} else if (typeName.contains("float")) {
					f.set(form, Float.parseFloat(request.getParameter(paraName)));
				} else if (typeName.contains("double")) {
					f.set(form, Double.parseDouble(request.getParameter(paraName)));
				} else if (typeName.contains("java.util.Date")) {
					SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
					java.util.Date date = sdFormat.parse(request.getParameter(paraName));
					f.set(form, date);
				} else if (typeName.contains("java.sql.Date")) {
					f.set(form, java.sql.Date.valueOf(request.getParameter(paraName)));
				} else if (typeName.contains("java.sql.Time")) {
					f.set(form, java.sql.Time.valueOf(request.getParameter(paraName)));
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			f.setAccessible(false);

			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "InstanceScopeを確認しています");
			//Sessionへの格納
			if (form.getClass().isAnnotationPresent(SessionScoped.class)) {
				logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Entityに@SessionScopedが付与されているのを確認しました");

				//Sessionの取得
				HttpSession session = request.getSession(true);
				//sessionに格納する名前を取得
				String attributeName = form.getClass().getAnnotation(SessionScoped.class).value();

				logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Sessionにインスタンスを格納します。" + "インスタンス名：" + form.getClass().getName() + "格納名：" + attributeName);
				//sessionに格納
				session.setAttribute(attributeName, form);
			}

			//Requestへの格納
			if (form.getClass().isAnnotationPresent(RequestScoped.class)) {
				logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Entityに@RequestScopedが付与されているのを確認しました");
				logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Requestにインスタンスを格納します。" + "インスタンス名：" + form.getClass().getName());
				Object obj = form;
				request.setAttribute(form.getClass().getName(), obj);
			}

			/*
			//Cookieの格納
			for(Field f1 : field.getClass().getDeclaredFields()) {
				//ログ発生箇所
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				//例外内容
				System.out.println("Cookieにインスタンスを格納します。" + "フィールド名：" + f1.getName());
				if(f1.isAnnotationPresent(CookieScoped.class)) {

					f.setAccessible(true);
					Object obj = f1.get(field);
					f.setAccessible(false);

					Cookie cookie = new Cookie(f1.getName(), obj.toString());
					response.addCookie(cookie);
				}
			}
			*/
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + formName + "への値のセットが完了しました。");
		}
	}

	//ログイン成功時初期処理
	private void InitProcess(final HttpServletRequest request) {

		//sessionの再発行
		//request.getSession(true).invalidate();
		//確認用
		HttpSession session = request.getSession();
		String beforeSessionId = session.getId();
		session.invalidate();

		session = request.getSession(true);
		String afterSessionId = session.getId();

		//確認用
		if (beforeSessionId != afterSessionId) {
			System.out.println("session再発行の完了");
		}
	}
}

