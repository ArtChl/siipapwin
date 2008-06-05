package com.luxsoft.siipap.cxc.model2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.luxsoft.siipap.cxc.domain.ComisionDeVentas;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.SQLUtils;

/**
 * 
 * @author Ruben Cancino
 *
 */
public class ComisionDao {
	
	@SuppressWarnings("unchecked")
	public List<ComisionDeVentas> buscarComisionesVendedor(final Periodo p){
		final String sql=SQLUtils.loadSQLQueryFromResource("sql/comisiones_vendedor.sql");
		Object[] vals={p.getFechaInicial(),p.getFechaFinal()};
		int[] tipos={Types.DATE,Types.DATE};		
		List coms=ServiceLocator.getJdbcTemplate().query(sql, vals, tipos, new VentasComisionMapper("vendedor"));
		return coms;
	}
	
	@SuppressWarnings("unchecked")
	public List<ComisionDeVentas> buscarComisionesCobrador(final Periodo p){
		final String sql=SQLUtils.loadSQLQueryFromResource("sql/comisiones_cobrador.sql");
		Object[] vals={p.getFechaInicial(),p.getFechaFinal()};
		int[] tipos={Types.DATE,Types.DATE};		
		List coms=ServiceLocator.getJdbcTemplate().query(sql, vals, tipos, new VentasComisionMapper("cobrador"));
		return coms;
	}
	
	private class VentasComisionMapper implements RowMapper{
		
		private final String target;
		
		public VentasComisionMapper(final String target){
			this.target=target;
		}

		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			final ComisionDeVentas c=new ComisionDeVentas();
			if("vendedor".equals(target))
				c.setVendedor(rs.getInt("VENDEDOR"));
			else
				c.setCobrador(rs.getInt("COBRADOR"));			
			c.setVenta(rs.getLong("VENTA_ID"));
			c.setOrigen(rs.getString("ORIGEN"));
			c.setFechaPago(rs.getDate("FECHA_FAC"));
			c.setSucursal(rs.getInt("SUCURSAL"));
			c.setClave(rs.getString("CLAVE"));
			c.setNombre(rs.getString("NOMBRE"));
			c.setFechaVenta(rs.getDate("FECHA_PAG"));
			c.setNumero(rs.getLong("NUMERO"));
			c.setSerie(rs.getString("SERIE"));
			c.setTipo(rs.getString("TIPO"));
			c.setTotal(rs.getBigDecimal("TOTAL"));
			c.setNotasAplicadas(rs.getBigDecimal("NOTAS_APLICADAS"));
			c.setVentaNeta(rs.getBigDecimal("VENTA_NETA"));
			c.setPagos(rs.getBigDecimal("PAGOS"));
			c.setPagosComisionables(rs.getBigDecimal("P_COMISIONABLE"));
			c.setSaldo(rs.getBigDecimal("SALDO"));
			c.setVencimiento(rs.getDate("VENCIMIENTO"));
			c.setDescuento(rs.getDouble("DESCUENTO"));
			c.setAtraso(rs.getInt("ATRASO"));
			c.setComision(rs.getDouble("COMISION"));			
			c.setCancelComentario(rs.getString("CANCELCOMIVENT"));
			c.setAplicado(rs.getBoolean("APLICADO"));
			c.setImporte(rs.getBigDecimal("IMPORTE"));
			return c;
		}
		
	};
	
	public static void main(String[] args) {
		ComisionDao dao=new ComisionDao();
		List<ComisionDeVentas> coms=dao.buscarComisionesVendedor(Periodo.periodoDeloquevaDelMes());
		System.out.println("Comisiones: "+coms.size());
	}


}
