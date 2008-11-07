package com.luxsoft.siipap.em.importar.ui;

import com.jgoodies.binding.beans.Model;

/**
 * JavaBean para mantener los parametros de replicacion de ventas
 * 
 * @author Ruben Cancino
 * 
 *
 */
public class VentasReplicaConfiguration extends Model{
	
	private String sourceUrl;
	
	private String targetUrl;

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	
	

}
