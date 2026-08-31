package com.ylmao.admin.service;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.dto.DeptDto;
import com.ylmao.admin.entity.Dept;
import com.ylmao.admin.entity.User;
import com.ylmao.admin.mapper.DeptMapper;
import com.ylmao.admin.mapper.UserMapper;
import com.ylmao.admin.vo.DeptVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeptService {

    private final DeptMapper deptMapper;
    private final UserMapper userMapper;

    public List<DeptVo.DeptOptionVo> listOptions() {
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Dept::getOrderNum);
        return deptMapper.selectList(wrapper).stream().map(DeptVo.DeptOptionVo::from).toList();
    }

    public List<DeptVo.DeptListVo> selectList(DeptDto.DeptList deptList) {
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        if (deptList != null) {
            if (StrUtil.isNotBlank(deptList.deptName())) {
                wrapper.like(Dept::getDeptName, deptList.deptName());
            }
            if (StrUtil.isNotBlank(deptList.parentId())) {
                wrapper.eq(Dept::getParentId, deptList.parentId());
            }
        }
        wrapper.orderByAsc(Dept::getOrderNum);
        return deptMapper.selectList(wrapper).stream().map(DeptVo.DeptListVo::from).toList();
    }

    @Transactional
    public void insert(DeptDto.DeptInsert deptInsert) {
        // 同级部门名称不能重复。
        if (checkDeptNameUnique(deptInsert.parentId(), deptInsert.deptName()) != null) {
            throw new BusinessException("同级部门名称已存在");
        }
        Dept dept = new Dept(deptInsert);
        fillDeptPath(dept);
        int rows = deptMapper.insert(dept);
        if (rows <= 0) {
            throw new BusinessException("新增部门失败");
        }
    }

    @Transactional
    public void updateById(DeptDto.DeptUpdate deptUpdate) {
        // 上级部门不能形成环，同级名称不能重复。
        Dept oldDept = deptMapper.selectById(deptUpdate.deptId());
        if (oldDept == null) {
            throw new BusinessException("部门不存在");
        }
        if (deptUpdate.deptId().equals(deptUpdate.parentId())) {
            throw new BusinessException("上级部门不能选择自身");
        }
        if (isChildDept(deptUpdate.deptId(), deptUpdate.parentId())) {
            throw new BusinessException("上级部门不能选择当前部门的下级");
        }
        Dept sameNameDept = checkDeptNameUnique(deptUpdate.parentId(), deptUpdate.deptName());
        if (sameNameDept != null && !sameNameDept.getDeptId().equals(deptUpdate.deptId())) {
            throw new BusinessException("同级部门名称已存在");
        }
        Dept dept = new Dept(deptUpdate);
        fillDeptPath(dept);
        int rows = deptMapper.updateById(dept);
        if (rows <= 0) {
            throw new BusinessException("部门不存在或修改失败");
        }
    }

    @Transactional
    public void deleteById(String ids) {
        if (StrUtil.isBlank(ids)) {
            throw new BusinessException("请选择要删除的部门");
        }
        List<String> idList = StrUtil.splitTrim(ids, ',');
        Long childCount = deptMapper.selectCount(new LambdaQueryWrapper<Dept>().in(Dept::getParentId, idList));
        if (childCount != null && childCount > 0) {
            throw new BusinessException("存在下级部门，不能删除");
        }
        Long userCount = userMapper.selectCount(new LambdaQueryWrapper<User>().in(User::getDeptId, idList));
        if (userCount != null && userCount > 0) {
            throw new BusinessException("部门已分配给用户，不能删除");
        }
        int rows = deptMapper.deleteByIds(idList);
        if (rows <= 0) {
            throw new BusinessException("部门不存在或删除失败");
        }
    }

    @Transactional
    public void updateDeptEnabled(DeptDto.UpdateEnabled updateEnabled) {
        Dept oldDept = deptMapper.selectById(updateEnabled.deptId());
        if (oldDept == null) {
            throw new BusinessException("部门不存在");
        }
        oldDept.setIsEnabled(updateEnabled.isEnabled());
        int rows = deptMapper.updateById(oldDept);
        if (rows <= 0) {
            throw new BusinessException("修改部门状态失败");
        }
    }

    public Dept selectById(String deptId) {
        return deptMapper.selectById(deptId);
    }

    public Dept checkDeptNameUnique(String parentId, String deptName) {
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getParentId, normalizeParentId(parentId));
        wrapper.eq(Dept::getDeptName, deptName);
        return deptMapper.selectOne(wrapper);
    }

    private void fillDeptPath(Dept dept) {
        String parentId = normalizeParentId(dept.getParentId());
        dept.setParentId(parentId);
        if ("0".equals(parentId)) {
            dept.setDeptPath("0");
            return;
        }
        Dept parent = deptMapper.selectById(parentId);
        if (parent == null) {
            throw new BusinessException("上级部门不存在");
        }
        dept.setDeptPath(StrUtil.isNotBlank(parent.getDeptPath())
                ? parent.getDeptPath() + "," + parent.getDeptId()
                : parent.getDeptId());
    }

    private boolean isChildDept(String deptId, String parentId) {
        if (StrUtil.isBlank(parentId) || "0".equals(parentId)) {
            return false;
        }
        Dept parent = deptMapper.selectById(parentId);
        return parent != null && StrUtil.isNotBlank(parent.getDeptPath())
                && ("," + parent.getDeptPath() + ",").contains("," + deptId + ",");
    }

    private String normalizeParentId(String parentId) {
        return StrUtil.isBlank(parentId) ? "0" : parentId;
    }
}
