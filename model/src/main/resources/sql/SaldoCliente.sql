SELECT NVL((
SELECT SUM(TOTAL
-(SELECT NVL(SUM(-B.IMPORTE),0) FROM SW_NOTASDET B WHERE A.VENTA_ID=B.VENTA_ID AND B.TIPO NOT IN('M') AND B.FECHA<=? AND B.NOTA_ID IN((SELECT X.NOTA_ID FROM SW_NOTAS X WHERE X.APLICABLE=0))) 
-(SELECT NVL(SUM(B.IMPORTE),0) FROM SW_PAGOS B WHERE A.VENTA_ID=B.VENTA_ID AND B.FORMADEPAGO NOT IN('K','U') AND B.FECHA<=?)) AS SALDO
FROM SW_VENTAS A
join SW_VENTASCREDITO B ON(A.VENTA_ID=B.VENTA_ID)
WHERE --A.CLAVE='Z050018' AND 
A.YEAR>=2007 AND A.FECHA<=? AND A.CLAVE LIKE @CLAVE AND A.ORIGEN='CRE'
GROUP BY A.CLAVE HAVING SUM(TOTAL
-(SELECT NVL(SUM(-B.IMPORTE),0) FROM SW_NOTASDET B WHERE A.VENTA_ID=B.VENTA_ID AND B.TIPO NOT IN('M') AND B.FECHA<=? AND B.NOTA_ID IN((SELECT X.NOTA_ID FROM SW_NOTAS X WHERE X.APLICABLE=0))) 
-(SELECT NVL(SUM(B.IMPORTE),0) FROM SW_PAGOS B WHERE A.VENTA_ID=B.VENTA_ID AND B.FORMADEPAGO NOT IN('K','U') AND B.FECHA<=?))<>0
),0)+
NVL((
select
SUM(round(a.importe*1.15,2))-SUM(NVL((select round(sum(b.importe),2) from sw_pagos b where b.nota_id=a.nota_id AND B.FECHA<=?),0)) as saldo 
from sw_notas a 
JOIN SW_CLIENTES_CREDITO X ON(A.CLAVE=X.CLAVE)
where SERIE='M'  and year>=2007 AND A.ORIGEN='CRE' AND A.FECHA<=? AND A.CLAVE LIKE @CLAVE
GROUP BY A.CLAVE HAVING  SUM(round(a.importe*1.15,2))-SUM(NVL((select round(sum(b.importe),2) from sw_pagos b where b.nota_id=a.nota_id AND B.FECHA<=?),0))>0.1
),0)+
NVL((
select 
SUM(round(a.importe*1.15,2))+SUM(NVL((select round(sum(b.importe),2) from sw_pagos b where b.notapago_id=a.nota_id AND B.FECHA<=?),0)) as saldo 
from sw_notas a 
JOIN SW_CLIENTES_CREDITO X ON(A.CLAVE=X.CLAVE)
where SERIE not in('M','U','V') AND A.APLICABLE=1 and year>=2007 AND ORIGEN='CRE' AND A.FECHA<=? AND A.CLAVE LIKE @CLAVE
GROUP BY A.CLAVE HAVING  SUM(round(a.importe*1.15,2))+SUM(NVL((select round(sum(b.importe),2) from sw_pagos b where b.notapago_id=a.nota_id AND B.FECHA<=?),0))<-0.1
),0) as SALDO
FROM DUAL