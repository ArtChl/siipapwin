package com.luxsoft.siipap.maquila.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.luxsoft.siipap.domain.CantidadMonetaria;

/**
 * Salida generica de Material
 * 
 * @author Ruben Cancino
 *
 */
public class SalidaDeMaterial extends MovimientoDeMaterial{
	
	private EntradaDeMaterial entrada;

	public EntradaDeMaterial getEntrada() {
		return entrada;
	}

	public void setEntrada(EntradaDeMaterial entrada) {
		this.entrada = entrada;
	}
	
	public BigDecimal getCosto(){
		return getKilos().multiply(BigDecimal.valueOf(getEntrada().getPrecioPorKilo())).setScale(2,RoundingMode.HALF_EVEN);
	}
	
	public CantidadMonetaria getCostoAsMoneda(){
		return CantidadMonetaria.pesos(getCosto().doubleValue());
	}
	

}
