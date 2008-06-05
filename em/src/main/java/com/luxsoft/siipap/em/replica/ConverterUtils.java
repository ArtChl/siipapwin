package com.luxsoft.siipap.em.replica;

import java.math.BigDecimal;

public class ConverterUtils {
	
	public static Double toDouble(Object val){
		if(val==null) return Double.valueOf(0);
		if(val instanceof Double) return (Double)val;
		if(val instanceof BigDecimal){
			BigDecimal bd=(BigDecimal)val;
			return bd.doubleValue();
		}else if(val instanceof String){
			String s=(String)val;
			return Double.valueOf(s);
		}else{
			throw new RuntimeException("Transformación a Double no soportada origen: "
					+val.getClass().getName());
		}
	}
	
	public static Integer toInteger(Object val){
		if(val==null) return new Integer(0);
		if(val instanceof BigDecimal){
			BigDecimal bd=(BigDecimal)val;
			return bd.intValue();
		}else if(val instanceof String){
			String s=(String)val;
			return Integer.valueOf(s);
		}else if(val instanceof Double){
			Double d=(Double)val;
			return d.intValue();
		}else if(val instanceof Integer){
			return (Integer)val;
		}else{
			throw new RuntimeException("Transformación a Integer no soportada origen: "
					+val.getClass().getName());
		}
	}
	
	public static Long toLong(Object val){
		if(val instanceof BigDecimal){
			BigDecimal bd=(BigDecimal)val;
			return bd.longValue();
		}else if(val instanceof String){
			String s=(String)val;
			return Long.valueOf(s);
		}else if(val instanceof Double){
			Double d=(Double)val;
			return d.longValue();
		}else{
			throw new RuntimeException("Transformación a Long no soportada origen: "
					+val.getClass().getName());
		}
	}
	
	public static String toString(Object val){
		if(val==null) return null;
		if(val instanceof Number){
			Number n=(Number)val;
			return n.toString();
		}
		return val.toString();
	}

}
