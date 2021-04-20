package jisaku_jpa.query;

import java.sql.ResultSet;

/**
 * SQL生成及び、SQLの実行を行うクラスのインターフェース</br>
 * やりとりには、{@link jisaku_jpa.query.QueryInfo}</br>
 * 実装クラスは{@link jisaku_jpa.query.MysqlQuery}、{@link jisaku_jpa.query.OracledbQuery}</br>
 *
 * {@link jisaku_jpa.query.RepositoryImpl}に利用される。
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.09
 */
public interface Query {

	/**
	 * INSERT文生成メソッド
	 * @param qi
	 * @return INSERT文を返す
	 */
	public String createInsertSql(QueryInfo qi);

	/**
	 * UPDATE文生成メソッド
	 * @param qi
	 * @return UPDATE文を返す
	 */
	public String createUpdateSql(QueryInfo qi);

	/**
	 * SELECT文生成メソッド
	 * @param qi
	 * @return SELECT文を返す
	 */
	public String createSelectSql(QueryInfo qi);

	/**
	 * DELETE文生成メソッド
	 * @param qi
	 * @return DELETE文を返す
	 * @throws noColumnValueException(削除対象がない場合にスローされる)
	 */
	public String createDeleteSql(QueryInfo qi);

	/**
	 * RECORD確認用メソッド
	 * @param qi
	 * @return
	 */
	public String createCheckRecordSql(QueryInfo qi);

	/**
	 * DB内のレコード数をチェックするメソッド
	 * @return
	 */
	public String createCheckCountSql(QueryInfo qi);

	/**
	 * DBデータの更新メソッド
	 * @param sql
	 * @return
	 */
	public int executeUpdate(String sql);

	/**
	 * DBデータの参照メソッド
	 * @param sql
	 * @return
	 */
	public ResultSet executeQuery(String sql);

}
