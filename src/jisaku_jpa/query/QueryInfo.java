package jisaku_jpa.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * SQL生成や実行に必要な情報を格納するクラス</br>
 *
 * {@link jisaku_jpa.query.Query}インタフェースの実装クラスに使用されている</br>
 * インスタンス生成には、Builderパターンを採用している</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.09
 */
public class QueryInfo {

	final Logger logger = Logger.getLogger(QueryInfo.class.getName());// Loggerクラスのインスタンスを生成する

	String tableName;//対象テーブル名
	String idName;//主キー
	ArrayList<String> columnNames;//カラム名
	Map<String, String> columnValues = new HashMap<>();//データ※メソッド利用開始時に、初期化の必要あり

	//PrimaryKeyの値を取得
	public String getIdValue() {return columnValues.get(idName);}

	//columnValuesの初期化
	public void clearQueryInfo() {
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "columnValuesを初期化しました");
		columnValues.clear();
	}

	/*
	 * 以下はbuilderの実装部分
	 */
	private QueryInfo(String tableName, String idName, ArrayList<String> columnNames, Map<String, String> columnValues) {
		this.tableName = tableName;
		this.idName = idName;
		this.columnNames = columnNames;
		this.columnValues = columnValues;
	}

	public static Builder builder() { return new Builder();}

	public static class Builder{
		String tableName;
		String idName;
		ArrayList<String> columnNames;
		Map<String, String> columnValues = new HashMap<>();

		public Builder() {}; //コンストラクタ

		public Builder setTableName(String tableName) {
			this.tableName = tableName;
			return this;
		}

		public Builder setIdName(String idName) {
			this.idName = idName;
			return this;
		}

		public Builder setColumnNames(ArrayList<String> columnNames) {
			this.columnNames = columnNames;
			return this;
		}

		public Builder setColumnValues(Map<String, String> columnValues) {
			this.columnValues = columnValues;
			return this;
		}

		public QueryInfo build() {
			return new QueryInfo(this.tableName, this.idName, this.columnNames, this.columnValues);
		}
	}


	//getter
	public String getTableName() {return tableName;}
	public String getIdName() {return idName;}
	public ArrayList<String> getColumnNames() {return columnNames;}
	public Map<String, String> getColumnValues() {return columnValues;}

}
