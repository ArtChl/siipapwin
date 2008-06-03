package com.luxsoft.siipap.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import com.luxsoft.siipap.domain.CantidadMonetaria;

public class MonedasUtils {
	
	public static final BigDecimal IVA=BigDecimal.valueOf(.15d);
	
	public final static Currency PESOS;
	public final static Currency DOLARES;
	public final static Currency EUROS;
	
	static{
		Locale mx=new Locale("es","mx");
		PESOS=Currency.getInstance(mx);
		DOLARES=Currency.getInstance(Locale.US);
		EUROS=Currency.getInstance("EUR");
	}
	
	
	
	/**
	 * Calcula el impuesto nacional 
	 * @param importe
	 * @return
	 */
	public static final CantidadMonetaria calcularImpuesto(CantidadMonetaria importe){
		return importe.multiply(IVA);
	}
	
	public static final CantidadMonetaria calcularTotal(CantidadMonetaria importe){
		return importe.add(calcularImpuesto(importe));
	}
	
	public static final CantidadMonetaria aplicarDescuentosEnCascada(final CantidadMonetaria precio,Double... descuentos){
		CantidadMonetaria neto=precio;
		for(Double d:descuentos){
			if(d==null)continue;
			if(d.doubleValue()>0){
				CantidadMonetaria descuento=neto.multiply(d);
				neto=neto.subtract(descuento);
			}/*else{
				neto=new CantidadMonetaria(0,precio.currency());
			}*/
		}
		return neto;
	}
	
	public static final CantidadMonetaria aplicarDescuentosEnCascada(final CantidadMonetaria importe,BigDecimal... descuentos){
		CantidadMonetaria neto=importe;
		for(BigDecimal d:descuentos){
			if(d==null)continue;
			if(d.doubleValue()>0){
				CantidadMonetaria descuento=neto.multiply(d);
				descuento=descuento.divide(BigDecimal.valueOf(100d));
				neto=neto.subtract(descuento);
			}
		}
		return neto;
	}
	
	public static final CantidadMonetaria calcularImporteDelTotal(CantidadMonetaria total){
		BigDecimal val=BigDecimal.valueOf(1.15);
		CantidadMonetaria importe=total.divide(val);
		return importe;
	}
	
	public static final CantidadMonetaria calcularImporteConDescuentos(CantidadMonetaria precio,BigDecimal cantidad,BigDecimal[] descuentos){
		CantidadMonetaria importe=precio.multiply(cantidad);
		return aplicarDescuentosEnCascada(importe,descuentos);
	}
	
	public static void main(String[] args) {
		CantidadMonetaria total=CantidadMonetaria.pesos(1000);
		CantidadMonetaria imp=MonedasUtils.calcularImporteDelTotal(total);
		CantidadMonetaria iva=MonedasUtils.calcularImpuesto(imp);
		System.out.println(total);
		System.out.println(iva);
		System.out.println(imp);
		
		double t=1000;
		double im=1000/1.15;
		double iv=im*.15;
		
		System.out.println(t);
		System.out.println(im);
		System.out.println(iv);
		
	}

}
