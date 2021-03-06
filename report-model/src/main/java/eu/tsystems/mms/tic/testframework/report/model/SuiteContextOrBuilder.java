// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: framework.proto

package eu.tsystems.mms.tic.testframework.report.model;

public interface SuiteContextOrBuilder extends
    // @@protoc_insertion_point(interface_extends:data.SuiteContext)
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
   * <pre>
   * list of all test
   * </pre>
   *
   * <code>repeated string test_context_ids = 6 [deprecated = true];</code>
   * @return A list containing the testContextIds.
   */
  @java.lang.Deprecated java.util.List<java.lang.String>
      getTestContextIdsList();
  /**
   * <pre>
   * list of all test
   * </pre>
   *
   * <code>repeated string test_context_ids = 6 [deprecated = true];</code>
   * @return The count of testContextIds.
   */
  @java.lang.Deprecated int getTestContextIdsCount();
  /**
   * <pre>
   * list of all test
   * </pre>
   *
   * <code>repeated string test_context_ids = 6 [deprecated = true];</code>
   * @param index The index of the element to return.
   * @return The testContextIds at the given index.
   */
  @java.lang.Deprecated java.lang.String getTestContextIds(int index);
  /**
   * <pre>
   * list of all test
   * </pre>
   *
   * <code>repeated string test_context_ids = 6 [deprecated = true];</code>
   * @param index The index of the value to return.
   * @return The bytes of the testContextIds at the given index.
   */
  @java.lang.Deprecated com.google.protobuf.ByteString
      getTestContextIdsBytes(int index);

  /**
   * <pre>
   * reference
   * </pre>
   *
   * <code>string execution_context_id = 7;</code>
   * @return The executionContextId.
   */
  java.lang.String getExecutionContextId();
  /**
   * <pre>
   * reference
   * </pre>
   *
   * <code>string execution_context_id = 7;</code>
   * @return The bytes for executionContextId.
   */
  com.google.protobuf.ByteString
      getExecutionContextIdBytes();
}
