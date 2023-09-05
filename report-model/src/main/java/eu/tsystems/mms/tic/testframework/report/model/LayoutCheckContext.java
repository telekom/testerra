// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: framework.proto

package eu.tsystems.mms.tic.testframework.report.model;

/**
 * Protobuf type {@code data.LayoutCheckContext}
 */
public final class LayoutCheckContext extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:data.LayoutCheckContext)
    LayoutCheckContextOrBuilder {
private static final long serialVersionUID = 0L;
  // Use LayoutCheckContext.newBuilder() to construct.
  private LayoutCheckContext(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private LayoutCheckContext() {
    image_ = "";
    expectedScreenshotId_ = "";
    actualScreenshotId_ = "";
    distanceScreenshotId_ = "";
    errorContextId_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new LayoutCheckContext();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private LayoutCheckContext(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            image_ = s;
            break;
          }
          case 17: {

            distance_ = input.readDouble();
            break;
          }
          case 26: {
            java.lang.String s = input.readStringRequireUtf8();

            expectedScreenshotId_ = s;
            break;
          }
          case 34: {
            java.lang.String s = input.readStringRequireUtf8();

            actualScreenshotId_ = s;
            break;
          }
          case 42: {
            java.lang.String s = input.readStringRequireUtf8();

            distanceScreenshotId_ = s;
            break;
          }
          case 50: {
            java.lang.String s = input.readStringRequireUtf8();

            errorContextId_ = s;
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return eu.tsystems.mms.tic.testframework.report.model.Framework.internal_static_data_LayoutCheckContext_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return eu.tsystems.mms.tic.testframework.report.model.Framework.internal_static_data_LayoutCheckContext_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext.class, eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext.Builder.class);
  }

  public static final int IMAGE_FIELD_NUMBER = 1;
  private volatile java.lang.Object image_;
  /**
   * <code>string image = 1;</code>
   * @return The image.
   */
  @java.lang.Override
  public java.lang.String getImage() {
    java.lang.Object ref = image_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      image_ = s;
      return s;
    }
  }
  /**
   * <code>string image = 1;</code>
   * @return The bytes for image.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getImageBytes() {
    java.lang.Object ref = image_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      image_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int DISTANCE_FIELD_NUMBER = 2;
  private double distance_;
  /**
   * <code>double distance = 2;</code>
   * @return The distance.
   */
  @java.lang.Override
  public double getDistance() {
    return distance_;
  }

  public static final int EXPECTED_SCREENSHOT_ID_FIELD_NUMBER = 3;
  private volatile java.lang.Object expectedScreenshotId_;
  /**
   * <code>string expected_screenshot_id = 3;</code>
   * @return The expectedScreenshotId.
   */
  @java.lang.Override
  public java.lang.String getExpectedScreenshotId() {
    java.lang.Object ref = expectedScreenshotId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      expectedScreenshotId_ = s;
      return s;
    }
  }
  /**
   * <code>string expected_screenshot_id = 3;</code>
   * @return The bytes for expectedScreenshotId.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getExpectedScreenshotIdBytes() {
    java.lang.Object ref = expectedScreenshotId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      expectedScreenshotId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int ACTUAL_SCREENSHOT_ID_FIELD_NUMBER = 4;
  private volatile java.lang.Object actualScreenshotId_;
  /**
   * <code>string actual_screenshot_id = 4;</code>
   * @return The actualScreenshotId.
   */
  @java.lang.Override
  public java.lang.String getActualScreenshotId() {
    java.lang.Object ref = actualScreenshotId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      actualScreenshotId_ = s;
      return s;
    }
  }
  /**
   * <code>string actual_screenshot_id = 4;</code>
   * @return The bytes for actualScreenshotId.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getActualScreenshotIdBytes() {
    java.lang.Object ref = actualScreenshotId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      actualScreenshotId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int DISTANCE_SCREENSHOT_ID_FIELD_NUMBER = 5;
  private volatile java.lang.Object distanceScreenshotId_;
  /**
   * <code>string distance_screenshot_id = 5;</code>
   * @return The distanceScreenshotId.
   */
  @java.lang.Override
  public java.lang.String getDistanceScreenshotId() {
    java.lang.Object ref = distanceScreenshotId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      distanceScreenshotId_ = s;
      return s;
    }
  }
  /**
   * <code>string distance_screenshot_id = 5;</code>
   * @return The bytes for distanceScreenshotId.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getDistanceScreenshotIdBytes() {
    java.lang.Object ref = distanceScreenshotId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      distanceScreenshotId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int ERROR_CONTEXT_ID_FIELD_NUMBER = 6;
  private volatile java.lang.Object errorContextId_;
  /**
   * <code>string error_context_id = 6;</code>
   * @return The errorContextId.
   */
  @java.lang.Override
  public java.lang.String getErrorContextId() {
    java.lang.Object ref = errorContextId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      errorContextId_ = s;
      return s;
    }
  }
  /**
   * <code>string error_context_id = 6;</code>
   * @return The bytes for errorContextId.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getErrorContextIdBytes() {
    java.lang.Object ref = errorContextId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      errorContextId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getImageBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, image_);
    }
    if (distance_ != 0D) {
      output.writeDouble(2, distance_);
    }
    if (!getExpectedScreenshotIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, expectedScreenshotId_);
    }
    if (!getActualScreenshotIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 4, actualScreenshotId_);
    }
    if (!getDistanceScreenshotIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 5, distanceScreenshotId_);
    }
    if (!getErrorContextIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 6, errorContextId_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getImageBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, image_);
    }
    if (distance_ != 0D) {
      size += com.google.protobuf.CodedOutputStream
        .computeDoubleSize(2, distance_);
    }
    if (!getExpectedScreenshotIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, expectedScreenshotId_);
    }
    if (!getActualScreenshotIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, actualScreenshotId_);
    }
    if (!getDistanceScreenshotIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(5, distanceScreenshotId_);
    }
    if (!getErrorContextIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(6, errorContextId_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext)) {
      return super.equals(obj);
    }
    eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext other = (eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext) obj;

    if (!getImage()
        .equals(other.getImage())) return false;
    if (java.lang.Double.doubleToLongBits(getDistance())
        != java.lang.Double.doubleToLongBits(
            other.getDistance())) return false;
    if (!getExpectedScreenshotId()
        .equals(other.getExpectedScreenshotId())) return false;
    if (!getActualScreenshotId()
        .equals(other.getActualScreenshotId())) return false;
    if (!getDistanceScreenshotId()
        .equals(other.getDistanceScreenshotId())) return false;
    if (!getErrorContextId()
        .equals(other.getErrorContextId())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + IMAGE_FIELD_NUMBER;
    hash = (53 * hash) + getImage().hashCode();
    hash = (37 * hash) + DISTANCE_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        java.lang.Double.doubleToLongBits(getDistance()));
    hash = (37 * hash) + EXPECTED_SCREENSHOT_ID_FIELD_NUMBER;
    hash = (53 * hash) + getExpectedScreenshotId().hashCode();
    hash = (37 * hash) + ACTUAL_SCREENSHOT_ID_FIELD_NUMBER;
    hash = (53 * hash) + getActualScreenshotId().hashCode();
    hash = (37 * hash) + DISTANCE_SCREENSHOT_ID_FIELD_NUMBER;
    hash = (53 * hash) + getDistanceScreenshotId().hashCode();
    hash = (37 * hash) + ERROR_CONTEXT_ID_FIELD_NUMBER;
    hash = (53 * hash) + getErrorContextId().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code data.LayoutCheckContext}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:data.LayoutCheckContext)
      eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContextOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return eu.tsystems.mms.tic.testframework.report.model.Framework.internal_static_data_LayoutCheckContext_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return eu.tsystems.mms.tic.testframework.report.model.Framework.internal_static_data_LayoutCheckContext_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext.class, eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext.Builder.class);
    }

    // Construct using eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      image_ = "";

      distance_ = 0D;

      expectedScreenshotId_ = "";

      actualScreenshotId_ = "";

      distanceScreenshotId_ = "";

      errorContextId_ = "";

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return eu.tsystems.mms.tic.testframework.report.model.Framework.internal_static_data_LayoutCheckContext_descriptor;
    }

    @java.lang.Override
    public eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext getDefaultInstanceForType() {
      return eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext.getDefaultInstance();
    }

    @java.lang.Override
    public eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext build() {
      eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext buildPartial() {
      eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext result = new eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext(this);
      result.image_ = image_;
      result.distance_ = distance_;
      result.expectedScreenshotId_ = expectedScreenshotId_;
      result.actualScreenshotId_ = actualScreenshotId_;
      result.distanceScreenshotId_ = distanceScreenshotId_;
      result.errorContextId_ = errorContextId_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext) {
        return mergeFrom((eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext other) {
      if (other == eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext.getDefaultInstance()) return this;
      if (!other.getImage().isEmpty()) {
        image_ = other.image_;
        onChanged();
      }
      if (other.getDistance() != 0D) {
        setDistance(other.getDistance());
      }
      if (!other.getExpectedScreenshotId().isEmpty()) {
        expectedScreenshotId_ = other.expectedScreenshotId_;
        onChanged();
      }
      if (!other.getActualScreenshotId().isEmpty()) {
        actualScreenshotId_ = other.actualScreenshotId_;
        onChanged();
      }
      if (!other.getDistanceScreenshotId().isEmpty()) {
        distanceScreenshotId_ = other.distanceScreenshotId_;
        onChanged();
      }
      if (!other.getErrorContextId().isEmpty()) {
        errorContextId_ = other.errorContextId_;
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object image_ = "";
    /**
     * <code>string image = 1;</code>
     * @return The image.
     */
    public java.lang.String getImage() {
      java.lang.Object ref = image_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        image_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string image = 1;</code>
     * @return The bytes for image.
     */
    public com.google.protobuf.ByteString
        getImageBytes() {
      java.lang.Object ref = image_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        image_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string image = 1;</code>
     * @param value The image to set.
     * @return This builder for chaining.
     */
    public Builder setImage(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      image_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string image = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearImage() {
      
      image_ = getDefaultInstance().getImage();
      onChanged();
      return this;
    }
    /**
     * <code>string image = 1;</code>
     * @param value The bytes for image to set.
     * @return This builder for chaining.
     */
    public Builder setImageBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      image_ = value;
      onChanged();
      return this;
    }

    private double distance_ ;
    /**
     * <code>double distance = 2;</code>
     * @return The distance.
     */
    @java.lang.Override
    public double getDistance() {
      return distance_;
    }
    /**
     * <code>double distance = 2;</code>
     * @param value The distance to set.
     * @return This builder for chaining.
     */
    public Builder setDistance(double value) {
      
      distance_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>double distance = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearDistance() {
      
      distance_ = 0D;
      onChanged();
      return this;
    }

    private java.lang.Object expectedScreenshotId_ = "";
    /**
     * <code>string expected_screenshot_id = 3;</code>
     * @return The expectedScreenshotId.
     */
    public java.lang.String getExpectedScreenshotId() {
      java.lang.Object ref = expectedScreenshotId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        expectedScreenshotId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string expected_screenshot_id = 3;</code>
     * @return The bytes for expectedScreenshotId.
     */
    public com.google.protobuf.ByteString
        getExpectedScreenshotIdBytes() {
      java.lang.Object ref = expectedScreenshotId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        expectedScreenshotId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string expected_screenshot_id = 3;</code>
     * @param value The expectedScreenshotId to set.
     * @return This builder for chaining.
     */
    public Builder setExpectedScreenshotId(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      expectedScreenshotId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string expected_screenshot_id = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearExpectedScreenshotId() {
      
      expectedScreenshotId_ = getDefaultInstance().getExpectedScreenshotId();
      onChanged();
      return this;
    }
    /**
     * <code>string expected_screenshot_id = 3;</code>
     * @param value The bytes for expectedScreenshotId to set.
     * @return This builder for chaining.
     */
    public Builder setExpectedScreenshotIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      expectedScreenshotId_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object actualScreenshotId_ = "";
    /**
     * <code>string actual_screenshot_id = 4;</code>
     * @return The actualScreenshotId.
     */
    public java.lang.String getActualScreenshotId() {
      java.lang.Object ref = actualScreenshotId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        actualScreenshotId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string actual_screenshot_id = 4;</code>
     * @return The bytes for actualScreenshotId.
     */
    public com.google.protobuf.ByteString
        getActualScreenshotIdBytes() {
      java.lang.Object ref = actualScreenshotId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        actualScreenshotId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string actual_screenshot_id = 4;</code>
     * @param value The actualScreenshotId to set.
     * @return This builder for chaining.
     */
    public Builder setActualScreenshotId(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      actualScreenshotId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string actual_screenshot_id = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearActualScreenshotId() {
      
      actualScreenshotId_ = getDefaultInstance().getActualScreenshotId();
      onChanged();
      return this;
    }
    /**
     * <code>string actual_screenshot_id = 4;</code>
     * @param value The bytes for actualScreenshotId to set.
     * @return This builder for chaining.
     */
    public Builder setActualScreenshotIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      actualScreenshotId_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object distanceScreenshotId_ = "";
    /**
     * <code>string distance_screenshot_id = 5;</code>
     * @return The distanceScreenshotId.
     */
    public java.lang.String getDistanceScreenshotId() {
      java.lang.Object ref = distanceScreenshotId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        distanceScreenshotId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string distance_screenshot_id = 5;</code>
     * @return The bytes for distanceScreenshotId.
     */
    public com.google.protobuf.ByteString
        getDistanceScreenshotIdBytes() {
      java.lang.Object ref = distanceScreenshotId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        distanceScreenshotId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string distance_screenshot_id = 5;</code>
     * @param value The distanceScreenshotId to set.
     * @return This builder for chaining.
     */
    public Builder setDistanceScreenshotId(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      distanceScreenshotId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string distance_screenshot_id = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearDistanceScreenshotId() {
      
      distanceScreenshotId_ = getDefaultInstance().getDistanceScreenshotId();
      onChanged();
      return this;
    }
    /**
     * <code>string distance_screenshot_id = 5;</code>
     * @param value The bytes for distanceScreenshotId to set.
     * @return This builder for chaining.
     */
    public Builder setDistanceScreenshotIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      distanceScreenshotId_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object errorContextId_ = "";
    /**
     * <code>string error_context_id = 6;</code>
     * @return The errorContextId.
     */
    public java.lang.String getErrorContextId() {
      java.lang.Object ref = errorContextId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        errorContextId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string error_context_id = 6;</code>
     * @return The bytes for errorContextId.
     */
    public com.google.protobuf.ByteString
        getErrorContextIdBytes() {
      java.lang.Object ref = errorContextId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        errorContextId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string error_context_id = 6;</code>
     * @param value The errorContextId to set.
     * @return This builder for chaining.
     */
    public Builder setErrorContextId(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      errorContextId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string error_context_id = 6;</code>
     * @return This builder for chaining.
     */
    public Builder clearErrorContextId() {
      
      errorContextId_ = getDefaultInstance().getErrorContextId();
      onChanged();
      return this;
    }
    /**
     * <code>string error_context_id = 6;</code>
     * @param value The bytes for errorContextId to set.
     * @return This builder for chaining.
     */
    public Builder setErrorContextIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      errorContextId_ = value;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:data.LayoutCheckContext)
  }

  // @@protoc_insertion_point(class_scope:data.LayoutCheckContext)
  private static final eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext();
  }

  public static eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<LayoutCheckContext>
      PARSER = new com.google.protobuf.AbstractParser<LayoutCheckContext>() {
    @java.lang.Override
    public LayoutCheckContext parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new LayoutCheckContext(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<LayoutCheckContext> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<LayoutCheckContext> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public eu.tsystems.mms.tic.testframework.report.model.LayoutCheckContext getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
