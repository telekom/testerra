/*
 * Testerra
 *
 * (C) 2026, Selina Natschke, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {createTheme} from "@mui/material/styles";
import React from "react";
import {
    logConsole,
    logLine,
    statusColors,
    generalDetails,
    codeView,
    type ReportThemeLogConsoleStyles,
    type ReportThemeLogLineStyles,
    type ReportThemeCodeViewStyles,
    type ReportThemeGeneralDetailsStyles,
    type Status
} from "./reportThemeCustom";

export type {Status};

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
        logLine,
        logConsole,
        generalDetails,
        codeView
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
            logLine: ReportThemeLogLineStyles;
            logConsole: ReportThemeLogConsoleStyles;
            generalDetails: ReportThemeGeneralDetailsStyles;
            codeView: ReportThemeCodeViewStyles;
        }
    }

    // expand "ThemeOptions" to add "statusColors"
    interface ThemeOptions {
        custom?: {
            statusColors?: Partial<typeof statusColors>;
            logLine?: Partial<ReportThemeLogLineStyles>;
            logConsole?: Partial<ReportThemeLogConsoleStyles>;
            generalDetails?: Partial<ReportThemeGeneralDetailsStyles>;
            codeView?: Partial<ReportThemeCodeViewStyles>;
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
