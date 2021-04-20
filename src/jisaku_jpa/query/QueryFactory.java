package jisaku_jpa.query;

/**
 * QueryクラスのFactoryクラス</br>
 * 現状OracleDBとMysqlのみ対応</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.09
 */
public class QueryFactory {
	public static Query getQueryClass(String dbType){

		Query query = null;

		if(dbType.equalsIgnoreCase("mysql") || dbType == null) //mysqlQueryインスタンス
			query = new MysqlQuery();

		else if(dbType.equalsIgnoreCase("oracledb"))//OracleDBQueryインスタンス
			query = new OracledbQuery();

		return query;
	}

}
