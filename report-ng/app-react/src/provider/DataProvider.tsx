import React, {createContext, type ReactNode, useContext, useEffect, useState} from 'react';

import {ExecutionAggregate, HistoryAggregate, LogMessageAggregate} from "./report-model/report_pb.ts";

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
    protoData: AllProtoData | null;
    isLoading: boolean;
    error: Error | null;
}

export const DataProvider: React.FC<Props> = ({children}) => {
    const [protoData, setProtoData] = useState<AllProtoData | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [error, setError] = useState<Error | null>(null);

    // function getFile(path: string): Promise<Response> {
    //     return fetch(path, {
    //         method: "GET",
    //     });
    // }
    //
    // async function responseToProtoMessage<T>(response: Response, protobufMessageType: ProtobufMessage<T>) {
    //     const arrayBuffer = await response.arrayBuffer();
    //     return  protobufMessageType.decode(new Uint8Array(arrayBuffer));
    // }

    useEffect(() => {
        async function loadBinaryData() {
            try {

                /*
                const response = await fetch("model/execution");
                if (!response.ok) throw new Error(`HTTP Error: ${response.status}`);
                const arrayBuffer = await response.arrayBuffer();
                const decodedMessage = ExecutionAggregate.decode(new Uint8Array(arrayBuffer));
                console.table(decodedMessage);
                // // 3. In ein Plain-Object umwandeln (für React State empfohlen)
                // const dataObject = ExecutionAggregate.toObject(decodedMessage, {
                //     longs: Number,
                //     enums: String,
                //     defaults: true, // Optional: füllt Standardwerte auf
                // });

                setProtoData(decodedMessage);
                */

                const [execution, logging, history] = await Promise.all([
                    fetchAndDecode<ExecutionAggregate>("execution", ExecutionAggregate),
                    fetchAndDecode<LogMessageAggregate>("logMessages", LogMessageAggregate),
                    fetchAndDecode<HistoryAggregate>("history", HistoryAggregate)
                ]);

                setProtoData({execution, logging, history});

            } catch (err) {
                setError(err instanceof Error ? err : new Error("Unknown error"));
            } finally {
                setIsLoading(false);
            }
        }

        loadBinaryData();
    }, []);

    const fetchAndDecode = async <T, >(
        file: string,
        // model: { decode: (d: Uint8Array) => any, toObject: (m: any, o?: any) => T }
        model: any
    ): Promise<T> => {
        const resp = await fetch("model/" + file);
        console.log(resp)
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
        <ProtobufContext.Provider value={{protoData, isLoading, error}}>
            {children}
        </ProtobufContext.Provider>
    );

}

export const useProtobuf = () => {
    const context = useContext(ProtobufContext);
    if (!context) throw new Error('useProtobuf must be used within ProtobufProvider');
    return context;
};
