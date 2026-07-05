package com.example.spms.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 统一分页返回格式
 *
 * @Author SPMS
 * @Date 2026/07/03
 */
@Data
public class Page<T> {

    /** 总记录数 */
    private long total;

    /** 总页数 */
    private long pages;

    /** 当前页 */
    private long current;

    /** 每页大小 */
    private long size;

    /** 数据列表 */
    private List<T> records;

    public static <T> Page<T> of(IPage<?> page, List<T> records) {
        Page<T> result = new Page<>();
        result.setTotal(page.getTotal());
        result.setPages(page.getPages());
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setRecords(records);
        return result;
    }

    public static <T> Page<T> of(IPage<T> page) {
        Page<T> result = new Page<>();
        result.setTotal(page.getTotal());
        result.setPages(page.getPages());
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setRecords(page.getRecords());
        return result;
    }

    public static <T> Page<T> empty(long current, long size) {
        Page<T> result = new Page<>();
        result.setTotal(0);
        result.setPages(0);
        result.setCurrent(current);
        result.setSize(size);
        result.setRecords(Collections.emptyList());
        return result;
    }
}