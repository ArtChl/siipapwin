package com.luxsoft.siipap.cxc.domain;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.hibernate.validator.ValidatorClass;


/**
 * min restriction on a CantidadMonetaria annotated elemnt (or the string representation of a numeric)
 *
 * @author Ruben Cancino
 */

@ValidatorClass(MinCantidadValidator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Documented
public @interface MinCantidad {
	
	long  min();
	
	String message() default "La cantidad monetaria no puede ser >=0";
	
	

}
