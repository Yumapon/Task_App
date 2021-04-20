package jisaku_jpa.dbConfigReader;

/**
 * データベースの設定ファイル</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.12
 */
public class DBConfig {

	public DBConfig() {}
	public DBConfig(String driver, String url, String user, String password, String schema, int numberOfAccess, String dbName, String dbType) {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
		this.schema = schema;
		this.numberOfAccess = numberOfAccess;
		this.dbName = dbName;
		this.dbType = dbType;
	}

	private String driver;//ドライバ名
	private String url;//DBのURL
	private String user;//DBのユーザ
	private String password;//パスワード
	private String schema;//スキーマ名
	private int numberOfAccess;//コネクションの確立数
	private String dbName;//DBName
	private String dbType;//DBType

	//getter
	public String getDriver() {return driver;}
	public String getUrl() {return url;}
	public String getUser() {return user;}
	public String getPassword() {return password;}
	public String getSchema() {return schema;}
	public int getNumberOfAccess() {return numberOfAccess;}
	public String getDbName() {return dbName;}
	public String getDbType() {return dbType;}

	//setter
	public void setDriver(String driver) {this.driver = driver;}
	public void setUrl(String url) {this.url = url;}
	public void setUser(String user) {this.user = user;}
	public void setPassword(String password) {this.password = password;}
	public void setSchema(String schema) {this.schema = schema;}
	public void setNumberOfAccess(int numberOfAccess) {this.numberOfAccess = numberOfAccess;}
	public void setDbName(String dbName) {this.dbName = dbName;}
	public void setDbType(String dbType) {this.dbType = dbType;}

}
