package com.asset.rest.service.impl;

import com.asset.rest.domain.RestInfo;
import com.asset.rest.service.RestInfoService;
import com.asset.rest.service.mapper.RestInfoMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author fisher
 * @date 2023-09-13: 17:18
 */
@Service
public class RestInfoServiceImpl extends ServiceImpl<RestInfoMapper, RestInfo> implements RestInfoService {
}
