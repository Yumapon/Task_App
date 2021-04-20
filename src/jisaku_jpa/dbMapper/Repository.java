package jisaku_jpa.dbMapper;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Repositoryインターフェースは、フレームワークを利用者側で使用するためのAPIインターフェースです。</br>
 * 実装クラスは、{@link jisaku_jpa.dbMapper.RepositoryImpl}を参照</br>
 *
 * クラス定義の際、TにはEntityクラスの型を、IDにはPrimaryKeyを指定してください</br>
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.09
 */
public interface Repository<T, ID> {

	/**
	 * saveメソッドは、引数で指定されたエンティティをDBに格納するクラスです。¸
	 * @param entity DBに格納するエンティティ
	 */
	void save(T entity);

	/**
	 * 指定された ID で識別されるエンティティを返します。
	 * @param primaryKey DBから検索したいエンティティの主キー
	 * @return paramで指定された主キーの検索結果を返す。（一致するものがない場合、NULL）
	 */
	Optional<T> findById(ID primaryKey);

	/**
	 * DBに格納されている全てのデータを返します。
	 * @return DBに格納されている全てのデータを返します。(DBに何も格納されていない場合、NULLを返す)
	 */
	Optional<ArrayList<T>> findAll();

	/**
	 * 指定された条件のエンティティを返します。
	 * @param entity
	 * @return
	 */
	Optional<ArrayList<T>> findAll(T entity);

	/**
	 * DBに格納されているデータの数を返します。
	 * @return データの数
	 */
	int count();

	/**
	 * 指定されたエンティティを削除します。
	 * @param 削除したいエンティティ
	 */
	void delete(T entity);

	/**
	 * 指定された ID のエンティティが存在するかどうかを判断する
	 * @param 検索したいデータの主キー
	 * @return 存在する場合TRUE、存在しない場合FALSE
	 */
	boolean existsById(ID primaryKey);

	/**
	 * 一部未実装
	 * 指定された ID で識別されるエンティティを返します。
	 * @param primaryKey
	 * @return
	 */
	Optional<T> multiFindById(ID primaryKey);/*@ManyToManyに関しては未実装*/

	/**
	 * 未実装
	 * @return
	 */
	Iterable<T> multifindAll();/*未実装*/


}
