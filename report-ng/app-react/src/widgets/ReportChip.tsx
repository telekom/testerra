import Chip from "@mui/material/Chip";
import type {SxProps, Theme} from '@mui/material/styles';
import type {ChipColor} from "../hooks/useTestListFilters";
import Tooltip from '@mui/material/Tooltip';

type ReportChipProps = {
    label: string,
    sx?: SxProps<Theme>,
    handleDelete?: () => void;
    color?: ChipColor,
    size?: "medium" | "small",
    tooltipText?: string,
    handleClick?: () => void;
}

const ReportChip = ({label, sx, handleDelete, color, size, tooltipText, handleClick}: ReportChipProps) => {
    return (
        <Tooltip title={tooltipText}>
            <Chip label={label} sx={sx} onDelete={handleDelete} color={color} size={size} onClick={() => handleClick ? handleClick(): undefined}/>
        </Tooltip>
    );
}

export default ReportChip;
