package com.luxsoft.siipap.cxc;

import org.springframework.util.StringUtils;

public enum CXCReportes {
	
	DiarioDeCobranza,
	RecepcionDeFacturas,
	NotaDeCredito,
	DiarioDeCobranzaCre,
	CobranzaCredito,
	PagosConNotaCre,
	AuxiliarNCCre,
	Provision,
	ClientesVencidos,
	Depositos,
	ChequeDevueltoContaForm,
	VentasPorVendedorReport,
	VentasCreditoContadoReport,
	ClientesCreditoReport,
	ClienteCreditoDetalleReport,
	;
	
	
	
	public String toString(){
		return StringUtils.uncapitalize(name());
	}
	
	public String getId() {
		return StringUtils.uncapitalize(name());
	}

}
