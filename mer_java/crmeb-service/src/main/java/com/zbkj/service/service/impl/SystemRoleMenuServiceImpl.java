package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.admin.SystemRoleMenu;
import com.zbkj.service.dao.SystemRoleMenuDao;
import com.zbkj.service.service.SystemRoleMenuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SystemRoleMenuServiceImpl 接口实现
 * 角色菜单关联服务实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class SystemRoleMenuServiceImpl extends ServiceImpl<SystemRoleMenuDao, SystemRoleMenu> implements SystemRoleMenuService {

    @Resource
    private SystemRoleMenuDao dao;

    /**
     * 通过角色id删除
     * @param rid 角色id
     */
    @Override
    public Boolean deleteByRid(Integer rid) {
        LambdaUpdateWrapper<SystemRoleMenu> luw = Wrappers.lambdaUpdate();
        luw.eq(SystemRoleMenu::getRid, rid);
        return dao.delete(luw) > 0;
    }

    /**
     * 通过角色id获取菜单列表
     * @param rid 角色id
     * @return List
     */
    @Override
    public List<Integer> getMenuListByRid(Integer rid) {
        LambdaQueryWrapper<SystemRoleMenu> lqw = Wrappers.lambdaQuery();
        lqw.select(SystemRoleMenu::getMenuId);
        lqw.eq(SystemRoleMenu::getRid, rid);
        List<SystemRoleMenu> roleMenuList = dao.selectList(lqw);
        return roleMenuList.stream().map(SystemRoleMenu::getMenuId).collect(Collectors.toList());
    }

    /**
     * 通过角色id和菜单id删除
     * @param rid 角色id
     * @param menuId 菜单id
     * @return Boolean
     */
    @Override
    public Boolean deleteByRidAndMenuId(Integer rid, Integer menuId) {
        LambdaUpdateWrapper<SystemRoleMenu> luw = Wrappers.lambdaUpdate();
        luw.eq(SystemRoleMenu::getRid, rid);
        luw.eq(SystemRoleMenu::getMenuId, menuId);
        return dao.delete(luw) > 0;
    }

    /**
     * 通过角色id和菜单id列表删除
     * @param rid 角色id
     * @param menuIdList 菜单id
     * @return Boolean
     */
    @Override
    public Boolean deleteByRidAndMenuIdList(Integer rid, List<Integer> menuIdList) {
        LambdaUpdateWrapper<SystemRoleMenu> luw = Wrappers.lambdaUpdate();
        luw.eq(SystemRoleMenu::getRid, rid);
        luw.in(SystemRoleMenu::getMenuId, menuIdList);
        return dao.delete(luw) > 0;
    }
}

