import SelectInput from "../../widgets/select-input/select-input";
import * as React from "react";
import TextField from '@mui/material/TextField';
import FormControlLabel from '@mui/material/FormControlLabel';
import Autocomplete from '@mui/material/Autocomplete';
import {Box, Card, CardContent, Divider, Grid, Switch, Typography} from "@mui/material";
import EChart from "../../widgets/echart/echart";
import type {EChartsOption} from "echarts-for-react";

const TestTimings = () => {

    const menuItems = [{value: 5, label: "5"}, {value: 10, label: "10"}, {value: 15, label: "15"}, {value: 20, label: "20"}]

    const [numberOfMethodRanges, setNumberOfMethodRanges] = React.useState(10);

    const currencies = [
        {
            value: 'USD',
            label: '$',
        },
        {
            value: 'EUR',
            label: '€',
        },
        {
            value: 'BTC',
            label: '฿',
        },
        {
            value: 'JPY',
            label: '¥',
        },
    ];

    const option: EChartsOption = {
        xAxis: {
            type: 'category',
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: [120, 200, 150, 80, 70, 110, 130],
                type: 'bar'
            }
        ]
    };

    const handleChange = (value: number) => {
        setNumberOfMethodRanges(value);
    };

    return (
        <Box
            sx={{width: '100%', maxWidth: {sm: '100%', md: '1700px'}}}
        >
            <Grid
                container
                spacing={2}
                columns={12}
            >
                <Grid size={4}>
                    <SelectInput label="Number of method ranges" value={numberOfMethodRanges} onChange={handleChange}
                                 menuItems={menuItems} sx={{width: "50%"}}/>
                </Grid>
                <Grid size={4}>
                    <Autocomplete
                        disablePortal
                        options={currencies}
                        sx={{width: "100%"}}
                        renderInput={(params) => <TextField {...params} label="Method"/>}
                    />
                </Grid>
                <Grid size={4} display="flex" justifyContent="flex-end">
                    <FormControlLabel control={<Switch defaultChecked/>} label="Show configuration methods"/>
                </Grid>
                <Grid size={12}>
                    <Card>
                        <CardContent
                            sx={{pt: 1, pb: 1}}
                        >
                            <Typography variant="h6">Test durations</Typography>
                        </CardContent>
                        <Divider/>
                        <CardContent
                            sx={{pt: 1, pb: 1}}
                        >
                            <EChart option={option}/>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Box>
    );
};
export default TestTimings;
