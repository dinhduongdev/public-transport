package com.publictransport.utils;

import com.publictransport.dto.params.BaseFilter;
import org.hibernate.query.Query;

public class PaginationUtils {
    private PaginationUtils() {
    }

    public static <T> void setQueryResultsRange(Query<T> query, BaseFilter filter) {
        int offset = (filter.getPage() - 1) * filter.getSize();
        query.setFirstResult(offset);
        query.setMaxResults(filter.getSize());
    }

    public static <T> void setQueryResultsRange(Query<T> query, int limit, int offset) {
        query.setFirstResult(offset);
        query.setMaxResults(limit);
    }
}
