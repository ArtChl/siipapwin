package com.luxsoft.siipap.em.replica.catalogos;

import java.util.List;

import com.luxsoft.siipap.cxc.dao.DescuentoPorVolumenDao;
import com.luxsoft.siipap.cxc.domain.DescuentoPorVolumen;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.AbstractReplicatorSupport;
import com.luxsoft.siipap.em.replica.DefaultMapper;
import com.luxsoft.siipap.em.replica.Replicador;
import com.luxsoft.siipap.em.replica.Replicadores;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;
import com.luxsoft.siipap.em.replica.service.ServiceManager;

@SuppressWarnings("unchecked")
public class DescuentoPorVolumenReplicator extends AbstractReplicatorSupport{
	
	
	public List importar(Periodo periodo) {
		getDescDao().eliminarDescuentos(getYear(periodo), getMes(periodo));
		List<DescuentoPorVolumen> desc=cargarDescuentos(periodo);
		for(DescuentoPorVolumen d:desc){
			d.setOrigen("CRE");			
			System.out.println(d);
			persistir(d);
		}
		return desc;
	}

	public List<ReplicaLog> validar(Periodo periodo) {
		
		return null;
	}

	public void bulkImport(Periodo p) {
		//No soportado
	}
	
	@Override
	public void persistir(Object bean) {
		
		getDescDao().salvar((DescuentoPorVolumen)bean);
	}
	
	private DescuentoPorVolumenDao getDescDao(){
		return (DescuentoPorVolumenDao)getDao();
	}
	
	
	private List<DescuentoPorVolumen> cargarDescuentos(Periodo p){
		//Importar los descuentos por credito
		
		String sql="select * from TABDESCR";
		DefaultMapper mapper=new DefaultMapper();
		mapper.setBeanClass(getBeanClass());
		mapper.setPropertyColumnMap(getPropertyColumnMap());
		List<DescuentoPorVolumen> desc=getFactory().getJdbcTemplate(p).query(sql,mapper);
		injectYearMonth(p, desc);
		return desc;
	}
	
	public static void main(String[] args) {
		Replicador r=ServiceManager.instance().getReplicador(Replicadores.DescuentosPorVolReplicator);		
		r.importar(new Periodo("01/02/2007","01/04/2007"));
	}
	

}
