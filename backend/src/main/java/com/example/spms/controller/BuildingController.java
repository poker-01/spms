package com.example.spms.controller;

import com.example.spms.common.CommunityFilterHelper;
import com.example.spms.common.Page;
import com.example.spms.common.Result;
import com.example.spms.model.bo.BuildingQueryRequest;
import com.example.spms.model.bo.BuildingSaveRequest;
import com.example.spms.model.bo.BuildingUpdateRequest;
import com.example.spms.model.vo.BuildingVO;
import com.example.spms.security.LoginUser;
import com.example.spms.service.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 楼栋管理控制器
 */
@Tag(name = "楼栋管理", description = "楼栋 CRUD 及关联查询接口")
@RestController
@RequestMapping("/api/v1/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @Operation(summary = "分页查询楼栋")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('building:query')")
    public Result<Page<BuildingVO>> page(BuildingQueryRequest request,
                                         @AuthenticationPrincipal LoginUser loginUser) {
        Long communityId = CommunityFilterHelper.getCommunityId(loginUser);
        return Result.success(buildingService.pageQuery(request, communityId));
    }

    @Operation(summary = "查询某小区所有楼栋（关联查询）")
    @GetMapping("/by-community/{communityId}")
    @PreAuthorize("hasAuthority('building:query')")
    public Result<List<BuildingVO>> listByCommunity(@PathVariable Long communityId) {
        return Result.success(buildingService.listByCommunityId(communityId));
    }

    @Operation(summary = "查询全部楼栋（下拉列表用）")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('building:query')")
    public Result<List<BuildingVO>> list(@AuthenticationPrincipal LoginUser loginUser) {
        Long communityId = CommunityFilterHelper.getCommunityId(loginUser);
        return Result.success(buildingService.listAll(communityId));
    }

    @Operation(summary = "查询楼栋详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('building:query')")
    public Result<BuildingVO> detail(@PathVariable Long id) {
        return Result.success(buildingService.getBuildingDetail(id));
    }

    @Operation(summary = "新增楼栋")
    @PostMapping
    @PreAuthorize("hasAuthority('building:add')")
    public Result<Void> save(@Valid @RequestBody BuildingSaveRequest request) {
        buildingService.saveBuilding(request);
        return Result.success();
    }

    @Operation(summary = "修改楼栋")
    @PutMapping
    @PreAuthorize("hasAuthority('building:edit')")
    public Result<Void> update(@Valid @RequestBody BuildingUpdateRequest request) {
        buildingService.updateBuilding(request);
        return Result.success();
    }

    @Operation(summary = "删除楼栋")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('building:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        buildingService.removeById(id);
        return Result.success();
    }
}