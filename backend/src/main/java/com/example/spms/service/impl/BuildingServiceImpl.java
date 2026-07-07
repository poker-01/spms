package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.common.Page;
import com.example.spms.exception.CustomException;
import com.example.spms.mapper.BuildingInfoMapper;
import com.example.spms.mapper.CommunityInfoMapper;
import com.example.spms.model.bo.BuildingQueryRequest;
import com.example.spms.model.bo.BuildingSaveRequest;
import com.example.spms.model.bo.BuildingUpdateRequest;
import com.example.spms.model.po.BuildingInfo;
import com.example.spms.model.po.CommunityInfo;
import com.example.spms.model.vo.BuildingVO;
import com.example.spms.service.BuildingService;
import com.example.spms.enums.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl extends ServiceImpl<BuildingInfoMapper, BuildingInfo>
        implements BuildingService {

    private final CommunityInfoMapper communityInfoMapper;

    @Override
    public Page<BuildingVO> pageQuery(BuildingQueryRequest request, Long communityId) {
        LambdaQueryWrapper<BuildingInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuildingInfo::getIsDeleted, 0);
        // 优先使用前端传入的 communityId，如果没有则使用当前用户绑定的小区
        if (request.getCommunityId() != null) {
            wrapper.eq(BuildingInfo::getCommunityId, request.getCommunityId());
        } else if (communityId != null) {
            wrapper.eq(BuildingInfo::getCommunityId, communityId);
        }
        wrapper.like(StringUtils.isNotBlank(request.getBuildingName()),
                BuildingInfo::getBuildingName, request.getBuildingName());
        wrapper.eq(request.getStatus() != null,
                BuildingInfo::getStatus, request.getStatus());
        wrapper.orderByAsc(BuildingInfo::getBuildingCode);

        // 使用完全限定名创建 MyBatis-Plus 的分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<BuildingInfo> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(request.getPageNum(), request.getPageSize());

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<BuildingInfo> result = this.page(mpPage, wrapper);

        List<BuildingVO> records = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        // 手动构建自定义 Page 对象返回
        Page<BuildingVO> customPage = new Page<>();
        customPage.setTotal(result.getTotal());
        customPage.setPages(result.getPages());
        customPage.setCurrent(result.getCurrent());
        customPage.setSize(result.getSize());
        customPage.setRecords(records);
        return customPage;
    }

    @Override
    @Transactional
    public void saveBuilding(BuildingSaveRequest request) {
        // 检查小区是否存在
        CommunityInfo community = communityInfoMapper.selectById(request.getCommunityId());
        if (community == null || community.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND, "所属小区不存在");
        }

        // 检查楼栋编码在同一个小区内唯一
        LambdaQueryWrapper<BuildingInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuildingInfo::getCommunityId, request.getCommunityId())
                .eq(BuildingInfo::getBuildingCode, request.getBuildingCode())
                .eq(BuildingInfo::getIsDeleted, 0);
        if (this.count(wrapper) > 0) {
            throw new CustomException(ResultCode.FAIL, "该小区下楼栋编码已存在");
        }

        BuildingInfo entity = new BuildingInfo();
        BeanUtils.copyProperties(request, entity);
        entity.setIsDeleted(0);
        entity.setVersion(0);
        this.save(entity);

        // 更新小区楼栋总数
        community.setTotalBuildings(community.getTotalBuildings() != null ?
                community.getTotalBuildings() + 1 : 1);
        communityInfoMapper.updateById(community);
    }

    @Override
    @Transactional
    public void updateBuilding(BuildingUpdateRequest request) {
        BuildingInfo exist = this.getById(request.getId());
        if (exist == null || exist.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }

        // 如果修改了小区ID，检查新小区是否存在
        if (request.getCommunityId() != null && !request.getCommunityId().equals(exist.getCommunityId())) {
            CommunityInfo community = communityInfoMapper.selectById(request.getCommunityId());
            if (community == null || community.getIsDeleted() == 1) {
                throw new CustomException(ResultCode.NOT_FOUND, "所属小区不存在");
            }
        }

        // 检查编码唯一性（排除自己）
        if (StringUtils.isNotBlank(request.getBuildingCode())) {
            LambdaQueryWrapper<BuildingInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BuildingInfo::getCommunityId,
                            request.getCommunityId() != null ? request.getCommunityId() : exist.getCommunityId())
                    .eq(BuildingInfo::getBuildingCode, request.getBuildingCode())
                    .eq(BuildingInfo::getIsDeleted, 0)
                    .ne(BuildingInfo::getId, request.getId());
            if (this.count(wrapper) > 0) {
                throw new CustomException(ResultCode.FAIL, "该小区下楼栋编码已存在");
            }
        }

        BeanUtils.copyProperties(request, exist, "id");
        this.updateById(exist);
    }

    @Override
    public BuildingVO getBuildingDetail(Long id) {
        BuildingInfo entity = this.getById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }
        return toVO(entity);
    }

    @Override
    public List<BuildingVO> listByCommunityId(Long communityId) {
        LambdaQueryWrapper<BuildingInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuildingInfo::getCommunityId, communityId)
                .eq(BuildingInfo::getIsDeleted, 0)
                .orderByAsc(BuildingInfo::getBuildingCode);
        return this.list(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BuildingVO> listAll(Long communityId) {
        LambdaQueryWrapper<BuildingInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BuildingInfo::getIsDeleted, 0);
        if (communityId != null) {
            wrapper.eq(BuildingInfo::getCommunityId, communityId);
        }
        wrapper.orderByAsc(BuildingInfo::getBuildingCode);
        return this.list(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    private BuildingVO toVO(BuildingInfo entity) {
        BuildingVO vo = new BuildingVO();
        BeanUtils.copyProperties(entity, vo);
        // 填充小区名称
        CommunityInfo community = communityInfoMapper.selectById(entity.getCommunityId());
        if (community != null) {
            vo.setCommunityName(community.getCommunityName());
        }
        return vo;
    }
}