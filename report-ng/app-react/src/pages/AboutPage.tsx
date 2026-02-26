import {Card, CardContent, Divider, Grid, Typography} from "@mui/material";
import {styled} from '@mui/material/styles';
import Box from "@mui/material/Box";

const Item = styled('div')(({theme}) => ({
    backgroundColor: '#fff',
    border: '1px solid',
    borderColor: '#ced7e0',
    padding: theme.spacing(1),
    borderRadius: '4px',
    textAlign: 'center',
    ...theme.applyStyles('dark', {
        backgroundColor: '#1A2027',
        borderColor: '#444d58',
    }),
}));

const AboutPage = () => {

    return (
        <Box
            sx={{width: '100%', maxWidth: {sm: '100%', md: '1700px'}}}
        >
            <Grid
                container
                spacing={2}
                columns={12}
            >
                <Grid size={8}>
                    <Item>size=8</Item>
                </Grid>
                <Grid size={4}>
                    <Item>size=4</Item>
                </Grid>
                <Grid size={4}>
                    <Item>size=4</Item>
                </Grid>
                <Grid size={8}>
                    {/*<Item>size=8</Item>*/}
                    <Card>
                        <CardContent
                            sx={{pt: 1, pb: 1}}
                        >
                            <Typography variant="h6">My Card</Typography>
                        </CardContent>
                        <Divider/>
                        <CardContent
                            sx={{pt: 1, pb: 1}}
                        >
                            <Typography>Foobar content card.</Typography>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Box>
    );
};
export default AboutPage;
