package com.lithoapp.drainage.service;

import com.lithoapp.drainage.enums.DrainageType;
import com.lithoapp.drainage.enums.JJType;

import java.time.LocalDate;
import java.util.Map;

/**
 * Encapsulates the clinical default durations (in days) for each drainage
 * device type and JJ stent sub-type.
 *
 * <p>These durations are used to automatically compute a {@code plannedRemovalDate}
 * when the urologist does not explicitly provide one during drainage creation.
 * If the urologist supplies a date, that value is always preserved as-is.</p>
 *
 * <p>Duration guidelines (may be adapted to local clinical protocols):</p>
 * <ul>
 *   <li>Ureteral catheter (URETERAL): 14 days – short-term post-op drain</li>
 *   <li>Nephrostomy (NEPHROSTOMY): 30 days – medium-term percutaneous drain</li>
 *   <li>Standard JJ 6F / Large JJ 7F: 90 days – standard recommended interval</li>
 *   <li>Biodegradable JJ: 30 days – dissolves over ~4 weeks</li>
 *   <li>Metallic JJ (permanent): 365 days – long-term / annual review</li>
 * </ul>
 */
public final class DrainageDurationPolicy {

    /**
     * Default durations (days) for non-JJ drainage types.
     * JJ is deliberately absent; its duration depends on {@link JJType}.
     */
    private static final Map<DrainageType, Integer> TYPE_DURATIONS = Map.of(
            DrainageType.URETERAL,    14,
            DrainageType.NEPHROSTOMY, 30
    );

    /**
     * Default durations (days) for JJ stent sub-types.
     */
    private static final Map<JJType, Integer> JJ_TYPE_DURATIONS = Map.of(
            JJType.STANDARD_6F,   90,
            JJType.LARGE_7F,      90,
            JJType.BIODEGRADABLE, 30,
            JJType.METALLIC,      365
    );

    private DrainageDurationPolicy() {
        // Utility class — no instances
    }

    /**
     * Returns the default planned removal date for the given device combination
     * and placement date, or {@code null} if no default is defined.
     *
     * @param type     the drainage type (never null)
     * @param jjType   the JJ sub-type; must be non-null when {@code type == JJ}
     * @param placedAt the date the device was placed (never null)
     * @return computed removal date, or {@code null}
     */
    public static LocalDate computeDefault(DrainageType type, JJType jjType, LocalDate placedAt) {
        Integer days = defaultDays(type, jjType);
        return (days != null) ? placedAt.plusDays(days) : null;
    }

    /**
     * Returns the default duration in days for the given type/jjType combination,
     * or {@code null} if no default is defined.
     *
     * @param type   the drainage type (never null)
     * @param jjType the JJ sub-type; required when {@code type == JJ}
     * @return duration in days, or {@code null}
     */
    public static Integer defaultDays(DrainageType type, JJType jjType) {
        if (type == DrainageType.JJ) {
            return (jjType != null) ? JJ_TYPE_DURATIONS.get(jjType) : null;
        }
        return TYPE_DURATIONS.get(type);
    }
}
