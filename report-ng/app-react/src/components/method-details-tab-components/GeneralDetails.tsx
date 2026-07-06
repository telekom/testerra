
interface GeneralDetailsProps {
    methodId?: string;
}

const GeneralDetails = ({methodId}: GeneralDetailsProps) => {
    return (
        <p>
            Here will be some details information about the method with id: {methodId}.
        </p>
    );
};
export default GeneralDetails;
