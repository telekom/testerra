import {useOutletContext} from "react-router-dom";
import type {MethodDetails} from "../../model/MethodDetails.ts";

const BrowserInfo = () => {

    const methodDetail = useOutletContext<MethodDetails | undefined>();

    return (
        <p>
            Here will be some browser information about MethodDetail {methodDetail?.identifier}.
        </p>
    );
};
export default BrowserInfo;
