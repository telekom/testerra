import {useOutletContext} from "react-router-dom";
import type {MethodDetails} from "../../model/MethodDetails.ts";

const ErrorDetails = () => {

    const methodDetail = useOutletContext<MethodDetails | undefined>();

    return (
        <p>
            Here will be some error details information about MethodDetail {methodDetail?.identifier}.
        </p>
    );
};
export default ErrorDetails;
