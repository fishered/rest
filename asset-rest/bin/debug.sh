#!/bin/bash
LOCAL_WORK_PATH=`echo $( cd "$( dirname "${BASH_SOURCE[0]}" )/../" && pwd )`
LOCAL_BIN_PATH=${LOCAL_WORK_PATH}/bin

source ${LOCAL_BIN_PATH}/server.sh restart debug
