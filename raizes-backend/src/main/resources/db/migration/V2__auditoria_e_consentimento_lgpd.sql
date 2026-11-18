CREATE TABLE IF NOT EXISTS auditoria_evento (
    id BIGINT NOT NULL AUTO_INCREMENT,
    evento VARCHAR(80) NOT NULL,
    entidade VARCHAR(60) NOT NULL,
    id_registro BIGINT NULL,
    responsavel VARCHAR(100) NOT NULL,
    data_hora DATETIME(6) NOT NULL,
    detalhes VARCHAR(500) NULL,
    PRIMARY KEY (id)
);