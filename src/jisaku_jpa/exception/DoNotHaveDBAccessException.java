package jisaku_jpa.exception;

/**
 * DBへのアクセスストックがない場合にスローされる例外。</br>
 * このエラーが出た場合は、DBProfile.yamlクラスのコネクション数を増やしてみてください。</br>
 *
 * この例外をスローするクラスは、{@link jisaku_jpa.dbConnection.ConnectionPool}があります。</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.11
 */
public class DoNotHaveDBAccessException extends Exception {

}
