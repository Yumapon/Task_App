package jisaku_jpa.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * このアノテーションは、Timestamp型をDate型に変更したい場合</br>
 * 変更対象の変数宣言箇所に付与します。</br>
 *
 * @author okamotoyuuma
 * @version 2.0.0
 * @version 2021.04.11
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface TimestampToDate {

}
