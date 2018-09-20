package eu.tsystems.mms.tic.testframework.report.model;

/**
 * Created by fakr on 06.10.2017
 */
public interface ReportConfigMethodStateHelper {

    enum ConfigMethodState {

        SHOWING("Hide", "Hide Config Methods"),
        HIDING("Show", "Show Config Methods");

        private String stateIndicator;

        private String label;

        ConfigMethodState(String stateIndicator, String label) {
            this.stateIndicator = stateIndicator;
            this.label = label;
        }

        public String getStateIndicator() {
            return stateIndicator;
        }

        public String getLabel() {
            return label;
        }
    }

    boolean changeConfigMethodDisplayStateTo(ConfigMethodState stateToChangeTo);

    void switchConfigMethodDisplayState();

    boolean hasExpectedState(ConfigMethodState expectedState);

    void assertHidingAndShowingOfConfigMethodSection();

}
