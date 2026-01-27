import {createTheme} from "@mui/material";

const statusColors = {
    "errorColor": "#FF0000"
}



export const reportTheme = createTheme({
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
        }
    },
});
