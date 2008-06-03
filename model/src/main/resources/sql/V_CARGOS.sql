create or replace view V_CARGOS as 
select 
round(a.importe*1.15,2) as total,(select round(sum(b.importe),2) from sw_pagos b where b.nota_id=a.nota_id) as pagos,
round(a.importe*1.15,2)-(select round(sum(b.importe),2) from sw_pagos b where b.nota_id=a.nota_id) as saldo
,a.* 
from sw_notas a where SERIE='M'  and year>2004