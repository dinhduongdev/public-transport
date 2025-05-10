package com.publictransport.services.impl;

import com.publictransport.models.RouteVariant;
import com.publictransport.repositories.RouteVariantRepository;
import com.publictransport.services.RouteVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteVariantServiceImpl implements RouteVariantService {

    @Autowired
    private RouteVariantRepository routeVariantRepository;

    @Override
    public List<RouteVariant> getRouteVariants(int page, int size) {
        return routeVariantRepository.findAllRouteVariants(page, size);
    }

    @Override
    public Long countRouteVariants() {
        return routeVariantRepository.countRouteVariants();
    }
}
