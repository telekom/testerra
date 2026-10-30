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

import {IconButton, Snackbar} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";

interface ReportSnackbarProps {
    text: string;
    open: boolean;
    onClose: () => void;
}

const ReportSnackbar = ({text, open, onClose}: ReportSnackbarProps) => (
    <Snackbar
        open={open}
        autoHideDuration={3000}
        onClose={(_, reason) => {
            if (reason !== "clickaway") {
                onClose();
            }
        }}
        message={text}
        anchorOrigin={{vertical: "bottom", horizontal: "center"}}
        action={(
            <IconButton
                size="small"
                aria-label="close"
                color="inherit"
                onClick={onClose}
            >
                <CloseIcon fontSize="small"/>
            </IconButton>
        )}
    />
);

export default ReportSnackbar;
