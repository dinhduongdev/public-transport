package com.publictransport.aspects;

import com.publictransport.dto.params.RouteNavigationFilter;
import com.publictransport.models.Coordinates;
import com.publictransport.proxies.MapProxy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class RouteNavigationFilterAspect {
    private final MapProxy mapProxy;

    @Autowired
    public RouteNavigationFilterAspect(MapProxy mapProxy) {
        this.mapProxy = mapProxy;
    }

    /**
     * Intercept bất kỳ phương thức nào có annotation @RebuildRouteNavigationFilterIfKeywordsSet
     * và có tham số là RouteNavigationFilter (tên nào cũng được).
     * Giải thích:
     * execution: hàm nào được thực thi mà có kiểu trả về là "*"(bất kỳ kiểu trả về nào),
     * dấu "*" thứ hai: bất kỳ tên hàm nào.
     * "..": bất kỳ tham số nào, trong đó có ít nhất một tham số là RouteNavigationFilter.
     */
    @Around("@annotation(com.publictransport.annotations.RebuildRouteNavigationFilterIfKeywordsSet) " +
            "&& execution(* *(.., com.publictransport.dto.params.RouteNavigationFilter, ..))")
    public Object interceptAnnotatedFilterMethod(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();

        // tìm kiếm tham số RouteNavigationFilter trong args
        RouteNavigationFilter oldFilter = null;
        int filterIndex = -1;

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof RouteNavigationFilter) {
                oldFilter = (RouteNavigationFilter) args[i];
                filterIndex = i;
                break;
            }
        }

        if (oldFilter == null) {
            throw new IllegalArgumentException("filter không được null");
        }

        // Chỉ xử lý khi keywords được set
        if (oldFilter.areKeywordsSet()) {
            var optStartPair = mapProxy.getCoordinates(oldFilter.getStartKw());
            var optEndPair = mapProxy.getCoordinates(oldFilter.getEndKw());

            if (optStartPair.isEmpty() || optEndPair.isEmpty()) {
                throw new IllegalArgumentException(String.format(
                        "Không tìm thấy tọa độ cho từ khóa: startKw='%s', endKw='%s'",
                        oldFilter.getStartKw(), oldFilter.getEndKw()));
            }

            // Tạo filter mới với tọa độ từ keywords
            RouteNavigationFilter newFilter = new RouteNavigationFilter();
            newFilter.setStartCoords(optStartPair.get().getRight().toLatLngString());
            newFilter.setEndCoords(optEndPair.get().getRight().toLatLngString());
            newFilter.setMaxNumOfTrip(oldFilter.getMaxNumOfTrip());

            // Thay thế filter cũ bằng filter mới
            args[filterIndex] = newFilter;
        }

        return pjp.proceed(args);
    }
}
