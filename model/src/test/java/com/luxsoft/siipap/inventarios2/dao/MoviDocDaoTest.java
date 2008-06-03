package com.luxsoft.siipap.inventarios2.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.SessionFactory;

import com.luxsoft.siipap.dao.AbstractDaoTest;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.inventarios2.domain.Concepto;
import com.luxsoft.siipap.inventarios2.domain.MoviDet;
import com.luxsoft.siipap.inventarios2.domain.MoviDoc;

/**
 * Integration test para MoviDao 
 * 
 * @author Ruben Cancino
 *
 */
public class MoviDocDaoTest extends AbstractDaoTest{
	
	private MoviDao dao;
	private ConceptoDao cdao;
	private SessionFactory factory;
	
	
	public void testAddDelete(){
		List<Concepto> cc=cdao.buscarManuales();
		for(Concepto c:cc){
			List<Articulo> arts=buscarArticulos();
			
			final MoviDoc mov=new MoviDoc();
			mov.setComentario("Pruebas");
			mov.setSucursal(10);
			mov.setConcepto(c);
			mov.setClaveConcepto(c.getClave());
			
			for(Articulo a:arts){
				MoviDet det=new MoviDet();
				det.setArticulo(a);
				det.setClave(a.getClave());
				BigDecimal canti=mov.getConcepto().getTipo().equals("E")?BigDecimal.valueOf(10):BigDecimal.valueOf(-10);
				det.setCantidad(canti);
				det.setUnixuni(1000);
				mov.agregarPartida(det);
			}
			
			dao.salvar(mov);
		}
		
		setComplete();
	}
	
	@SuppressWarnings("unchecked")
	private List<Articulo> buscarArticulos(){
		return factory.getCurrentSession()
		.createQuery("from Articulo ").setMaxResults(20).list();
	}

	public void setDao(MoviDao dao) {
		this.dao = dao;
	}

	public void setCdao(ConceptoDao cdao) {
		this.cdao = cdao;
	}

	public void setFactory(SessionFactory factory) {
		this.factory = factory;
	}
	
	
	
	
	

}
