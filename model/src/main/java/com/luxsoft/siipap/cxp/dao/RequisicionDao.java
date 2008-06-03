package com.luxsoft.siipap.cxp.dao;

import java.util.List;

import com.luxsoft.siipap.cxp.domain.Requisicion;
import com.luxsoft.siipap.cxp.domain.RequisicionDetalle;
import com.luxsoft.siipap.domain.Periodo;

public interface RequisicionDao {
	
	public void salvarRequisicion(final Requisicion req);
	
	public void actualizarRequisicion(final Requisicion reg);
	
	public void eliminarRequisicion(final Requisicion reg);
	
	public Requisicion buscarRequisicion(final Long id);
	
	public List<Requisicion> buscarRequisicionesDeFacturas(final Periodo p);
	
	public List<Requisicion> buscarRequisicionesDeFacturas();
	
	public void inicializar(final Requisicion r);
	
	public List<Requisicion> buscarRequisicionesPendientes();
	
	public List<RequisicionDetalle> buscarRequisicionesConPagosAplicadas();
	
	public List<Requisicion> buscarRequisicionesConNCAplicadas();
	

}
