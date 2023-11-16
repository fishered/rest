package com.asset.rest.biz;

import com.alibaba.fastjson.JSON;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fisher
 * @date 2023-09-15: 14:28
 */
@Component
public class TemplateBiz {

    /**
     * 根据模板解析输入
     * @param input 输入参数
     * @param templateStr 模板
     * @return
     */
    public String template(String input, String templateStr) throws IOException {
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Configuration cfg = Configuration.defaultConfiguration();

        GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
        Template template = gt.getTemplate(templateStr);

        // 将JSON字符串转换为Map
        Map<String, Object> data = JSON.parseObject(input, HashMap.class);

        template.binding(data);
        return template.render();
    }

    public String template(Map<String, Object> map, String templateStr) throws IOException {
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Configuration cfg = Configuration.defaultConfiguration();

        GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
        Template template = gt.getTemplate(templateStr);

        template.binding(map);
        return template.render();
    }

    public static void main(String[] args) throws IOException {
//        String template = "{\"inner_ip\":\"${assetIp}\",\"web_url\":\"${ipOrUrl}\",\"web_fore_end_framework\":[],\"net_position\":\"0\",\"web_port\":[${port}],\"survive_status\":\"1\",\"web_protocol\":\"http\",\"ip_address\":\"inner_ip\",\"web_development_language\":[]}";
//        Map<String, Object> map = new HashMap<>();
//        map.put("assetIp", "127.0.0.1");
//        map.put("ipOrUrl", "http://127.0.0.1");
////        map.put("port", null);
//
//        TemplateBiz biz = new TemplateBiz();
//        String template1 = biz.template(map, template);
//        System.out.println(template1);

        String template = "{\"inner_ip\":\"${assetIp}\",\"web_url\":\"${ipOrUrl}\",\"web_fore_end_framework\":[],\"net_position\":\"0\",\"web_port\":[${port!\"\"}],\"survive_status\":\"1\",\"web_protocol\":\"http\",\"ip_address\":\"inner_ip\",\"web_development_language\":[]}";
        Map<String, Object> map = new HashMap<>();
        map.put("assetIp", "127.0.0.1");
        map.put("ipOrUrl", "http://127.0.0.1");
// map.put("port", null);

        TemplateBiz biz = new TemplateBiz();
        String template1 = biz.template(map, template);
        System.out.println(template1);
    }


}
