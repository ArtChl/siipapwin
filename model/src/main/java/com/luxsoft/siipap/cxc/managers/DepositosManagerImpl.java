package com.luxsoft.siipap.cxc.managers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.dao.PagoMDao;
import com.luxsoft.siipap.cxc.domain.Deposito;
import com.luxsoft.siipap.cxc.domain.DepositoUnitario;
import com.luxsoft.siipap.dao2.UniversalDao;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.DateUtils;

public class DepositosManagerImpl extends HibernateDaoSupport implements DepositosManager{
	
	private UniversalDao universalDao;
	private PagoMDao pagoMDao;
	private String destino;
	
	
		
	
	public void salvarDepositos(List<Deposito> depositos,final Date fecha) {
		for(Deposito d:depositos){
			d.setFecha(fecha);
			d.actualizarDatos();
			getUniversalDao().save(d);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Deposito> generaDepositosCheque(final List<DepositoUnitario> depositos) {
		
		final List<DepositoUnitario> bancoDestino=(List<DepositoUnitario>) CollectionUtils.select(depositos, new Predicate(){
			public boolean evaluate(Object object) {
				DepositoUnitario d=(DepositoUnitario)object;
				return d.getBanco().startsWith(getDestino());
			}
			
		});
		
		final List<DepositoUnitario> otrosBancos=(List<DepositoUnitario>) CollectionUtils.select(depositos, new Predicate(){
			public boolean evaluate(Object object) {
				DepositoUnitario d=(DepositoUnitario)object;
				return !d.getBanco().startsWith(getDestino());
			}
			
		});
		
		final List<Deposito> res=crearDepositos(bancoDestino);
		res.addAll(crearDepositos(otrosBancos));
		return res;
		
	}
	
	private List<Deposito> crearDepositos(final List<DepositoUnitario> deps){
		final List<Deposito> depositos=new ArrayList<Deposito>();
		int count=0;
		Deposito deposito=new Deposito();
		for(DepositoUnitario dd:deps){
			deposito.getPartidas().add(dd);			
			count++;
			if(count%5==0){
				deposito.actualizarImporte();
				depositos.add(deposito);				
				deposito=new Deposito();				
			}
		}
		deposito.actualizarImporte();
		depositos.add(deposito);
		return depositos;
	}
	
	
	public String getDestino() {
		return destino;
	}
	public void setDestino(String destino) {
		this.destino = destino;
	}


	public UniversalDao getUniversalDao() {
		return universalDao;
	}
	public void setUniversalDao(UniversalDao universalDao) {
		this.universalDao = universalDao;
	}

	public PagoMDao getPagoMDao() {
		return pagoMDao;
	}
	public void setPagoMDao(PagoMDao pagoMDao) {
		this.pagoMDao = pagoMDao;
	}
	
	
	public static void main(String[] args) {
		final DepositosManager manager=(DepositosManager)ServiceLocator.getDaoContext().getBean("depositosManager");
		final List<DepositoUnitario> unitarios=new ArrayList<DepositoUnitario>();
		for (int i=0;i<14;i++){
			DepositoUnitario du=new DepositoUnitario("BANAMEX",i+500,BigDecimal.valueOf(350));
			unitarios.add(du);
		}
		for (int i=0;i<7;i++){
			DepositoUnitario du=new DepositoUnitario("SCOTIA",i+500,BigDecimal.valueOf(350));
			unitarios.add(du);
		}
		final List<Deposito> res=manager.generaDepositosCheque(unitarios);
		manager.salvarDepositos(res, DateUtils.obtenerFecha("14/11/2007"));
	}
	

}
