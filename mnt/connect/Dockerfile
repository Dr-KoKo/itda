FROM quay.io/debezium/connect:latest

COPY confluentinc-kafka-connect-elasticsearch-14.1.2.zip /tmp/elasticsearch-connector.zip
RUN mkdir -p /kafka/connect/elasticsearch \
    && unzip /tmp/elasticsearch-connector.zip -d /kafka/connect/elasticsearch

# Kafka Connect 플러그인 경로 설정
ENV CONNECT_PLUGIN_PATH="/kafka/connect,/kafka/connect/elasticsearch"
