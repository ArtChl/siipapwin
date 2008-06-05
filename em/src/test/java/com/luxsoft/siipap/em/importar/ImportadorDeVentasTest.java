package com.luxsoft.siipap.em.importar;

import java.util.List;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.em.replica.ventas.MovcreMapper;

import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

public class ImportadorDeVentasTest extends AbstractImportadoTest{
	
	private ImportadorDeVentas importador;
	private MovcreMapper mapper=new MovcreMapper();
	
	@SuppressWarnings("unchecked")
	public void testImportarVentas(){
		final Periodo p=new Periodo("03/05/2007","03/05/2007");
		String sql=ReplicationUtils.resolveSQL(p, "MOVCRE", "MCRFECHA")+" and MCRIDENOPE=1";
		System.out.println(sql);
		List<Venta> ventas=factory.getJdbcTemplate(2007).query(sql, mapper);
		for(Venta v:ventas){
			v.setYear(2007);
			v.setMes(5); 
		}
		System.out.println("Ventas localizadas en SIIPAP: "+ventas.size());
		for(int i=0;i<=2;i++){
			Venta v=ventas.get(i);
			importador.importar(v);
			assertFalse(v.getPartidas().isEmpty());
			assertNotNull(v.getCliente());
			for(VentaDet det:v.getPartidas()){
				assertNotNull(det.getArticulo());
				assertEquals(v.getYear(), det.getYear());
				assertEquals(v.getMes(), det.getMes());
			}
		}
				
	}
	
	
	public void setImportador(ImportadorDeVentas importador) {
		this.importador = importador;
	}

	
	
	

}
