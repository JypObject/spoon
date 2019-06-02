package com.huadong.spoon.dao.user;

import com.huadong.spoon.model.user.UmsAdminPermissionRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户权限自定义Dao
 *
 * @author jinjinhui
 * @date 2019/5/9
 */
public interface UmsAdminPermissionRelationDao {
    int insertList(@Param("list") List<UmsAdminPermissionRelation> list);
}
