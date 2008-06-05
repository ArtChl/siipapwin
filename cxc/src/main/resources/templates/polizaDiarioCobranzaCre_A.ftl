Ig
1
Cobranza de Credito ${fecha}

<#list bancos as banco>
${banco.cuentaContable}                , 0
${banco.concepto}
${banco.importeAsDouble?c},1.00
</#list>
206-0001-001                , 0
COBRANZA CREDITO ${fecha}

${ivaVentas?c}
206-0002-001                , 0
COBRANZA CREDITO ${fecha}
${ivaVentas?c}
902-0003-000                , 0
ACUMULABLE IETU (CRE)
${ietu?c}
903-0003-000                , 0
IETU ACUMULABLE (CRE) 

${ietu?c}
<#list tarjetas as pago>
203-D002-000                , 0
Acredores diversos ${pago.cuenta?substring(4,8)}
${pago.importeAsDouble?c},1.00
</#list>
<#list pagos as pago>
${pago.cuenta}                , 0
PAGO DE FACTURA No ${pago.numero?c}

${pago.importeAsDouble?c},1.00
</#list>
<#list pagosConOtros as pago>
${pago.cuenta}                , 0
PAGO DE FAC ${pago.numero?c} (Otros)

${pago.importeAsDouble?c},1.00
</#list>
<#list menos as pago>
${pago.cuenta}                , 0
Otros Gastos D ${pago.cuenta?substring(4,8)}

${pago.importeAsDouble?c},1.00
</#list>  
<#list notas as nota>
${nota.cuenta}                , 0
NC ${nota.tipo} ${nota.numero?c} Fac: ${nota.numDocumento?c}

${nota.importeSinIva?c},1.00
</#list>
<#list cargos as nota>
${nota.cuenta}                , 0
Nota de Cargo M ${nota.numero?c}
${nota.importeSinIva?c},1.00
</#list>
701-0002-001                , 0
Intereses Moratorios

${interesesMoratorios?c}
206-0002-004                , 0
Iva otros ingresos pend  trasladar

${ivaEnOtrosIngresos?c}
<#list saldos as pago>
702-0002-000                , 0
Otros Ingresos ${pago.cuenta?substring(4,8)}

${pago.saldoAsDouble?c},1.00
</#list>
<#list acredores as pago>
203-D002-000                , 0
Acredores diversos ${pago.cuenta?substring(4,8)}

${pago.saldoAsDouble?c},1.00
</#list>
<#list otros as pago>
203-D002-000                , 0
Pago con saldo S ${pago.cuenta?substring(4,8)}
${pago.importeAsDouble?c},1.00
</#list>
<#list menos as pago>
704-0001-000                , 0
Otros Gastos D ${pago.cuenta?substring(4,8)}
${pago.importeAsDouble?c},1.00
</#list>
206-0001-001
Iva en ventas por otros ingresos

${ivaVentaOtrosIngresos?c},1.00
206-0002-001
Iva en ventas pendiente
${ivaVentaOtrosIngresos?c},1.00
<#list descuentos as desc>
${desc.cuenta}              , 0
Descuentos sobre ventas
${desc.importeAsDouble?c},1.00
</#list>
<#list devos as dev>
${dev.cuenta}               , 0
Descuentos sobre ventas
${dev.importeAsDouble?c},1.00
</#list>
206-0002-003                , 0
Iva descuentos
${ivaDesc?c}
206-0002-002                , 0
Iva devoluciones
${ivaDevos?c}
800-0000-000                , 0
Cuenta de cuadre
${cuadre?c},1.00
FIN

