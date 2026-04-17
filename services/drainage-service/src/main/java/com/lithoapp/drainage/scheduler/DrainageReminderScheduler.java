package com.lithoapp.drainage.scheduler;

import com.lithoapp.drainage.entity.Drainage;
import com.lithoapp.drainage.enums.DrainageStatus;
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
 * Runs daily to detect drainages that are approaching their planned removal date.
 *
 * Current behaviour: logs only — no real notifications are sent.
 *
 * Future extension points:
 *  - Inject a NotificationClient (Feign) and replace the log.info calls
 *    with actual HTTP calls to the Notification Service.
 *  - Alternatively, publish a domain event to Kafka so the
 *    Notification Service can consume it asynchronously.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DrainageReminderScheduler {

    private final DrainageRepository drainageRepository;

    /**
     * Configurable lead-time (days before planned removal) for the pre-reminder.
     * Default: 3 days. Override in application.yml via drainage.reminder.days-before.
     */
    @Value("${drainage.reminder.days-before:3}")
    private int daysBefore;

    /**
     * Runs every day at 08:00 (server time).
     * Cron: second minute hour day month weekday
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void checkReminders() {
        log.info("[DrainageScheduler] Running daily reminder check (daysBefore={})", daysBefore);

        LocalDate today = LocalDate.now();
        LocalDate preReminderTarget = today.plusDays(daysBefore);

        sendPreReminders(preReminderTarget);
        sendDayOfReminders(today);
    }

    // ── Pre-reminders (X days before planned removal) ─────────────────────────

    private void sendPreReminders(LocalDate targetDate) {
        List<Drainage> drainages = drainageRepository
                .findByStatusAndPlannedRemovalDateAndPreReminderSentAtIsNull(
                        DrainageStatus.ACTIVE, targetDate);

        if (drainages.isEmpty()) {
            log.debug("[DrainageScheduler] No pre-reminders to send for {}", targetDate);
            return;
        }

        for (Drainage drainage : drainages) {
            // TODO: replace with NotificationClient.sendPreReminder(drainage)
            log.info(
                "[NOTIFICATION - PRE-REMINDER] Drainage id={} | patient={} | type={} | side={} " +
                "| planned removal in {} days ({})",
                drainage.getId(),
                drainage.getPatientId(),
                drainage.getDrainageType(),
                drainage.getSide(),
                daysBefore,
                targetDate
            );

            drainage.setPreReminderSentAt(LocalDateTime.now());
            drainageRepository.save(drainage);
        }

        log.info("[DrainageScheduler] Pre-reminders logged for {} drainage(s)", drainages.size());
    }

    // ── Day-of reminders (removal date = today) ───────────────────────────────

    private void sendDayOfReminders(LocalDate today) {
        List<Drainage> drainages = drainageRepository
                .findByStatusAndPlannedRemovalDateAndDayOfReminderSentAtIsNull(
                        DrainageStatus.ACTIVE, today);

        if (drainages.isEmpty()) {
            log.debug("[DrainageScheduler] No day-of reminders to send for {}", today);
            return;
        }

        for (Drainage drainage : drainages) {
            // TODO: replace with NotificationClient.sendDayOfReminder(drainage)
            log.info(
                "[NOTIFICATION - DAY-OF REMINDER] Drainage id={} | patient={} | type={} | side={} " +
                "| planned removal is TODAY ({})",
                drainage.getId(),
                drainage.getPatientId(),
                drainage.getDrainageType(),
                drainage.getSide(),
                today
            );

            drainage.setDayOfReminderSentAt(LocalDateTime.now());
            drainageRepository.save(drainage);
        }

        log.info("[DrainageScheduler] Day-of reminders logged for {} drainage(s)", drainages.size());
    }
}
