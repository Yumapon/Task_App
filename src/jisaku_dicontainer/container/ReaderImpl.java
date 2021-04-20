package jisaku_dicontainer.container;

import java.util.HashMap;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

/**
 * {@link jisaku_dicontainer.container.Reader}の実装クラス
 *  yamlで記載した設定ファイルを取り込むクラス
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.12
 * @param <T> 設定値を格納するエンティティ
 */
public class ReaderImpl<T> implements Reader<T> {

	final Logger logger = Logger.getLogger(ReaderImpl.class.getName());// Loggerクラスのインスタンスを生成する

	/**
	 * 使用禁止
	 */
	@Override
	public HashMap<String, T> read() {
		// 空実装
		return null;
	}


	/**
	 * @param 設定ファイル名
	 */
	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, T> read(String configFileName) {

		//設定ファイルを格納するマッパーの作成
		HashMap<String, T> definitions = new HashMap<String, T>();
		Yaml yaml = new Yaml();//snakeYamlの生成

		//YamlFileのデータを全て取得
		logger.info(Thread.currentThread().getStackTrace()[1].getClassName() + ":" + "yamlファイルを読み込んでいます	");
		for(Object definition : yaml.loadAll(getClass().getResourceAsStream(configFileName))){
			//HashMap型のBean定義格納クラスにデータを格納
			definitions.put((definition).toString(), ((T) definition));
		}

		return definitions;
	}

}
