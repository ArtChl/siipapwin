##
create table SIIPAP.SW_CLIENTES_CREDITO (
        CLIENTE_ID number(19,0) not null,
        chequesDevueltos number(10,0),
        comentarioCxc varchar2(255 char),
        comentariosVentas varchar2(255 char),
        creado timestamp,
        credito number(19,2),
        descuentosYtd number(19,2),
        devolucionesYtd number(19,2),
        facturas number(10,0),
        facturasVencidas number(10,0),
        modificado timestamp,
        notaCredito varchar2(255 char),
        notaVentas varchar2(2 char),
        pagosYtd number(19,2),
        respaldo number(19,2),
        saldo number(19,2),
        saldoInicial number(19,2),
        saldoVencido number(19,2),
        ultimaVenta date,
        ultimoPago date,
        vcontadoYtd number(19,2),
        vcreditoYtd number(19,2),
        primary key (CLIENTE_ID)
    );
    
    alter table SIIPAP.SW_CLIENTES_CREDITO 
        add constraint FKE48C346B70310BB0 
        foreign key (CLIENTE_ID) 
        references SIIPAP.SW_CLIENTES;