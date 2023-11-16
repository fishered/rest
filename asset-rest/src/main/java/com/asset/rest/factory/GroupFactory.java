package com.asset.rest.factory;

import cn.hutool.core.bean.BeanUtil;
import com.asset.rest.domain.Group;
import com.asset.rest.factory.req.GroupReq;

import java.util.Objects;
import java.util.UUID;

/**
 * @author fisher
 * @date 2023-09-26: 14:32
 */
public class GroupFactory {

    public static Group createGroup(GroupReq req){
        if (Objects.isNull(req)){
            return null;
        }
        Group group = new Group();
        BeanUtil.copyProperties(req, group);
//        if (group.getId() == null){
//            group.setId(UUID.randomUUID().toString());
//        }
        return group;
    }

}
