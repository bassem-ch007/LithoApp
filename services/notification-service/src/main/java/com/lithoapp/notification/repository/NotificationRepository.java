package com.lithoapp.notification.repository;

import com.lithoapp.notification.entity.Notification;
import com.lithoapp.notification.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * Notifications visible to the calling user:
     *  - personal notifications (recipient_username = :username), OR
     *  - role-wide notifications (recipient_username IS NULL AND recipient_role = :role)
     *    when the user owns that role.
     */
    @Query("""
           SELECT n FROM Notification n
           WHERE (n.recipientUsername = :username)
              OR (n.recipientUsername IS NULL AND n.recipientRole = :role)
           ORDER BY n.createdAt DESC
           """)
    Page<Notification> findVisibleToUser(
            @Param("username") String username,
            @Param("role") String role,
            Pageable pageable);

    @Query("""
           SELECT COUNT(n) FROM Notification n
           WHERE n.status = :status
             AND ( n.recipientUsername = :username
                OR (n.recipientUsername IS NULL AND n.recipientRole = :role) )
           """)
    long countByVisibilityAndStatus(
            @Param("username") String username,
            @Param("role") String role,
            @Param("status") NotificationStatus status);

    /** Mark all UNREAD notifications visible to the user as READ. */
    @Query("""
           UPDATE Notification n
              SET n.status = com.lithoapp.notification.enums.NotificationStatus.READ,
                  n.readAt = CURRENT_TIMESTAMP
            WHERE n.status = com.lithoapp.notification.enums.NotificationStatus.UNREAD
              AND ( n.recipientUsername = :username
                 OR (n.recipientUsername IS NULL AND n.recipientRole = :role) )
           """)
    @org.springframework.data.jpa.repository.Modifying
    int markAllAsRead(@Param("username") String username, @Param("role") String role);
}
