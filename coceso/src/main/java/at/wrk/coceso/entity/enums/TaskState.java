package at.wrk.coceso.entity.enums;

/**
 * Assigned - Zugewiesen
 * ZBO - Zum Berufungsort -
 * ABO - Am Berufungsort -
 * ZAO - Zum Abgabeort -
 * AAO - Am Abgabeort -
 * Detached - Einsatz nicht mehr zugewiesen
 */
public enum TaskState {
    Assigned, ZBO, ABO, ZAO, AAO, Detached;

    public static TaskState[] orderedValues() {
        return new TaskState[]{Assigned, ZBO, ABO, ZAO, AAO, Detached};
    }
}
