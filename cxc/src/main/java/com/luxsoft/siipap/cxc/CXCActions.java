package com.luxsoft.siipap.cxc;

import javax.swing.Action;

import org.springframework.util.StringUtils;

/**
 * Catalogo de acciones del modulo de CXC
 * 
 * @author Ruben Cancino
 *
 */
public enum CXCActions {
	
	NotasDeCreditoCreditoView("Vista de notas de credito y cargo"),
	EliminarNotasDeCredito("Eliminar notas de credito"),
	CrearNotaPorBonificacionCRE("Alta de N.C. por Bonificación CRE"),
	CrearNotaPorDevolucionCRE("Alta de N.C. por Devolución CRE"),
	CrearNotaPorDescuentoCRE("Alta de N.C. por Descuento CRE"),
	CrearCargoCRE("Alta de Cargo tipo CRE"),
	ActualizarVentasEnCobranza("Actualizar Ventas en Cobranza"),
	ShowAltA("Consulta rápida de clientes"),
	ReImprimirNotas("Re Imprimir notas de credito/cargo")
	,CarteraDeClientes("Mostrar cartera de clientes")
	,PagosAplicados("Lista de pagos aplicados")
	,OldPagosAplicados("Lista de pagos aplicados en ambos sistemas")
	,PagosAplicadosPorCliente("Lista de pagos aplicados por cliente")
	,ActualizarProvision("Actualizar las ventas y la provision")
	,ShowAnalisisDeCarteraView("Analisis de cartera de credito")
	,EliminarPagoM("Eliminar pago M")
	,EliminarPago("Eliminar pago aplicado")
	,MantenimientoDeClientesCredito("Mantenimiento al catalogo de clientes credito")
	,ImprimirNotasDescuento("Imprimir notas de descuento")
	,MostrarNotasDisponibles("Mostrar la lista de notas de credito disponibles para pagos")
	,MostrarPagosConDisponible("Mostrar pagos con disponible")
	,MostrarCXCView("Consulta de cuentas por cobrar")
	,GenerarNotaPorDevolucion("Generar nota de credito por devolución")
	,GenerarNotaPorBonificacion("Generar nota de crédito por bonificación")
	,GenerarNotaPorDesuentoFinanciero("Genera nota por descuento financiero")
	,EliminarProvision("Elimina los datos de provision y de ventas a credito")
	,ConsultaDeNotasDeCargo("Muestra la visata de notas de cargo")
	,GenerarDescuentosPorAnticipado("Generar descuento por anticipado ")
	,PagarCargoNoraml("Pagar Nota de cargo de forma normal")
	,PagarCargoConNota("Pagar Nota de Cargo con  nota de credito")
	,PagarCargoConDisponible("Pagar Nota de Cargo el disponible de otro pago y/o anticipo")
	,PagarDiferienciasCambiarias("Pago de diferencias cambiarias")
	,ActualizarComentariosDeVentasCredito("Actializa los comentario de las ventas a credito")
	,ActualizarVentas("Actualiza los datos de las ventas a credito")
	,ActualizarCargos("Actualiza los vencimientos de los cargos")
	,ReprogramarVentas("Repgrogramar pagos y revisiones para ventas")
	,ReprogramarVentasManual("Repgrogramar pagos y revisiones para ventas en grupo y manual")
	,EstadoDeCuentaReport("Estado de cuenta por cliente")
	,MandarRecordatorio("Mandar recordatorio de pago")
	,GenerarPolizaCredito1("Genera la poliza de credito tipo 1")
	,CancelarNotas("Cancelación de notas de credito y cargo")
	,CalcularComisionesVend("Aplicar comisiones para vendedores ")
	,CalcularComisionesCob("Aplicar comisiones para cobradores")
	,AplicarComisiones("Aplicar comisiones")
	,CancelarComisiones("Cancelar comisiones")
	,EliminarComisiones("Eliminar comisiones")
	,MostrarChequesView("Mostrar el mantenimiento de cheques devueltos")
	,RegistrarChequeDevuelto("Registrar un cheque devuelto")
	,CancelarChequeDevuelto("Cancelar un cheque devuelto")
	,PagarChequeDevuelto("Pago al cargo generado por un cheque devuelto")
	,PagoAutomaticoDeCargos("Pago automatico de cargos")
	,MostrarDepositos("Mantenimiento a depositos")
	,TransferirAJuridico("Transferir a jurídico")
	;
	
	
	private final String descripcion;
	
	private CXCActions(final String descripcion){
		this.descripcion=descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}
	
	public String getId() {
		return StringUtils.uncapitalize(name());
	}

	public void decorate(final Action action){
		action.putValue(Action.NAME, name());
		action.putValue(Action.SHORT_DESCRIPTION, getDescripcion());
		action.putValue(Action.LONG_DESCRIPTION, getDescripcion());
	}
	
	

}
