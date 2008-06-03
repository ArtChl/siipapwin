package com.luxsoft.siipap.cxp.domain;


import java.util.Set;

import com.luxsoft.siipap.maquila.domain.SalidaDeBobinas;
import com.luxsoft.siipap.maquila.domain.SalidaDeHojas;

/**
 * Implementacion de Analisis originados en Maquila 
 * 
 * @author Ruben Cancino
 *
 */
public class AnalisisDeEntradaMaq extends AnalisisDeEntrada{
	
	private Set<SalidaDeHojas> salidasDeHojas;
	private Set<SalidaDeBobinas> salidasDeBobinas;
	
	
	public Set<SalidaDeBobinas> getSalidasDeBobinas() {
		return salidasDeBobinas;
	}
	public void setSalidasDeBobinas(Set<SalidaDeBobinas> salidasDeBobinas) {
		this.salidasDeBobinas = salidasDeBobinas;
	}
	public Set<SalidaDeHojas> getSalidasDeHojas() {
		return salidasDeHojas;
	}
	public void setSalidasDeHojas(Set<SalidaDeHojas> salidasDeHojas) {
		this.salidasDeHojas = salidasDeHojas;
	}
	
	

}
