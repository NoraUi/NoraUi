/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RetryOnFailure {

    /**
     * @return How many times to retry.
     */
    int attempts() default 1;

    /**
     * @return Delay between attempts, in time units.
     */
    long delay() default 10;

    /**
     * @return Time units of delay.
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * @return Shall it be fully verbose (show full exception trace) or just
     */
    boolean verbose() default false;

}