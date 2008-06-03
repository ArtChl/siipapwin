package com.luxsoft.siipap.ventas.agents;

import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;

import com.luxsoft.siipap.utils.SiipapDateUtils;

/**
 * 
 * Valida la funcionalidad de VentasManager
 * 
 * @author Ruben Cancino
 *
 */
public class AgenteDeVentasTest extends AbstractAnnotationAwareTransactionalTests{
	
	private AgenteDeVentas agente;

	
	
	protected String[] getConfigLocations() {
		return new String[]{"classpath*:conf/**-test-ctx.xml"};
		//return new String[]{"conf/dao-test-ctx.xml"};
    }
	
	
	public void testRevisarVentas(){
		agente.revisarVentas(SiipapDateUtils.getMXDate("16/04/07"));
	}

	public void setAgente(AgenteDeVentas agente) {
		this.agente = agente;
	}
	
	

}
