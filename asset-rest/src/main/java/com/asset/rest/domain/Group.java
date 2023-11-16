package com.asset.rest.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fisher
 * @date 2023-09-13: 15:56
 */
@TableName("rest_group")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Group {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    private String description;

}
