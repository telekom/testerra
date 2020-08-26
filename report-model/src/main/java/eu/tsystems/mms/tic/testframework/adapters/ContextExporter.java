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

    protected <T, R> void value(T value, Function<T, R> function) {
        if (value != null) {
            function.apply(value);
        }
    }

    protected<T, R, M> void value(T value, Function<T, M> mappingFunction, Function<M, R> function) {
        if (value != null) {
            M mappedValue = mappingFunction.apply(value);
            function.apply(mappedValue);
        }
    }

    protected<T, M, R> void valueMapping(T value, Function<T, M> map, Function<M, R> function) {
        if (value != null) {
            M m = map.apply(value);
            function.apply(m);
        }
    }

    protected<T, M, R> void valueList(List<T> list, Function<T, M> map, Function<List<M>, R> function) {
        if (list != null && !list.isEmpty()) {
            List<M> collect = list.stream().map(map).collect(Collectors.toList());
            function.apply(collect);
        }
    }

    protected ContextValues createContextValues(AbstractContext context) {
        ContextValues.Builder builder = ContextValues.newBuilder();

        value(context.id, builder::setId);
        value(context.swi, builder::setSwi);
        value(System.currentTimeMillis(), builder::setCreated);
        value(context.name, builder::setName);
        valueMapping(context.startTime, Date::getTime, builder::setStartTime);
        valueMapping(context.endTime, Date::getTime, builder::setEndTime);

        if (context instanceof MethodContext) {
            MethodContext methodContext = (MethodContext) context;

            // result status
            valueMapping(methodContext.status, this::getMappedStatus, builder::setResultStatus);

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
