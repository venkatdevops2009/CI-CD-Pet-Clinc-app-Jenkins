FROM mysql:8.4

COPY DB/ /docker-entrypoint-initdb.d/
RUN chown -R mysql:mysql /docker-entrypoint-initdb.d

USER mysql