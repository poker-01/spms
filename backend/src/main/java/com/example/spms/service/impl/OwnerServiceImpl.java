package com.example.spms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spms.common.Page;
import com.example.spms.exception.CustomException;
import com.example.spms.enums.ResultCode;
import com.example.spms.mapper.BuildingInfoMapper;
import com.example.spms.mapper.CommunityInfoMapper;
import com.example.spms.mapper.HouseInfoMapper;
import com.example.spms.mapper.OwnerHouseRelMapper;
import com.example.spms.mapper.OwnerInfoMapper;
import com.example.spms.model.bo.OwnerQueryRequest;
import com.example.spms.model.bo.OwnerSaveRequest;
import com.example.spms.model.bo.OwnerUpdateRequest;
import com.example.spms.model.po.BuildingInfo;
import com.example.spms.model.po.CommunityInfo;
import com.example.spms.model.po.HouseInfo;
import com.example.spms.model.po.OwnerHouseRel;
import com.example.spms.model.po.OwnerInfo;
import com.example.spms.model.vo.OwnerHouseRelVO;
import com.example.spms.model.vo.OwnerVO;
import com.example.spms.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl extends ServiceImpl<OwnerInfoMapper, OwnerInfo>
        implements OwnerService {

    private final HouseInfoMapper houseInfoMapper;
    private final BuildingInfoMapper buildingInfoMapper;
    private final CommunityInfoMapper communityInfoMapper;
    private final OwnerHouseRelMapper ownerHouseRelMapper;

    @Override
    public Page<OwnerVO> pageQuery(OwnerQueryRequest request) {
        LambdaQueryWrapper<OwnerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OwnerInfo::getIsDeleted, 0);
        wrapper.like(StringUtils.isNotBlank(request.getOwnerName()),
                OwnerInfo::getOwnerName, request.getOwnerName());
        wrapper.eq(StringUtils.isNotBlank(request.getOwnerPhone()),
                OwnerInfo::getOwnerPhone, request.getOwnerPhone());
        wrapper.eq(request.getStatus() != null,
                OwnerInfo::getStatus, request.getStatus());
        wrapper.orderByDesc(OwnerInfo::getCreateTime);

        // 使用 MyBatis-Plus 的 Page（使用完全限定名，不导入）
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OwnerInfo> mpPage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(request.getPageNum(), request.getPageSize());

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OwnerInfo> result = this.page(mpPage, wrapper);

        List<OwnerVO> records = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        // 手动构建自定义 Page 对象
        Page<OwnerVO> customPage = new Page<>();
        customPage.setTotal(result.getTotal());
        customPage.setPages(result.getPages());
        customPage.setCurrent(result.getCurrent());
        customPage.setSize(result.getSize());
        customPage.setRecords(records);
        return customPage;
    }

    @Override
    @Transactional
    public void saveOwner(OwnerSaveRequest request) {
        // 检查电话唯一性
        LambdaQueryWrapper<OwnerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OwnerInfo::getOwnerPhone, request.getOwnerPhone())
                .eq(OwnerInfo::getIsDeleted, 0);
        if (this.count(wrapper) > 0) {
            throw new CustomException(ResultCode.FAIL, "业主电话已存在");
        }

        OwnerInfo entity = new OwnerInfo();
        BeanUtils.copyProperties(request, entity);
        entity.setIsDeleted(0);
        entity.setVersion(0);
        this.save(entity);
    }

    @Override
    @Transactional
    public void updateOwner(OwnerUpdateRequest request) {
        OwnerInfo exist = this.getById(request.getId());
        if (exist == null || exist.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }

        // 检查电话唯一性（排除自己）
        if (StringUtils.isNotBlank(request.getOwnerPhone())) {
            LambdaQueryWrapper<OwnerInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OwnerInfo::getOwnerPhone, request.getOwnerPhone())
                    .eq(OwnerInfo::getIsDeleted, 0)
                    .ne(OwnerInfo::getId, request.getId());
            if (this.count(wrapper) > 0) {
                throw new CustomException(ResultCode.FAIL, "业主电话已存在");
            }
        }

        BeanUtils.copyProperties(request, exist, "id");
        this.updateById(exist);
    }

    @Override
    public OwnerVO getOwnerDetail(Long id) {
        OwnerInfo entity = this.getById(id);
        if (entity == null || entity.getIsDeleted() == 1) {
            throw new CustomException(ResultCode.NOT_FOUND);
        }
        return toVO(entity);
    }

    @Override
    public OwnerVO getOwnerByHouseId(Long houseId) {
        // 查询房屋关联的业主关系
        LambdaQueryWrapper<OwnerHouseRel> relWrapper = new LambdaQueryWrapper<>();
        relWrapper.eq(OwnerHouseRel::getHouseInfoId, houseId)
                .eq(OwnerHouseRel::getIsDeleted, 0)
                .orderByDesc(OwnerHouseRel::getIsPrimary)
                .last("LIMIT 1");
        OwnerHouseRel rel = ownerHouseRelMapper.selectOne(relWrapper);

        if (rel == null) {
            return null;
        }

        OwnerInfo owner = this.getById(rel.getOwnerInfoId());
        return owner != null ? toVO(owner) : null;
    }

    @Override
    public List<OwnerHouseRelVO> listOwnerHouses(Long ownerId) {
        LambdaQueryWrapper<OwnerHouseRel> relWrapper = new LambdaQueryWrapper<>();
        relWrapper.eq(OwnerHouseRel::getOwnerInfoId, ownerId)
                .eq(OwnerHouseRel::getIsDeleted, 0)
                .orderByDesc(OwnerHouseRel::getIsPrimary);
        List<OwnerHouseRel> rels = ownerHouseRelMapper.selectList(relWrapper);

        if (rels.isEmpty()) {
            return List.of();
        }

        return rels.stream().map(rel -> {
            OwnerHouseRelVO vo = new OwnerHouseRelVO();
            BeanUtils.copyProperties(rel, vo);

            // 构建完整房屋名称
            HouseInfo house = houseInfoMapper.selectById(rel.getHouseInfoId());
            if (house != null) {
                BuildingInfo building = buildingInfoMapper.selectById(house.getBuildingId());
                if (building != null) {
                    CommunityInfo community = communityInfoMapper.selectById(building.getCommunityId());
                    if (community != null) {
                        vo.setHouseFullName(community.getCommunityName() + "-"
                                + building.getBuildingName() + "-"
                                + house.getHouseNumber());
                    }
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<OwnerVO> listAll() {
        LambdaQueryWrapper<OwnerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OwnerInfo::getIsDeleted, 0)
                .orderByAsc(OwnerInfo::getOwnerName);
        return this.list(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    private OwnerVO toVO(OwnerInfo entity) {
        OwnerVO vo = new OwnerVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}