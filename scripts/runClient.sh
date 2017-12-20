#!/bin/bash

java -jar -Dloader.main=ie.cit.teambravo.mqttclient.MessageClient ../build/libs/cardsec-*.jar $1 $2
