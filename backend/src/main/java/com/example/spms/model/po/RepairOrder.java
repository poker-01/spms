package com.example.spms.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName repair_order
 */
@TableName(value ="repair_order")
@Data
public class RepairOrder implements Serializable {
    private Long id;

    private String orderNo;

    private Long houseId;

    private Long ownerId;

    private String repairType;

    private String repairDesc;

    private String repairPhone;

    private Integer priority;

    private Integer status;

    private Long assigneeId;

    private Date assignTime;

    private Date repairTime;

    private BigDecimal repairCost;

    private Integer evaluateScore;

    private String evaluateComment;

    private Integer isDeleted;

    private Integer version;

    private Date createTime;

    private Date updateTime;

    private Long createUser;

    private Long updateUser;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        RepairOrder other = (RepairOrder) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderNo() == null ? other.getOrderNo() == null : this.getOrderNo().equals(other.getOrderNo()))
            && (this.getHouseId() == null ? other.getHouseId() == null : this.getHouseId().equals(other.getHouseId()))
            && (this.getOwnerId() == null ? other.getOwnerId() == null : this.getOwnerId().equals(other.getOwnerId()))
            && (this.getRepairType() == null ? other.getRepairType() == null : this.getRepairType().equals(other.getRepairType()))
            && (this.getRepairDesc() == null ? other.getRepairDesc() == null : this.getRepairDesc().equals(other.getRepairDesc()))
            && (this.getRepairPhone() == null ? other.getRepairPhone() == null : this.getRepairPhone().equals(other.getRepairPhone()))
            && (this.getPriority() == null ? other.getPriority() == null : this.getPriority().equals(other.getPriority()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getAssigneeId() == null ? other.getAssigneeId() == null : this.getAssigneeId().equals(other.getAssigneeId()))
            && (this.getAssignTime() == null ? other.getAssignTime() == null : this.getAssignTime().equals(other.getAssignTime()))
            && (this.getRepairTime() == null ? other.getRepairTime() == null : this.getRepairTime().equals(other.getRepairTime()))
            && (this.getRepairCost() == null ? other.getRepairCost() == null : this.getRepairCost().equals(other.getRepairCost()))
            && (this.getEvaluateScore() == null ? other.getEvaluateScore() == null : this.getEvaluateScore().equals(other.getEvaluateScore()))
            && (this.getEvaluateComment() == null ? other.getEvaluateComment() == null : this.getEvaluateComment().equals(other.getEvaluateComment()))
            && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()))
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getCreateUser() == null ? other.getCreateUser() == null : this.getCreateUser().equals(other.getCreateUser()))
            && (this.getUpdateUser() == null ? other.getUpdateUser() == null : this.getUpdateUser().equals(other.getUpdateUser()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderNo() == null) ? 0 : getOrderNo().hashCode());
        result = prime * result + ((getHouseId() == null) ? 0 : getHouseId().hashCode());
        result = prime * result + ((getOwnerId() == null) ? 0 : getOwnerId().hashCode());
        result = prime * result + ((getRepairType() == null) ? 0 : getRepairType().hashCode());
        result = prime * result + ((getRepairDesc() == null) ? 0 : getRepairDesc().hashCode());
        result = prime * result + ((getRepairPhone() == null) ? 0 : getRepairPhone().hashCode());
        result = prime * result + ((getPriority() == null) ? 0 : getPriority().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getAssigneeId() == null) ? 0 : getAssigneeId().hashCode());
        result = prime * result + ((getAssignTime() == null) ? 0 : getAssignTime().hashCode());
        result = prime * result + ((getRepairTime() == null) ? 0 : getRepairTime().hashCode());
        result = prime * result + ((getRepairCost() == null) ? 0 : getRepairCost().hashCode());
        result = prime * result + ((getEvaluateScore() == null) ? 0 : getEvaluateScore().hashCode());
        result = prime * result + ((getEvaluateComment() == null) ? 0 : getEvaluateComment().hashCode());
        result = prime * result + ((getIsDeleted() == null) ? 0 : getIsDeleted().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getCreateUser() == null) ? 0 : getCreateUser().hashCode());
        result = prime * result + ((getUpdateUser() == null) ? 0 : getUpdateUser().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", houseId=").append(houseId);
        sb.append(", ownerId=").append(ownerId);
        sb.append(", repairType=").append(repairType);
        sb.append(", repairDesc=").append(repairDesc);
        sb.append(", repairPhone=").append(repairPhone);
        sb.append(", priority=").append(priority);
        sb.append(", status=").append(status);
        sb.append(", assigneeId=").append(assigneeId);
        sb.append(", assignTime=").append(assignTime);
        sb.append(", repairTime=").append(repairTime);
        sb.append(", repairCost=").append(repairCost);
        sb.append(", evaluateScore=").append(evaluateScore);
        sb.append(", evaluateComment=").append(evaluateComment);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", version=").append(version);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", createUser=").append(createUser);
        sb.append(", updateUser=").append(updateUser);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}