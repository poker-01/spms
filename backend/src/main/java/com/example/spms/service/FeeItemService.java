package com.example.spms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spms.common.Page;
import com.example.spms.model.bo.FeeItemQueryRequest;
import com.example.spms.model.bo.FeeItemSaveRequest;
import com.example.spms.model.bo.FeeItemUpdateRequest;
import com.example.spms.model.po.FeeItem;
import com.example.spms.model.vo.FeeItemVO;

/**
 * 费用项目服务接口
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
public interface FeeItemService extends IService<FeeItem> {

    /**
     * 分页查询费用项目
     */
    Page<FeeItemVO> pageFeeItems(FeeItemQueryRequest request);

    /**
     * 新增费用项目
     */
    void saveFeeItem(FeeItemSaveRequest request);

    /**
     * 修改费用项目
     */
    void updateFeeItem(FeeItemUpdateRequest request);

    /**
     * 获取所有启用的费用项目
     */
    java.util.List<FeeItem> listEnabled();
}