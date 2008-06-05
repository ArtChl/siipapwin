package com.luxsoft.siipap.em.replica.legacy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.em.replica.service.ServiceManager;

@SuppressWarnings("unchecked")
public class DdocreReplicator extends BasicReplicator{
	
	

	public DdocreReplicator() {
		setClazz(Ddocre.class);
		setTabla("DDOCRE");
	}

	@Override
	public int bulkImportarMes(Periodo mes) {
		logger.debug("Importando registros "+getTabla()+" para: "+mes);
		
		
		int importados=0;
		String sql="select * from "+ReplicationUtils.resolveTable("MOVCRE", mes.getFechaFinal());
		sql+=" where MCRIDENOPE=3 and (MCRSERIEFA=\'U\' or  MCRSERIEFA=\'V\')";
		//System.out.println(sql);
		List<Map> registros=getFactory().getJdbcTemplate(mes).queryForList(sql);
		List beans=new ArrayList();
		for(Map row:registros){
			Date fecha=(Date)row.get("MCRFECHA");
			String cliente=(String)row.get("MCRCLAVCLI");
			Number numero=(Number)row.get("MCRNUMDOCT");
			numero=numero!=null?numero:new Long(0);
			Number grupo=((Number)row.get("MCRNCRPROV"));
			grupo=grupo!=null?grupo:new Long(0);
			String sql2="select * from "+ReplicationUtils.resolveTable(getTabla(), mes.getFechaFinal());
			sql2+=" where DCRNUMERO=@NUMERO and DCRGRUPO=@GRUPO";
			sql2=sql2.replaceAll("@NUMERO", String.valueOf(numero.longValue()));
			sql2=sql2.replaceAll("@GRUPO", String.valueOf(grupo));
			System.out.println(" SQL2: "+sql2);
			List<Map> ddocres=getFactory().getJdbcTemplate(mes).queryForList(sql2);
			logger.debug("ddocres encontrados:" +ddocres.size());
			for(Map rr:ddocres){
				Ddocre bean=(Ddocre)ReplicationUtils.transformarRegistro(rr, getClazz());
				bean.setMES(Periodo.obtenerMes(mes.getFechaFinal())+1);
				bean.setYEAR(Periodo.obtenerYear(mes.getFechaFinal()));
				bean.setMCRFECHA(fecha);
				bean.setMCRCLAVCLI(cliente);
				beans.add(bean);
			}
			
		}
		logger.debug("Beans creados: "+beans.size());
		importados=bulkSave(beans);
		logger.debug("Beans Importados: "+beans.size());
		return importados;
	}

	public static void main(String[] args) {
		//Periodo p=Periodo.getPeriodoDelYear(2006);
		try {
			Periodo p=new Periodo("01/01/2006","31/12/2006");
			ServiceManager.instance().getDdocreReplicator().bulkImport(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	

}
