package com.example.spms.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 巡检记录VO（选做）
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
@Builder
public class InspectionVO {

    private Long id;

    private String inspectionNo;

    private String location;

    private Integer inspectionType;

    private String inspectionTypeName;

    private String inspectionDesc;

    private Integer result;

    private String resultName;

    private String resultDesc;

    private String attachments;

    private String inspectorName;

    private String inspectionTime;

    private String createTime;
}