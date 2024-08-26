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

 JAR_PATH="/home/ubuntu/OFZ-payment/OFZ-payment.jar"
 chmod +x $JAR_PATH
 nohup java -jar $JAR_PATH >> /home/ubuntu/OFZ-payment/deploy.log 2>> /home/ubuntu/OFZ-payment/deploy_err.log &
 echo "payment deploy success"
