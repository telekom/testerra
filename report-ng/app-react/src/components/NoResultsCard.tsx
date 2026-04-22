/*
 * Testerra
 *
 * (C) 2026, Selina Natschke, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {Card, CardContent, Stack, Typography} from "@mui/material";
import FindInPageIcon from '@mui/icons-material/FindInPage';

const NoResultsCard = () => {
    return (
        <Card>
            <CardContent>
                <Stack direction="column" spacing={2} sx={{alignItems: "center", p: 2}}>
                    <Stack direction="row" spacing={1}>
                        <FindInPageIcon fontSize="large"/>
                        <Typography variant="h4"> No methods matching this criteria </Typography>
                    </Stack>
                    <Typography variant="subtitle1">Please note, that your filter criteria may only match configuration methods.</Typography>
                </Stack>
            </CardContent>
        </Card>
    );
};
export default NoResultsCard;
