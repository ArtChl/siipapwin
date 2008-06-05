package com.luxsoft.siipap.cxc.model;



import java.util.List;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoConNota;
import com.luxsoft.siipap.cxc.domain.PagoConOtros;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;

public class PagosFactoryImpl  implements PagosFactory{

	
	

	/**
	 * Genera un PagoM para ventas con saldo menor o igual a 100 pesos
	 * 
	 * @param ventas
	 * @return
	 */
	public PagoM crearPagoAutomatico(List<Venta> ventas) {		
		Assert.notEmpty(ventas, "La collecion de ventas debe tener elementos");
		CXCFiltros.filtrarParaPagoAutomatico(ventas);
		if(ventas.isEmpty())
			return null;
		final String tipo=ventas.get(0).getTipo();
		if(ventas.isEmpty())
			return null;
		CXCFiltros.filtrarVentasParaUnTipo(ventas, tipo);
		final PagoM pago=new PagoM();
		pago.setFormaDePago(FormaDePago.D);
		pago.setTipoDeDocumento(tipo);
		pago.setCliente(ventas.get(0).getCliente());
		CantidadMonetaria total=CantidadMonetaria.pesos(0);
		for(Venta v:ventas){
			final CantidadMonetaria importe=v.getSaldoEnMoneda();
			pago.aplicarPago(v, importe);
			total=total.add(importe);
		}
		pago.setImporte(total);
		//pago.calcularDisponible();
		return pago;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.model.PagosFactory#crearPagoAutomaticoParaNotaDeCargo(java.util.List)
	 */
	public PagoM crearPagoAutomaticoParaNotaDeCargo(List<NotaDeCredito> cargos) {
		Assert.notEmpty(cargos, "La collecion de cargos debe tener elementos");		
		
		final PagoM pago=crearPago(cargos.get(0).getCliente(), cargos);
		pago.setFormaDePago(FormaDePago.D);
		CantidadMonetaria total=CantidadMonetaria.pesos(0);
		for(NotaDeCredito c:cargos){
			final CantidadMonetaria importe=c.getSaldoDelCargoEnMoneda();
			pago.aplicarPago(c, importe);
			total=total.add(importe);
		}
		pago.setImporte(total);
		return pago;
	}



	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.model.PagosFactory#crearPagoParaDiferienciaCambiaria(java.util.List)
	 */
	public PagoM crearPagoParaDiferienciaCambiaria(final List<Venta> ventas){
		Assert.notEmpty(ventas, "La collecion de ventas debe tener elementos");
		if(ventas.isEmpty())
			return null;
		final String tipo=ventas.get(0).getTipo();
		final PagoM pago=new PagoM();
		pago.setFormaDePago(FormaDePago.F);
		pago.setTipoDeDocumento(tipo);
		pago.setCliente(ventas.get(0).getCliente());
		pago.setComentario("DIFERENCIA CAMBIARA DE MENOS");
		CantidadMonetaria total=CantidadMonetaria.pesos(0);
		for(Venta v:ventas){
			final CantidadMonetaria importe=v.getSaldoEnMonedaSinImportaroSigno();
			pago.aplicarPago(v, importe);
			total=total.add(importe);
		}
		pago.setImporte(total);		
		return pago;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.model.PagosFactory#crearPagoParaDiferienciaCambiaria(com.luxsoft.siipap.cxc.domain.PagoM)
	 */
	public PagoConOtros crearPagoParaDiferienciaCambiaria(final PagoM origen){
		Assert.notNull(origen, "El pago no debe ser nulo");
		Assert.isTrue(origen.getDisponible().doubleValue()>0);
		final String tipo=origen.getTipoDeDocumento();		
		final CantidadMonetaria total=origen.getDisponibleAsMoneda();
		
		final PagoConOtros pago=new PagoConOtros();
		pago.setOrigen(origen);
		pago.setReferencia(origen.getId().toString());
		pago.setCliente(origen.getCliente());		
		pago.setTipoDeDocumento(tipo);
		pago.setCliente(origen.getCliente());		
		pago.setFormaDePago(FormaDePago.F);				
		pago.setComentario("DIFERENCIA CAMBIARA DE MAS");
				
		pago.setImporte(total);
		return pago;
	}
	
	/**
	 * Genera un {@link PagoConOtros} para un grupo de ventas
	 * 
	 * @param origen
	 * @param ventas
	 * @return
	 */
	public PagoConOtros crearPago(final PagoM origen,final List<Venta> ventas){
		Assert.notEmpty(ventas, "La collecion de ventas debe tener elementos");
		Assert.isTrue(origen.getDisponible().doubleValue()>0,"El pago origen debe tener disponible");
		CXCFiltros.filtrarVentasConSaldo(ventas);
		if(ventas.isEmpty())
			return null;
		final String tipo=ventas.get(0).getTipo();
		if(ventas.isEmpty())
			return null;
		CXCFiltros.filtrarVentasParaUnTipo(ventas, tipo);
		final PagoConOtros pago=new PagoConOtros();
		pago.setOrigen(origen);
		pago.setReferencia(origen.getId().toString());
		pago.setCliente(origen.getCliente());
		pago.setFormaDePago(FormaDePago.S);
		pago.setTipoDeDocumento(tipo);
		pago.setCliente(ventas.get(0).getCliente());
		for(Venta v:ventas){
			pago.aplicarPago(v, CantidadMonetaria.pesos(0));
		}
		return pago;
	}

	/**
	 * Genera un {@link PagoConNota} para un grupo de ventas
	 * 
	 * @param nota
	 * @param ventas
	 * @return
	 */
	public PagoConNota crearPagoConNota(NotaDeCredito nota, List<Venta> ventas) {
		Assert.notEmpty(ventas, "La collecion de ventas debe tener elementos");
		Assert.isTrue(nota.getSaldo()<0,"La nota origen debe tener disponible");
		CXCFiltros.filtrarVentasConSaldo(ventas);
		if(ventas.isEmpty())
			return null;
		final String tipo=ventas.get(0).getTipo();
		if(ventas.isEmpty())
			return null;
		CXCFiltros.filtrarVentasParaUnTipo(ventas, tipo);
		final PagoConNota pago=new PagoConNota();
		pago.setNota(nota);
		pago.setReferencia(nota.getTipo()+" "+nota.getNumero());
		pago.setCliente(nota.getCliente());
		pago.setFormaDePago(FormaDePago.T);
		pago.setTipoDeDocumento(tipo);
		pago.setCliente(ventas.get(0).getCliente());
		for(Venta v:ventas){
			Pago pp=pago.aplicarPago(v, CantidadMonetaria.pesos(0));
			pp.setNotaDelPago(nota);
			pp.setFormaDePago2(pago.getFormaDePago());
			pp.setDescFormaDePago(pago.getFormaDePago().getDesc());
			pp.setReferencia(nota.getTipo()+" "+nota.getNumero());
		}
		return pago;
	}
	
	/**
	 * Genera un {@link PagoConNota} para un grupo de cargos
	 * 
	 * @param nota
	 * @param ventas
	 * @return
	 */
	public PagoConNota crearPagoDeCargoConNota(final NotaDeCredito disponible, List<NotaDeCredito> cargos) {
		Assert.notEmpty(cargos, "La collecion de cargos debe tener elementos");
		Assert.isTrue(disponible.getSaldo()<0,"La nota origen debe tener disponible el disponible es: "+disponible.getSaldo());
		
		final PagoConNota pago=new PagoConNota();
		pago.setNota(disponible);
		pago.setReferencia(disponible.getId().toString());
		pago.setCliente(disponible.getCliente());
		pago.setFormaDePago(FormaDePago.T);
		pago.setTipoDeDocumento("M");
		pago.setCliente(cargos.get(0).getCliente());
		
		CollectionUtils.forAllDo(cargos, new Closure(){
			public void execute(Object input) {
				NotaDeCredito cargo=(NotaDeCredito)input;
				Pago p=new Pago();
				p.setCliente(pago.getCliente());
				p.setClave(pago.getClave());
				p.setPagoM(pago);
				p.setNota(cargo);
				p.setNotaDelPago(disponible);
				pago.getPagos().add(p);				
				p.setOrigen(cargo.getOrigen());
				p.setTipoDocto(cargo.getTipo());
				p.setNumero(cargo.getNumero());
				p.setFormaDePago2(pago.getFormaDePago());
				p.setDescFormaDePago(pago.getFormaDePago().getDesc());
				p.setReferencia(disponible.getTipo()+" "+disponible.getNumero());
				p.setSucursal(1);
			}			
			
		});
		
		return pago;
	}
	
	/**
	 * Genera un pago para el grupo de cargos indicado
	 * 
	 * @param c
	 * @param cargos
	 * @return
	 */
	public PagoM crearPago(final Cliente c,final List<NotaDeCredito> cargos){
		Assert.notEmpty(cargos, "La collecion de cargos debe tener elementos");
		final PagoM pago=new PagoM();
		pago.setCliente(c);
		pago.setClave(c.getClave());
		pago.setTipoDeDocumento("M");
		CollectionUtils.forAllDo(cargos, new Closure(){

			public void execute(Object input) {
				NotaDeCredito cargo=(NotaDeCredito)input;
				Pago p=new Pago();
				p.setCliente(pago.getCliente());
				p.setClave(pago.getClave());
				p.setPagoM(pago);
				p.setNota(cargo);
				pago.getPagos().add(p);
				
				p.setOrigen(cargo.getOrigen());
				p.setTipoDocto(cargo.getTipo());
				p.setNumero(cargo.getNumero());
				p.setSucursal(1);
			}			
			
		});
		return pago;
		
	}

	public PagoConOtros crearPagoDeCargo(PagoM origen, List<NotaDeCredito> cargos) {
		Assert.notEmpty(cargos, "La collecion de cargos debe tener elementos");
		Assert.isTrue(origen.getDisponible().doubleValue()>0,"El pago origen debe tener disponible");
		CXCFiltros.filtrarCargosConSaldo(cargos);
		if(cargos.isEmpty())
			return null;
		final PagoConOtros pago=new PagoConOtros();
		pago.setOrigen(origen);
		pago.setReferencia(origen.getId().toString());
		pago.setCliente(origen.getCliente());
		pago.setFormaDePago(FormaDePago.S);
		pago.setTipoDeDocumento(cargos.get(0).getTipo());
		pago.setCliente(cargos.get(0).getCliente());
		for(NotaDeCredito c:cargos){
			Pago pp=pago.aplicarPago(c, CantidadMonetaria.pesos(0));
			pp.setSucursal(1);
		}
		return pago;
	}

	
}
