package jisaku_jpa.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * このアノテーションは、DBとやりとりするエンティティクラスに付与する。</br>
 * valueには、エンティティに対応するテーブル名を指定する。</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.11
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Table {

	String value();

}
