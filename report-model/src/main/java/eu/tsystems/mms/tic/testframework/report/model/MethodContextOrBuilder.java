// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: framework.proto

package eu.tsystems.mms.tic.testframework.report.model;

public interface MethodContextOrBuilder extends
    // @@protoc_insertion_point(interface_extends:data.MethodContext)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.data.ContextValues context_values = 1;</code>
   * @return Whether the contextValues field is set.
   */
  boolean hasContextValues();
  /**
   * <code>.data.ContextValues context_values = 1;</code>
   * @return The contextValues.
   */
  eu.tsystems.mms.tic.testframework.report.model.ContextValues getContextValues();
  /**
   * <code>.data.ContextValues context_values = 1;</code>
   */
  eu.tsystems.mms.tic.testframework.report.model.ContextValuesOrBuilder getContextValuesOrBuilder();

  /**
   * <code>.data.MethodType method_type = 7;</code>
   * @return The enum numeric value on the wire for methodType.
   */
  int getMethodTypeValue();
  /**
   * <code>.data.MethodType method_type = 7;</code>
   * @return The methodType.
   */
  eu.tsystems.mms.tic.testframework.report.model.MethodType getMethodType();

  /**
   * <code>repeated string parameters = 8;</code>
   * @return A list containing the parameters.
   */
  java.util.List<java.lang.String>
      getParametersList();
  /**
   * <code>repeated string parameters = 8;</code>
   * @return The count of parameters.
   */
  int getParametersCount();
  /**
   * <code>repeated string parameters = 8;</code>
   * @param index The index of the element to return.
   * @return The parameters at the given index.
   */
  java.lang.String getParameters(int index);
  /**
   * <code>repeated string parameters = 8;</code>
   * @param index The index of the value to return.
   * @return The bytes of the parameters at the given index.
   */
  com.google.protobuf.ByteString
      getParametersBytes(int index);

  /**
   * <code>repeated string method_tags = 9;</code>
   * @return A list containing the methodTags.
   */
  java.util.List<java.lang.String>
      getMethodTagsList();
  /**
   * <code>repeated string method_tags = 9;</code>
   * @return The count of methodTags.
   */
  int getMethodTagsCount();
  /**
   * <code>repeated string method_tags = 9;</code>
   * @param index The index of the element to return.
   * @return The methodTags at the given index.
   */
  java.lang.String getMethodTags(int index);
  /**
   * <code>repeated string method_tags = 9;</code>
   * @param index The index of the value to return.
   * @return The bytes of the methodTags at the given index.
   */
  com.google.protobuf.ByteString
      getMethodTagsBytes(int index);

  /**
   * <code>int32 retry_number = 10;</code>
   * @return The retryNumber.
   */
  int getRetryNumber();

  /**
   * <code>int32 method_run_index = 11;</code>
   * @return The methodRunIndex.
   */
  int getMethodRunIndex();

  /**
   * <code>string thread_name = 12;</code>
   * @return The threadName.
   */
  java.lang.String getThreadName();
  /**
   * <code>string thread_name = 12;</code>
   * @return The bytes for threadName.
   */
  com.google.protobuf.ByteString
      getThreadNameBytes();

  /**
   * <pre>
   *TestStep failed_step = 13;
   * </pre>
   *
   * <code>.data.FailureCorridorValue failure_corridor_value = 14;</code>
   * @return The enum numeric value on the wire for failureCorridorValue.
   */
  int getFailureCorridorValueValue();
  /**
   * <pre>
   *TestStep failed_step = 13;
   * </pre>
   *
   * <code>.data.FailureCorridorValue failure_corridor_value = 14;</code>
   * @return The failureCorridorValue.
   */
  eu.tsystems.mms.tic.testframework.report.model.FailureCorridorValue getFailureCorridorValue();

  /**
   * <code>string class_context_id = 15;</code>
   * @return The classContextId.
   */
  java.lang.String getClassContextId();
  /**
   * <code>string class_context_id = 15;</code>
   * @return The bytes for classContextId.
   */
  com.google.protobuf.ByteString
      getClassContextIdBytes();

  /**
   * <code>string execution_context_id = 16;</code>
   * @return The executionContextId.
   */
  java.lang.String getExecutionContextId();
  /**
   * <code>string execution_context_id = 16;</code>
   * @return The bytes for executionContextId.
   */
  com.google.protobuf.ByteString
      getExecutionContextIdBytes();

  /**
   * <code>repeated .data.ErrorContext non_functional_infos = 17;</code>
   */
  java.util.List<eu.tsystems.mms.tic.testframework.report.model.ErrorContext> 
      getNonFunctionalInfosList();
  /**
   * <code>repeated .data.ErrorContext non_functional_infos = 17;</code>
   */
  eu.tsystems.mms.tic.testframework.report.model.ErrorContext getNonFunctionalInfos(int index);
  /**
   * <code>repeated .data.ErrorContext non_functional_infos = 17;</code>
   */
  int getNonFunctionalInfosCount();
  /**
   * <code>repeated .data.ErrorContext non_functional_infos = 17;</code>
   */
  java.util.List<? extends eu.tsystems.mms.tic.testframework.report.model.ErrorContextOrBuilder> 
      getNonFunctionalInfosOrBuilderList();
  /**
   * <code>repeated .data.ErrorContext non_functional_infos = 17;</code>
   */
  eu.tsystems.mms.tic.testframework.report.model.ErrorContextOrBuilder getNonFunctionalInfosOrBuilder(
      int index);

  /**
   * <code>repeated .data.ErrorContext collected_assertions = 18;</code>
   */
  java.util.List<eu.tsystems.mms.tic.testframework.report.model.ErrorContext> 
      getCollectedAssertionsList();
  /**
   * <code>repeated .data.ErrorContext collected_assertions = 18;</code>
   */
  eu.tsystems.mms.tic.testframework.report.model.ErrorContext getCollectedAssertions(int index);
  /**
   * <code>repeated .data.ErrorContext collected_assertions = 18;</code>
   */
  int getCollectedAssertionsCount();
  /**
   * <code>repeated .data.ErrorContext collected_assertions = 18;</code>
   */
  java.util.List<? extends eu.tsystems.mms.tic.testframework.report.model.ErrorContextOrBuilder> 
      getCollectedAssertionsOrBuilderList();
  /**
   * <code>repeated .data.ErrorContext collected_assertions = 18;</code>
   */
  eu.tsystems.mms.tic.testframework.report.model.ErrorContextOrBuilder getCollectedAssertionsOrBuilder(
      int index);

  /**
   * <code>repeated string infos = 19;</code>
   * @return A list containing the infos.
   */
  java.util.List<java.lang.String>
      getInfosList();
  /**
   * <code>repeated string infos = 19;</code>
   * @return The count of infos.
   */
  int getInfosCount();
  /**
   * <code>repeated string infos = 19;</code>
   * @param index The index of the element to return.
   * @return The infos at the given index.
   */
  java.lang.String getInfos(int index);
  /**
   * <code>repeated string infos = 19;</code>
   * @param index The index of the value to return.
   * @return The bytes of the infos at the given index.
   */
  com.google.protobuf.ByteString
      getInfosBytes(int index);

  /**
   * <code>string priority_message = 21;</code>
   * @return The priorityMessage.
   */
  java.lang.String getPriorityMessage();
  /**
   * <code>string priority_message = 21;</code>
   * @return The bytes for priorityMessage.
   */
  com.google.protobuf.ByteString
      getPriorityMessageBytes();

  /**
   * <code>repeated string related_method_context_ids = 23;</code>
   * @return A list containing the relatedMethodContextIds.
   */
  java.util.List<java.lang.String>
      getRelatedMethodContextIdsList();
  /**
   * <code>repeated string related_method_context_ids = 23;</code>
   * @return The count of relatedMethodContextIds.
   */
  int getRelatedMethodContextIdsCount();
  /**
   * <code>repeated string related_method_context_ids = 23;</code>
   * @param index The index of the element to return.
   * @return The relatedMethodContextIds at the given index.
   */
  java.lang.String getRelatedMethodContextIds(int index);
  /**
   * <code>repeated string related_method_context_ids = 23;</code>
   * @param index The index of the value to return.
   * @return The bytes of the relatedMethodContextIds at the given index.
   */
  com.google.protobuf.ByteString
      getRelatedMethodContextIdsBytes(int index);

  /**
   * <code>repeated string depends_on_method_context_ids = 24;</code>
   * @return A list containing the dependsOnMethodContextIds.
   */
  java.util.List<java.lang.String>
      getDependsOnMethodContextIdsList();
  /**
   * <code>repeated string depends_on_method_context_ids = 24;</code>
   * @return The count of dependsOnMethodContextIds.
   */
  int getDependsOnMethodContextIdsCount();
  /**
   * <code>repeated string depends_on_method_context_ids = 24;</code>
   * @param index The index of the element to return.
   * @return The dependsOnMethodContextIds at the given index.
   */
  java.lang.String getDependsOnMethodContextIds(int index);
  /**
   * <code>repeated string depends_on_method_context_ids = 24;</code>
   * @param index The index of the value to return.
   * @return The bytes of the dependsOnMethodContextIds at the given index.
   */
  com.google.protobuf.ByteString
      getDependsOnMethodContextIdsBytes(int index);

  /**
   * <code>.data.ErrorContext error_context = 25;</code>
   * @return Whether the errorContext field is set.
   */
  boolean hasErrorContext();
  /**
   * <code>.data.ErrorContext error_context = 25;</code>
   * @return The errorContext.
   */
  eu.tsystems.mms.tic.testframework.report.model.ErrorContext getErrorContext();
  /**
   * <code>.data.ErrorContext error_context = 25;</code>
   */
  eu.tsystems.mms.tic.testframework.report.model.ErrorContextOrBuilder getErrorContextOrBuilder();

  /**
   * <code>repeated .data.PTestStep test_steps = 26;</code>
   */
  java.util.List<eu.tsystems.mms.tic.testframework.report.model.PTestStep> 
      getTestStepsList();
  /**
   * <code>repeated .data.PTestStep test_steps = 26;</code>
   */
  eu.tsystems.mms.tic.testframework.report.model.PTestStep getTestSteps(int index);
  /**
   * <code>repeated .data.PTestStep test_steps = 26;</code>
   */
  int getTestStepsCount();
  /**
   * <code>repeated .data.PTestStep test_steps = 26;</code>
   */
  java.util.List<? extends eu.tsystems.mms.tic.testframework.report.model.PTestStepOrBuilder> 
      getTestStepsOrBuilderList();
  /**
   * <code>repeated .data.PTestStep test_steps = 26;</code>
   */
  eu.tsystems.mms.tic.testframework.report.model.PTestStepOrBuilder getTestStepsOrBuilder(
      int index);

  /**
   * <code>string test_context_id = 27;</code>
   * @return The testContextId.
   */
  java.lang.String getTestContextId();
  /**
   * <code>string test_context_id = 27;</code>
   * @return The bytes for testContextId.
   */
  com.google.protobuf.ByteString
      getTestContextIdBytes();

  /**
   * <code>string suite_context_id = 28;</code>
   * @return The suiteContextId.
   */
  java.lang.String getSuiteContextId();
  /**
   * <code>string suite_context_id = 28;</code>
   * @return The bytes for suiteContextId.
   */
  com.google.protobuf.ByteString
      getSuiteContextIdBytes();

  /**
   * <code>repeated string session_context_ids = 29;</code>
   * @return A list containing the sessionContextIds.
   */
  java.util.List<java.lang.String>
      getSessionContextIdsList();
  /**
   * <code>repeated string session_context_ids = 29;</code>
   * @return The count of sessionContextIds.
   */
  int getSessionContextIdsCount();
  /**
   * <code>repeated string session_context_ids = 29;</code>
   * @param index The index of the element to return.
   * @return The sessionContextIds at the given index.
   */
  java.lang.String getSessionContextIds(int index);
  /**
   * <code>repeated string session_context_ids = 29;</code>
   * @param index The index of the value to return.
   * @return The bytes of the sessionContextIds at the given index.
   */
  com.google.protobuf.ByteString
      getSessionContextIdsBytes(int index);

  /**
   * <code>repeated string video_ids = 30;</code>
   * @return A list containing the videoIds.
   */
  java.util.List<java.lang.String>
      getVideoIdsList();
  /**
   * <code>repeated string video_ids = 30;</code>
   * @return The count of videoIds.
   */
  int getVideoIdsCount();
  /**
   * <code>repeated string video_ids = 30;</code>
   * @param index The index of the element to return.
   * @return The videoIds at the given index.
   */
  java.lang.String getVideoIds(int index);
  /**
   * <code>repeated string video_ids = 30;</code>
   * @param index The index of the value to return.
   * @return The bytes of the videoIds at the given index.
   */
  com.google.protobuf.ByteString
      getVideoIdsBytes(int index);

  /**
   * <pre>
   *    repeated string screenshot_ids = 31 [deprecated = true];
   * </pre>
   *
   * <code>string custom_context_json = 32;</code>
   * @return The customContextJson.
   */
  java.lang.String getCustomContextJson();
  /**
   * <pre>
   *    repeated string screenshot_ids = 31 [deprecated = true];
   * </pre>
   *
   * <code>string custom_context_json = 32;</code>
   * @return The bytes for customContextJson.
   */
  com.google.protobuf.ByteString
      getCustomContextJsonBytes();
}
