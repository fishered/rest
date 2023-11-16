package com.asset.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asset.rest.biz.ProcessContextVerificationBiz;
import com.asset.rest.biz.RestBiz;
import com.asset.rest.biz.context.auth.AuthData;
import com.asset.rest.biz.context.handler.AfterHandlerContext;
import com.asset.rest.biz.context.handler.HandlerContext;
import com.asset.rest.biz.context.header.HeaderContext;
import com.asset.rest.biz.context.header.HeaderParam;
import com.asset.rest.biz.context.param.ParamFix;
import com.asset.rest.biz.context.param.TemplateContext;
import com.asset.rest.biz.context.sign.SignContext;
import com.asset.rest.domain.RestInfo;
import com.asset.rest.enums.FixedValue;
import com.asset.rest.enums.Handler;
import com.asset.rest.enums.RestType;
import com.asset.rest.factory.req.RestInfoReq;
import com.asset.rest.service.RestInfoService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class AssetRestApplicationTests {

    @Autowired
    private ProcessContextVerificationBiz processContextVerificationBiz;

    @Autowired
    private RestInfoService restInfoService;

    @Autowired
    private RestBiz restBiz;

    @Test
    void contextLoads() {
    }

    @Test
    void test(){
        AfterHandlerContext afterHandlerContext = new AfterHandlerContext();

        HandlerContext handlerContext = new HandlerContext();
        handlerContext.setHandler(Handler.SQL);
        handlerContext.setSort(1);
        handlerContext.setHandlerData("select * from a");
        handlerContext.setOperaType("sql_select");

        afterHandlerContext.setHandlerContexts(Arrays.asList(handlerContext));
        System.out.println(JSON.toJSONString(afterHandlerContext));

        SignContext signContext = new SignContext();
        signContext.setVersion("1");
        signContext.setStatus(true);
        signContext.setTimestamp("1234567890");
        signContext.setApp_id("fisher");
        signContext.setApp_secret("1234570qwertyuiozxcvbnmasdfghjkzxcvbnm");
        System.out.println(JSON.toJSONString(signContext));

        HeaderContext headerContext = new HeaderContext();
        HeaderParam header = new HeaderParam();
        header.setKey("testHeader");
        header.setValue("testValue");
        headerContext.setHeaderParams(Arrays.asList(header));
        System.out.println(JSON.toJSONString(headerContext));

        ParamFix paramFix = new ParamFix();
        paramFix.setFixed(FixedValue.FIX_FILL);
        paramFix.setKey("testKey");
        paramFix.setValue("testValue");
        System.out.println(Arrays.asList(JSON.toJSONString(paramFix)));
    }

    @Test
    public void testInsert(){
        RestInfoReq req = new RestInfoReq();
        req.setCode("test");
        req.setDescription("test");
        req.setIp("192.168.0.1");
        req.setIsHttps(1);
        req.setName("test");
        req.setPort("443");
        req.setSuffixUrl("/test");
        req.setContentType("multipart/form-data");
        req.setRestType(RestType.POST);
        String auth = "{\"authDataList\":[{\"key\":\"username\",\"position\":\"URL_JOIN\",\"value\":\"guotao\"},{\"key\":\"password\",\"position\":\"URL_JOIN\",\"value\":\"P@55Word\"}]}";
        List<AuthData> authData = JSON.parseArray(JSONObject.parseObject(auth).get("authDataList").toString(), AuthData.class);
        req.setAuthData(authData);
        String paramFix = "[{\"fixed\":\"FIX_FILL\",\"key\":\"testKey\",\"value\":\"testValue\"}]";
        req.setParamsFix(JSON.parseArray(paramFix, ParamFix.class));
        String header = "{\"headerParams\":[{\"key\":\"testHeader\",\"value\":\"testValue\"}]}";
        req.setHeaderParams(JSON.parseArray(JSONObject.parseObject(header).get("headerParams").toString(), HeaderParam.class));
        String beforeHandler = "{\"handlerContexts\":[{\"handler\":\"SQL\",\"handlerData\":\"select * from group\",\"operaType\":\"sql_select\",\"sort\":1}]}";
        req.setBeforeHandler(JSON.parseArray(JSONObject.parseObject(beforeHandler).get("handlerContexts").toString(), HandlerContext.class));
        req.setAfterHandler(JSON.parseArray(JSONObject.parseObject(beforeHandler).get("handlerContexts").toString(), HandlerContext.class));
        String sign = "{\"app_id\":\"fisher\",\"app_secret\":\"1234570qwertyuiozxcvbnmasdfghjkzxcvbnm\",\"status\":true,\"timestamp\":\"1234567890\",\"version\":\"1\"}";
        req.setSign(JSON.parseObject(sign, SignContext.class));

        RestInfo restInfo = req.convertDomain();
        processContextVerificationBiz.verifyRestInfoThrowEx(restInfo);
    }

    @Test
    public void update(){
        String json = "{\"code\":\"1\",\"groupId\":1,\"name\":\"123\",\"ip\":\"10.20.240.31\",\"port\":\"443\",\"suffixUrl\":\"api/task/vul/create\",\"restType\":\"POST\",\"contentType\":\"multipart/form-data\",\"isHttps\":1,\"paramsFix\":[{\"fixed\":\"FIX_FILL\",\"key\":\"testKey\",\"value\":\"testValue\"}],\"paramTemplate\":null,\"header\":{\"headerParams\":[{\"key\":\"testHeader\",\"value\":\"testValue\"}]},\"auth\":{\"authDataList\":[{\"key\":\"username\",\"position\":\"URL_JOIN\",\"value\":\"guotao\"},{\"key\":\"password\",\"position\":\"URL_JOIN\",\"value\":\"P@55Word\"}]},\"sign\":{\"app_id\":\"fisher\",\"app_secret\":\"1234570qwertyuiozxcvbnmasdfghjkzxcvbnm\",\"status\":true,\"timestamp\":\"1234567890\",\"version\":\"1\"},\"beforeHandler\":[{\"handler\":\"SQL\",\"handlerData\":\"select * from group\",\"operaType\":\"sql_select\",\"sort\":1,\"resp\":\"\",\"params\":{}}],\"afterHandler\":[{\"handler\":\"SQL\",\"handlerData\":\"select * from group\",\"operaType\":\"sql_select\",\"sort\":1,\"resp\":\"\",\"params\":{}}],\"description\":\"test\"}";
        RestInfoReq req = JSONObject.parseObject(json, RestInfoReq.class);
        System.out.println(req);
        RestInfo restInfo = req.convertDomain();
        Optional.ofNullable(restInfoService.getById(req.getCode()))
                .orElseThrow(() -> new IllegalArgumentException("当前数据不存在！"));
        restInfoService.updateById(restInfo);
    }

    public static void main(String[] args) {
        String json = "{\n" +
                "  \"code\": 200,\n" +
                "  \"msg\": null,\n" +
                "  \"data\": {\n" +
                "    \"code\": \"1\",\n" +
                "    \"groupId\": 1,\n" +
                "    \"name\": \"123\",\n" +
                "    \"ip\": \"10.20.240.31\",\n" +
                "    \"port\": \"443\",\n" +
                "    \"suffixUrl\": \"api/task/vul/create\",\n" +
                "    \"restType\": \"POST\",\n" +
                "    \"contentType\": \"multipart/form-data\",\n" +
                "    \"isHttps\": 1,\n" +
                "    \"paramsFix\": \"[{\\\"fixed\\\":\\\"FIX_FILL\\\",\\\"key\\\":\\\"testKey\\\",\\\"value\\\":\\\"testValue\\\"}]\",\n" +
                "    \"paramTemplate\": null,\n" +
                "    \"header\": \"{\\\"headerParams\\\":[{\\\"key\\\":\\\"testHeader\\\",\\\"value\\\":\\\"testValue\\\"}]}\",\n" +
                "    \"auth\": \"{\\\"authDataList\\\":[{\\\"key\\\":\\\"username\\\",\\\"position\\\":\\\"URL_JOIN\\\",\\\"value\\\":\\\"guotao\\\"},{\\\"key\\\":\\\"password\\\",\\\"position\\\":\\\"URL_JOIN\\\",\\\"value\\\":\\\"P@55Word\\\"}]}\",\n" +
                "    \"sign\": \"{\\\"app_id\\\":\\\"fisher\\\",\\\"app_secret\\\":\\\"1234570qwertyuiozxcvbnmasdfghjkzxcvbnm\\\",\\\"status\\\":true,\\\"timestamp\\\":\\\"1234567890\\\",\\\"version\\\":\\\"1\\\"}\",\n" +
                "    \"beforeHandler\": \"{\\\"handlerContexts\\\":[{\\\"handler\\\":\\\"SQL\\\",\\\"handlerData\\\":\\\"select * from group\\\",\\\"operaType\\\":\\\"sql_select\\\",\\\"params\\\":{},\\\"resp\\\":\\\"\\\",\\\"sort\\\":1}]}\",\n" +
                "    \"afterHandler\": \"{\\\"handlerContexts\\\":[{\\\"handler\\\":\\\"SQL\\\",\\\"handlerData\\\":\\\"select * from group\\\",\\\"operaType\\\":\\\"sql_select\\\",\\\"params\\\":{},\\\"resp\\\":\\\"\\\",\\\"sort\\\":1}]}\",\n" +
                "    \"description\": \"test\"\n" +
                "  }\n" +
                "}";
        Object o = JSONObject.parseObject(json).get("data");
        String jsonString = JSON.toJSONString(o);
        System.out.println(jsonString);
    }

    @Test
    public void dipuInsert(){
        RestInfoReq req = new RestInfoReq();
        req.setCode("dipu");
        req.setDescription("测试脚本，勿删勿改");
        req.setName("dipu_test");

        List<ParamFix> paramFixes = new ArrayList<>();
        paramFixes.add(
                ParamFix.builder().fixed(FixedValue.FIX_FILL).key("assetSubType").value("Web").build()
        );
        paramFixes.add(
                ParamFix.builder().fixed(FixedValue.FIX_FILL).key("dataType").value("assetAdd").build()
        );
        paramFixes.add(
                ParamFix.builder().fixed(FixedValue.FIX_FILL).key("scanType").value("5").build()
        );
        paramFixes.add(
                ParamFix.builder().fixed(FixedValue.FIX_FILL).key("modelId").value("602312562431889415").build()
        );
        paramFixes.add(
                ParamFix.builder().fixed(FixedValue.FIX_FILL).key("scanResultId").value("868658729095139329").build()
        );
        paramFixes.add(
                ParamFix.builder().fixed(FixedValue.FIX_FILL).key("probeId").value("853539531012575232").build()
        );
        paramFixes.add(
                ParamFix.builder().fixed(FixedValue.FIX_FILL).key("executeId").value("868658523775569920").build()
        );
        paramFixes.add(
                ParamFix.builder().fixed(FixedValue.FIX_FILL).key("assetType").value("software_web").build()
        );
        paramFixes.add(
                ParamFix.builder().fixed(FixedValue.MAPPING_KEY).key("assetIp").value("ip").build()
        );
        req.setParamsFix(paramFixes);

        List<TemplateContext> templateContexts = new ArrayList<>();
        templateContexts.add(
                TemplateContext.builder().key("ipOrUrl").template("http://${ip}<% if(port){%>:${port}<%}%>").build()
        );
        templateContexts.add(
                TemplateContext.builder().key("unKnowAsset").template("{\"inner_ip\":\"${ip}\",\"web_url\":\"${ipOrUrl}\",\"web_fore_end_framework\":[],\"net_position\":\"0\",\"web_port\":[${port!\"\"}],\"survive_status\":\"1\",\"web_protocol\":\"http\",\"ip_address\":\"inner_ip\",\"web_development_language\":[]}").build()
        );
        req.setParamTemplate(templateContexts);

        HandlerContext handlerContext = new HandlerContext();
        handlerContext.setHandler(Handler.KAFKA);
        handlerContext.setOperaType("asset-warn-info");
        handlerContext.setHandlerData("10.66.11.141:19092");

        req.setBeforeHandler(Arrays.asList(handlerContext));
//        String beforeHandler = "{\"handlerContexts\":[{\"handler\":\"SQL\",\"handlerData\":\"select * from group\",\"operaType\":\"sql_select\",\"sort\":1}]}";
//        req.setBeforeHandler(JSON.parseArray(JSONObject.parseObject(beforeHandler).get("handlerContexts").toString(), HandlerContext.class));
//        req.setAfterHandler(JSON.parseArray(JSONObject.parseObject(beforeHandler).get("handlerContexts").toString(), HandlerContext.class));

        RestInfo restInfo = req.convertDomain();
        processContextVerificationBiz.verifyRestInfoThrowEx(restInfo);
        restInfoService.saveOrUpdate(restInfo);
    }

    @Test
    public void process(){
//        String code = "dipu";
//        String input = " {\"assetGeo\":\"中国,台湾,台湾\",\"alarmType\":\"assetAlarm\",\"assetRiskLevel\":\"比较安全\",\"assetOrgIdName\":\"好好干11\",\"assetName\":\"服务器[121.195.237.10]\",\"assetSource\":\"手工录入\",\"assetIp\":\"121.195.237.48\",\"assetWeight\":\"4\",\"assetgroup\":\"7070\",\"assetType\":\"服务器\",\"timestamp\":1663226346638}";
//        String input2 = "{\"assetGeo\":\"中国,台湾,台湾\",\"alarmType\":\"assetAlarm\",\"assetRiskLevel\":\"比较安全\",\"assetOrgIdName\":\"好好干11\",\"assetName\":\"服务器[121.195.237.44]\",\"assetSource\":\"手工录入\",\"assetIp\":\"121.195.237.48\",\"assetWeight\":\"4\",\"assetgroup\":\"7070\",\"assetType\":\"服务器\",\"timestamp\":1663226346638}";
        String code = "chinaUnicomSoft";
        String input2 = "{ \"code\": \"chinaUnicomSoft\", \"input\": \"{\\\"mobileList\\\":[\\\"130xxx\\\",\\\"130xxx\\\"],\\\"subject\\\":\\\"测试短信\\\",\\\"msgText\\\":\\\"测试短信内容\\\"}\"}";

        JSONObject codeObject = new JSONObject();
        codeObject.put("code", code);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "测试");
        jsonObject.put("mobileList",Arrays.asList("130xxx","130xxx"));
        jsonObject.put("subject","测试");
        codeObject.put("input", jsonObject);
        System.out.println(JSON.toJSONString(codeObject));
        restBiz.rest(code, input2);
    }

    /**
     * 中国联通.....接口
     */
    @Test
    public void generalChinaUnicomNetworkOperationsCenter(){
        RestInfoReq req = new RestInfoReq();
        req.setCode("chinaUnicomNetwork");
        req.setDescription("中国联通 智网创新中心【网络能力运营中心】接口定制");
        req.setName("中国联通 智网创新中心【网络能力运营中心】");
        req.setIp("172.25.34.180");
        req.setPort("443");
        req.setSuffixUrl("api/middlleplatform/xgrI5g/52pgE8/v1.0");
        req.setRestType(RestType.POST);
        req.setIsHttps(1);
        req.setContentType("application/json;charset=UTF-8");
        List<ParamFix> paramFixes = new ArrayList<>();
//        paramFixes.add(
//                ParamFix.builder().fixed(FixedValue.FIX_FILL).key("province").value("BJ").build()
//        );
//        paramFixes.add(
//                ParamFix.builder().fixed(FixedValue.FIX_FILL).key("score").value("600").build()
//        );
//        paramFixes.add(
//                ParamFix.builder().fixed(FixedValue.FIX_FILL).key("identity").value("370827200010031014").build()
//        );
        req.setParamsFix(paramFixes);
        List<HeaderParam> headerParams = new ArrayList<>();
        headerParams.add(HeaderParam.builder().key("APP_ID").value("2531043c-9459-4d4d-836e-66bc2811e78b").build());
        headerParams.add(HeaderParam.builder().key("APP_SECRET").value("9606b4df96b24d159ed73954a29ee9a6").build());
        req.setHeaderParams(headerParams);
        req.setCustomAuth("unicom");

        RestInfo restInfo = req.convertDomain();
        processContextVerificationBiz.verifyRestInfoThrowEx(restInfo);
        restInfoService.saveOrUpdate(restInfo);

    }

    @Test
    public void generalChinaUnicomSoftware(){
        RestInfoReq req = new RestInfoReq();
        req.setCode("chinaUnicomSoft");
        req.setDescription("中国联通 中国联通数字化能力开放平台 接口定制");
        req.setName("中国联通 中国联通数字化能力开放平台 接口定制");
        req.setIp("10.124.150.230");
        req.setPort("8000");
        req.setSuffixUrl("api/chinaUnicom/microservice/notice/smsindustry/v1");
        req.setRestType(RestType.POST);
        req.setIsHttps(0);
        req.setContentType("application/json; charset=UTF-8");
//        req.setContentType("application/json;charset=UTF-8");
        List<HeaderParam> headerParams = new ArrayList<>();
        headerParams.add(
                HeaderParam.builder().key("Content-Type").value("application/json; charset=UTF-8").build()
        );
        headerParams.add(
                HeaderParam.builder().key("Accept").value("application/json").build()
        );
        headerParams.add(
                HeaderParam.builder().key("Accept-Encoding").value(StringUtils.EMPTY).build()
        );
        req.setHeaderParams(headerParams);

        List<ParamFix> paramFixes = new ArrayList<>();
        paramFixes.add(
                ParamFix.builder().fixed(FixedValue.FIX_FILL).key("APP_ID").value("123").build()
        );
        paramFixes.add(
                ParamFix.builder().fixed(FixedValue.FIX_FILL).key("APP_SECRET").value("test").build()
        );
        req.setParamsFix(paramFixes);
        req.setCustomAuth("unicom_soft");

        RestInfo restInfo = req.convertDomain();
        processContextVerificationBiz.verifyRestInfoThrowEx(restInfo);
        restInfoService.saveOrUpdate(restInfo);
    }

}
