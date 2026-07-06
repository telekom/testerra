import { useParams } from "react-router";
import TabNavigation from "../widgets/TabNavigation.tsx";
import Box from "@mui/material/Box";
import {Outlet} from "react-router-dom";
import {routesConfig} from "../router/mainRouter.tsx";
import {generateTabsFromRoutes} from "../utils/generateTabsFromRoutes.ts";
import GeneralDetails from "../components/method-details-tab-components/GeneralDetails.tsx";

const MethodDetailsPage = () => {
    const params = useParams();
    const methodDetailsRoute = routesConfig[0].children?.find((route) => route.path === "method/:methodId?");
    const tabs = generateTabsFromRoutes(methodDetailsRoute?.children);

    return (
    <Box sx={{width: '100%', p: '24px 32px'}}>
        <GeneralDetails methodId={params.methodId}/>
        <TabNavigation tabs={tabs}/>

        <Box sx={{p: '24px 32px'}}>
            {/* Placeholder to render child component from router */}
            <Outlet/>
        </Box>
    </Box>
    );
};
export default MethodDetailsPage;
