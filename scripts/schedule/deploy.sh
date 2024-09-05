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

 JAR_PATH="/home/ubuntu/OFZ-schedule/OFZ-schedule.jar"
 chmod +x $JAR_PATH
 nohup java -jar $JAR_PATH --spring.profiles.active=prod  >> /home/ubuntu/OFZ-schedule/deploy.log 2>> /home/ubuntu/OFZ-schedule/deploy_err.log &
 echo "schedule deploy success"
