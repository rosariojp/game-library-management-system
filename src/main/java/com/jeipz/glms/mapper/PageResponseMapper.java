package com.jeipz.glms.mapper;

import com.jeipz.glms.model.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PageResponseMapper<T> implements Function<Page<T>, PageResponse<T>> {

    @Override
    public PageResponse<T> apply(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber() + 1,
                page.getTotalPages(),
                page.getTotalElements());
    }
}
