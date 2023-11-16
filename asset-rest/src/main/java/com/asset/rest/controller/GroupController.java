//package com.asset.rest.controller;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.lang.Assert;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.asset.rest.domain.Group;
//import com.asset.rest.domain.RestInfo;
//import com.asset.rest.factory.GroupFactory;
//import com.asset.rest.factory.req.GroupReq;
//import com.asset.rest.factory.req.GroupUpdateReq;
//import com.asset.rest.res.R;
//import com.asset.rest.service.GroupService;
//import com.asset.rest.service.RestInfoService;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
///**
// * @author fisher
// * @date 2023-09-26: 14:14
// */
//@RestController
//@RequestMapping("group")
//@Api(tags = "分组接口")
//public class GroupController {
//
//    @Autowired
//    private GroupService groupService;
//
//    @Autowired
//    private RestInfoService restInfoService;
//
//    @PostMapping("/insert")
//    @ApiOperation(value = "新增分组")
//    public R insertGroup(@Validated @RequestBody GroupReq req){
//        Assert.notNull(req, "传入的group不能为空！");
//        return R.ok(groupService.save(GroupFactory.createGroup(req)));
//    }
//
//    @PostMapping("/update")
//    @ApiOperation(value = "修改分组")
//    public R updateGroup(@Validated @RequestBody GroupUpdateReq req){
//        String id = req.getId();
//        Group group = Optional.ofNullable(groupService.getById(id)).orElseThrow(
//                () -> new IllegalArgumentException(String.format("%s下找不到对应分组信息！", id)));
//
//        BeanUtil.copyProperties(req, group);
//        return R.ok(groupService.updateById(group));
//    }
//
//    @DeleteMapping("/delete/{id}")
//    @ApiOperation(value = "删除分组")
//    public R deleteGroup(@ApiParam(value = "分组id", required = true) @PathVariable("id") Long id){
//        Assert.notNull(id, "删除分组信息id信息不能为空！");
//        //校验有没有绑定的restInfo
//
//        return R.ok(groupService.removeById(id));
//    }
//
//    @GetMapping("/list")
//    @ApiOperation(value = "查看列表")
//    public R list(){
//        return R.ok(groupService.list());
//    }
//
//    @GetMapping("/export/{groupId}")
//    @ApiOperation(value = "导出分组配置")
//    public void exportConfig(@ApiParam(value = "分组id", required = true) @PathVariable("groupId") String groupId, HttpServletResponse response){
//        Group group = Optional.ofNullable(groupService.getById(groupId)).orElseThrow(
//                () -> new IllegalArgumentException("当前分组找不到对应的信息！")
//        );
//        Assert.notEmpty(groupId, "获取分组配置信息分组id不能为空！");
//        List<RestInfo> list = restInfoService.list(
//                new LambdaQueryWrapper<RestInfo>()
//                        .eq(RestInfo::getGroupId, groupId)
//        );
//        if (list.isEmpty()){
//            throw new IllegalArgumentException("当前分组下没有配置信息！");
//        }
////        List<String> configs = list.stream().map(e -> e.generalJsonConfig()).collect(Collectors.toList());
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.addAll(list);
//
//        String fileName = group.getName() + ".json";
//        try {
//            // 设置响应头，指定文件名和类型
//            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName);
//            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
//
//            // 将 JSON 字符串转换为字节数组，并写入响应流
//            response.getOutputStream().write(
//                    jsonArray.toJSONString().getBytes());
//            response.flushBuffer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
