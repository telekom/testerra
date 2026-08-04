import Box from "@mui/material/Box";
import {Typography} from "@mui/material";
import {useSearchParams} from "react-router-dom";

const ThreadsPage = () => {
    const [searchParams] = useSearchParams();
    const methodId = searchParams.get("methodId") ?? undefined;

    return (
        <Box sx={{width: '100%', p: '24px 32px'}}>
            Here will be some threads.
            {methodId && (<Typography>MethodId: {methodId} </Typography>)}
        </Box>
    );
};
export default ThreadsPage;
