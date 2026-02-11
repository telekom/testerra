import {createRoot} from 'react-dom/client'

import React from "react"
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {RouterProvider} from "react-router-dom";
import {router} from "./router/mainRouter.tsx";
import { StyledEngineProvider } from "@mui/material";
import {DataProvider} from "./provider/DataProvider.tsx";

const client = new QueryClient()

createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <StyledEngineProvider injectFirst>
            <QueryClientProvider client={client}>
                <DataProvider>
                    <RouterProvider router={router}/>
                </DataProvider>
            </QueryClientProvider>
        </StyledEngineProvider>
    </React.StrictMode>
)
