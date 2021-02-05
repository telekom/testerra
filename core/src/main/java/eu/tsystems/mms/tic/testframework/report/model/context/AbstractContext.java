/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.internal.IDUtils;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.time.DurationFormatUtils;

public abstract class AbstractContext implements SynchronizableContext, Loggable {
    protected String name;
    private final String id = IDUtils.getB64encXID();
    protected AbstractContext parentContext;
    private final Date startTime = new Date();
    private Date endTime;

    public AbstractContext getParentContext() {
        return this.parentContext;
    }

    public String getName() {
        return this.name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getId() {
        return this.id;
    }

    /**
     * Gets an context for a specified name.
     * If it not exists, it will be created by a supplier,
     * preconfigured by setting the name,
     * added to the given queue of contexts and supplied to a consumer.
     *
     * @param contexts           The queue to add the context when created
     * @param newContextSupplier Supplier for the new context
     * @param whenAddedToQueue   Consumer when added to the queue
     * @return {@link AbstractContext} or NULL if the context doesn't exists or should not be created
     */
    protected <T extends AbstractContext> T getOrCreateContext(
            Collection<T> contexts,
            String name,
            Supplier<T> newContextSupplier,
            Consumer<T> whenAddedToQueue
    ) {
        List<T> list = contexts.stream()
                .filter(context -> name.equals(context.getName()))
                .collect(Collectors.toList());

        if (list.size() == 0) {
            if (newContextSupplier == null) {
                return null;
            }
            try {
                T context = newContextSupplier.get();
                context.name = name;
                contexts.add(context);

                if (whenAddedToQueue != null) {
                    whenAddedToQueue.accept(context);
                }
                return context;
            } catch (Exception e) {
                throw new SystemException("Error creating Context Class", e);
            }
        } else {
            T first = list.get(0);
            if (list.size() > 1) {
                log().warn("Found " + list.size() + " duplicate items of " + first.getClass().getSimpleName() + ", picking first one");
            }
            return first;
        }
    }

    public abstract TestStatusController.Status getStatus();

    private TestStatusController.Status getStatusFromCounts(Map<TestStatusController.Status, Integer> counts) {
        for (TestStatusController.Status key : counts.keySet()) {
            Integer value = counts.get(key);
            if (value > 0 && key.isFailed(true, false, false)) {
                return TestStatusController.Status.FAILED;
            }
        }
        return TestStatusController.Status.PASSED;
    }

    TestStatusController.Status getStatusFromContexts(Stream<? extends AbstractContext> contexts) {
        Map<TestStatusController.Status, Integer> counts = new LinkedHashMap<>();
        /*
        get statuses
         */
        // init with 0
        Arrays.stream(TestStatusController.Status.values()).forEach(status -> counts.put(status, 0));

        contexts.forEach(abstractContext -> {
            TestStatusController.Status status = abstractContext.getStatus();
            int value = 0;
            if (counts.containsKey(status)) {
                value = counts.get(status);
            }
            counts.put(status, value + 1);
        });

        /*
        find actual status
         */
        TestStatusController.Status status = getStatusFromCounts(counts);
        status.counts = counts;
        return status;
    }

    public String getDurationAsString() {
        Date endTime = this.endTime;
        if (endTime == null) {
            endTime = new Date();
        }
        Duration between = Duration.between(startTime.toInstant(), endTime.toInstant());
        long millis = between.toMillis();
        if (millis > 60 * 60 * 1000) {
            return DurationFormatUtils.formatDuration(millis, "H 'h' mm 'm' ss 's'", false);
        }
        if (millis > 60 * 1000) {
            return DurationFormatUtils.formatDuration(millis, "mm 'm' ss 's'", false);
        }
        if (millis > 1000) {
            return DurationFormatUtils.formatDuration(millis, "ss 's' SSS 'ms'", false);
        }
        return DurationFormatUtils.formatDuration(millis, "SSS 'ms'", false);
    }

    public long nrOfFailed(Map<TestStatusController.Status, Integer> counts) {
        final AtomicReference<Integer> count = new AtomicReference<>();
        count.set(0);
        counts.keySet().forEach(status -> {
            if (status.isFailed(false, false, false)) {
                count.set(count.get() + counts.get(status));
            }
        });
        return count.get();
    }

    public long nrOfPassed(Map<TestStatusController.Status, Integer> counts) {
        final AtomicReference<Integer> count = new AtomicReference<>();
        count.set(0);
        counts.keySet().forEach(status -> {
            if (status.isPassed()) {
                count.set(count.get() + counts.get(status));
            }
        });
        return count.get();
    }

    public long nrOfSkipped(Map<TestStatusController.Status, Integer> counts) {
        final AtomicReference<Integer> count = new AtomicReference<>();
        count.set(0);
        counts.keySet().forEach(status -> {
            if (status == TestStatusController.Status.SKIPPED) {
                count.set(count.get() + counts.get(status));
            }
        });
        return count.get();
    }

    public long passRate(Map<TestStatusController.Status, Integer> counts, long numberOfTests) {
        if (numberOfTests == 0) {
            return 0;
        }
        return nrOfPassed(counts) * 100 / numberOfTests;
    }

    public void updateEndTimeRecursive(Date date) {
        AbstractContext context = this;
        while (context != null) {
            //            LOGGER.info("Updating " + context.getClass().getSimpleName() + " context " + context.name + ": " + date);

            context.endTime = date;
            context = context.parentContext;
        }
    }

    @Override
    public SynchronizableContext getParent() {
        return parentContext;
    }
}
