package com.publictransport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation đánh dấu các phương thức có param RouteNavigationFilter
 * để build lại filter từ từ khóa nếu được set.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RebuildRouteNavigationFilterIfKeywordsSet {
}
