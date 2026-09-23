CREATE TABLE consumo_ia(
    id                  UUID PRIMARY KEY,
    data_hora           TIMESTAMP,
    modelo              VARCHAR(25),
    tokens_entrada      INTEGER,
    tokens_saida        INTEGER,
    total_tokens        INTEGER,
    custo               NUMERIC(10,8)
);