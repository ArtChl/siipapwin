package com.luxsoft.siipap.inventarios.dao;

import java.util.List;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.domain.Transformacion;

public interface TransformacionDao {
	
	public void salvarTransformacion(final Transformacion t);
	
	public void borrarTransformacion(final Transformacion t);	
	
	public List<Transformacion> buscarTransfomraciones(final Periodo p);
	
	public List<Transformacion> buscarTransfomraciones();
	
	//public List buscarMovimientosDeTransformacion(final Periodo p);
	
}
