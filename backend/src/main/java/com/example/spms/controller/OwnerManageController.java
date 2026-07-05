package com.example.spms.controller;

import com.example.spms.common.Page;
import com.example.spms.common.Result;
import com.example.spms.model.bo.OwnerQueryRequest;
import com.example.spms.model.bo.OwnerSaveRequest;
import com.example.spms.model.bo.OwnerUpdateRequest;
import com.example.spms.model.vo.OwnerHouseRelVO;
import com.example.spms.model.vo.OwnerVO;
import com.example.spms.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "业主管理（后端）", description = "业主 CRUD 接口")
@RestController
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
public class OwnerManageController {

    private final OwnerService ownerService;

    @Operation(summary = "分页查询业主")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('owner:query')")
    public Result<Page<OwnerVO>> page(OwnerQueryRequest request) {
        return Result.success(ownerService.pageQuery(request));
    }

    @Operation(summary = "根据房屋ID查询业主（关联查询）")
    @GetMapping("/by-house/{houseId}")
    @PreAuthorize("hasAuthority('owner:query')")
    public Result<OwnerVO> getByHouse(@PathVariable Long houseId) {
        return Result.success(ownerService.getOwnerByHouseId(houseId));
    }

    @Operation(summary = "查询业主关联房屋列表")
    @GetMapping("/{ownerId}/houses")
    @PreAuthorize("hasAuthority('owner:query')")
    public Result<List<OwnerHouseRelVO>> getOwnerHouses(@PathVariable Long ownerId) {
        return Result.success(ownerService.listOwnerHouses(ownerId));
    }

    @Operation(summary = "查询全部业主（下拉列表用）")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('owner:query')")
    public Result<List<OwnerVO>> list() {
        return Result.success(ownerService.listAll());
    }

    @Operation(summary = "查询业主详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('owner:query')")
    public Result<OwnerVO> detail(@PathVariable Long id) {
        return Result.success(ownerService.getOwnerDetail(id));
    }

    @Operation(summary = "新增业主")
    @PostMapping
    @PreAuthorize("hasAuthority('owner:add')")
    public Result<Void> save(@Valid @RequestBody OwnerSaveRequest request) {
        ownerService.saveOwner(request);
        return Result.success();
    }

    @Operation(summary = "修改业主")
    @PutMapping
    @PreAuthorize("hasAuthority('owner:edit')")
    public Result<Void> update(@Valid @RequestBody OwnerUpdateRequest request) {
        ownerService.updateOwner(request);
        return Result.success();
    }

    @Operation(summary = "删除业主")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('owner:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        ownerService.removeById(id);
        return Result.success();
    }
}