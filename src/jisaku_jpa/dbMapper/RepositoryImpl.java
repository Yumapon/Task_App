package jisaku_jpa.dbMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import jisaku_jpa.annotation.ManyToMany;
import jisaku_jpa.annotation.ManyToOne;
import jisaku_jpa.annotation.OneToMany;
import jisaku_jpa.annotation.OneToOne;
import jisaku_jpa.annotation.Table;
import jisaku_jpa.annotation.TimestampToDate;
import jisaku_jpa.annotation.column;
import jisaku_jpa.annotation.id;
import jisaku_jpa.dbConfigReader.DBConfig;
import jisaku_jpa.dbConfigReader.EnvironmentConfigReader;
import jisaku_jpa.query.Query;
import jisaku_jpa.query.QueryFactory;
import jisaku_jpa.query.QueryInfo;

/**
 * {@link jisaku_jpa.dbMapper.Repository}インターフェースの実装クラス</br>
 *
 *  クラス定義の際、TにはEntityクラスの型を、IDにはPrimaryKeyを指定してください</br>
 *  各メソッドの説明は、{@link jisaku_jpa.dbMapper.Repository}を参照してください</br>
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.09
 */
public class RepositoryImpl<T, ID> implements Repository<T, ID>{

	final Logger logger = Logger.getLogger(RepositoryImpl.class.getName());// Loggerクラスのインスタンスを生成する

	//設定ファイルからDBTypeを取得
	EnvironmentConfigReader ecr = new EnvironmentConfigReader();//設定ファイル取得クラス
	DBConfig dbc = ecr.read();//設定ファイルの取得
	String dbType = dbc.getDbType();

	Query query = QueryFactory.getQueryClass(dbType);//Queryクラス
	QueryInfo qi;//Query用オブジェクト

	//Entity情報
	private String tableName;
	private String idName;
	private ArrayList<String> columnNames = new ArrayList<>();
	private Class<T> entityType;

	@SuppressWarnings({ "unchecked"})
	public RepositoryImpl(T... t) {//コンストラクタ
		//EntityのTypeを取得する
		Class<T> entityType = (Class<T>) t.getClass().getComponentType();
		this.entityType = entityType;

		//Entity情報を取得
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Entityクラス情報の取得を開始します");
		try {
			T entity = this.entityType.getDeclaredConstructor().newInstance();

			//Entityのテーブル名を取得
			if (entity == null)
				logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "nullです");
			else if (entity.getClass().isAnnotationPresent(Table.class)) {
				logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "@Tableを発見 テーブル名を取得します");
				this.tableName = entity.getClass().getAnnotation(Table.class).value();
			} else {
				logger.warning(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + entity.getClass().getName() +  "@Tableが付与されていません、付与してください");
			}

			//EntityからPrimaryKeyのカラム名を取り出す
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Entityクラスのカラム名を取得します");
			for (Field f : entity.getClass().getDeclaredFields()) {
				//カラム名の取得
				if (f.isAnnotationPresent(column.class))
					columnNames.add(f.getName());
				//@idが付与されているメンバを探索
				if (f.isAnnotationPresent(id.class)) {
					try {
						logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "EntityクラスのPrimaryKey名を取得します");
						//Field名を取得する
						idName = f.getName();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}

		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Entityクラス情報の取得が完了しました");

		//Query用オブジェクトにEntity情報を格納
		qi = QueryInfo.builder()
				 	  .setTableName(tableName)
					  .setIdName(idName)
					  .setColumnNames(columnNames)
					  .build();
	}


	/**
	 * saveメソッドは、引数で指定されたエンティティをDBに格納するクラスです。</br>
	 * DBにデータがすでに格納されている主キーのデータであった場合、上書き処理を行う。</br>
	 * 主キーが登録されていない場合、新規で登録する。</br>
	 * （DBのデータが多くなるほどパフォーマンスが下がっちゃう問題あり）</br>
	 *
	 * @param entity DBに格納するエンティティ
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void save(T entity) {

		qi.clearQueryInfo();//QueryInfoの初期化
		ID idValue = null;//PrimaryKeyの値

		try {
			//entityのカラム値を取得
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "entityのカラム値を取得");
			for (Field f : entity.getClass().getDeclaredFields()) {
				try {
					f.setAccessible(true);
					//@columnアノテーションが付与されていないか、データが格納されていな場合は飛ばす
					if (!f.isAnnotationPresent(column.class) || f.get(entity) == null) {continue;}

					if(f.isAnnotationPresent(TimestampToDate.class)) {
						String date = f.get(entity).toString().substring(0,10);
						qi.getColumnValues().put(f.getName(), date);
					}else {
						logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "@TimestampToDateはついていません");
						qi.getColumnValues().put(f.getName(), f.get(entity).toString());
					}
					f.setAccessible(false);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					logger.log(Level.INFO, "例外のスローを捕捉", e);
				}
			}

			//EntityからPrimaryKeyの値を取り出す
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "EntityクラスのPrimaryKeyの値を取得する");
			Field primaryIdField = entity.getClass().getDeclaredField(idName);
			primaryIdField.setAccessible(true);
			idValue = (ID) primaryIdField.get(entity);
			primaryIdField.setAccessible(false);

		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}

		//すでにDBに格納されているEntityかどうかを確認する。
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Entityがすでに格納されているか確認します");
		if (existsById(idValue)) {//DBにデータが格納されている場合
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Entityがすでに格納されているため、データの上書きを行います");

			//SQL文を発行
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLを発行します");
			String sql = query.createUpdateSql(qi);

			//上書き処理を行う
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLを実行します");
			query.executeUpdate(sql);
		} else {//DBにデータが格納されていない場合
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Entityは格納されていないため、DBへ新規登録を行います");

			//SQL文を発行
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLを発行します");
			String sql = query.createInsertSql(qi);

			//登録処理を行う
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLを実行します");
			query.executeUpdate(sql);
		}
	}

	/**
	 * 主キーでEntityを探索するメソッド
	 * @param 主キー
	 * @return 結果データ
	 */
	@Override
	public Optional<T> findById(ID primaryKey) {

		qi.clearQueryInfo();//QueryInfoの初期化
		Optional<T> entityOpt = null;//return値
		T entity = null;

		//SQL文を発行
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQL文を生成します");
		qi.getColumnValues().put(idName, primaryKey.toString());
		String sql = query.createSelectSql(qi);

		//SQLを実行
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQL文を実行します");
		ResultSet result = query.executeQuery(sql);

		//resultから値を取得
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "結果からEntity情報を取得します");
		try {
			entity = this.entityType.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}
		Field f;
		try {
			//ResultSetのカーソルを先頭に持ってくる
			result.beforeFirst();
			if (result.next()) {
				for (String column : columnNames) {
					Object columnValue = result.getObject(column);
					f = entity.getClass().getDeclaredField(column);
					f.setAccessible(true);
					f.set(entity, columnValue);
					f.setAccessible(false);
				}
				//Entityをオプショナル型に変換
				entityOpt = Optional.of(entity);
			} else {
				entityOpt = Optional.empty();//データがなければ、Optional型でNULLを返す
			}

		} catch (SQLException | NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}
		return entityOpt;
	}

	/**
	 * DBに格納されている全てのデータを返します。
	 * @return DBに格納されている全てのデータを返します。(DBに何も格納されていない場合、NULLを返す)
	 */
	@Override
	public Optional<ArrayList<T>> findAll() {

		qi.clearQueryInfo();//QueryInfoの初期化
		Optional<ArrayList<T>> list = Optional.of(new ArrayList<>());//Entity格納用//Entity格納用
		T entity = null;//Entity

		//SQL文を発行
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQL文を生成します");
		String sql = query.createSelectSql(qi);

		//SQLを実行
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQL文を実行します");
		ResultSet result = query.executeQuery(sql);

		//resultから値を取得
		Field f;
		try {
			while (result.next()) {
				entity = this.entityType.getDeclaredConstructor().newInstance();
				for (String column : columnNames) {
					Object columnValue = result.getObject(column);
					f = entity.getClass().getDeclaredField(column);
					f.setAccessible(true);
					f.set(entity, columnValue);
					f.setAccessible(false);
				}
				list.get().add(entity);
			}
		}  catch (SQLException | NoSuchFieldException |
				 SecurityException | IllegalArgumentException |
				 IllegalAccessException | InstantiationException |
				 InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}
		//return result;
		return list;
	}

	/**
	 * 指定された条件のエンティティを返します。
	 * @param entity
	 * @return
	 */
	@Override
	public Optional<ArrayList<T>> findAll(T entity) {

		qi.clearQueryInfo();//QueryInfoの初期化
		Optional<ArrayList<T>> list = Optional.of(new ArrayList<>());//Entity格納用

		//Entityから検索条件を取得する
		for (Field f : entity.getClass().getDeclaredFields()) {
			try {
				f.setAccessible(true);
				if (f.get(entity) == null || f.getName().equals(idName))
					continue;
				qi.getColumnValues().put(f.getName(), f.get(entity).toString());
				f.setAccessible(false);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		//SQL文を発行
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQL文を生成します");
		String sql = query.createSelectSql(qi);

		//SQLを実行
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQL文を実行します");
		ResultSet result = query.executeQuery(sql);

		//resultから値を取得
		Field f;
		try {
			while (result.next()) {
				entity = this.entityType.getDeclaredConstructor().newInstance();
				for (String column : columnNames) {
					Object columnValue = result.getObject(column);
					f = entity.getClass().getDeclaredField(column);
					f.setAccessible(true);
					f.set(entity, columnValue);
					f.setAccessible(false);
				}
				list.get().add(entity);
			}
		} catch (SQLException | NoSuchFieldException |
				 SecurityException | IllegalArgumentException |
				 IllegalAccessException | InstantiationException |
				 InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}

		//return result;
		return list;
	}

	/**
	 * DBに格納されているデータの数を返します。
	 * @return データの数
	 */
	@Override
	public int count() {

		qi.clearQueryInfo();//QueryInfoの初期化

		//SQL文の生成
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQL文を生成します");
		String sql = query.createCheckCountSql(qi);

		//SQL文の実行
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQL文を実行します");
		ResultSet rs = query.executeQuery(sql);

		//ResultSetからレコード数を受け取る
		int i = 0;
		try {
			rs.next();
			i = rs.getInt("COUNT(*)");
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}

		return i;
	}

	/**
	 * Entity削除用メソッド
	 * @param Entity
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void delete(T entity) {
		qi.clearQueryInfo();//QueryInfoの初期化
		ID idValue;//PrimaryKeyの値

		//EntityからPrimaryKeyの値を取り出す
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "EntityクラスのPrimaryKeyの値を取得します");
		Field primaryIdField;
		try {
			primaryIdField = entity.getClass().getDeclaredField(idName);
			primaryIdField.setAccessible(true);
			idValue = (ID) primaryIdField.get(entity);
			primaryIdField.setAccessible(false);

			//主キーが格納されていない場合
			if (idValue == null) {
				logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "主キーに何もパラメータがセットされていません、カラムの値を取得します");
				//entityのカラム値を取得
				for (Field f : entity.getClass().getDeclaredFields()) {
					try {
						f.setAccessible(true);
						if (f.get(entity) == null || f.getName().equals(idName))
							continue;
						qi.getColumnValues().put(f.getName(), f.get(entity).toString());
						f.setAccessible(false);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
						logger.log(Level.INFO, "例外のスローを捕捉", e);
					}
				}
			} else {
				logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "主キーにパラメータがセットされています");
				qi.getColumnValues().put(idName, idValue.toString());
			}
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}

		//SQLの生成
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLを生成します");
		String sql = query.createDeleteSql(qi);

		//SQLの実行
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQL文を実行します");
		int i = query.executeUpdate(sql);
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + i + "件更新しました");
	}

	/**
	 *主キーでEntityが格納されているか確認するメソッド
	 *@param ID
	 *@return boolean
	 */
	@Override
	public boolean existsById(ID primaryKey) {
		//SQLを発行する
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLを生成します");
		qi.getColumnValues().put(idName, primaryKey.toString());
		String sql = query.createCheckRecordSql(qi);

		//SQLの実行
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQL文を実行します");
		ResultSet rs = query.executeQuery(sql);

		//ResultSetからレコード数を受け取る
		int i = 0;
		try {
			rs.next();
			i = rs.getInt("COUNT(*)");
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}

		if (i <= 0) {return false;}
		else        {return true;}
	}

	@Override
	public Optional<T> multiFindById(ID primaryKey) {
		Optional<T> entityOpt = findById(primaryKey);//return用Object

		//まずは@columnに値をセットする
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "@columnに値をセットします");

		//親Entity
		T entity;
		//リレーションアノテーションが設定されているカラムの情報を取得する
		if (entityOpt.isPresent()) {
			entity = entityOpt.get();
			//親EntityクラスのFieldを取得する
			for (Field f : entity.getClass().getDeclaredFields()) {
				//リレーション情報が付与されているFieldを取得
				if (f.isAnnotationPresent(column.class))
					continue;
				//@OneToOneがついている場合
				else if (f.isAnnotationPresent(OneToOne.class)) {
					//子Entityの生成
					Object childEntity = getEntityObj(f);

					//親EntityのPrimaryKeyで子Entityを検索
					//子Entityのテーブル名を取得
					String childTableName = getTableName(childEntity);

					//子Entityからカラム名を取り出す
					String childIdName = null;
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "子EntityクラスのPrimaryKeyを取得します");
					for (Field f1 : childEntity.getClass().getDeclaredFields()) {
						//@idが付与されているメンバを探索
						if (f1.isAnnotationPresent(id.class)) {
							try {
								logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "EntityクラスのPrimaryKey名を取得します");
								//Field名を取得する
								childIdName = f1.getName();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
								logger.log(Level.INFO, "例外のスローを捕捉", e);
							}
						}
					}

					//QueryInfoオブジェクトの発行
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "子EntityクラスのQueryInfoを作成します");
					final String childIdNameMap = childIdName;
					QueryInfo qi2 = QueryInfo.builder()
											 .setTableName(childTableName)
											 .setIdName(childIdName)
											 .setColumnValues(new HashMap<String, String>() {{put(childIdNameMap, primaryKey.toString());}})
											 .build();

					//SQL文の生成(子Entity)
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLを生成します（子クラス）");
					String sql1 = query.createSelectSql(qi2);

					//SQL文の実行(子Entity)
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLを実行します（子クラス）");
					ResultSet rs1 = query.executeQuery(sql1);

					//子クラスに結果を代入
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "結果をセットします（子クラス）");
					try {
						if (rs1.next()) {
							for (Field f2 : childEntity.getClass().getDeclaredFields()) {
								if (!f2.isAnnotationPresent(column.class))
									continue;
								f2.setAccessible(true);
								f2.set(childEntity, rs1.getObject(f2.getName()));
								f2.setAccessible(false);
							}
						}
					} catch (SecurityException | IllegalArgumentException | IllegalAccessException | SQLException e1) {
						e1.printStackTrace();
						logger.log(Level.INFO, "例外のスローを捕捉", e1);
					}

					//生成した子クラスを親EntityのFieldにSET
					f.setAccessible(true);
					try {
						f.set(entity, childEntity);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
						logger.log(Level.INFO, "例外のスローを捕捉", e);
					}
					f.setAccessible(false);
					continue;

				} else if (f.isAnnotationPresent(ManyToOne.class)) {//@ManyToOneがついている場合

					Object childEntity = getEntityObj(f);//子Entityの生成
					String childTableName = getTableName(childEntity);//子Entityのテーブル名を取得
					String childIdName = null;//子Entityからカラム名を取り出す

					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "子EntityクラスのPrimaryKeyを取得します");
					for (Field f1 : childEntity.getClass().getDeclaredFields()) {
						//@idが付与されているメンバを探索
						if (f1.isAnnotationPresent(id.class)) {
							try {
								logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "EntityクラスのPrimaryKey名を取得します");
								//Field名を取得する
								childIdName = f1.getName();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
								logger.log(Level.INFO, "例外のスローを捕捉", e);
							}
						}
					}

					//QueryInfoオブジェクトの発行
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "子EntityクラスのQueryInfoを作成します");
					String idValue = null;
					try {
						Field f1 = entity.getClass().getDeclaredField(idName);
						f1.setAccessible(true);
						idValue = (String) f1.get(entity);
						f1.setAccessible(false);
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
							| SecurityException e) {
						e.printStackTrace();
						logger.log(Level.INFO, "例外のスローを捕捉", e);
					}

					final String childIdNameMap = childIdName;
					final String idValueMap = idValue;
					QueryInfo qi2 = QueryInfo.builder()
											 .setTableName(childTableName)
											 .setIdName(childIdName)
											 .setColumnValues(new HashMap<String, String>() {{put(childIdNameMap, idValueMap);}})
											 .build();

					//SQL文の生成(子Entity)
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLを生成します（子クラス）");
					String sql1 = query.createSelectSql(qi2);

					//SQL文の実行(子Entity)
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLを実行します（子クラス）");
					ResultSet rs1 = query.executeQuery(sql1);

					//子クラスに結果を代入
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "結果をセットします（子クラス）");
					try {
						if (rs1.next()) {
							for (Field f2 : childEntity.getClass().getDeclaredFields()) {
								if (!f2.isAnnotationPresent(column.class))
									continue;
								f2.setAccessible(true);
								f2.set(childEntity, rs1.getObject(f2.getName()));
								f2.setAccessible(false);
							}
						}
					} catch (SecurityException | IllegalArgumentException | IllegalAccessException | SQLException e1) {
						e1.printStackTrace();
						logger.log(Level.INFO, "例外のスローを捕捉", e1);
					}

					//生成した子クラスを親EntityのFieldにSET
					f.setAccessible(true);
					try {
						f.set(entity, childEntity);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
						logger.log(Level.INFO, "例外のスローを捕捉", e);
					}
					f.setAccessible(false);
					continue;

					//@OneToManyがついている場合
				} else if (f.isAnnotationPresent(OneToMany.class)) {
					//子Entityの生成
					Object childEntity = null;
					Type superType = f.getGenericType();
					Type[] types = ((ParameterizedType) superType).getActualTypeArguments();
					try {
						Class<?> clazz = Class.forName(types[0].getTypeName());
						childEntity = clazz.getDeclaredConstructor().newInstance();
					} catch (ClassNotFoundException | InstantiationException |
							 IllegalAccessException | IllegalArgumentException |
							 InvocationTargetException | NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
						logger.log(Level.INFO, "例外のスローを捕捉", e);
					}

					//子Entityのテーブル名を取得
					String childTableName = getTableName(childEntity);

					//@OneToManyから連結カラム名を取り出す
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "@OneToManyから連結カラム名を取得します");
					String[] mapping = f.getAnnotation(OneToMany.class).mappingBy();

					//検索条件を取得
					Map<String, String> columnValues = new HashMap<>();
					Field f2 = null;
					String columnValue = null;
					for (String columnName : mapping) {
						try {
							f2 = entity.getClass().getDeclaredField(columnName);
							f2.setAccessible(true);
							columnValue = f2.get(entity).toString();
							f2.setAccessible(false);
						} catch (NoSuchFieldException | SecurityException e) {
							e.printStackTrace();
							logger.log(Level.INFO, "例外のスローを捕捉", e);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
							logger.log(Level.INFO, "例外のスローを捕捉", e);
						}
						columnValues.put(columnName, columnValue);
					}

					//主キー検索でないので、カラム名情報を取得する
					ArrayList<String> columnNames = new ArrayList<>();
					for (String columnName : mapping) {
						columnNames.add(columnName);
					}
					QueryInfo qi2 = QueryInfo.builder()
							 .setTableName(childTableName)
							 .setColumnValues(columnValues)
							 .setColumnNames(columnNames)
							 .build();

					//SQL文の生成(子Entity)
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLを生成します（子クラス）");
					String sql1 = query.createSelectSql(qi2);

					//SQL文の実行(子Entity)
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "SQLを実行します（子クラス）");
					ResultSet rs1 = query.executeQuery(sql1);

					//子クラスに結果を代入
					logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "結果をセットします（子クラス）");
					List<Object> list = new ArrayList<>();
					try {
						//子クラスに検索結果の値をセットし、リストに格納する。
						while (rs1.next()) {
							for (Field f3 : childEntity.getClass().getDeclaredFields()) {
								if (!f3.isAnnotationPresent(column.class))
									continue;
								f3.setAccessible(true);
								f3.set(childEntity, rs1.getObject(f3.getName()));
								f3.setAccessible(false);
							}
							list.add(childEntity);
						}
					} catch (SecurityException | IllegalArgumentException | SQLException | IllegalAccessException e) {
						e.printStackTrace();
						logger.log(Level.INFO, "例外のスローを捕捉", e);
					}

					//生成した子クラスを親EntityのFieldにSET
					f.setAccessible(true);
					try {
						f.set(entity, list);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
						logger.log(Level.INFO, "例外のスローを捕捉", e);
					}
					f.setAccessible(false);

					continue;

					//@ManyToManyがついている場合
				} else if (f.isAnnotationPresent(ManyToMany.class)) {
					//TODO
					//未実装
				}
			}
		} else {
			return entityOpt;
		}
		return entityOpt;
	}

	/**
	 * 未実装
	 */
	@Override
	public Iterable<T> multifindAll() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	/**
	 *インスタンスのテーブル名を取得するメソッド
	 * @param childEntity
	 * @return
	 */
	private String getTableName(Object childEntity) {
		String childTableName = null;
		//子Entityクラス用のQueryInfoの生成
		if (childEntity.getClass().isAnnotationPresent(Table.class)) {
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "子Entityの@Tableを発見 テーブル名を取得します");
			childTableName = childEntity.getClass().getAnnotation(Table.class).value();
		} else {
			logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "子Entityに@Tableが付与されていません、付与してください");
		}
		return childTableName;

	}

	/**
	 * Single用子Entityメソッド
	 * @param f
	 * @return
	 */
	private Object getEntityObj(Field f) {
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "Entityを生成します（子クラス）");
		//子Entityの生成
		Object childEntity = null;
		try {
			Class<?> clazz = Class.forName(f.getType().getName());
			childEntity = clazz.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException | InstantiationException |
				 IllegalAccessException | IllegalArgumentException |
				 InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "例外のスローを捕捉", e);
		}
		return childEntity;
	}

}
