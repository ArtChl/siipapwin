package com.luxsoft.siipap.cxc.domain;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;
import org.hibernate.validator.PropertyConstraint;
import org.hibernate.validator.Validator;

import com.luxsoft.siipap.domain.CantidadMonetaria;

public class MinCantidadValidator implements Validator<MinCantidad>, PropertyConstraint{
	
	private long min;

	public void initialize(MinCantidad parameters) {
		min=parameters.min();
		
	}

	public boolean isValid(Object value) {		
		if(value==null) return true;
		if(value instanceof CantidadMonetaria){
			CantidadMonetaria monto=(CantidadMonetaria)value;
			return monto.amount().longValue()>=min;
		}
		return false;
	}

	public void apply(Property property) {
		Column col = (Column) property.getColumnIterator().next();
		col.setCheckConstraint( col.getName() + ">=" + min );
		
	}

}
