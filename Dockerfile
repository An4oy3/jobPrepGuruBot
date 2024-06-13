FROM postgres:latest

ENV LANG en_US.utf8
ENV LC_ALL en_US.utf8

RUN apt-get update && \
    apt-get install -y locales && \
    locale-gen en_US.utf8 && \
    update-locale LANG=en_US.utf8 LC_ALL=en_US.utf8