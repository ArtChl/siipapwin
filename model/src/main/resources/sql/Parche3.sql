
alter table SIIPAP.SW_CLIENTES_CREDITO add pagare number(1,0)

alter table SIIPAP.SW_CLIENTES_CREDITO add ordenDeCompra number(1,0)

update SW_CLIENTES_CREDITO set ORDENDECOMPRA=0;

update SW_CLIENTES_CREDITO set orde=0;