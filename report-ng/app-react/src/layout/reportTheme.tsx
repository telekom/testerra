import {createTheme} from "@mui/material";

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
        }
    },
});

declare module "@mui/material/styles" {
    // expand theme to add "custom" (necessary to use colors from theme in other files)
    interface Theme {
        custom: {
            statusColors: {
                passed: string;
                skipped: string;
                failed: string;
                crashed: string;
                running: string;
                failed_minor: string;
                expected_failed: string;
            };
        };
    }

    // expand "ThemeOptions" to add "statusColors"
    interface ThemeOptions {
        custom?: {
            statusColors?: {
                passed?: string;
                skipped?: string;
                failed?: string;
                crashed?: string;
                running?: string;
                failed_minor?: string;
                expected_failed?: string;
            };
        };
    }

    // augment palette to include colors for chips
    interface Palette {
        blue: Palette['primary'];
        green: Palette['primary'];
        purple: Palette['primary'];
    }
    interface PaletteOptions {
        blue?: PaletteOptions['primary'];
        green?: PaletteOptions['primary'];
        purple?: PaletteOptions['primary'];
    }
}
