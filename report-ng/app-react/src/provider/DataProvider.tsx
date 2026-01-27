import React, {createContext, type ReactNode, useContext, useEffect, useState} from 'react';

import {ExecutionAggregate, HistoryAggregate, LogMessageAggregate} from "../model/report-model/report_pb.ts";
import {ExecutionStatisticsManager} from "./ExecutionStatisticsManager.ts";

const ProtobufContext = createContext<ProtobufContextType | undefined>(undefined);

interface Props {
    children: ReactNode;
}

// export interface ProtobufMessage<T> {
//     decode(reader: Uint8Array, length?: number): T
// }

export interface AllProtoData {
    execution: ExecutionAggregate;
    logging: LogMessageAggregate;
    history: HistoryAggregate;
}

export interface ProtobufContextType {
    executionMngr: ExecutionStatisticsManager | null;
    // historyMngr: HistoryStatisticsManager;
    isLoading: boolean;
    error: Error | null;
}

export const DataProvider: React.FC<Props> = ({children}) => {
    const [managers, setManagers] = useState<{
        executionStatistics: ExecutionStatisticsManager | null;
        // historyStatistics: HistoryStatisticsManager | null;
    }>(
        {
            executionStatistics: null,
            // historyStatistics: null
        }
    );
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [error, setError] = useState<Error | null>(null);


    useEffect(() => {
        async function initDataManagers() {
            try {

                const [execution, logging, history] = await Promise.all([
                    fetchAndDecode<ExecutionAggregate>("execution", ExecutionAggregate),
                    fetchAndDecode<LogMessageAggregate>("logMessages", LogMessageAggregate),
                    fetchAndDecode<HistoryAggregate>("history", HistoryAggregate)
                ]);

                let executionStatisticsManager = new ExecutionStatisticsManager(execution, logging, history);
                await executionStatisticsManager.init();

                setManagers({
                    executionStatistics: executionStatisticsManager,
                    // historyStatistics: new HistoryStatisticsManager(history)
                })

            } catch (err) {
                setError(err instanceof Error ? err : new Error("Unknown error"));
            } finally {
                setIsLoading(false);
            }
        }

        initDataManagers();
    }, []);

    const fetchAndDecode = async <T, >(
        file: string,
        // model: { decode: (d: Uint8Array) => any, toObject: (m: any, o?: any) => T }
        model: any
    ): Promise<T> => {
        const resp = await fetch("model/" + file);
        // TODO: Always status=200, no 404 possible?
        if (!resp.ok) {
            if (resp.status === 404) {
                console.error(`Cannot find file ${file}.`);
            } else {
                throw new Error(`Cannot load ${file}: ${resp.status} - ${resp.body}`);
            }
        }
        const buffer = await resp.arrayBuffer();
        const message = model.decode(new Uint8Array(buffer));
        // TODO: model.toObject is not a function -> outdated version?
        // return model.toObject(message, {longs: Number, enums: String}) as T;
        return message
    };

    return (
        <ProtobufContext.Provider value={{
            executionMngr: managers.executionStatistics,
            isLoading,
            error
        }}>
            {children}
        </ProtobufContext.Provider>
    );

}

export const useReportData = () => {
    const context = useContext(ProtobufContext);
    if (!context) throw new Error('useProtobuf must be used within ProtobufProvider');
    return context;
};
