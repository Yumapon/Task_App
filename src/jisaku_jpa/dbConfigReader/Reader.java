package jisaku_jpa.dbConfigReader;

/**
 * 設定ファイルを読み込むクラスのインタフェース</br>
 * 実装クラスは、{@link jisaku_jpa.dbConfigReader.EnvironmentConfigReader}がある。</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.09
 *
 * @param <T>
 */
public interface Reader <T>{

	/**
	 * 設定ファイルを読み込む（ファイル名デフォルトの場合）
	 * @return 設定値を格納したインスタンス
	 */
	T read();

	/**
	 * 設定ファイルを読み込む（設定ファイル変更時）
	 * @param fileName 設定ファイルの名前をデフォルトから変更する場合、その名前を指定する。
	 * @return
	 */
	T read(String fileName);

}