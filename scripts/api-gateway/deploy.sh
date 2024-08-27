#!/bin/bash
CURRENT_PID=$(lsof -ti:8087)
 echo "$CURRENT_PID"
 if [ -z $CURRENT_PID ]; then
         echo "no process"
 else
         echo "kill $CURRENT_PID"
         kill -9 $CURRENT_PID
         sleep 3
 fi

 JAR_PATH="/home/ubuntu/OFZ-api-gateway/OFZ-api-gateway.jar"
 chmod +x $JAR_PATH
 nohup java -jar $JAR_PATH --spring.profiles.active=prod >> /home/ubuntu/OFZ-api-gateway/deploy.log 2>> /home/ubuntu/OFZ-api-gateway/deploy_err.log &
 echo "api-gateway deploy success"
