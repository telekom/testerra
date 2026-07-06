import {useOutletContext} from "react-router-dom";
import type {MethodDetails} from "../../model/MethodDetails.ts";

const Video = () => {

    const methodDetail = useOutletContext<MethodDetails | undefined>();

    return (
        <p>
            Here will be a video about MethodDetail {methodDetail?.identifier}.
        </p>
    );
};
export default Video;
