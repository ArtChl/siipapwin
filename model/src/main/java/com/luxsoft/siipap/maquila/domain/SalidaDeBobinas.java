package com.luxsoft.siipap.maquila.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;

/**
 * Salida de bobinas que se venden directamente, es decir sin proceso de corte
 * el destino de las mismas debe ser un COM con unidad de medida en kilos
 * 
 * @author Ruben Cancino
 *
 */
public class SalidaDeBobinas extends SalidaDeMaterial{
	
	private AnalisisDeEntrada destino;

	/**
	 * Referencia a AnalisisDeEntrada para vincular con el COM de entrada
	 * 
	 * @return
	 */
	public AnalisisDeEntrada getDestino() {
		return destino;
	}
	public void setDestino(AnalisisDeEntrada destino) {
		//Assert.isTrue(destino.getUnidad().equals("KGS"),"La entrada de material debe ser en Kilos");
		this.destino = destino;
	}
	
	
	public BigDecimal getCosto(){
		return getKilos().multiply(BigDecimal.valueOf(getEntrada().getPrecioPorKilo())).setScale(2,RoundingMode.HALF_EVEN);
	}

}
