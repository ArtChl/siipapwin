package com.luxsoft.siipap.em.importar;

import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;

public class VentasSyncTest extends AbstractImportadoTest{
	
	protected VentasSupport support;
	protected VentasSync sync;
	private VentasDao ventasDao;
	
	@SuppressWarnings("unchecked")
	public void testImportarVentas(){
		final Date dia=new Date();
		List<Venta> siipap=support.buscarVentasEnSiipap(dia);
		System.out.println("Registros en siipap "+siipap.size());
		
		assertTrue(!siipap.isEmpty());
		List<Venta> win=ventasDao.buscarVentas(dia);
		System.out.println("Beans en win: "+win.size());
		
		assertTrue("La venta requiere de registros para importar",siipap.size()>win.size());
		sync.sinconizar(dia);
		
		win=ventasDao.buscarVentas(dia);
		assertEquals(siipap.size(), win.size());
		
	}

	public void setSupport(VentasSupport support) {
		this.support = support;
	}

	public void setSync(VentasSync sync) {
		this.sync = sync;
	}

	public void setVentasDao(VentasDao ventasDao) {
		this.ventasDao = ventasDao;
	}
	
	

}
