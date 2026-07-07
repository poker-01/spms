package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.common.Page;
import com.example.spms.exception.CustomException;
import com.example.spms.enums.ResultCode;
import com.example.spms.mapper.BuildingInfoMapper;
import com.example.spms.mapper.HouseInfoMapper;
import com.example.spms.mapper.OwnerHouseRelMapper;
import com.example.spms.model.bo.HouseQueryRequest;
import com.example.spms.model.bo.HouseSaveRequest;
import com.example.spms.model.bo.HouseUpdateRequest;
import com.example.spms.model.po.BuildingInfo;
import com.example.spms.model.po.HouseInfo;
import com.example.spms.model.po.OwnerHouseRel;
import com.example.spms.model.vo.HouseVO;
import com.example.spms.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseServiceImpl extends ServiceImpl<HouseInfoMapper, HouseInfo>
        implements HouseService {

    private final BuildingInfoMapper buildingInfoMapper;
    private final OwnerHouseRelMapper ownerHouseRelMapper;

    @Override
    public Page<HouseVO> pageQuery(HouseQueryRequest request, Long communityId) {
        LambdaQueryWrapper<HouseInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HouseInfo::getIsDeleted, 0);
        wrapper.eq(request.getBuildingId() != null,
                HouseInfo::getBuildingId, request.getBuildingId());
        wrapper.like(StringUtils.isNotBlank(request.getHouseNumber()),
                HouseInfo::getHouseNumber, request.getHouseNumber());
        wrapper.eq(request.getStatus() != null,
                HouseInfo::getStatus, request.getStatus());

        // 非超级管理员，仅查询自己小区下的房屋（通过楼栋关联）
        if (communityId != null && request.getBuildingId() == null) {
            // 先查出该小区下所有楼栋ID
            LambdaQueryWrapper<BuildingInfo> buildingWrapper = new LambdaQueryWrapper<>();
            buildingWrapper.eq(BuildingInfo::getCommunityId, communityId)
                    .eq(BuildingInfo::getIsDeleted, 0);
            List<Long> buildingIds = buildingInfoMapper.selectList(buildingWrapper).stream()
                    .map(BuildingInfo::getId)
                    .collect(Collectors.toList());
            if (buildingIds.isEmpty()) {
                Page<HouseVO> emptyPage = new Page<>();
                emptyPage.setTotal(0L);
                emptyPage.setPages(0L);
                emptyPage.setCurrent(request.getPageNum());
                emptyPage.setSize(request.getPageSize());
                emptyPage.setRecords(List.of());
                return emptyPage;
            }
            wrapper.in(HouseInfo::getBuildingId, buildingIds);
        }

        wrapper.orderByAsc(HouseInfo::getHouseNumber);

        // 使用 MyBatis-Plus 的 Page（使用完全限定名，不导入）
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<HouseInfo> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(request.getPageNum(), request.getPageSize());

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<HouseInfo> result = this.page(mpPage, wrapper);

        List<HouseVO> records = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        // 手动构建自定义 Page 对象
        Page<HouseVO> customPage = new Page<>();
        customPage.setTotal(result.getTotal());
        customPage.setPages(result.getPages());
        customPage.setCurrent(result.getCurrent());
        customPage.setSize(result.getSize());
        customPage.setRecords(records);
        return customPage;
    }

    @Override
    @Transactional
    public void saveHouse(HouseSaveRequest request) {
        // 检查楼栋是否存在
        BuildingInfo building = buildingInfoMapper.selectById(request.getBuildingId());
        if (building == null || building.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND, "所属楼栋不存在");
        }

        // 检查房屋编号在同一个楼栋内唯一
        LambdaQueryWrapper<HouseInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HouseInfo::getBuildingId, request.getBuildingId())
                .eq(HouseInfo::getHouseNumber, request.getHouseNumber())
                .eq(HouseInfo::getIsDeleted, 0);
        if (this.count(wrapper) > 0) {
            throw new CustomException(ResultCode.FAIL, "该楼栋下房屋编号已存在");
        }

        HouseInfo entity = new HouseInfo();
        BeanUtils.copyProperties(request, entity);
        entity.setIsDeleted(0);
        entity.setVersion(0);
        this.save(entity);
    }

    @Override
    @Transactional
    public void updateHouse(HouseUpdateRequest request) {
        HouseInfo exist = this.getById(request.getId());
        if (exist == null || exist.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }

        // 如果修改了楼栋ID，检查新楼栋是否存在
        if (request.getBuildingId() != null && !request.getBuildingId().equals(exist.getBuildingId())) {
            BuildingInfo building = buildingInfoMapper.selectById(request.getBuildingId());
            if (building == null || building.getIsDeleted() == 1) {
                throw new CustomException(ResultCode.NOT_FOUND, "所属楼栋不存在");
            }
        }

        // 检查编号唯一性（排除自己）
        if (StringUtils.isNotBlank(request.getHouseNumber())) {
            LambdaQueryWrapper<HouseInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HouseInfo::getBuildingId,
                            request.getBuildingId() != null ? request.getBuildingId() : exist.getBuildingId())
                    .eq(HouseInfo::getHouseNumber, request.getHouseNumber())
                    .eq(HouseInfo::getIsDeleted, 0)
                    .ne(HouseInfo::getId, request.getId());
            if (this.count(wrapper) > 0) {
                throw new CustomException(ResultCode.FAIL, "该楼栋下房屋编号已存在");
            }
        }

        BeanUtils.copyProperties(request, exist, "id");
        this.updateById(exist);
    }

    @Override
    public HouseVO getHouseDetail(Long id) {
        HouseInfo entity = this.getById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }
        return toVO(entity);
    }

    @Override
    public List<HouseVO> listByBuildingId(Long buildingId) {
        LambdaQueryWrapper<HouseInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HouseInfo::getBuildingId, buildingId)
                .eq(HouseInfo::getIsDeleted, 0)
                .orderByAsc(HouseInfo::getHouseNumber);
        return this.list(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HouseVO> listByOwnerId(Long ownerId) {
        // 先查询业主关联的房屋ID
        LambdaQueryWrapper<OwnerHouseRel> relWrapper = new LambdaQueryWrapper<>();
        relWrapper.eq(OwnerHouseRel::getOwnerInfoId, ownerId)
                .eq(OwnerHouseRel::getIsDeleted, 0);
        List<Long> houseIds = ownerHouseRelMapper.selectList(relWrapper).stream()
                .map(OwnerHouseRel::getHouseInfoId)
                .collect(Collectors.toList());

        if (houseIds.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<HouseInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(HouseInfo::getId, houseIds)
                .eq(HouseInfo::getIsDeleted, 0)
                .orderByAsc(HouseInfo::getHouseNumber);
        return this.list(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HouseVO> listAvailableByBuildingId(Long buildingId) {
        // 查询该楼栋下所有未删除的房屋
        LambdaQueryWrapper<HouseInfo> houseWrapper = new LambdaQueryWrapper<>();
        houseWrapper.eq(HouseInfo::getBuildingId, buildingId)
                .eq(HouseInfo::getIsDeleted, 0)
                .orderByAsc(HouseInfo::getHouseNumber);
        List<HouseInfo> allHouses = this.list(houseWrapper);

        if (allHouses.isEmpty()) {
            return List.of();
        }

        // 查询已被占用的房屋ID（存在有效业主关联的房屋）
        List<Long> allHouseIds = allHouses.stream().map(HouseInfo::getId).collect(Collectors.toList());
        LambdaQueryWrapper<OwnerHouseRel> relWrapper = new LambdaQueryWrapper<>();
        relWrapper.in(OwnerHouseRel::getHouseInfoId, allHouseIds)
                .eq(OwnerHouseRel::getIsDeleted, 0);
        var occupiedHouseIds = ownerHouseRelMapper.selectList(relWrapper).stream()
                .map(OwnerHouseRel::getHouseInfoId)
                .collect(Collectors.toSet());

        // 过滤掉已被占用的房屋
        return allHouses.stream()
                .filter(h -> !occupiedHouseIds.contains(h.getId()))
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HouseVO> listAll(Long communityId) {
        LambdaQueryWrapper<HouseInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HouseInfo::getIsDeleted, 0);

        if (communityId != null) {
            // 先查出该小区下所有楼栋ID
            LambdaQueryWrapper<BuildingInfo> buildingWrapper = new LambdaQueryWrapper<>();
            buildingWrapper.eq(BuildingInfo::getCommunityId, communityId)
                    .eq(BuildingInfo::getIsDeleted, 0);
            List<Long> buildingIds = buildingInfoMapper.selectList(buildingWrapper).stream()
                    .map(BuildingInfo::getId)
                    .collect(Collectors.toList());
            if (buildingIds.isEmpty()) {
                return List.of();
            }
            wrapper.in(HouseInfo::getBuildingId, buildingIds);
        }

        wrapper.orderByAsc(HouseInfo::getHouseNumber);
        return this.list(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    private HouseVO toVO(HouseInfo entity) {
        HouseVO vo = new HouseVO();
        BeanUtils.copyProperties(entity, vo);

        BuildingInfo building = buildingInfoMapper.selectById(entity.getBuildingId());
        if (building != null) {
            vo.setBuildingName(building.getBuildingName());
        }
        return vo;
    }
}