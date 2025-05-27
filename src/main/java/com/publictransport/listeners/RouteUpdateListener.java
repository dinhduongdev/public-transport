package com.publictransport.listeners;


import com.publictransport.events.RouteUpdatedEvent;
import com.publictransport.models.Favorite;
import com.publictransport.models.Notification;
import com.publictransport.repositories.FavoriteRepository;
import com.publictransport.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RouteUpdateListener {
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @EventListener
    public void handleRouteUpdated(RouteUpdatedEvent event) {
        Long routeId = event.getRouteId();
        List<Favorite> favorites = favoriteRepository.findAllByTargetIdAndTargetType(routeId, "ROUTE");
        for (Favorite favorite : favorites) {
            if (Boolean.TRUE.equals(favorite.getIsObserved())) {
                Notification noti = new Notification();
                noti.setUser(favorite.getUser());
                noti.setTitle("Cập nhật tuyến đường");
                noti.setMessage("Tuyến đường bạn theo dõi đã được cập nhật.");
                noti.setCreatedAt(LocalDateTime.now());
                notificationRepository.save(noti);
            }
        }
    }
}
