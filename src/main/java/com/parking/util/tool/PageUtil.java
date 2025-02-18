package com.parking.util.tool;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 分页工具类
 */
public class PageUtil {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;

    /**
     * 创建分页请求
     */
    public static Pageable createPageable(Integer page, Integer size) {
        return createPageable(page, size, null);
    }

    /**
     * 创建带排序的分页请求
     */
    public static Pageable createPageable(Integer page, Integer size, Sort sort) {
        int pageNum = page == null || page < 1 ? DEFAULT_PAGE : page;
        int pageSize = size == null || size < 1 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);

        if (sort != null) {
            return PageRequest.of(pageNum - 1, pageSize, sort);
        }
        return PageRequest.of(pageNum - 1, pageSize);
    }

    /**
     * 创建按创建时间降序的分页请求
     */
    public static Pageable createTimeDescPageable(Integer page, Integer size) {
        return createPageable(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
    }
}