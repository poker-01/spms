package com.example.spms.model.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 投诉回复请求
 *
 * @Author SPMS
 * @Date 2026/07/05
 */
@Data
public class ComplaintReplyRequest {

    @NotNull(message = "投诉ID不能为空")
    private Long complaintId;

    @NotBlank(message = "回复内容不能为空")
    private String replyContent;
}