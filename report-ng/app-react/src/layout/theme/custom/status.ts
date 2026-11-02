export const statusColors = {
    passed: "#417336",
    skipped: "#f7af3e",
    failed: "#e63946",
    crashed: "#5d6f81",
    running: "#0089b6",
    failed_minor: "#f7af3e",
    expected_failed: "#4f031b",
};

export type Status = keyof typeof statusColors;
