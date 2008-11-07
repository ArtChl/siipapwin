package com.luxsoft.siipap.em.replica.ventas;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;

public class MocomoMapper implements RowMapper{
	
	public String getPrefix(){
		return "MCM";
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		Venta venta=new Venta();
		venta.setOrigen("MOS");
		venta.setFechaReal(rs.getDate(getPrefix()+"FECREAL"));
		venta.setSucursal(rs.getBigDecimal(getPrefix()+"SUCURSA").intValue());
		venta.setNumero(rs.getBigDecimal(getPrefix()+"NUMDOCT").longValue());
		venta.setFecha(rs.getDate(getPrefix()+"FECHA"));		
		venta.setSerie(rs.getString(getPrefix()+"SERIEFA"));		
		venta.setTipo(rs.getString(getPrefix()+"TIPOFAC"));
		
		BigDecimal cant=rs.getBigDecimal(getPrefix()+"CANTIDA");
		if(cant!=null)
			venta.setCantidad(cant.doubleValue());
		
		venta.setClasificacion(rs.getBigDecimal(getPrefix()+"CLASCLI").intValue());
		String clave=rs.getString(getPrefix()+"CLAVCLI");		
		venta.setClave(clave);
		BigDecimal cortes=rs.getBigDecimal(getPrefix()+"CANTCOR");	
		
		venta.setCortes(cortes==null?0:cortes.intValue());
		venta.setDescuentoFacturado(convert(rs.getString(getPrefix()+"PORDESF")).doubleValue());
		venta.setDescuentoReal(convert(rs.getString(getPrefix()+"PORDESR")).doubleValue());
		
		
		
		venta.setImporteBruto(toCantidadMonetaria(rs.getBigDecimal(getPrefix()+"IMPARTI")));		
		venta.setImporteManiobras(toCantidadMonetaria(rs.getBigDecimal(getPrefix()+"IMPCONC")));
		venta.setPrecioCorte(toCantidadMonetaria(rs.getBigDecimal(getPrefix()+"IMPCORT")));
		venta.setSubTotal(toCantidadMonetaria(rs.getBigDecimal(getPrefix()+"SUBTOT")));
		venta.setImpuesto(toCantidadMonetaria(rs.getBigDecimal(getPrefix()+"IMPUEST")));
		venta.setTotal(toCantidadMonetaria(rs.getBigDecimal(getPrefix()+"TOTAL")));
		
		BigDecimal kil=rs.getBigDecimal(getPrefix()+"KGSFACT");
		if(kil!=null)		
			venta.setKilos(kil.doubleValue());
		venta.setNombre(rs.getString(getPrefix()+"NOMBCLI"));
		venta.setNombreSocio(rs.getString(getPrefix()+"NOMBSOC"));
		venta.setSocio(rs.getString(getPrefix()+"CLAVSOC"));
				
		BigDecimal ped=	rs.getBigDecimal(getPrefix()+"NODOCTO");
		if(ped!=null)
			venta.setPedido(ped.intValue());
		
		venta.setPedidoUsuario(rs.getString(getPrefix()+"NOMPEDI"));
		
		BigDecimal nf=rs.getBigDecimal(getPrefix()+"NOFISCA");
		if(nf!=null)
			venta.setNumeroFiscal(nf.intValue());
		
		venta.calcularImporteDeCorte();
		
		venta.setTipoDePago(rs.getString(getPrefix()+"FORMPAG"));
		
		venta.setFacturista(rs.getString(getPrefix()+"FACTURI"));
		venta.setSurtidor(rs.getString(getPrefix()+"SURTIO"));
		venta.setComentario(rs.getString(getPrefix()+"COMFAC"));
		venta.setFechaCancelacion(rs.getDate(getPrefix()+"FECHCAN"));
		venta.setCancelo(rs.getString(getPrefix()+"CANCELO"));
		venta.setMotivoCancelacion(rs.getString(getPrefix()+"MOTICAN"));
		
		
		BigDecimal ven=rs.getBigDecimal(getPrefix()+"VENDEDO");
		if(ven!=null)
			venta.setVendedor(ven.intValue());
		
		BigDecimal cob=rs.getBigDecimal(getPrefix()+"COBRADO");
		if(cob!=null)
			venta.setCobrador(cob.intValue());
		
		BigDecimal ch=rs.getBigDecimal(getPrefix()+"CHOFER");
		if(ch!=null)
			venta.setChofer(ch.intValue());
		
		//venta.setVencimiento(rs.getDate(getPrefix()+"VTO"));
		//venta.setDiaRevision(rs.getDate(getPrefix()+"DIAREVI"));
		//venta.setDiaPago(rs.getDate(getPrefix()+"DIAPAGO"));
		
		venta.calcularImporteDeCorte();
		//System.out.println("Venta generada: "+venta.getNumero());
		return venta;
	}
	
	private CantidadMonetaria toCantidadMonetaria(BigDecimal val){
		if(val!=null)
			return CantidadMonetaria.pesos(val.doubleValue());
		return CantidadMonetaria.pesos(0);
	}
	
	private BigDecimal convert(final String val){		
		if(val!=null)
			try{
				return new BigDecimal(val.trim());
			}catch(NumberFormatException nfe){
				//System.out.println("Valor invalido: "+val);
				return BigDecimal.ZERO;
			}
		return BigDecimal.ZERO;
	}

}
