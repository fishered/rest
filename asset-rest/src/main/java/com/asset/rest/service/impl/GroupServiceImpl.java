package com.asset.rest.service.impl;

import com.asset.rest.domain.Group;
import com.asset.rest.service.GroupService;
import com.asset.rest.service.mapper.GroupMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author fisher
 * @date 2023-09-13: 17:17
 */
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {
}
