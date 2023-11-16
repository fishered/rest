//package com.asset.rest.controller;
//
//import cn.hutool.core.lang.Assert;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.asset.rest.biz.RestBiz;
//import com.asset.rest.domain.RestInfo;
//import com.asset.rest.factory.req.RestInfoQueryReq;
//import com.asset.rest.factory.req.RestInfoReq;
//import com.asset.rest.res.R;
//import com.asset.rest.service.GroupService;
//import com.asset.rest.service.RestInfoService;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DuplicateKeyException;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.Objects;
//import java.util.Optional;
//
///**
// * @author fisher
// * @date 2023-09-15: 9:45
// */
//@RestController
//@RequestMapping("rest")
//@Api(tags = "基本配置接口")
//public class BaseRestController {
//
//    @Autowired
//    private RestBiz restBiz;
//
//    @Autowired
//    private RestInfoService restInfoService;
//
//    @Autowired
//    private GroupService groupService;
//
//    @GetMapping("/code")
//    public void manual(@RequestParam("code") String code){
//        String json = "{\"name\":\"fisher\",\"template_id\":\"100\",\"targets\":\"207.12.188.255\"}";
//        restBiz.rest(code, json);
//    }
//
//    @PostMapping("/insert")
//    @ApiOperation(value = "新增配置")
//    public R insertRest(@Validated @RequestBody RestInfoReq req){
//        Assert.notNull(req, "传入的group不能为空！");
//        if (req.getGroupId() != null){
//            Optional.ofNullable(groupService.getById(req.getGroupId())).orElseThrow(
//                    () -> new IllegalArgumentException("当前分组找不到对应的信息！")
//            );
//        }
//        try {
//            return R.ok(restInfoService.save(req.convertDomain()));
//        } catch (DuplicateKeyException e) {
//            throw new RuntimeException(String.format("%s已经存在！", req.getCode()));
//        }
//    }
//
//    @PostMapping("/update")
//    @ApiOperation(value = "修改配置")
//    public R updateRest(@Validated @RequestBody RestInfoReq req){
//        Optional.ofNullable(restInfoService.getById(req.getCode()))
//                .orElseThrow(() -> new IllegalArgumentException("当前数据不存在！"));
//        return R.ok(restInfoService.updateById(req.convertDomain()));
//    }
//
//    @DeleteMapping("/delete/{code}")
//    @ApiOperation(value = "删除配置")
//    public R deleteRest(@ApiParam(value = "配置编号", required = true) @PathVariable("code") String code){
//        Assert.notBlank(code, "删除配置id信息不能为空！");
//        return R.ok(groupService.removeById(code));
//    }
//
//    @PostMapping("/list")
//    @ApiOperation(value = "查询列表")
//    public R list(@RequestBody @Validated RestInfoQueryReq req){
//        return R.ok(restInfoService.list(
//                new LambdaQueryWrapper<RestInfo>()
//                        .eq(StringUtils.isNotBlank(req.getCode()), RestInfo::getCode, req.getCode())
//                        .likeRight(StringUtils.isNotBlank(req.getName()), RestInfo::getName, req.getName())
//                        .eq(Objects.nonNull(req.getRestType()), RestInfo::getRestType, req.getRestType())
//        ));
//    }
//
//    @GetMapping("/getByCode/{code}")
//    @ApiOperation(value = "根据code查找配置")
//    public R getOne(@ApiParam(value = "配置编号", required = true) @PathVariable("code") String code){
//        Assert.notEmpty(code, "获取配置信息编码不能为空！");
//        return R.ok(restInfoService.getOne(
//                new LambdaQueryWrapper<RestInfo>()
//                        .eq(RestInfo::getCode, code)
//        ));
//    }
//
//    @GetMapping("/export/{code}")
//    @ApiOperation(value = "导出配置")
//    public void exportConfig(@ApiParam(value = "配置编号", required = true) @PathVariable("code") String code, HttpServletResponse response){
//        Assert.notEmpty(code, "获取配置信息编码不能为空！");
//        RestInfo restInfo = Optional.ofNullable(restInfoService.getOne(
//                new LambdaQueryWrapper<RestInfo>()
//                        .eq(RestInfo::getCode, code)
//        )).orElseThrow(() -> new IllegalArgumentException("当前数据不存在！"));
//
//        String fileName = restInfo.getName() + ".json";
//        try {
//            // 设置响应头，指定文件名和类型
//            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()));
//            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
//
//            // 将 JSON 字符串转换为字节数组，并写入响应流
//            byte[] jsonBytes = restInfo.generalJsonConfig().getBytes(StandardCharsets.UTF_8);
//            response.getOutputStream().write(jsonBytes);
//            response.flushBuffer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
