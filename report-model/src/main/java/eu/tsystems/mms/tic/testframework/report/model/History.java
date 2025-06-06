// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: report.proto

package eu.tsystems.mms.tic.testframework.report.model;

/**
 * Protobuf type {@code data.History}
 */
public final class History extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:data.History)
    HistoryOrBuilder {
private static final long serialVersionUID = 0L;
  // Use History.newBuilder() to construct.
  private History(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private History() {
    entries_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new History();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private History(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
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
            if (!((mutable_bitField0_ & 0x00000001) != 0)) {
              entries_ = new java.util.ArrayList<eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate>();
              mutable_bitField0_ |= 0x00000001;
            }
            entries_.add(
                input.readMessage(eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate.parser(), extensionRegistry));
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
      if (((mutable_bitField0_ & 0x00000001) != 0)) {
        entries_ = java.util.Collections.unmodifiableList(entries_);
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return eu.tsystems.mms.tic.testframework.report.model.Report.internal_static_data_History_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return eu.tsystems.mms.tic.testframework.report.model.Report.internal_static_data_History_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            eu.tsystems.mms.tic.testframework.report.model.History.class, eu.tsystems.mms.tic.testframework.report.model.History.Builder.class);
  }

  public static final int ENTRIES_FIELD_NUMBER = 1;
  private java.util.List<eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate> entries_;
  /**
   * <code>repeated .data.HistoryAggregate entries = 1;</code>
   */
  @java.lang.Override
  public java.util.List<eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate> getEntriesList() {
    return entries_;
  }
  /**
   * <code>repeated .data.HistoryAggregate entries = 1;</code>
   */
  @java.lang.Override
  public java.util.List<? extends eu.tsystems.mms.tic.testframework.report.model.HistoryAggregateOrBuilder> 
      getEntriesOrBuilderList() {
    return entries_;
  }
  /**
   * <code>repeated .data.HistoryAggregate entries = 1;</code>
   */
  @java.lang.Override
  public int getEntriesCount() {
    return entries_.size();
  }
  /**
   * <code>repeated .data.HistoryAggregate entries = 1;</code>
   */
  @java.lang.Override
  public eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate getEntries(int index) {
    return entries_.get(index);
  }
  /**
   * <code>repeated .data.HistoryAggregate entries = 1;</code>
   */
  @java.lang.Override
  public eu.tsystems.mms.tic.testframework.report.model.HistoryAggregateOrBuilder getEntriesOrBuilder(
      int index) {
    return entries_.get(index);
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
    for (int i = 0; i < entries_.size(); i++) {
      output.writeMessage(1, entries_.get(i));
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    for (int i = 0; i < entries_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, entries_.get(i));
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
    if (!(obj instanceof eu.tsystems.mms.tic.testframework.report.model.History)) {
      return super.equals(obj);
    }
    eu.tsystems.mms.tic.testframework.report.model.History other = (eu.tsystems.mms.tic.testframework.report.model.History) obj;

    if (!getEntriesList()
        .equals(other.getEntriesList())) return false;
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
    if (getEntriesCount() > 0) {
      hash = (37 * hash) + ENTRIES_FIELD_NUMBER;
      hash = (53 * hash) + getEntriesList().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static eu.tsystems.mms.tic.testframework.report.model.History parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.History parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.History parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.History parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.History parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.History parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.History parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.History parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.History parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.History parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.History parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static eu.tsystems.mms.tic.testframework.report.model.History parseFrom(
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
  public static Builder newBuilder(eu.tsystems.mms.tic.testframework.report.model.History prototype) {
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
   * Protobuf type {@code data.History}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:data.History)
      eu.tsystems.mms.tic.testframework.report.model.HistoryOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return eu.tsystems.mms.tic.testframework.report.model.Report.internal_static_data_History_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return eu.tsystems.mms.tic.testframework.report.model.Report.internal_static_data_History_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              eu.tsystems.mms.tic.testframework.report.model.History.class, eu.tsystems.mms.tic.testframework.report.model.History.Builder.class);
    }

    // Construct using eu.tsystems.mms.tic.testframework.report.model.History.newBuilder()
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
        getEntriesFieldBuilder();
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      if (entriesBuilder_ == null) {
        entries_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
      } else {
        entriesBuilder_.clear();
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return eu.tsystems.mms.tic.testframework.report.model.Report.internal_static_data_History_descriptor;
    }

    @java.lang.Override
    public eu.tsystems.mms.tic.testframework.report.model.History getDefaultInstanceForType() {
      return eu.tsystems.mms.tic.testframework.report.model.History.getDefaultInstance();
    }

    @java.lang.Override
    public eu.tsystems.mms.tic.testframework.report.model.History build() {
      eu.tsystems.mms.tic.testframework.report.model.History result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public eu.tsystems.mms.tic.testframework.report.model.History buildPartial() {
      eu.tsystems.mms.tic.testframework.report.model.History result = new eu.tsystems.mms.tic.testframework.report.model.History(this);
      int from_bitField0_ = bitField0_;
      if (entriesBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0)) {
          entries_ = java.util.Collections.unmodifiableList(entries_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.entries_ = entries_;
      } else {
        result.entries_ = entriesBuilder_.build();
      }
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
      if (other instanceof eu.tsystems.mms.tic.testframework.report.model.History) {
        return mergeFrom((eu.tsystems.mms.tic.testframework.report.model.History)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(eu.tsystems.mms.tic.testframework.report.model.History other) {
      if (other == eu.tsystems.mms.tic.testframework.report.model.History.getDefaultInstance()) return this;
      if (entriesBuilder_ == null) {
        if (!other.entries_.isEmpty()) {
          if (entries_.isEmpty()) {
            entries_ = other.entries_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureEntriesIsMutable();
            entries_.addAll(other.entries_);
          }
          onChanged();
        }
      } else {
        if (!other.entries_.isEmpty()) {
          if (entriesBuilder_.isEmpty()) {
            entriesBuilder_.dispose();
            entriesBuilder_ = null;
            entries_ = other.entries_;
            bitField0_ = (bitField0_ & ~0x00000001);
            entriesBuilder_ = 
              com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                 getEntriesFieldBuilder() : null;
          } else {
            entriesBuilder_.addAllMessages(other.entries_);
          }
        }
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
      eu.tsystems.mms.tic.testframework.report.model.History parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (eu.tsystems.mms.tic.testframework.report.model.History) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private java.util.List<eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate> entries_ =
      java.util.Collections.emptyList();
    private void ensureEntriesIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        entries_ = new java.util.ArrayList<eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate>(entries_);
        bitField0_ |= 0x00000001;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
        eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate, eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate.Builder, eu.tsystems.mms.tic.testframework.report.model.HistoryAggregateOrBuilder> entriesBuilder_;

    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public java.util.List<eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate> getEntriesList() {
      if (entriesBuilder_ == null) {
        return java.util.Collections.unmodifiableList(entries_);
      } else {
        return entriesBuilder_.getMessageList();
      }
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public int getEntriesCount() {
      if (entriesBuilder_ == null) {
        return entries_.size();
      } else {
        return entriesBuilder_.getCount();
      }
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate getEntries(int index) {
      if (entriesBuilder_ == null) {
        return entries_.get(index);
      } else {
        return entriesBuilder_.getMessage(index);
      }
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public Builder setEntries(
        int index, eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate value) {
      if (entriesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureEntriesIsMutable();
        entries_.set(index, value);
        onChanged();
      } else {
        entriesBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public Builder setEntries(
        int index, eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate.Builder builderForValue) {
      if (entriesBuilder_ == null) {
        ensureEntriesIsMutable();
        entries_.set(index, builderForValue.build());
        onChanged();
      } else {
        entriesBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public Builder addEntries(eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate value) {
      if (entriesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureEntriesIsMutable();
        entries_.add(value);
        onChanged();
      } else {
        entriesBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public Builder addEntries(
        int index, eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate value) {
      if (entriesBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureEntriesIsMutable();
        entries_.add(index, value);
        onChanged();
      } else {
        entriesBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public Builder addEntries(
        eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate.Builder builderForValue) {
      if (entriesBuilder_ == null) {
        ensureEntriesIsMutable();
        entries_.add(builderForValue.build());
        onChanged();
      } else {
        entriesBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public Builder addEntries(
        int index, eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate.Builder builderForValue) {
      if (entriesBuilder_ == null) {
        ensureEntriesIsMutable();
        entries_.add(index, builderForValue.build());
        onChanged();
      } else {
        entriesBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public Builder addAllEntries(
        java.lang.Iterable<? extends eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate> values) {
      if (entriesBuilder_ == null) {
        ensureEntriesIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, entries_);
        onChanged();
      } else {
        entriesBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public Builder clearEntries() {
      if (entriesBuilder_ == null) {
        entries_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
      } else {
        entriesBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public Builder removeEntries(int index) {
      if (entriesBuilder_ == null) {
        ensureEntriesIsMutable();
        entries_.remove(index);
        onChanged();
      } else {
        entriesBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate.Builder getEntriesBuilder(
        int index) {
      return getEntriesFieldBuilder().getBuilder(index);
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public eu.tsystems.mms.tic.testframework.report.model.HistoryAggregateOrBuilder getEntriesOrBuilder(
        int index) {
      if (entriesBuilder_ == null) {
        return entries_.get(index);  } else {
        return entriesBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public java.util.List<? extends eu.tsystems.mms.tic.testframework.report.model.HistoryAggregateOrBuilder> 
         getEntriesOrBuilderList() {
      if (entriesBuilder_ != null) {
        return entriesBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(entries_);
      }
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate.Builder addEntriesBuilder() {
      return getEntriesFieldBuilder().addBuilder(
          eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate.getDefaultInstance());
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate.Builder addEntriesBuilder(
        int index) {
      return getEntriesFieldBuilder().addBuilder(
          index, eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate.getDefaultInstance());
    }
    /**
     * <code>repeated .data.HistoryAggregate entries = 1;</code>
     */
    public java.util.List<eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate.Builder> 
         getEntriesBuilderList() {
      return getEntriesFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilderV3<
        eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate, eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate.Builder, eu.tsystems.mms.tic.testframework.report.model.HistoryAggregateOrBuilder> 
        getEntriesFieldBuilder() {
      if (entriesBuilder_ == null) {
        entriesBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
            eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate, eu.tsystems.mms.tic.testframework.report.model.HistoryAggregate.Builder, eu.tsystems.mms.tic.testframework.report.model.HistoryAggregateOrBuilder>(
                entries_,
                ((bitField0_ & 0x00000001) != 0),
                getParentForChildren(),
                isClean());
        entries_ = null;
      }
      return entriesBuilder_;
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


    // @@protoc_insertion_point(builder_scope:data.History)
  }

  // @@protoc_insertion_point(class_scope:data.History)
  private static final eu.tsystems.mms.tic.testframework.report.model.History DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new eu.tsystems.mms.tic.testframework.report.model.History();
  }

  public static eu.tsystems.mms.tic.testframework.report.model.History getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<History>
      PARSER = new com.google.protobuf.AbstractParser<History>() {
    @java.lang.Override
    public History parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new History(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<History> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<History> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public eu.tsystems.mms.tic.testframework.report.model.History getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

