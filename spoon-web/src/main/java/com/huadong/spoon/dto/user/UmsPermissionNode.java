package com.huadong.spoon.dto.user;

import com.huadong.spoon.model.user.UmsPermission;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 * @author jinjinhui
 * @date 2019/5/9
 */
public class UmsPermissionNode extends UmsPermission {
    @Getter
    @Setter
    private List<UmsPermissionNode> children;
}
