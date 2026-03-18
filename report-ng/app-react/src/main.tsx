import {createRoot} from 'react-dom/client'
import React from "react"
import {RouterProvider} from "react-router-dom";
import {router} from "./router/mainRouter.tsx";
import { StyledEngineProvider } from "@mui/material";
import {DataProvider} from "./provider/DataProvider.tsx";


createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <StyledEngineProvider injectFirst>
            <DataProvider>
                <RouterProvider router={router}/>
            </DataProvider>
        </StyledEngineProvider>
    </React.StrictMode>
)
