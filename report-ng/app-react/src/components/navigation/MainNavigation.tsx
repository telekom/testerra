import {type RouteHandle, routesConfig} from "../../router/mainRouter.tsx";
import {Link, useLocation} from "react-router-dom";
import Toolbar from "@mui/material/Toolbar";
import Box from "@mui/material/Box";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemText from "@mui/material/ListItemText";
import Drawer, {drawerClasses} from "@mui/material/Drawer";
import AppBar from "@mui/material/AppBar";
import IconButton from "@mui/material/IconButton";
import MenuIcon from "@mui/icons-material/Menu";
import PrintIcon from "@mui/icons-material/Print";
import * as React from "react";
import {useState} from "react";
import styled from "@emotion/styled";
import {Divider, ListItemIcon, Typography} from "@mui/material";
import logo from "../../assets/logo.png";
import {blueGrey} from "@mui/material/colors";
import PrintDialog from "../print/PrintDialog";
import {useReportData} from "../../provider/DataProvider";
import {ExecutionStatistics} from "../../model/ExecutionStatistics.ts";

const drawerWidth = 240;

const MenuDrawer = styled(Drawer)({
    width: drawerWidth,
    flexShrink: 0,
    boxSizing: 'border-box',
    marginTop: '64px',
    [`& .${drawerClasses.paper}`]: {
        width: drawerWidth,
        boxSizing: 'border-box',
        top: 64,
        height: 'calc(100% - 64px)',
    },
});

const MainNavigation = () => {

    // https://mui.com/material-ui/react-drawer/#clipped-under-the-app-bar
    // https://mui.com/material-ui/react-drawer/#responsive-drawer

    const menuRoutes = routesConfig[0].children || [];
    const location = useLocation();
    const {executionMngr} = useReportData();
    const [mobileOpen, setMobileOpen] = React.useState(false);
    const [printDialogOpen, setPrintDialogOpen] = useState(false);

    if (!executionMngr) return null;

    const execStatistics: ExecutionStatistics = executionMngr.getExecutionStatistics();

    const toggleDrawer = (open: boolean) => () => {
        setMobileOpen(open);
    };

    const itemClasses = {
        selectedItem: {
            // backgroundColor: theme.palette.grey["400"],
            backgroundColor: blueGrey[100],
            color: blueGrey[800],
            pointerEvents: "none"
        },
        unSelectedItem: {
            // color: theme.palette.primary.contrastText,
            // backgroundColor: "#2b2b35"
        }
    }

    const isRouteActive = (routePath: string): boolean => {
        // check regex: path can either match exactly or start with a trailing "/" and further characters
        // (prevents that "/" path is always highlighted or subroutes are not highlighted)
        const regex = new RegExp(`^${routePath}(\\/.*)?$`);
        return regex.test(location.pathname);
    }

    const drawerContent = (
        <Box sx={{height: '100%', overflow: 'hidden'}}>
            {/*<Box>*/}
            {/*    <Toolbar*/}
            {/*        sx={{*/}
            {/*            backgroundColor: 'primary.main',*/}
            {/*            color: 'primary.contrastText',*/}
            {/*        }}*/}
            {/*    >*/}
            {/*        <Box component="img" src={logo} alt="Testerra report" sx={{width: 60, height: 60}} />*/}
            {/*        /!*<Typography variant="h6" sx={{pl: 1}}>{execStatistics.getExecutionAggregate.executionContext?.runConfig?.reportName}</Typography>*!/*/}
            {/*        <Typography variant="h6" sx={{pl: 1}}>Test report</Typography>*/}
            {/*    </Toolbar>*/}
            {/*</Box>*/}

            <Box
                sx={{
                    overflow: 'auto',
                    height: '100%',
                    display: 'flex',
                    flexDirection: 'column',
                    backgroundColor: 'grey.100',
                }}
            >
                <List>
                    <ListItem>
                        <ListItemText>{execStatistics.getExecutionAggregate.executionContext?.contextValues?.name}</ListItemText>
                    </ListItem>
                </List>
                <Divider/>
                <List>
                    {menuRoutes
                        .filter((route) => (route.handle as RouteHandle)?.show)
                        .map((route, index) => {
                            const path = route.index ? "/" : `/${route.path}`;
                            const label = (route.handle as RouteHandle).label;
                            const icon = (route.handle as RouteHandle).icon;

                            const isActive = isRouteActive(path)     // important for subroute highlighting

                            return (
                                <ListItem
                                    disablePadding
                                    qa-item={'menu-' + label}
                                    key={index}
                                    sx={
                                        isActive
                                            ? itemClasses.selectedItem
                                            : itemClasses.unSelectedItem
                                    }
                                >
                                    <ListItemButton
                                        component={Link}
                                        to={path}
                                        onClick={() => setMobileOpen(false)}
                                    >
                                        <ListItemIcon
                                            sx={
                                                location.pathname === path
                                                    ? itemClasses.selectedItem
                                                    : itemClasses.unSelectedItem
                                            }
                                        >{icon}</ListItemIcon>
                                        <ListItemText primary={label}/>
                                    </ListItemButton>
                                </ListItem>
                            );
                        })
                    }
                </List>
                <Divider/>
                <List>
                    <ListItem disablePadding>
                        <ListItemButton onClick={() => {
                            setMobileOpen(false);
                            setPrintDialogOpen(true);
                        }}>
                            <ListItemIcon>
                                <PrintIcon/>
                            </ListItemIcon>
                            <ListItemText primary="Print Report"/>
                        </ListItemButton>
                    </ListItem>
                </List>

            </Box>
        </Box>
    );

    return (
        <>
            <AppBar
                position="fixed"
                sx={{
                    width: {xs: "100%", md: `${drawerWidth}px`},
                    left: 0,
                    right: "auto",
                    boxSizing: 'border-box',
                    backgroundColor: 'primary.main',
                    color: 'primary.contrastText',
                }}
            >
                <Toolbar>
                    <Box component="img" src={logo} alt="Testerra report" sx={{width: 60, height: 60}} />
                    <Typography
                        variant="h6"
                        sx={{flexGrow: 1, pl: 1}}
                    >
                        Report
                    </Typography>
                    <Box sx={{display: {xs: 'block', md: 'none'}}}>
                        <IconButton onClick={toggleDrawer(true)} color="inherit">
                            <MenuIcon/>
                        </IconButton>
                    </Box>
                </Toolbar>
            </AppBar>

            {/* Drawer that opens after user hits burger icon if screen width is too small*/}
            <Drawer
                variant="temporary"
                open={mobileOpen}
                onClose={toggleDrawer(false)}
                sx={{
                    display: {xs: 'block', md: 'none'},
                    [`& .${drawerClasses.paper}`]: {
                        width: drawerWidth,
                        top: 64,
                        height: 'calc(100% - 64px)',
                    },
                }}
            >
                {drawerContent}
            </Drawer>

            {/* Default menu drawer */}
            <MenuDrawer
                variant="permanent"
                sx={{
                    display: {xs: 'none', md: 'block'},
                    [`& .${drawerClasses.paper}`]: {
                        backgroundColor: 'background.paper',
                    },
                }}
            >
                {drawerContent}
            </MenuDrawer>

            <PrintDialog
                open={printDialogOpen}
                onClose={() => setPrintDialogOpen(false)}
                executionStatistics={executionMngr?.getExecutionStatistics() || null}
            />
        </>
    );
};
export default MainNavigation;
