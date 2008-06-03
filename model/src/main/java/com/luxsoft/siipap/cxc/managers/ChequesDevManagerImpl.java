package com.luxsoft.siipap.cxc.managers;

import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.luxsoft.siipap.cxc.domain.ChequeDevuelto;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;

public class ChequesDevManagerImpl extends HibernateDaoSupport implements ChequesDevManager{
	
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public ChequeDevuelto salvar(final ChequeDevuelto ch){		
		final Date fecha=ch.getFecha();
		ch.setCreado(new Date());		
		ch.setYear(Periodo.obtenerYear(fecha));
		ch.setMes(Periodo.obtenerMes(fecha)+1);
		getHibernateTemplate().saveOrUpdate(ch);		
		return (ChequeDevuelto)getHibernateTemplate().merge(ch);
	}

	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void cancelar(final ChequeDevuelto ch) {
		getSession().delete(ch);
	}
	
	public ChequeDevuelto refresh(final ChequeDevuelto ch){
		return (ChequeDevuelto)getHibernateTemplate().get(ChequeDevuelto.class, ch.getId());
	}
	
	@SuppressWarnings("unchecked")
	public List<Pago> buscarPagosDeCheques(){
		return getHibernateTemplate().find("from Pago p " +
				" left join fetch p.pagoM " +
				" left join fetch p.cheque " +
				" left join fetch p.cliente c" +
				" where p.origen=\'CHE\' and p.year>=2008");
	}
	
	
	public static void main(String[] args) {
		List<Pago> pagos=ServiceLocator.getChequesDevManager().buscarPagosDeCheques();
		for(Pago p:pagos){
			System.out.println(p);
		}
	}

}
