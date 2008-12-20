select a.origen,a.venta_id,b.fecha as FECHA_FAC,b.COBRADOR,b.sucursal,a.clave,b.nombre,a.NUMERO,b.serie,b.tipo,b.total
,nvl((select sum(x.importe) from sw_notasdet x where x.venta_id=a.venta_id and x.tipo<>'M' and x.nota_id in(select xx.nota_id from sw_notas xx where xx.nota_id=x.nota_id and xx.aplicable=0) ),0) as NOTAS_APLICADAS
,b.total+nvl((select sum(x.importe) from sw_notasdet x where x.venta_id=a.venta_id and x.tipo<>'M' and x.nota_id in(select xx.nota_id from sw_notas xx where xx.nota_id=x.nota_id and xx.aplicable=0) ),0) as VENTA_NETA
,(select sum(x.importe) from sw_pagos x where x.venta_id=a.venta_id) as pagos
,(select sum(x.importe) from sw_pagos x where x.venta_id=a.venta_id) as P_COMISIONABLE
,b.total+nvl((select round(sum(x.importe),2) from sw_notasdet x where x.venta_id=a.venta_id and x.tipo<>'M' and x.nota_id in(select xx.nota_id from sw_notas xx where xx.nota_id=x.nota_id and xx.aplicable=0) ),0)-(select round(sum(x.importe),2) from sw_pagos x where x.venta_id=a.venta_id) as SALDO
,case when a.origen<>'CRE' then max(a.fecha) else max((select x.vencimiento     from SW_VENTASCREDITO x where a.venta_id=x.venta_id))  end as VENCIMIENTO
,max(a.fecha) as FECHA_PAG
,ROUND(case when a.origen<>'CRE' then 1 else (select x.descuento     from SW_VENTASCREDITO x where a.venta_id=x.venta_id)  end,2) as DESCUENTO
,max(case when a.origen<>'CRE' then 0 else (select round(a.fecha-x.vencimiento,0)     from SW_VENTASCREDITO x where a.venta_id=x.venta_id)  end) as ATRASO
,CASE WHEN a.CLAVE='U050008' THEN 0.05 ELSE 0.12 END as COMISION
,b.CANCELCOMIVENT,case when b.PAGOCOMICOB is not null  then 1 else 0 end as APLICADO
,b.IMPCOMICOB as IMPORTE
from sw_pagos a join sw_ventas b on(a.venta_id=b.venta_id)
where a.year>=2007 
and a.FORMADEPAGO  in('H','E','C','B','Q','Y','N','O') 
and a.TIPODOCTO<>'M' 
AND A.ORIGEN='CRE'
and a.fecha between ? and ?
and b.cobrador in(select x.clave from SW_COBRADOR x where x.activo=1)
GROUP BY a.origen,a.venta_id,b.fecha,b.COBRADOR,b.sucursal,a.clave,b.nombre,a.NUMERO,b.serie,b.tipo,b.total,b.CANCELCOMIVENT,b.PAGOCOMICOB,b.IMPCOMICOB
order by b.COBRADOR,a.clave