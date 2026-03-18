import { useParams } from "react-router";

const MethodDetailsPage = () => {
    let params = useParams();

    return (
        <p>MethodId: {params.methodId}</p>
    );
};
export default MethodDetailsPage;
