import {useOutletContext} from "react-router-dom";
import type {MethodDetails} from "../../model/MethodDetails.ts";

const Steps = () => {

    const methodDetail = useOutletContext<MethodDetails | undefined>();

    return (
        <p>
            Here will be some steps information about MethodDetail {methodDetail?.identifier}.
        </p>
    );
};
export default Steps;
