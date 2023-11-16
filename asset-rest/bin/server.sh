#!/bin/bash
source /etc/profile
if [ -f ~/.bash_profile ]
  then source ~/.bash_profile
fi

#加载配置文件
function prepare()
{
    LOCAL_WORK_PATH=`echo $( cd "$( dirname "${BASH_SOURCE[0]}" )/../" && pwd )`
    LOCAL_BIN_PATH=${LOCAL_WORK_PATH}/bin
    LOCAL_LIB_PATH=${LOCAL_WORK_PATH}/lib
    LOCAL_EXT_LIB_PATH=${LOCAL_WORK_PATH}/extlib
    LOCAL_CONF_PATH=${LOCAL_WORK_PATH}/conf
    LOCAL_LOGS_PATH=${LOCAL_WORK_PATH}/logs
    LOCAL_TMP_PATH=${LOCAL_WORK_PATH}/tmp
    APP_PID=${LOCAL_BIN_PATH}/rest.pid
    RUN_LOG=${LOCAL_WORK_PATH}/run.logs
    MAIN_CLASS=com.asset.rest.AssetRestApplication
    #如果配置文件中需要获取环境变量,需要加export
    export LOCAL_LOGS_PATH
    #创建上传文件临时路径
    LOCAL_UPLOAD_PATH=${LOCAL_TMP_PATH}
    mkdir -p ${LOCAL_UPLOAD_PATH}
    export LOCAL_UPLOAD_PATH

    JAVA_OPTS="$JAVA_OPTS -server"
    JAVA_OPTS="$JAVA_OPTS -Xss256k -Xms2g -Xmx2g -Xss256k"
    #JAVA_OPTS="$JAVA_OPTS -XX:PermSize=512m -XX:MaxPermSize=512m"
    #JAVA_OPTS="$JAVA_OPTS -XX:+DisableExplicitGC -XX:ParallelGCThreads=16 -XX:+CMSClassUnloadingEnabled -XX:+UseConcMarkSweepGC -XX:+UseParNewGC"
    #JAVA_OPTS="$JAVA_OPTS -XX:CMSFullGCsBeforeCompaction=5 -XX:CMSInitiatingOccupancyFraction=80 -XX:MaxTenuringThreshold=15 "
    #JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintSafepointStatistics -XX:+PrintTenuringDistribution -XX:+PrintHeapAtGC -Xloggc:./gc.log "
    JAVA_OPTS="$JAVA_OPTS -Dclient.enczoding.override=UTF-8 -Dfile.encoding=UTF-8 -Duser.language=zh -Duser.region=CN"
    JAVA_OPTS="$JAVA_OPTS -Djava.ext.dirs=$LOCAL_LIB_PATH:$LOCAL_EXT_LIB_PATH:$JAVA_HOME/jre/lib/ext -Djava.library.path=$LOCAL_LIB_PATH:$LOCAL_EXT_LIB_PATH -Xbootclasspath/a:$LOCAL_CONF_PATH"
    #开启远程debug服务
    JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=40110,suspend=n"

    #开启jmx远程
    #JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote"
    #JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote=true"
    #JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.port=18899"
    #JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.ssl=false"
    #JAVA_OPTS="$JAVA_OPTS -Dcom.sun.managementote.ssl=false"
    #JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.local.only=false"
    #JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.authenticate=false"
    JAVA_OPTS="$JAVA_OPTS -Dlog4j2.formatMsgNoLookups=true"

    cd $LOCAL_WORK_PATH
    #解决环境里缺少TERM环境变量导致采集流量脚本服错
    TERM=xterm-256color
    export TERM

    return 0
}

prepare

command="$1"
args="$2"

###################################
#(函数)判断程序是否已启动
#
#说明：
#使用JDK自带的JPS命令及grep命令组合，准确查找pid
#jps 加 l 参数，表示显示java的完整包路径
#使用awk，分割出pid ($1部分)，及Java程序名称($2部分)
#当jps命令不可用时,使用: ps -ef | grep $MAIN_CLASS | grep -v "grep" | awk '{print $2}' 代替
###################################
#初始化psid变量（全局）
psid=0
function checkpid() {
   javaps=`$JAVA_HOME/bin/jps -l | grep $MAIN_CLASS`
   #javaps=`ps -ef | grep $MAIN_CLASS | grep -v "grep" | awk '{print $2}'`

   if [ -n "$javaps" ]; then
      psid=`echo $javaps | awk '{print $1}'`
   else
      psid=0
   fi
}

###################################
#(函数)启动程序
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则提示程序已启动
#3. 如果程序没有被启动，则执行启动命令行
#4. 启动命令执行后，再次调用checkpid函数
#5. 如果步骤4的结果能够确认程序的pid,则打印[OK]，否则打印[Failed]
#注意：echo -n 表示打印字符后，不换行
#注意: "nohup 某命令 >/dev/null 2>&1 &" 的用法
###################################
function start() {
   checkpid

   if [ $psid -ne 0 ]; then
      echo "================================"
      echo "warn: $MAIN_CLASS already started! (pid=$psid)"
      echo "================================"
   else
     echo -n "Starting $MAIN_CLASS ..."
     echo -n "${JAVA_OPTS}"
      # -DlogFn=active 指的是生产日志文件名为active
      if [ "$args" == "debug" ]; then
        nohup ${JAVA_HOME}/bin/java ${JAVA_OPTS} ${MAIN_CLASS} AssetRestApplication$@ >${RUN_LOG} 2>&1 &
      else
        nohup ${JAVA_HOME}/bin/java ${JAVA_OPTS} ${MAIN_CLASS} AssetRestApplication$@ >/dev/null 2>&1 &
      fi

      checkpid
      if [ $psid -ne 0 ]; then
         echo "(pid=$psid) [OK]"
      else
         echo "[Failed]"
      fi
   fi
}

###################################
#(函数)停止程序
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则开始执行停止，否则，提示程序未运行
#3. 使用kill -9 pid命令进行强制杀死进程
#4. 执行kill命令行紧接其后，马上查看上一句命令的返回值: $?
#5. 如果步骤4的结果$?等于0,则打印[OK]，否则打印[Failed]
#6. 为了防止java程序被启动多次，这里增加反复检查进程，反复杀死的处理（递归调用stop）。
#注意：echo -n 表示打印字符后，不换行
#注意: 在shell编程中，"$?" 表示上一句命令或者一个函数的返回值
###################################
function stop() {
   checkpid

   if [ $psid -ne 0 ]; then
      echo -n "Stopping $MAIN_CLASS ...(pid=$psid) "
      #kill -9 $psid  #有点暴力
      kill -15 $psid
      if [ $? -eq 0 ]; then
         echo "[OK]"
      else
         echo "[Failed]"
      fi

      checkpid
      if [ $psid -ne 0 ]; then
         stop
      fi
   else
      echo "================================"
      echo "warn: $MAIN_CLASS is not running"
      echo "================================"
   fi
}

function restart() {
  stop
  start $2
}

###################################
#(函数)检查程序运行状态
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则提示正在运行并表示出pid
#3. 否则，提示程序未运行
###################################
function status() {
   checkpid

   if [ $psid -ne 0 ];  then
      echo "$MAIN_CLASS is running! (pid=$psid)"
   else
      echo "$MAIN_CLASS is not running"
   fi
}

###################################
#(函数)打印系统环境参数
###################################
function info() {
   echo "System Information:"
   echo "****************************"
   #echo `head -n 1 /etc/issue`
   echo `uname -a`
   echo
   echo "JAVA_HOME=$JAVA_HOME"
   echo `$JAVA_HOME/bin/java -version`
   echo
   echo "LOCAL_WORK_PATH=$LOCAL_WORK_PATH"
   echo "MAIN_CLASS=$MAIN_CLASS"
   echo "****************************"
}

# See how we were called.
case "$1" in
    'start')
        start
        ;;
    'stop')
        stop
        ;;
    'status')
        status
        ;;
    'restart')
        restart
        ;;
    'info')
         info
         ;;
    *)
        echo $"Usage: $0 {start|stop|restart|status|info}"
        exit 1
esac
