package com.lithoapp.drainage.scheduler;

import com.lithoapp.drainage.entity.Drainage;
import com.lithoapp.drainage.enums.DrainageStatus;
import com.lithoapp.drainage.notification.NotificationPublisher;
import com.lithoapp.drainage.repository.DrainageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Runs daily to detect drainages that:
 *  - approach their planned removal date (pre-reminder, configurable lead-time),
 *  - reach their planned removal date (day-of reminder),
 *  - have passed their planned removal date (overdue).
 *
 * Each detected drainage produces a notification event toward
 * notification-service through {@link NotificationPublisher}. The "*-SentAt"
 * columns guarantee one notification per phase, per drainage.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DrainageReminderScheduler {

    private final DrainageRepository drainageRepository;
    private final NotificationPublisher notificationPublisher;

    @Value("${drainage.reminder.days-before:3}")
    private int daysBefore;

    @Scheduled(cron = "${drainage.reminder.cron:0 0 8 * * *}")
    @Transactional
    public void checkReminders() {
        log.info("[DrainageScheduler] Running daily reminder check (daysBefore={})", daysBefore);

        LocalDate today = LocalDate.now();
        sendPreReminders(today.plusDays(daysBefore));
        sendDayOfReminders(today);
        sendOverdueReminders(today);
    }

    private void sendPreReminders(LocalDate targetDate) {
        List<Drainage> drainages = drainageRepository
                .findByStatusAndPlannedRemovalDateAndPreReminderSentAtIsNull(
                        DrainageStatus.ACTIVE, targetDate);

        if (drainages.isEmpty()) return;

        for (Drainage drainage : drainages) {
            notificationPublisher.drainageRemovalSoon(drainage, daysBefore);
            drainage.setPreReminderSentAt(LocalDateTime.now());
            drainageRepository.save(drainage);
        }
        log.info("[DrainageScheduler] Pre-reminders dispatched for {} drainage(s)", drainages.size());
    }

    private void sendDayOfReminders(LocalDate today) {
        List<Drainage> drainages = drainageRepository
                .findByStatusAndPlannedRemovalDateAndDayOfReminderSentAtIsNull(
                        DrainageStatus.ACTIVE, today);

        if (drainages.isEmpty()) return;

        for (Drainage drainage : drainages) {
            notificationPublisher.drainageRemovalSoon(drainage, 0);
            drainage.setDayOfReminderSentAt(LocalDateTime.now());
            drainageRepository.save(drainage);
        }
        log.info("[DrainageScheduler] Day-of reminders dispatched for {} drainage(s)", drainages.size());
    }

    private void sendOverdueReminders(LocalDate today) {
        List<Drainage> drainages = drainageRepository
                .findByStatusAndPlannedRemovalDateBeforeAndOverdueReminderSentAtIsNull(
                        DrainageStatus.ACTIVE, today);

        if (drainages.isEmpty()) return;

        for (Drainage drainage : drainages) {
            notificationPublisher.drainageOverdue(drainage);
            drainage.setOverdueReminderSentAt(LocalDateTime.now());
            drainageRepository.save(drainage);
        }
        log.info("[DrainageScheduler] Overdue reminders dispatched for {} drainage(s)", drainages.size());
    }
}
