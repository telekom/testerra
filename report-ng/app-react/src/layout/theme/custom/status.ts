export const statusColors = {
    passed: "#417336",
    skipped: "#f7af3e",
    failed: "#e63946",
    norun: "#757575",
    failed_minor: "#f7af3e",
    expected_failed: "#4f031b",
};

export type Status = keyof typeof statusColors;
