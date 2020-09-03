package eu.tsystems.mms.tic.testframework.adapters;

import eu.tsystems.mms.tic.testframework.report.TestStatusController;
import eu.tsystems.mms.tic.testframework.report.model.ContextValues;
import eu.tsystems.mms.tic.testframework.report.model.ExecStatusType;
import eu.tsystems.mms.tic.testframework.report.model.ResultStatusType;
import eu.tsystems.mms.tic.testframework.report.model.context.AbstractContext;
import eu.tsystems.mms.tic.testframework.report.model.context.ExecutionContext;
import eu.tsystems.mms.tic.testframework.report.model.context.MethodContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ContextExporter {

    private static final Map<TestStatusController.Status, ResultStatusType> STATUS_MAPPING = new LinkedHashMap<>();

    public ContextExporter() {
        for (TestStatusController.Status status : TestStatusController.Status.values()) {
            /*
            Status
             */
            ResultStatusType resultStatusType = ResultStatusType.valueOf(status.name());

            // add to map
            STATUS_MAPPING.put(status, resultStatusType);
        }
    }

    ResultStatusType getMappedStatus(TestStatusController.Status status) {
        return STATUS_MAPPING.get(status);
    }

    @Deprecated
    protected <T, R> void value(T value, Function<T, R> function) {
        if (value != null) {
            function.apply(value);
        }
    }

    protected ContextValues createContextValues(AbstractContext context) {
        ContextValues.Builder builder = ContextValues.newBuilder();

        value(context.id, builder::setId);
        value(context.swi, builder::setSwi);
        value(System.currentTimeMillis(), builder::setCreated);
        value(context.name, builder::setName);
        builder.setStartTime(context.startTime.getTime());
        builder.setEndTime(context.endTime.getTime());

        if (context instanceof MethodContext) {
            MethodContext methodContext = (MethodContext) context;

            // result status
            builder.setResultStatus(getMappedStatus(methodContext.status));

            // exec status
            if (methodContext.status == TestStatusController.Status.NO_RUN) {
                builder.setExecStatus(ExecStatusType.RUNNING);
            } else {
                builder.setExecStatus(ExecStatusType.FINISHED);
            }
        } else if (context instanceof ExecutionContext) {
            ExecutionContext executionContext = (ExecutionContext) context;
            if (executionContext.crashed) {
                /*
                crashed state
                 */
                builder.setExecStatus(ExecStatusType.CRASHED);
            } else {
                if (TestStatusController.getTestsSkipped() == executionContext.estimatedTestMethodCount) {
                    builder.setResultStatus(ResultStatusType.SKIPPED);
                    builder.setExecStatus(ExecStatusType.VOID);
                } else if (TestStatusController.getTestsFailed() + TestStatusController.getTestsSuccessful() == 0) {
                    builder.setResultStatus(ResultStatusType.NO_RUN);
                    builder.setExecStatus(ExecStatusType.VOID);

                } else {
                    ResultStatusType resultStatusType = STATUS_MAPPING.get(executionContext.getStatus());
                    builder.setResultStatus(resultStatusType);

                    // exec status
                    if (ExecutionContextController.testRunFinished) {
                        builder.setExecStatus(ExecStatusType.FINISHED);
                    } else {
                        builder.setExecStatus(ExecStatusType.RUNNING);
                    }
                }
            }
        }
        return builder.build();
    }
}
