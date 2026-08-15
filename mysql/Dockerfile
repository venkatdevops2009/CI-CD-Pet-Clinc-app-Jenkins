FROM mysql:8.4

USER root

RUN rm -rf /usr/lib/mysqlsh /usr/local/bin/gosu 2>/dev/null || true \
    && rm -rf /var/cache/yum /var/cache/dnf /tmp/* 2>/dev/null || true \
    && chown -R mysql:mysql /docker-entrypoint-initdb.d

COPY DB/ /docker-entrypoint-initdb.d/

USER mysql