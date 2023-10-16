package eu.tsystems.mms.tic.testframework.internal.metrics;

import java.time.Instant;

/**
 * Created on 2023-10-16
 *
 * @author mgn
 */
public class TimeInfo {
    private Instant startTime;
    private Instant endTime;

    public TimeInfo() {
        this.startTime = Instant.now();
    }

    public void finish() {
        if (this.endTime == null) {
            this.endTime = Instant.now();
        }
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }
}
