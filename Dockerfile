FROM openjdk:8-jdk as builder
ARG version=0.0.0

RUN apt-get update
RUN apt-get install -y apt-transport-https apt-utils

RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
RUN apt-get update
RUN apt-get install -y sbt git

WORKDIR /scala
RUN git clone https://github.com/damontic/SimpleScalaRestApi.git
WORKDIR /scala/SimpleScalaRestApi
RUN git checkout $version
RUN sbt assembly

FROM openjdk:8-jre-alpine
ARG version=0.0.0
COPY --from=builder /scala/SimpleScalaRestApi/target/scala-2.12/SimpleScalaRestApi_2.12-$version.jar .
CMD ["java", "-jar", "SimpleScalaRestApi_2.12-$version.jar"]
