package com.example.spms.controller;

import com.example.spms.common.Page;
import com.example.spms.common.Result;
import com.example.spms.model.bo.HouseQueryRequest;
import com.example.spms.model.bo.HouseSaveRequest;
import com.example.spms.model.bo.HouseUpdateRequest;
import com.example.spms.model.vo.HouseVO;
import com.example.spms.service.HouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "房屋管理", description = "房屋 CRUD 接口")
@RestController
@RequestMapping("/api/v1/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;

    @Operation(summary = "分页查询房屋")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('house:query')")
    public Result<Page<HouseVO>> page(HouseQueryRequest request) {
        return Result.success(houseService.pageQuery(request));
    }

    @Operation(summary = "查询某楼栋所有房屋（关联查询）")
    @GetMapping("/by-building/{buildingId}")
    @PreAuthorize("hasAuthority('house:query')")
    public Result<List<HouseVO>> listByBuilding(@PathVariable Long buildingId) {
        return Result.success(houseService.listByBuildingId(buildingId));
    }

    @Operation(summary = "查询某业主所有房屋（关联查询）")
    @GetMapping("/by-owner/{ownerId}")
    @PreAuthorize("hasAuthority('house:query')")
    public Result<List<HouseVO>> listByOwner(@PathVariable Long ownerId) {
        return Result.success(houseService.listByOwnerId(ownerId));
    }

    @Operation(summary = "查询全部房屋（下拉列表用）")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('house:query')")
    public Result<List<HouseVO>> list() {
        return Result.success(houseService.listAll());
    }

    @Operation(summary = "查询房屋详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('house:query')")
    public Result<HouseVO> detail(@PathVariable Long id) {
        return Result.success(houseService.getHouseDetail(id));
    }

    @Operation(summary = "新增房屋")
    @PostMapping
    @PreAuthorize("hasAuthority('house:add')")
    public Result<Void> save(@Valid @RequestBody HouseSaveRequest request) {
        houseService.saveHouse(request);
        return Result.success();
    }

    @Operation(summary = "修改房屋")
    @PutMapping
    @PreAuthorize("hasAuthority('house:edit')")
    public Result<Void> update(@Valid @RequestBody HouseUpdateRequest request) {
        houseService.updateHouse(request);
        return Result.success();
    }

    @Operation(summary = "删除房屋")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('house:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        houseService.removeById(id);
        return Result.success();
    }
}