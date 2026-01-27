import {Card, CardContent, Divider, Typography} from "@mui/material";
import type {SxProps, Theme} from '@mui/material/styles';

export interface CardProps {
    label: string;
    children: any;
    sx?: SxProps<Theme>;
    className: string;
}

const ReportCard = ({label, children, sx = {pt: 1, pb: 1}, className}: CardProps) => {

    return (
        <Card sx={{p: 0}} className={className}>
            <CardContent
                sx={{pt: 1, pb: 1}}
            >
                <Typography variant="subtitle2" color="primary">{label}</Typography>
            </CardContent>
            <Divider/>
            <CardContent sx={sx}>
                {children}
            </CardContent>
        </Card>
    )
};

export default ReportCard;