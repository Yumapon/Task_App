package jisaku_jpa.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * このアノテーションは、ビジネスロジッククラスのTransaction処理を行いたいメソッドに対して付与します。</br>
 *
 * このアノテーションを検知した際に、内部でTransactionIDを発行し、</br>
 * 自動的に{@link jisaku_jpa.dbConnection.ConnectionPool}から{@link jisaku_jpa.dbConnection.DBAccess}をチェックアウトします。</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.11
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface Transactional {

}
