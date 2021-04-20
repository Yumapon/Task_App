package jisaku_servlet.servlet;

import java.util.ArrayList;

/**
 * Framework_MVCクラス
 * {@link HogeHogeServletTest.servlet.HogeHogeServlet}に使用される
 *
 * @author okamotoyuuma
 * @version 1.0.0
 * @version 2021.04.20
 *
 */
public class Model {
	String type = "WEB";//Type(Web,API)
	String nextPage;//遷移先
	String method = "forword";//リダイレクトかフォワードか
	ArrayList<Value> sessionObj = new ArrayList<>();//sessionに格納するオブジェクト
	ArrayList<Value> requestObj = new ArrayList<>();//Requestに格納するオブジェクト
	Object jsonObj;//JSONで返すオブジェクト
	private boolean LoginCheckerFlag;//LoginChecker

	//getter
	public String getType() {return type;}
	public String getNextPage() {return nextPage;}
	public String getMethod() {return method;}
	public ArrayList<Value> getSessionObj() {return sessionObj;}
	public ArrayList<Value> getRequestObj() {return requestObj;}
	public Object getJsonObj() {return jsonObj;}

	//setter
	public void setType(String type) {this.type = type;}
	public void setWeb() {this.type = "WEB";}
	public void setJSON() {this.type = "JSON";}
	public void setNextPage(String nextPage) {this.nextPage = nextPage;}
	public void setMethod(String method) {this.method = method;}
	public void setSessionObj(ArrayList<Value> sessionObj) {this.sessionObj = sessionObj;}
	public void setRequestObj(ArrayList<Value> requestObj) {this.requestObj = requestObj;}
	public void setJsonObj(Object jsonObj) {this.jsonObj = jsonObj;}
	public void setLoginCheckerFlag(boolean loginCheckerFlag) {LoginCheckerFlag = loginCheckerFlag;}

	//Flagチェック
	public boolean isLoginCheckerFlag() {return LoginCheckerFlag;}

}
