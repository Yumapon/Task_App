package jisaku_servlet.servlet;


/**
 * Framework_MVCクラス
 * {@link jisakuservlet.servlet.Model}に使用される
 *
 * @author okamotoyuuma
 * @version 1.0.0
 * @version 2021.04.20
 *
 */
public class Value{
	String name;
	Object obj;

	//getter
	public String getName() {return name;}
	public Object getObj() {return obj;}

	//setter
	public void setName(String name) {this.name = name;}
	public void setObj(Object obj) {this.obj = obj;}
}