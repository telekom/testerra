// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: framework.proto

package eu.tsystems.mms.tic.testframework.report.model;

public interface RunConfigOrBuilder extends
    // @@protoc_insertion_point(interface_extends:data.RunConfig)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string runcfg = 1;</code>
   * @return The runcfg.
   */
  java.lang.String getRuncfg();
  /**
   * <code>string runcfg = 1;</code>
   * @return The bytes for runcfg.
   */
  com.google.protobuf.ByteString
      getRuncfgBytes();

  /**
   * <code>.data.BuildInformation build_information = 2;</code>
   * @return Whether the buildInformation field is set.
   */
  boolean hasBuildInformation();
  /**
   * <code>.data.BuildInformation build_information = 2;</code>
   * @return The buildInformation.
   */
  eu.tsystems.mms.tic.testframework.report.model.BuildInformation getBuildInformation();
  /**
   * <code>.data.BuildInformation build_information = 2;</code>
   */
  eu.tsystems.mms.tic.testframework.report.model.BuildInformationOrBuilder getBuildInformationOrBuilder();

  /**
   * <code>string report_name = 3;</code>
   * @return The reportName.
   */
  java.lang.String getReportName();
  /**
   * <code>string report_name = 3;</code>
   * @return The bytes for reportName.
   */
  com.google.protobuf.ByteString
      getReportNameBytes();
}
