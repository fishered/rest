package com.asset.rest.controller;

import com.asset.rest.biz.RestBiz;
import com.asset.rest.factory.req.RestParam;
import com.asset.rest.res.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author fisher
 * @date 2023-10-16: 10:25
 */
@RestController
@RequestMapping("process")
public class CoreController {

    @Autowired
    private RestBiz restBiz;

    @PostMapping("/code")
    public R process(@Validated @RequestBody RestParam req){
        return R.ok(restBiz.rest(req.getCode(), req.getInput()));
    }


}
