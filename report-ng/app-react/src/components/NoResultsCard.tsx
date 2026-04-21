import {Card, CardContent, Stack, Typography} from "@mui/material";
import FindInPageIcon from '@mui/icons-material/FindInPage';

interface NoResultsCardProps {
    title: string,
    subtitle?: string
}

const NoResultsCard = ({title, subtitle}: NoResultsCardProps) => {
    return (
        <Card>
            <CardContent>
                <Stack direction="column" spacing={2} sx={{alignItems: "center", p: 2}}>
                    <Stack direction="row" spacing={1}>
                        <FindInPageIcon fontSize="large"/>
                        <Typography variant="h4">{title} </Typography>
                    </Stack>
                    {subtitle ?? <Typography variant="subtitle1">{subtitle}</Typography>}
                </Stack>
            </CardContent>
        </Card>
    );
};
export default NoResultsCard;
