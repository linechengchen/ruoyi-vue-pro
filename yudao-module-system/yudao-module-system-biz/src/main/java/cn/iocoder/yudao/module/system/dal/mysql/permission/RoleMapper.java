package cn.iocoder.yudao.module.system.dal.mysql.permission;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.ArrayUtils;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.AdminRoleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.RoleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.tenant.TenantDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapperX<RoleDO> {

    default PageResult<RoleDO> selectPage(RolePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RoleDO>()
                .likeIfPresent(RoleDO::getName, reqVO.getName())
                .likeIfPresent(RoleDO::getCode, reqVO.getCode())
                .eqIfPresent(RoleDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(BaseDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(RoleDO::getSort));
    }

    @TenantIgnore
    default PageResult<AdminRoleDO> selectAdminPage(RolePageReqVO reqVO) {
        Object val1 = ArrayUtils.get(reqVO.getCreateTime(), 0);
        Object val2 = ArrayUtils.get(reqVO.getCreateTime(), 1);
        return selectJoinPage(reqVO, AdminRoleDO.class, new MPJLambdaWrapper<RoleDO>()
                .selectAll(RoleDO.class)
                .likeIfExists(AdminRoleDO::getName, reqVO.getName())
                .likeIfExists(AdminRoleDO::getCode, reqVO.getCode())
                .eqIfExists(AdminRoleDO::getStatus, reqVO.getStatus())
                .geIfExists(AdminRoleDO::getCreateTime, val1)
                .leIfExists(AdminRoleDO::getCreateTime, val2)
                .selectAs(TenantDO::getName, AdminRoleDO::getTenantName)
                .leftJoin(TenantDO.class, TenantDO::getId, AdminRoleDO::getTenantId));

    }


    PageResult<AdminRoleDO> selectPage(RolePageReqVO reqVO, LambdaQueryWrapper<AdminRoleDO> adminRoleDOLambdaQueryWrapper);

    default RoleDO selectByName(String name) {
        return selectOne(RoleDO::getName, name);
    }

    default RoleDO selectByCode(String code) {
        return selectOne(RoleDO::getCode, code);
    }

    default List<RoleDO> selectListByStatus(@Nullable Collection<Integer> statuses) {
        return selectList(RoleDO::getStatus, statuses);
    }

}
