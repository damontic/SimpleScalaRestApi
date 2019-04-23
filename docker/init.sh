#! /bin/sh

consul-template -config consul_template_config.hcl -template "application.conf.template:application.conf" -once 

java -Dconfig.file=application.conf -jar simple-scala-rest-api_2.12-$VERSION.jar