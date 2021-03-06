package com.rubber.admin.core.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rubber.admin.core.authorize.AuthorizeKeys;
import com.rubber.admin.core.authorize.AuthorizeTools;
import com.rubber.admin.core.authorize.entity.AuthGroupMenu;
import com.rubber.admin.core.authorize.service.IAuthGroupMenuService;
import com.rubber.admin.core.base.BaseAdminService;
import com.rubber.admin.core.enums.AdminCode;
import com.rubber.admin.core.enums.StatusEnums;
import com.rubber.admin.core.system.entity.SysRole;
import com.rubber.admin.core.system.entity.SysRoleMenu;
import com.rubber.admin.core.system.entity.SysUserRole;
import com.rubber.admin.core.system.exception.RoleException;
import com.rubber.admin.core.system.mapper.SysRoleMapper;
import com.rubber.admin.core.system.model.RoleOptionAuthorize;
import com.rubber.admin.core.system.service.ISysRoleMenuService;
import com.rubber.admin.core.system.service.ISysRoleService;
import com.rubber.admin.core.system.service.ISysUserRoleService;
import com.rubber.admin.core.tools.ServletUtils;
import com.rubber.common.utils.result.code.SysCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色信息表 服务实现类
 * </p>
 *
 * @author luffyu-auto
 * @since 2019-05-13
 */
@Service
public class SysRoleServiceImpl extends BaseAdminService<SysRoleMapper, SysRole> implements ISysRoleService {

    @Resource
    private ISysRoleMenuService iSysRoleMenuService;

    @Resource
    private IAuthGroupMenuService iAuthGroupMenuService;

    @Resource
    private ISysUserRoleService iSysUserRoleService;

    /**
     * 通过用户的id查询角色信息
     * @param userId 用户id
     * @return 返回用户的角色list信息
     */
    @Override
    public List<SysRole> findByUserId(Integer userId) {
        return getBaseMapper().findByUserId(userId);
    }




    @Override
    public SysRole getAndVerifyById(Integer roleId) throws RoleException {
        SysRole byId = getAndVerifyNull(roleId);
        if (byId.getDelFlag() == StatusEnums.DISABLE){
            throw new RoleException(AdminCode.ROLE_IS_DELETE);
        }
        return byId;
    }


    @Override
    public SysRole getInfoById(Integer roleId) throws RoleException {
        SysRole sysRole = getAndVerifyNull(roleId);
        Set<String> menus = new HashSet<>();
        List<SysRoleMenu> roleMenus = iSysRoleMenuService.queryByRoleId(sysRole.getRoleId());
        if (!CollUtil.isEmpty(roleMenus)){
            for (SysRoleMenu sysRoleMenu:roleMenus){
                String optionKey = sysRoleMenu.getOptionKey();
                if (StrUtil.isEmpty(optionKey)){
                    continue;
                }
                String[] split = optionKey.split(AuthorizeKeys.MEMBER_LINK_KEY);
                for (String s:split){
                    menus.add(sysRoleMenu.getMenuId() + AuthorizeKeys.AUTH_LINK_KEY + s);
                }
            }
        }
        sysRole.setRoleMenuOptions(menus);
        return sysRole;
    }

    /**
     * 获取角色信息并验证
     * @param roleId 角色id
     * @return
     * @throws RoleException
     */
    private SysRole getAndVerifyNull(Integer roleId) throws RoleException {
        if(roleId == null){
            throw new RoleException(AdminCode.ROLE_NOT_EXIST);
        }
        SysRole byId = getById(roleId);
        if(byId == null){
            throw new RoleException(AdminCode.ROLE_NOT_EXIST);
        }
        return byId;
    }




    private SysRole getByRoleKey(String roleName)  {
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_name",roleName);
        return getOne(queryWrapper);
    }


    @Transactional(
            rollbackFor = Exception.class
    )
    @Override
    public boolean saveOrUpdateRole(SysRole sysRole) throws RoleException {
        if(sysRole == null){
            return false;
        }
        if(sysRole.getRoleId() == null){
            doSave(sysRole);
        }else {
            doUpdate(sysRole);
        }
        iSysRoleMenuService.addRoleMenuOption(sysRole);
        return true;
    }

    @Transactional(
            rollbackFor = Exception.class
    )
    @Override
    public void delRoleById(Integer roleId) throws RoleException {
        SysRole dbRole = getAndVerifyById(roleId);
        //验证角色是否有关联的用户信息
        List<SysUserRole> listByRoleId = iSysUserRoleService.getListByRoleId(roleId);
        if (!CollUtil.isEmpty(listByRoleId)){
            throw new RoleException(AdminCode.ROLE_DELETE_ERROR,"删除的角色[{}]被用户关联，无法删除",dbRole.getRoleName());
        }
        //删除角色关联的菜单
        if (!iSysRoleMenuService.removeByRoleId(roleId)){
            throw new RoleException(AdminCode.ROLE_DELETE_ERROR,"删除角色[{}]关联的菜单失败",dbRole.getRoleName());
        }
        if(!removeById(roleId)){
            throw new RoleException(AdminCode.ROLE_DELETE_ERROR,"删除角色{}信息失败",dbRole.getRoleName());
        }
    }


    @Override
    public List<RoleOptionAuthorize> queryRoleAuthorizeKeys(Set<Integer> roleId) {
        List<SysRoleMenu> roleMenus = iSysRoleMenuService.queryByRoleId(roleId);
        return getRoleAuthorizeKeys(roleMenus);
    }


    @Override
    public Map<Integer, Set<String>> margeRoleMenuOptions(List<RoleOptionAuthorize> roleOptionAuthorizes) {
        if (CollUtil.isEmpty(roleOptionAuthorizes)){
            return new HashMap<>(2);
        }
        Map<Integer,Set<String>> map = new HashMap<>(roleOptionAuthorizes.size() * 2);
        for (RoleOptionAuthorize roleOptionAuthorize:roleOptionAuthorizes){
            Set<String> options = map.get(roleOptionAuthorize.getMenuId());
            if (options == null){
                options = roleOptionAuthorize.getOptionKeys();
            }else {
                options.addAll(roleOptionAuthorize.getOptionKeys());
            }
            map.put(roleOptionAuthorize.getMenuId(),options);
        }
        return map;
    }

    /**
     * 处理查询的角色关联的菜单信息
     * @param roleMenus 角色的菜单信息
     * @return 返回菜单信息
     */
    private List<RoleOptionAuthorize> getRoleAuthorizeKeys(List<SysRoleMenu> roleMenus){
        //获取角色关联的菜单信息
        if (CollUtil.isEmpty(roleMenus)){
            return null;
        }
        List<RoleOptionAuthorize> roleOptionAuthorizes = new ArrayList<>();

        Set<Integer> needAuthMenus = new HashSet<>();
        for (SysRoleMenu sysRoleMenu:roleMenus){
            roleOptionAuthorizes.add(new RoleOptionAuthorize(sysRoleMenu));
            if (StrUtil.isEmpty(sysRoleMenu.getOptionKey())){
                continue;
            }
            needAuthMenus.add(sysRoleMenu.getMenuId());
        }
        if (CollectionUtil.isNotEmpty(needAuthMenus)){
            doFindRoleMenuAuthorize(needAuthMenus,roleOptionAuthorizes);
        }
        return roleOptionAuthorizes;
    }

    /**
     * 查询权限的信息
     * @param needAuthMenus
     * @param roleOptionAuthorizes
     */
    private void doFindRoleMenuAuthorize(Set<Integer> needAuthMenus,List<RoleOptionAuthorize> roleOptionAuthorizes){
        List<AuthGroupMenu> authGroupMenus = iAuthGroupMenuService.queryByMenuId(needAuthMenus);
        if (CollUtil.isEmpty(authGroupMenus)){
            return;
        }
        Map<String, String> menuApplyOptionKey = authGroupMenus.stream().collect(Collectors.toMap(
                i -> { return i.getMenuId() + AuthorizeKeys.UNDER_LINE_KEY +i.getOptionKey(); },
                AuthGroupMenu::getRelatedApply));
        for (RoleOptionAuthorize roleOptionAuthorize:roleOptionAuthorizes){
            if (CollUtil.isEmpty(roleOptionAuthorize.getOptionKeys())){
                continue;
            }
            Set<String> authorizeKeys = new HashSet<>();
            for (String option:roleOptionAuthorize.getOptionKeys()){
                String key = roleOptionAuthorize.getMenuId() + AuthorizeKeys.UNDER_LINE_KEY + option;
                String applies = menuApplyOptionKey.get(key);
                if (StringUtils.isEmpty(applies)){
                    continue;
                }
                String[] appliesArray = applies.split(AuthorizeKeys.MEMBER_LINK_KEY);
                for (String apply:appliesArray){
                    authorizeKeys.add(AuthorizeTools.createAuthKey(apply,option));
                }
            }
            roleOptionAuthorize.setAuthorizeKeys(authorizeKeys);
        }
    }

    /**
     * 保存角色信息
     * @param sysRole
     * @return
     */
    private boolean doSave(SysRole sysRole) throws RoleException {
        //验证key是否已经存在
        SysRole byRoleKey = getByRoleKey(sysRole.getRoleName());
        if(byRoleKey != null){
            throw new RoleException(AdminCode.ROLE_NAME_EXIST);
        }
        Date now = new Date();
        Integer loginUserId = ServletUtils.getLoginUserId();

        sysRole.setCreateBy(loginUserId);
        sysRole.setCreateTime(now);
        sysRole.setUpdateBy(loginUserId);
        sysRole.setUpdateTime(now);
        if(!save(sysRole)){
            throw new RoleException(SysCode.SYSTEM_ERROR,"添加角色信息{}失败",sysRole);
        }
        return true;
    }


    private boolean doUpdate(SysRole sysRole) throws RoleException {
        SysRole dbRole = getAndVerifyById(sysRole.getRoleId());

        dbRole.setRoleName(sysRole.getRoleName());
        dbRole.setRemark(sysRole.getRemark());
        dbRole.setSeq(sysRole.getSeq());
        dbRole.setStatus(sysRole.getStatus());

        if(!updateById(dbRole)){
            throw new RoleException(AdminCode.ROLE_NOT_EXIST,"更新角色信息失败",dbRole);
        }
        return true;
    }



    @Override
    public boolean updateById(SysRole entity) {
        if(entity == null){
            return false;
        }
        Date now = new Date();
        Integer loginUserId = ServletUtils.getLoginUserId();
        entity.setUpdateBy(loginUserId);
        entity.setUpdateTime(now);
        return super.updateById(entity);
    }




}
