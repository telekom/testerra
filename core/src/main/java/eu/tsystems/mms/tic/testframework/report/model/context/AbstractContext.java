/*
 * Testerra
 *
 * (C) 2020,  Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 *
 */
 package eu.tsystems.mms.tic.testframework.report.model.context;

import eu.tsystems.mms.tic.testframework.events.ContextUpdateEvent;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.internal.IDUtils;
import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public abstract class AbstractContext implements SynchronizableContext {

    @FunctionalInterface
    public interface CreateDownStreamContext<T extends AbstractContext> {

        T create();
    }

    public String name;
    public final String id = IDUtils.getB64encXID();
    public AbstractContext parentContext;
    public String swi; // system-wide identifier
    public Date startTime = new Date();
    public Date endTime = new Date();

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected static void fillBasicContextValues(AbstractContext context, AbstractContext parentContext, String name) {
        context.name = name;
        context.swi = parentContext.swi + "_" + name;
    }

    protected <T extends AbstractContext> T getContext(Class<T> contextClass, List<T> contexts, String name, boolean autocreate, CreateDownStreamContext<T> createDownStreamContext) {
        synchronized (contexts) {
            List<T> collect = contexts.stream().filter(context -> name.equals(context.name)).collect(Collectors.toList());
            if (collect.isEmpty()) {
                if (!autocreate) {
                    return null;
                }
                try {
                    /*
                    CREATE a new context
                     */

                    T context = createDownStreamContext.create();
                    fillBasicContextValues(context, this, name);
                    contexts.add(context);
                    TesterraListener.getEventBus().post(new ContextUpdateEvent().setContext(context));
                    return context;
                } catch (Exception e) {
                    throw new TesterraSystemException("Error creating Context Class", e);
                }

            } else {
                if (collect.size() > 1) {
                    LOGGER.error("Found " + collect.size() + " " + contextClass.getSimpleName() + "s with name " + name + ", picking first one");
                }
                return collect.get(0);
            }
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

    TestStatusController.Status getStatusFromContexts(AbstractContext[] contexts) {
        Map<TestStatusController.Status, Integer> counts = new LinkedHashMap<>();
        /*
        get statuses
         */
        synchronized (contexts) {
            // init with 0
            Arrays.stream(TestStatusController.Status.values()).forEach(status -> counts.put(status, 0));

            for (AbstractContext context : contexts) {
                TestStatusController.Status status = context.getStatus();
                int value = 0;
                if (counts.containsKey(status)) {
                    value = counts.get(status);
                }
                counts.put(status, value + 1);
            }
        }

        /*
        find actual status
         */
        TestStatusController.Status status = getStatusFromCounts(counts);
        status.counts = counts;
        return status;
    }

    public String getDuration(Date startTime, Date endTime) {
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

    public int nrOfFailed(Map<TestStatusController.Status, Integer> counts) {
        final AtomicReference<Integer> count = new AtomicReference<>();
        count.set(0);
        counts.keySet().forEach(status -> {
            if (status.isFailed(false, false, false)) {
                count.set(count.get() + counts.get(status));
            }
        });
        return count.get();
    }

    public int nrOfPassed(Map<TestStatusController.Status, Integer> counts) {
        final AtomicReference<Integer> count = new AtomicReference<>();
        count.set(0);
        counts.keySet().forEach(status -> {
            if (status.isPassed()) {
                count.set(count.get() + counts.get(status));
            }
        });
        return count.get();
    }

    public int nrOfSkipped(Map<TestStatusController.Status, Integer> counts) {
        final AtomicReference<Integer> count = new AtomicReference<>();
        count.set(0);
        counts.keySet().forEach(status -> {
            if (status == TestStatusController.Status.SKIPPED) {
                count.set(count.get() + counts.get(status));
            }
        });
        return count.get();
    }

    public int passRate(Map<TestStatusController.Status, Integer> counts, int numberOfTests) {
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
