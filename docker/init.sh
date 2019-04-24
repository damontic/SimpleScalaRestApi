#! /bin/sh

consul-template -config consul_template_config.hcl -template "application.conf.template:application.conf" -once

test -f application.conf && java -Dconfig.file=application.conf -jar simple-scala-rest-api_2.12-$VERSION.jar || echo "application.conf does not exist"
