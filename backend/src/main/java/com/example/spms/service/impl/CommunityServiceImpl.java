package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.common.Page;
import com.example.spms.exception.CustomException;
import com.example.spms.mapper.CommunityInfoMapper;
import com.example.spms.model.bo.CommunityQueryRequest;
import com.example.spms.model.bo.CommunitySaveRequest;
import com.example.spms.model.bo.CommunityUpdateRequest;
import com.example.spms.model.po.CommunityInfo;
import com.example.spms.model.vo.CommunityVO;
import com.example.spms.service.CommunityService;
import com.example.spms.enums.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl extends ServiceImpl<CommunityInfoMapper, CommunityInfo>
        implements CommunityService {

    @Override
    public Page<CommunityVO> pageQuery(CommunityQueryRequest request, Long communityId) {
        LambdaQueryWrapper<CommunityInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommunityInfo::getIsDeleted, 0);
        wrapper.like(StringUtils.isNotBlank(request.getCommunityName()),
                CommunityInfo::getCommunityName, request.getCommunityName());
        wrapper.eq(StringUtils.isNotBlank(request.getCity()),
                CommunityInfo::getCity, request.getCity());
        wrapper.eq(request.getStatus() != null,
                CommunityInfo::getStatus, request.getStatus());
        // 非超级管理员，仅查询自己绑定的小区
        if (communityId != null) {
            wrapper.eq(CommunityInfo::getId, communityId);
        }
        wrapper.orderByDesc(CommunityInfo::getCreateTime);

        // 使用 MyBatis-Plus 的 Page（使用完全限定名，不导入）
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CommunityInfo> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(request.getPageNum(), request.getPageSize());

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<CommunityInfo> result = this.page(mpPage, wrapper);

        List<CommunityVO> records = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        // 手动构建自定义 Page 对象
        Page<CommunityVO> customPage = new Page<>();
        customPage.setTotal(result.getTotal());
        customPage.setPages(result.getPages());
        customPage.setCurrent(result.getCurrent());
        customPage.setSize(result.getSize());
        customPage.setRecords(records);
        return customPage;
    }

    @Override
    @Transactional
    public void saveCommunity(CommunitySaveRequest request) {
        // 检查编码唯一性
        LambdaQueryWrapper<CommunityInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommunityInfo::getCommunityCode, request.getCommunityCode())
                .eq(CommunityInfo::getIsDeleted, 0);
        if (this.count(wrapper) > 0) {
            throw new CustomException(ResultCode.FAIL, "小区编码已存在");
        }

        CommunityInfo entity = new CommunityInfo();
        BeanUtils.copyProperties(request, entity);
        entity.setIsDeleted(0);
        entity.setVersion(0);
        this.save(entity);
    }

    @Override
    @Transactional
    public void updateCommunity(CommunityUpdateRequest request) {
        CommunityInfo exist = this.getById(request.getId());
        if (exist == null || exist.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }

        // 检查编码唯一性（排除自己）
        if (StringUtils.isNotBlank(request.getCommunityCode())) {
            LambdaQueryWrapper<CommunityInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CommunityInfo::getCommunityCode, request.getCommunityCode())
                    .eq(CommunityInfo::getIsDeleted, 0)
                    .ne(CommunityInfo::getId, request.getId());
            if (this.count(wrapper) > 0) {
                throw new CustomException(ResultCode.FAIL, "小区编码已存在");
            }
        }

        BeanUtils.copyProperties(request, exist, "id");
        this.updateById(exist);
    }

    @Override
    public CommunityVO getCommunityDetail(Long id) {
        CommunityInfo entity = this.getById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }
        return toVO(entity);
    }

    @Override
    public List<CommunityVO> listAll(Long communityId) {
        LambdaQueryWrapper<CommunityInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommunityInfo::getIsDeleted, 0);
        // 非超级管理员，仅查询自己绑定的小区
        if (communityId != null) {
            wrapper.eq(CommunityInfo::getId, communityId);
        }
        wrapper.orderByAsc(CommunityInfo::getCommunityName);
        return this.list(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    private CommunityVO toVO(CommunityInfo entity) {
        CommunityVO vo = new CommunityVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}