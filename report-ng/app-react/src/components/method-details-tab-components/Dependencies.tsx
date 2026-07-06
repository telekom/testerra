import {useOutletContext} from "react-router-dom";
import type {MethodDetails} from "../../model/MethodDetails.ts";

const Dependencies = () => {

    const methodDetail = useOutletContext<MethodDetails | undefined>();

    return (
        <p>
            Here will be some dependency information about MethodDetail {methodDetail?.identifier}.
        </p>
    );
};
export default Dependencies;
