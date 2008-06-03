package com.luxsoft.siipap.clipper.dao;

import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.clipper.domain.Mvcomp;
import com.luxsoft.siipap.dao.GenericDao;
import com.luxsoft.siipap.inventarios.domain.Movimiento;

public interface MvcompDao extends GenericDao<Mvcomp,Long>{
	
	/**
	 * Busca las entradas para determinado dia
	 * @param date
	 * @return
	 */
	public List<Mvcomp> buscaEntradas(final Date date);
	
	/**
	 * Busca los movimientos de tipo  COM,DEC que no tiene padre
	 *  
	 * @param date
	 * @return
	 */
	public List<Movimiento> buscarPartidasPendientes(final Date date);

}
