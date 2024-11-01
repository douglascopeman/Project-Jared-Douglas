package javasimulation;

// Define IntegratorType enum to represent different integration methods
@Deprecated
public enum IntegratorType {
    EULER,
    SYMPLECTIC_EULER,
    THREE_STEP_LEAPFROG,
    YOSHIDA,
    FOREST_RUTH
}