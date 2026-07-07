package com.example.spms.controller;

import com.example.spms.common.CommunityFilterHelper;
import com.example.spms.common.Page;
import com.example.spms.common.Result;
import com.example.spms.model.bo.CommunityQueryRequest;
import com.example.spms.model.bo.CommunitySaveRequest;
import com.example.spms.model.bo.CommunityUpdateRequest;
import com.example.spms.model.vo.CommunityVO;
import com.example.spms.security.LoginUser;
import com.example.spms.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小区管理控制器
 */
@Tag(name = "小区管理", description = "小区 CRUD 接口")
@RestController
@RequestMapping("/api/v1/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @Operation(summary = "分页查询小区")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('community:query')")
    public Result<Page<CommunityVO>> page(CommunityQueryRequest request,
                                          @AuthenticationPrincipal LoginUser loginUser) {
        Long communityId = CommunityFilterHelper.getCommunityId(loginUser);
        return Result.success(communityService.pageQuery(request, communityId));
    }

    @Operation(summary = "查询全部小区（下拉列表用）")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('community:query')")
    public Result<List<CommunityVO>> list(@AuthenticationPrincipal LoginUser loginUser) {
        Long communityId = CommunityFilterHelper.getCommunityId(loginUser);
        return Result.success(communityService.listAll(communityId));
    }

    @Operation(summary = "查询小区详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('community:query')")
    public Result<CommunityVO> detail(@PathVariable Long id) {
        return Result.success(communityService.getCommunityDetail(id));
    }

    @Operation(summary = "新增小区")
    @PostMapping
    @PreAuthorize("hasAuthority('community:add')")
    public Result<Void> save(@Valid @RequestBody CommunitySaveRequest request) {
        communityService.saveCommunity(request);
        return Result.success();
    }

    @Operation(summary = "修改小区")
    @PutMapping
    @PreAuthorize("hasAuthority('community:edit')")
    public Result<Void> update(@Valid @RequestBody CommunityUpdateRequest request) {
        communityService.updateCommunity(request);
        return Result.success();
    }

    @Operation(summary = "删除小区")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('community:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        communityService.removeById(id);
        return Result.success();
    }
}