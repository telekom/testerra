import Box from "@mui/material/Box";
import TabNavigation from "../widgets/TabNavigation";
import {Outlet} from 'react-router-dom';
import { generateTabsFromRoutes } from "../utils/generateTabsFromRoutes";
import { routesConfig } from "../router/mainRouter.tsx";

const TimingsPage = () => {

    const timingsRoute = routesConfig[0].children?.find((route) => route.path === "timings");
    const tabs = generateTabsFromRoutes(timingsRoute?.children);

    return (
        <Box sx={{width: '100%'}}>
            <TabNavigation tabs={tabs} withTopPadding={false}/>

            <Box sx={{py: '32px'}}>
                {/* Placeholder to render child component from router */}
                <Outlet/>
            </Box>
        </Box>
    );
};
export default TimingsPage;
