# 'management' only required for UI during development
FROM rabbitmq:management-alpine
ENV RABBITMQ_SERVER_ADDITIONAL_ERL_ARGS "-rabbitmq_stomp trailing_lf false hide_server_info true"
RUN rabbitmq-plugins enable --offline rabbitmq_stomp
