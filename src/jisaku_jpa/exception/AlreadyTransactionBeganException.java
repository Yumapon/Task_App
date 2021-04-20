package jisaku_jpa.exception;

/**
 * すでにスレッドでトランザクションが開始されている状態で、再度開始しようとするとスローされる</br>
 *
 * この例外をスローするクラスは、{@link jisaku_jpa.transactionManager.TransactionManager}があります。</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.11
 */
public class AlreadyTransactionBeganException extends Exception {

}
