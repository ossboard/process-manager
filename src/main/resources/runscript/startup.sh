#!/bin/bash

export userid=$(whoami)
if [ ${userid} == 'root' ]
then
  echo "root 권한으로 실행할 수 없습니다"
  exit 0
fi

export JAVA=java
export T_NAME=nara-migration-2021.1.jar
export T_USAGE="Usage: $0 {test | mig | log}"

fn_run() {
  ${JAVA} -jar ${T_NAME} $1
}

fn_log() {
 tail -f logs/log.log
}

case $1 in
  test) fn_run ;;
  mig) fn_run ;;
  log) fn_log ;;
  *) echo ${T_USAGE} ;;
esac
exit 0
