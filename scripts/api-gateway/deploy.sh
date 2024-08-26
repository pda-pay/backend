#!/bin/bash
 CURRENT_PID=$(pgrep -f .jar)
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
 nohup java -jar $JAR_PATH >> /home/ubuntu/OFZ-api-gateway/deploy.log 2>> /home/ubuntu/OFZ-api-gateway/deploy_err.log &
 echo "eureka deploy success"
