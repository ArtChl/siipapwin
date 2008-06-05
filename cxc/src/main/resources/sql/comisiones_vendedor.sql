select a.origen,a.venta_id,b.fecha as FECHA_FAC,b.vendedor,b.sucursal,a.clave,b.nombre,a.NUMERO,b.serie,b.tipo,b.total
,nvl((select sum(x.importe) from sw_notasdet x where x.venta_id=a.venta_id and x.tipo<>'M' and x.nota_id in(select xx.nota_id from sw_notas xx where xx.nota_id=x.nota_id and xx.aplicable=0) ),0) as NOTAS_APLICADAS
,b.total+nvl((select sum(x.importe) from sw_notasdet x where x.venta_id=a.venta_id and x.tipo<>'M' and x.nota_id in(select xx.nota_id from sw_notas xx where xx.nota_id=x.nota_id and xx.aplicable=0) ),0) as VENTA_NETA
,(select sum(x.importe) from sw_pagos x where x.venta_id=a.venta_id) as pagos
,(select sum(x.importe/1.15) from sw_pagos x where x.venta_id=a.venta_id) as P_COMISIONABLE
,b.total+nvl((select round(sum(x.importe),2) from sw_notasdet x where x.venta_id=a.venta_id and x.tipo<>'M' and x.nota_id in(select xx.nota_id from sw_notas xx where xx.nota_id=x.nota_id and xx.aplicable=0) ),0)-(select round(sum(x.importe),2) from sw_pagos x where x.venta_id=a.venta_id) as SALDO
,MAX(case when b.origen<>'CRE' then a.fecha else (select x.vencimiento     from SW_VENTASCREDITO x where a.venta_id=x.venta_id)  end) as VENCIMIENTO
,MAX(a.fecha) as FECHA_PAG
,ROUND(CASE WHEN A.CLAVE='U050008' THEN 0.1 ELSE case when A.origen<>'CRE' then 1 else (select x.descuento     from SW_VENTASCREDITO x where a.venta_id=x.venta_id)  end END ,2) as DESCUENTO
,MAX(case when A.origen<>'CRE' then 0 else (select round(a.fecha-x.vencimiento,0)     from SW_VENTASCREDITO x where a.venta_id=x.venta_id)  end) as ATRASO
,CASE WHEN A.CLAVE='U050008' THEN 0.1 ELSE case when A.origen<>'CRE' then 1  else (select x.comision from SW_COMISIONES X where (select round(xx.descuento,2) from SW_VENTASCREDITO xx where a.venta_id=xx.venta_id) between x.minimo and x.maximo  ) end END as COMISION
,b.CANCELCOMIVENT,case when b.PAGOCOMIVEN is not null  then 1 else 0 end as APLICADO
,b.IMPCOMIVENT as IMPORTE
from sw_pagos a join sw_ventas b on(a.venta_id=b.venta_id)
where a.year>=2007 
and a.FORMADEPAGO  in('H','E','C','B','Q','Y','N','O') 
and a.TIPODOCTO NOT IN('M','Q') 
and a.fecha between ? and ?
and b.vendedor in(1,47,28,50,52)
and (b.total+nvl((select round(sum(x.importe),2) from sw_notasdet x where x.venta_id=a.venta_id and x.tipo<>'M' and x.nota_id in(select xx.nota_id from sw_notas xx where xx.nota_id=x.nota_id and xx.aplicable=0) ),0)-(select round(sum(x.importe),2) from sw_pagos x where x.venta_id=a.venta_id))<=5
GROUP BY a.origen,a.venta_id,b.fecha,b.vendedor,b.sucursal,a.clave,b.nombre,a.NUMERO,b.serie,b.tipo,b.total,b.CANCELCOMIVENT,b.PAGOCOMIVEN,b.IMPCOMIVENT
order by b.vendedor,a.clave