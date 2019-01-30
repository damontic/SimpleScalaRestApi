FROM damontic/ojdk8-git-sbt-builder:latest as builder
LABEL stage=intermediate
ARG version=0.0.0
ENV APP_VER ${version}
WORKDIR /scala
RUN git clone https://github.com/damontic/SimpleScalaRestApi.git
WORKDIR /scala/SimpleScalaRestApi
RUN git checkout $version
RUN sbt assembly

FROM openjdk:8-jre-alpine
ARG version=0.0.0
ENV VERSION ${version}
COPY --from=builder /scala/SimpleScalaRestApi/target/scala-2.12/SimpleScalaRestApi_2.12-$version.jar .
CMD java -jar SimpleScalaRestApi_2.12-$VERSION.jar
