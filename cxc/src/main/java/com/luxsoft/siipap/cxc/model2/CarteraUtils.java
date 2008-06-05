package com.luxsoft.siipap.cxc.model2;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.services.ServiceLocator;

public class CarteraUtils {
	
	@SuppressWarnings("unchecked")
	public void actualizarCliente(final ClienteCredito cc){
		List<Map<String, Object>> rows = ServiceLocator.getJdbcTemplate()
		.queryForList(
				"select clave,nombre,vendedor,ATRASO_MAX,ULTIMAVENTA,VTA_NETA_CRE,pagos,saldo,SALDO_VENC,FACTS_VENC,ULTIMOPAGO from V_CLIENTESCRE" +
				" where clave=?",new Object[]{cc.getClave()},new int[]{Types.VARCHAR});
		if(!rows.isEmpty()){			
			Map<String, Object> row =rows.get(0);
			final String clave = (String) row.get("clave");
			Assert.isTrue(clave.equals(cc.getClave()));
			int atraso = ((BigDecimal) row.get("ATRASO_MAX")).intValue();
			Date uv = (Date) row.get("ULTIMAVENTA");
			final BigDecimal venta = ((BigDecimal) row.get("VTA_NETA_CRE"));
			final BigDecimal pagos = ((BigDecimal) row.get("pagos"));
			final BigDecimal saldo = ((BigDecimal) row.get("saldo"));
			final BigDecimal saldov = ((BigDecimal) row.get("SALDO_VENC"));
			int vencidas = ((BigDecimal) row.get("FACTS_VENC")).intValue();
			final Date ultimo = ((Date) row.get("ULTIMOPAGO"));
			cc.setAtrasoMaximo(atraso);
			cc.setPagos(pagos);
			cc.setSaldo(saldo);
			cc.setSaldoVencido(saldov);
			cc.setFacturasVencidas(vencidas);
			cc.setUltimoPago(ultimo);
			cc.setVentaNeta(CantidadMonetaria.pesos(venta.doubleValue()));
			cc.setUltimaVenta(uv);			
		}
		
		ServiceLocator.getCXCManager().actualizarCliente(cc);
	}
	
}
