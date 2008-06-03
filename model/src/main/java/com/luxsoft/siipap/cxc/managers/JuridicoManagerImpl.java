package com.luxsoft.siipap.cxc.managers;

import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.luxsoft.siipap.cxc.domain.ChequeDevuelto;
import com.luxsoft.siipap.cxc.domain.Juridico;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Implementacion de {@link JuridicoManager}
 * 
 * @author Ruben Cancino
 *
 */
public class JuridicoManagerImpl extends HibernateDaoSupport implements JuridicoManager{
	
	
	

	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void transferirJuridico(final Venta v,final Date fecha) {
		getSession().update(v);
		final Juridico j=new Juridico();
		j.setClaveAbogado(0);
		j.setComentarios("");
		j.setCreado(new Date());
		j.setFechaTraspaso(fecha);
		j.setMes(Periodo.obtenerMes(j.getFechaTraspaso())+1);
		j.setYear(Periodo.obtenerYear(j.getFechaTraspaso()));
		j.setNombreAbogado("SIN NOMBRE");
		j.setSaldoDoc(v.getSaldo().doubleValue());	
		j.setVenta(v);
		v.setOrigen("JUR");		
		getSession().update(v);
		getSession().saveOrUpdate(j);
		
	}

	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void transferirJuridico(NotaDeCredito cargo,final Date fecha) {
		getSession().update(cargo);
		final Juridico j=new Juridico();
		j.setClaveAbogado(0);
		j.setComentarios("");
		j.setCreado(new Date());
		j.setFechaTraspaso(fecha);
		j.setMes(Periodo.obtenerMes(j.getFechaTraspaso())+1);
		j.setYear(Periodo.obtenerYear(j.getFechaTraspaso()));
		j.setNombreAbogado("SIN NOMBRE");
		j.setSaldoDoc(cargo.getSaldoDelCargo());	
		j.setNota(cargo);
		cargo.setOrigen("JUR");
		for(NotasDeCreditoDet det:cargo.getPartidas()){
			det.setOrigen(cargo.getOrigen());
		}
		getSession().update(cargo);
		getSession().saveOrUpdate(j);		
	}
	
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void transferirJuridico(final ChequeDevuelto cheque,final Date fecha){
		final Juridico j=new Juridico();
		j.setClaveAbogado(0);
		j.setOrigen("CHE");
		j.setComentarios("CHEQUE DEVUELTO NUMERO: "+cheque.getNumero());
		j.setCreado(new Date());
		j.setFechaTraspaso(fecha);
		j.setMes(Periodo.obtenerMes(j.getFechaTraspaso())+1);
		j.setYear(Periodo.obtenerYear(j.getFechaTraspaso()));
		j.setNombreAbogado("SIN NOMBRE");
		j.setSaldoDoc(cheque.getSaldo().doubleValue());		
		cheque.setJuridico(true);
		j.setCheque(cheque);
		getSession().update(cheque);
		getSession().saveOrUpdate(j);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Juridico> buscarChequesDeJuridico(){
		return getHibernateTemplate().find("from Juridico j" +
				" left join fetch j.cheque ch" +
				" left join fetch j.cheque.cliente c" +
				" left join fetch j.nota nota " +
				" left join fetch j.nota.cliente c2" +
				"  where j.origen=\'CHE\'");
	}
	
	public static void main(String[] args) {
		ServiceLocator.getJuridicoManager().buscarChequesDeJuridico();
	}

		

}
