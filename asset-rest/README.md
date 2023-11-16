
### Biz组件设计

    当前版本：v1.0
    作者：fisher
    下面文档主要面向集成开发者及现场运维人员，为了帮助您更好配置，请先了解以下内容：
        rest协议
        beetl模板引擎
        基本sql用法
        集成的第三方中间件，如KAFKA，ES等

### 拓展

    在biz中定义了上层的边界 AbstractProcess 以及抽象接口 HookProcess CoreProcess
    *自定义实现实现模板方法，选择需要的实现节点 其他设置为空实现
    *在主流程下还需要做额外的动作，请自行实现HookProcess， 并将钩子注入到bean中

### 什么是rest-biz
    
    *rest-biz 顾名思义是用于一系列rest请求流程的动作。我们面临了与第三方集成因第三方规范来回变动大量重复无意义的工作。
    *rest-biz 中定义了几个节点 主要由核心的模板方法和各种context实现。
    *目前使用的是okhttp，考虑到性能及支持拓展、异步回调等方式，当然你也可以采用你喜欢的方式如restTemplate，变动无需修改代码
    只需要再去实现core 并且让自定义的bean覆盖就好
    *当然 它应该不仅仅支持各种转换rest 还可以满足某一些动作。例如我发送rest后想保存入库怎么办？自定义after handler规范
    rest-biz 支持：
        1.通过一组公共的配置处理你需要的参数列表（默认只支持json类型字符串，请自行格式化）
        2.通过配置组装，生成想要的数据模型（复杂的拼装需要使用beetl模板 请自行了解beetl的模板规则）
        3.支持额外的调用处理及预处理，主要体现在以下几方面：
            hook：依赖rest-biz后，可以通过自定义hook并将其注册到上下文中实现外部钩子函数
            handler：handler是一组环绕的around处理，体现在配置中before、afterhandler中，目前集成了DB操作和KAFKA Producer
        3.1.如果我想自定义handler规则怎么办呢？
            答：请自行实现handler接口及其抽象类
        3.2.如何选择前置handle还是后置handler？
            答：handler可以有多种，例如我们可以配置前置handler先在KAFKA topic1发送消息，再在topic2发送消息，只需要设置handler的顺序就好，
        不同的是，后置handler是core核心处理完成后的，所以handler中有core中返回的上下文。
        4.我担心我的配置错误怎么办？
            答：在verifyBiz中提供了校验配置项是否异常的方法，可以手动调用，也可以在加载时由程序去校验。
        5.如果我只想做一系列操作，最后不一定去走rest动作，怎么办？
            答：在配置中，某些基于rest-core核心的context不全就不会去调用rest处理。当然你也可以做一个空实现实现CoreProcess。
        6.配置如何加载？
            答：除此之外，我会提供一个rest-config的包，主程序可以在db中通过接口将配置打包成json文件，支持单个配置或者分组配置打包，将配置放入
        rest-config指定的目录下，然后在将config发布出去，在主程序中依赖config，就会在启动时自动加载所有的配置并注入。

### 我该如何配置定制化

    *目前 配置的规则基于restInfo和各种context 很多配置是基于json存储的
    首先我来解释一下各种枚举的用处：
        DBType:
            MYSQL,ORACLE,POSTGRESQL  ##这是支持的db类型
        FixedValue:
            FIX_FILL ##固定值填充：例如我想给这个规则每次再param都加上"name"="1" 
            SETTING_FIX ##指定固定值：比如我每次请求的param name都必须是1
            MAPPING_KEY ##映射固定key：比如我参数传了name = 1 但是第三方接收的是name2 
        Handler:
            INPUT ##同理我想在handle 添加一个k-v
            SQL_FULL ##sql填充：我想执行一个sql将结果集填充进去 （此功能待定）
        Position: ##位置枚举
            URL_JOIN ##当前的kv需要拼接到url后
            HEADER ##当前的kv需要放入request header
            PARAMS ##当前的kv需要放入request params中
        ProcessType: ##支持操作的类型 与process core对应使用
            REST ##目前支持基于rest模式 就是根据规则处理后发送rest请求
        RestType: 定义了各种rest method 例如GET POST等

    在restInfo中 针对配置的几列属性：
        rest_type：对应RestType枚举
        content_type：对应请求的contentType 请正确填写
        is_https：是否是https 1是0否
        params_fix：json字符串 包含FixedValue key value FixedValue就是上面枚举定义的内容 kv对应kv值
        param_template：基于beetl的模板 具体请参考beetl模板内容
        header：json字符串 包含了需要输入到header的kv
        auth：针对认证的处理 json字符串 position参考上面枚举 和kv 可有多个
        before、after handler：handler的处理 包含Handler枚举和kv

    举个例子：
    {
    "auth": "{\"authDataList\":[{\"key\":\"username\",\"position\":\"URL_JOIN\",\"value\":\"guotao\"},{\"key\":\"password\",\"position\":\"URL_JOIN\",\"value\":\"P@55Word\"}]}", 
    "code": "1", 
    "contentType": "multipart/form-data", 
    "groupId": 1, 
    "https": true, 
    "ip": "10.20.240.31", 
    "isHttps": 1, 
    "name": "123", 
    "port": "443", 
    "restType": "POST", 
    "suffixUrl": "api/task/vul/create",
    "beforeHandler": "{"handlerContexts":[{"handler":"KAFKA","handlerData":"10.66.11.31:9092","operaType":"asset-warn-info"}]}"
    }
    上面的例子定义的就是 multipart/form-data请求 post 认证参数username=guotao和password=P@55Word拼接在请求url里
    更多的配置信息请参考上面的枚举介绍

    解释一下上面的配置处理流程：
        向 10.20.240.31:443 发送post api/task/vul/create 并且添加auth：username和password拼接在url
        在发送请求前，实现前置handler，先向kafka发送一条message kafka为10.66.11.31:9092 topic：asset-warn-info

### 为什么要存在db中

    目前考虑的是会有接口保存具体的配置信息，并且可以导出一个或一组配置 可能生成对应的json文件供系统使用
    这也是为什么要再定义一个group的原因
    后面会做一个loader加载所有lib下符合条件的配置并在具体业务调用中引用

### 组件结构树
```
├─src
│  ├─main
│  │  ├─java
│  │  │  └─com
│  │  │      └─asset
│  │  │          └─rest
│  │  │              ├─biz
│  │  │              │  ├─context
│  │  │              │  │  ├─auth  ##认证的上下文
│  │  │              │  │  ├─handler  ##handler的上下文
│  │  │              │  │  ├─header  ##header头的上下文
│  │  │              │  │  ├─param  ##请求的上下文
│  │  │              │  │  └─resp  ##返回的上下文
│  │  │              │  ├─core  ##核心处理 AbstractProcess CoreProcess HookProcess
│  │  │              │  └─util  ##一些工具包
│  │  │              ├─config  ##配置
│  │  │              ├─contrast  ##常量
│  │  │              ├─controller
│  │  │              ├─domain
│  │  │              ├─enums
│  │  │              ├─exception ##自定义异常
│  │  │              ├─service
│  │  │              │  ├─impl
│  │  │              │  └─mapper
│  │  │              └─util
│  │  └─resources
│  └─test
│      └─java
│          └─com
│              └─asset
└─                 └─rest

```
### 其他配置说明
    打开application.yml 设置如下
    pg：（变更自行替换）
        driver-class-name: org.postgresql.Driver
        url: jdbc:postgresql://10.35.30.105:5433/asset218
        type: com.zaxxer.hikari.HikariDataSource
        password: idss@1234
        username: postgres
    
    redis:
        host: 10.66.11.57
        port: 6379
        password: dat@ori123
        database: 0

    port:8080  自行替换为实际配置
    数据库脚本参考init.sql

### v1.1优化：
    优化了基于beetl解析流程的问题
    优化了核心处理