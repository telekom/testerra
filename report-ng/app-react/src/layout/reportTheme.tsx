import {createTheme} from "@mui/material";
import React from "react";

const statusColors = {
    passed: '#417336',
    skipped: '#f7af3e',
    failed: '#e63946',
    crashed: '#5d6f81',
    running: '#0089b6',
    failed_minor: '#f7af3e',
    expected_failed: '#4f031b'
}
export type Status = keyof typeof statusColors;

// Update the Chip's color options to include blue, purple and green option
declare module '@mui/material/Chip' {
    export interface ChipPropsColorOverrides {
        blue: true;
        green: true;
        purple: true;
        lightGrey: true;
    }
}


export const reportTheme = createTheme({
    custom: {
        statusColors,
    },
    cssVariables: {
        nativeColor: true,
    },
    palette: {
        primary: {
            main: "#4b4b4b"
        },
        secondary: {
            main: "#f6a821"
        },
        background: {
            default: '#fafafa'
        },

        // these only use main and contrastText, but the others have to be present to not cause typing errors in the component
        blue: {
            main: '#0085FF20',
            light: '#0085FF20',
            dark: '#0085FF20',
            contrastText: '#0085FF',
        },
        green: {
            main: '#3F848820',
            light: '#3F848820',
            dark: '#3F848820',
            contrastText: '#3F8488',
        },
        purple: {
            main: '#884AB920',
            light: '#884AB920',
            dark: '#884AB920',
            contrastText: '#884AB9',
        },
        lightGrey: {
            main: '#0000008A',
            light: '#0000008A',
            dark: '#0000008A',
            contrastText: '#0000008A',
        }
    },
    mixins: {
        cardHeight: (units: number) => ({
            height: units * 192,
        }),
    },
    components: {
        MuiTableContainer: {
            styleOverrides: {
                root: {
                    border: "1px solid rgba(0, 0, 0, 0.12)",
                    boxShadow: "none"
                },
            },
        },
        MuiTableHead: {
            styleOverrides: {
                root: {
                    "& .MuiTableCell-root": {
                        borderBottomColor: "rgba(0, 0, 0, 0.12)",
                    },
                },
            },
        },
        MuiLink: {
            styleOverrides: {
                root: () => ({
                    color: "blue",
                    textDecoration: "underline",
                    textDecorationColor: "blue",
                    "&:visited": {
                        color: "purple",
                        textDecoration: "underline",
                        textDecorationColor: "purple",
                    },
                }),
            },
        }
    },
});

declare module "@mui/material/styles" {
    // expand theme to add "custom" (necessary to use colors from theme in other files)
    interface Theme {
        custom: {
            statusColors: typeof statusColors;
        }
    }

    // expand "ThemeOptions" to add "statusColors"
    interface ThemeOptions {
        custom?: {
            statusColors?: Partial<typeof statusColors>;
        }
    }

    // augment palette to include colors for chips
    interface Palette {
        blue: Palette['primary'];
        green: Palette['primary'];
        purple: Palette['primary'];
        lightGrey: Palette['primary']
    }
    interface PaletteOptions {
        blue?: PaletteOptions['primary'];
        green?: PaletteOptions['primary'];
        purple?: PaletteOptions['primary'];
        lightGrey?: PaletteOptions['primary'];
    }

    interface Mixins {
        cardHeight: (units: number) => React.CSSProperties;
    }
    interface MixinsOptions {
        cardHeight?: (units: number) => React.CSSProperties;
    }
}
