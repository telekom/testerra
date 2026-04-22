import InfoOutlineIcon from '@mui/icons-material/InfoOutline';import Tooltip from '@mui/material/Tooltip';

interface InfoTooltipProps {
    text: string
}

export default function InfoTooltip({text}: InfoTooltipProps) {
    return (
        <Tooltip title={text}>
            <InfoOutlineIcon color="primary"/>
        </Tooltip>
    );
}
