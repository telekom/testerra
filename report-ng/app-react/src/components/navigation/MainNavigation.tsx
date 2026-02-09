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
import * as React from "react";
import {useEffect} from "react";
import styled from "@emotion/styled";
import {Divider, ListItemIcon, Typography} from "@mui/material";
import logo from "../../assets/logo.png";
import "./MainNavigation.css";
import { blueGrey } from "@mui/material/colors";

const drawerWidth = 240;

const MenuDrawer = styled(Drawer)({
    width: drawerWidth,
    flexShrink: 0,
    boxSizing: 'border-box',
    mt: 10,
    [`& .${drawerClasses.paper}`]: {
        width: drawerWidth,
        boxSizing: 'border-box',
    },
});

const MainNavigation = () => {

    // https://mui.com/material-ui/react-drawer/#clipped-under-the-app-bar
    // https://mui.com/material-ui/react-drawer/#responsive-drawer

    const menuRoutes = routesConfig[0].children || [];
    const location = useLocation();
    const [mobileOpen, setMobileOpen] = React.useState(false);

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


    useEffect(() => {
        console.log('Pfad hat sich geändert zu:', location.pathname);
        console.table(location); // Zeigt die Daten schick als Tabelle an
    }, [location]);

    const isRouteActive = (routePath: string): boolean => {
        // check regex: path can either match exactly or start with a trailing "/" and further characters
        // (prevents that "/" path is always highlighted or subroutes are not highlighted)
        const regex = new RegExp(`^${routePath}(\\/.*)?$`);
        return regex.test(location.pathname);
    }

    const drawerContent = (
        <Box sx={{height: '100%', overflow: 'hidden'}}>
            <Box>
                <Toolbar
                    sx={{
                        backgroundColor: 'primary.main',
                        color: 'primary.contrastText',
                    }}
                >
                    <img src={logo} className="logo" alt="Testerra report"/>
                    <Typography variant="h6" sx={{pl: 1}}>Test project</Typography>
                </Toolbar>
            </Box>

            <Box
                sx={{
                    overflow: 'auto',
                    height: '100%',
                    display: 'flex',
                    flexDirection: 'column',
                    backgroundColor: 'grey.100'
                }}
            >
                <List>
                    <ListItem>
                        <ListItemText>Regression</ListItemText>
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

            </Box>
        </Box>
    );

    return (
        <>
            {/* Horizontal top bar with burger button that appears if the screen width is too small */}
            <AppBar
                position="fixed"
                sx={{
                    display: {xs: 'block', md: 'none'},
                    bgcolor: 'background.paper',
                    color: 'text.primary',
                }}
            >
                <Toolbar>
                    <Typography variant="h6" sx={{flexGrow: 1}}>
                        Test project
                    </Typography>

                    <IconButton onClick={toggleDrawer(true)}>
                        <MenuIcon/>
                    </IconButton>
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
        </>
    );
};
export default MainNavigation;
