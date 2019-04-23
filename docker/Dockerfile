FROM damontic/ojdk8-git-sbt-builder:latest as builder
LABEL stage=intermediate
ARG version=0.0.0
ENV APP_VER ${version}
WORKDIR /scala
RUN git clone https://github.com/damontic/simple-scala-rest-api.git
WORKDIR /scala/simple-scala-rest-api
RUN git checkout $version
RUN sbt assembly

FROM damontic/java-jdk-consul-template:1-8-0-20
ARG version=0.0.0
ENV VERSION ${version}
COPY --from=builder /scala/simple-scala-rest-api/target/scala-2.12/simple-scala-rest-api_2.12-$version.jar .
COPY init.sh application.conf.template consul_template_config.hcl .
CMD sh init.sh
