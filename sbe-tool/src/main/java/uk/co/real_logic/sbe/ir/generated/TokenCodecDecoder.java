/* Generated SBE (Simple Binary Encoding) message codec. */
package uk.co.real_logic.sbe.ir.generated;

import org.agrona.MutableDirectBuffer;
import org.agrona.DirectBuffer;


/**
 * Codec for an IR Token.
 */
@SuppressWarnings("all")
public final class TokenCodecDecoder
{
    private static final boolean ENABLE_BOUNDS_CHECKS = !Boolean.getBoolean("agrona.disable.bounds.checks");

    private static final boolean ENABLE_ACCESS_ORDER_CHECKS = Boolean.parseBoolean(System.getProperty(
        "sbe.enable.access.order.checks",
        Boolean.toString(ENABLE_BOUNDS_CHECKS)));

    /**
     * The states in which a encoder/decoder/codec can live.
     *
     * <p>The state machine diagram below describes the valid state transitions
     * according to the order in which fields may be accessed safely.
     *
     * <pre>{@code
     *   digraph G {
     *       NOT_WRAPPED -> V0_BLOCK [label="  .wrap(...)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  .tokenOffset(value)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  .tokenSize(value)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  .fieldId(value)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  .tokenVersion(value)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  .componentTokenCount(value)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  .signal(value)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  .primitiveType(value)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  .byteOrder(value)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  .presence(value)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  .deprecated(value)  "];
     *       V0_BLOCK -> V0_NAME_DONE [label="  .name(value)  "];
     *       V0_NAME_DONE -> V0_CONSTVALUE_DONE [label="  .constValue(value)  "];
     *       V0_CONSTVALUE_DONE -> V0_MINVALUE_DONE [label="  .minValue(value)  "];
     *       V0_MINVALUE_DONE -> V0_MAXVALUE_DONE [label="  .maxValue(value)  "];
     *       V0_MAXVALUE_DONE -> V0_NULLVALUE_DONE [label="  .nullValue(value)  "];
     *       V0_NULLVALUE_DONE -> V0_CHARACTERENCODING_DONE [label="  .characterEncoding(value)  "];
     *       V0_CHARACTERENCODING_DONE -> V0_EPOCH_DONE [label="  .epoch(value)  "];
     *       V0_EPOCH_DONE -> V0_TIMEUNIT_DONE [label="  .timeUnit(value)  "];
     *       V0_TIMEUNIT_DONE -> V0_SEMANTICTYPE_DONE [label="  .semanticType(value)  "];
     *       V0_SEMANTICTYPE_DONE -> V0_DESCRIPTION_DONE [label="  .description(value)  "];
     *       V0_DESCRIPTION_DONE -> V0_REFERENCEDNAME_DONE [label="  .referencedName(value)  "];
     *   }
     * }</pre>
     */
    private enum CodecState
    {
        NOT_WRAPPED,
        V0_BLOCK,
        V0_NAME_DONE,
        V0_CONSTVALUE_DONE,
        V0_MINVALUE_DONE,
        V0_MAXVALUE_DONE,
        V0_NULLVALUE_DONE,
        V0_CHARACTERENCODING_DONE,
        V0_EPOCH_DONE,
        V0_TIMEUNIT_DONE,
        V0_SEMANTICTYPE_DONE,
        V0_DESCRIPTION_DONE,
        V0_REFERENCEDNAME_DONE,
    }

    private CodecState codecState = CodecState.NOT_WRAPPED;

    private CodecState codecState()
    {
        return codecState;
    }

    private void codecState(CodecState newState)
    {
        codecState = newState;
    }

    public static final int BLOCK_LENGTH = 28;
    public static final int TEMPLATE_ID = 2;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 0;
    public static final String SEMANTIC_VERSION = "";
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final TokenCodecDecoder parentMessage = this;
    private DirectBuffer buffer;
    private int initialOffset;
    private int offset;
    private int limit;
    int actingBlockLength;
    int actingVersion;

    public int sbeBlockLength()
    {
        return BLOCK_LENGTH;
    }

    public int sbeTemplateId()
    {
        return TEMPLATE_ID;
    }

    public int sbeSchemaId()
    {
        return SCHEMA_ID;
    }

    public int sbeSchemaVersion()
    {
        return SCHEMA_VERSION;
    }

    public String sbeSemanticType()
    {
        return "";
    }

    public DirectBuffer buffer()
    {
        return buffer;
    }

    public int initialOffset()
    {
        return initialOffset;
    }

    public int offset()
    {
        return offset;
    }

    public TokenCodecDecoder wrap(
        final DirectBuffer buffer,
        final int offset,
        final int actingBlockLength,
        final int actingVersion)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.initialOffset = offset;
        this.offset = offset;
        this.actingBlockLength = actingBlockLength;
        this.actingVersion = actingVersion;
        limit(offset + actingBlockLength);

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch(actingVersion)            {
                case 0:
                    codecState(CodecState.V0_BLOCK);
                    break;
                default:
                    codecState(CodecState.V0_BLOCK);
                    break;
            }
        }

        return this;
    }

    public TokenCodecDecoder wrapAndApplyHeader(
        final DirectBuffer buffer,
        final int offset,
        final MessageHeaderDecoder headerDecoder)
    {
        headerDecoder.wrap(buffer, offset);

        final int templateId = headerDecoder.templateId();
        if (TEMPLATE_ID != templateId)
        {
            throw new IllegalStateException("Invalid TEMPLATE_ID: " + templateId);
        }

        return wrap(
            buffer,
            offset + MessageHeaderDecoder.ENCODED_LENGTH,
            headerDecoder.blockLength(),
            headerDecoder.version());
    }

    public TokenCodecDecoder sbeRewind()
    {
        return wrap(buffer, initialOffset, actingBlockLength, actingVersion);
    }

    public int sbeDecodedLength()
    {
        final int currentLimit = limit();
        final CodecState currentCodecState = codecState();
        sbeSkip();
        final int decodedLength = encodedLength();
        limit(currentLimit);

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            codecState(currentCodecState);
        }

        return decodedLength;
    }

    public int encodedLength()
    {
        return limit - offset;
    }

    public int limit()
    {
        return limit;
    }

    public void limit(final int limit)
    {
        this.limit = limit;
    }

    public static int tokenOffsetId()
    {
        return 1;
    }

    public static int tokenOffsetSinceVersion()
    {
        return 0;
    }

    public static int tokenOffsetEncodingOffset()
    {
        return 0;
    }

    public static int tokenOffsetEncodingLength()
    {
        return 4;
    }

    public static String tokenOffsetMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int tokenOffsetNullValue()
    {
        return -2147483648;
    }

    public static int tokenOffsetMinValue()
    {
        return -2147483647;
    }

    public static int tokenOffsetMaxValue()
    {
        return 2147483647;
    }

    public int tokenOffset()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            if (codecState() == CodecState.NOT_WRAPPED)
            {
                throw new IllegalStateException("Cannot access field \"tokenOffset\" in state: " + codecState());
            }
        }

        return buffer.getInt(offset + 0, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int tokenSizeId()
    {
        return 2;
    }

    public static int tokenSizeSinceVersion()
    {
        return 0;
    }

    public static int tokenSizeEncodingOffset()
    {
        return 4;
    }

    public static int tokenSizeEncodingLength()
    {
        return 4;
    }

    public static String tokenSizeMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int tokenSizeNullValue()
    {
        return -2147483648;
    }

    public static int tokenSizeMinValue()
    {
        return -2147483647;
    }

    public static int tokenSizeMaxValue()
    {
        return 2147483647;
    }

    public int tokenSize()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            if (codecState() == CodecState.NOT_WRAPPED)
            {
                throw new IllegalStateException("Cannot access field \"tokenSize\" in state: " + codecState());
            }
        }

        return buffer.getInt(offset + 4, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int fieldIdId()
    {
        return 3;
    }

    public static int fieldIdSinceVersion()
    {
        return 0;
    }

    public static int fieldIdEncodingOffset()
    {
        return 8;
    }

    public static int fieldIdEncodingLength()
    {
        return 4;
    }

    public static String fieldIdMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int fieldIdNullValue()
    {
        return -2147483648;
    }

    public static int fieldIdMinValue()
    {
        return -2147483647;
    }

    public static int fieldIdMaxValue()
    {
        return 2147483647;
    }

    public int fieldId()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            if (codecState() == CodecState.NOT_WRAPPED)
            {
                throw new IllegalStateException("Cannot access field \"fieldId\" in state: " + codecState());
            }
        }

        return buffer.getInt(offset + 8, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int tokenVersionId()
    {
        return 4;
    }

    public static int tokenVersionSinceVersion()
    {
        return 0;
    }

    public static int tokenVersionEncodingOffset()
    {
        return 12;
    }

    public static int tokenVersionEncodingLength()
    {
        return 4;
    }

    public static String tokenVersionMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int tokenVersionNullValue()
    {
        return -2147483648;
    }

    public static int tokenVersionMinValue()
    {
        return -2147483647;
    }

    public static int tokenVersionMaxValue()
    {
        return 2147483647;
    }

    public int tokenVersion()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            if (codecState() == CodecState.NOT_WRAPPED)
            {
                throw new IllegalStateException("Cannot access field \"tokenVersion\" in state: " + codecState());
            }
        }

        return buffer.getInt(offset + 12, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int componentTokenCountId()
    {
        return 5;
    }

    public static int componentTokenCountSinceVersion()
    {
        return 0;
    }

    public static int componentTokenCountEncodingOffset()
    {
        return 16;
    }

    public static int componentTokenCountEncodingLength()
    {
        return 4;
    }

    public static String componentTokenCountMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int componentTokenCountNullValue()
    {
        return -2147483648;
    }

    public static int componentTokenCountMinValue()
    {
        return -2147483647;
    }

    public static int componentTokenCountMaxValue()
    {
        return 2147483647;
    }

    public int componentTokenCount()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            if (codecState() == CodecState.NOT_WRAPPED)
            {
                throw new IllegalStateException("Cannot access field \"componentTokenCount\" in state: " + codecState());
            }
        }

        return buffer.getInt(offset + 16, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int signalId()
    {
        return 6;
    }

    public static int signalSinceVersion()
    {
        return 0;
    }

    public static int signalEncodingOffset()
    {
        return 20;
    }

    public static int signalEncodingLength()
    {
        return 1;
    }

    public static String signalMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public short signalRaw()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            if (codecState() == CodecState.NOT_WRAPPED)
            {
                throw new IllegalStateException("Cannot access field \"signal\" in state: " + codecState());
            }
        }

        return ((short)(buffer.getByte(offset + 20) & 0xFF));
    }

    public SignalCodec signal()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            if (codecState() == CodecState.NOT_WRAPPED)
            {
                throw new IllegalStateException("Cannot access field \"signal\" in state: " + codecState());
            }
        }

        return SignalCodec.get(((short)(buffer.getByte(offset + 20) & 0xFF)));
    }


    public static int primitiveTypeId()
    {
        return 7;
    }

    public static int primitiveTypeSinceVersion()
    {
        return 0;
    }

    public static int primitiveTypeEncodingOffset()
    {
        return 21;
    }

    public static int primitiveTypeEncodingLength()
    {
        return 1;
    }

    public static String primitiveTypeMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public short primitiveTypeRaw()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            if (codecState() == CodecState.NOT_WRAPPED)
            {
                throw new IllegalStateException("Cannot access field \"primitiveType\" in state: " + codecState());
            }
        }

        return ((short)(buffer.getByte(offset + 21) & 0xFF));
    }

    public PrimitiveTypeCodec primitiveType()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            if (codecState() == CodecState.NOT_WRAPPED)
            {
                throw new IllegalStateException("Cannot access field \"primitiveType\" in state: " + codecState());
            }
        }

        return PrimitiveTypeCodec.get(((short)(buffer.getByte(offset + 21) & 0xFF)));
    }


    public static int byteOrderId()
    {
        return 8;
    }

    public static int byteOrderSinceVersion()
    {
        return 0;
    }

    public static int byteOrderEncodingOffset()
    {
        return 22;
    }

    public static int byteOrderEncodingLength()
    {
        return 1;
    }

    public static String byteOrderMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public short byteOrderRaw()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            if (codecState() == CodecState.NOT_WRAPPED)
            {
                throw new IllegalStateException("Cannot access field \"byteOrder\" in state: " + codecState());
            }
        }

        return ((short)(buffer.getByte(offset + 22) & 0xFF));
    }

    public ByteOrderCodec byteOrder()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            if (codecState() == CodecState.NOT_WRAPPED)
            {
                throw new IllegalStateException("Cannot access field \"byteOrder\" in state: " + codecState());
            }
        }

        return ByteOrderCodec.get(((short)(buffer.getByte(offset + 22) & 0xFF)));
    }


    public static int presenceId()
    {
        return 9;
    }

    public static int presenceSinceVersion()
    {
        return 0;
    }

    public static int presenceEncodingOffset()
    {
        return 23;
    }

    public static int presenceEncodingLength()
    {
        return 1;
    }

    public static String presenceMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public short presenceRaw()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            if (codecState() == CodecState.NOT_WRAPPED)
            {
                throw new IllegalStateException("Cannot access field \"presence\" in state: " + codecState());
            }
        }

        return ((short)(buffer.getByte(offset + 23) & 0xFF));
    }

    public PresenceCodec presence()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            if (codecState() == CodecState.NOT_WRAPPED)
            {
                throw new IllegalStateException("Cannot access field \"presence\" in state: " + codecState());
            }
        }

        return PresenceCodec.get(((short)(buffer.getByte(offset + 23) & 0xFF)));
    }


    public static int deprecatedId()
    {
        return 10;
    }

    public static int deprecatedSinceVersion()
    {
        return 0;
    }

    public static int deprecatedEncodingOffset()
    {
        return 24;
    }

    public static int deprecatedEncodingLength()
    {
        return 4;
    }

    public static String deprecatedMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "optional";
        }

        return "";
    }

    public static int deprecatedNullValue()
    {
        return 0;
    }

    public static int deprecatedMinValue()
    {
        return -2147483647;
    }

    public static int deprecatedMaxValue()
    {
        return 2147483647;
    }

    public int deprecated()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            if (codecState() == CodecState.NOT_WRAPPED)
            {
                throw new IllegalStateException("Cannot access field \"deprecated\" in state: " + codecState());
            }
        }

        return buffer.getInt(offset + 24, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int nameId()
    {
        return 11;
    }

    public static int nameSinceVersion()
    {
        return 0;
    }

    public static String nameCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.UTF_8.name();
    }

    public static String nameMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int nameHeaderLength()
    {
        return 2;
    }

    public int nameLength()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_BLOCK:
                    codecState(CodecState.V0_NAME_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"name\" in state: " + codecState());
            }
        }

        final int limit = parentMessage.limit();
        return (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }

    public int skipName()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_BLOCK:
                    codecState(CodecState.V0_NAME_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"name\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int dataOffset = limit + headerLength;
        parentMessage.limit(dataOffset + dataLength);

        return dataLength;
    }

    public int getName(final MutableDirectBuffer dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_BLOCK:
                    codecState(CodecState.V0_NAME_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"name\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public int getName(final byte[] dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_BLOCK:
                    codecState(CodecState.V0_NAME_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"name\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public void wrapName(final DirectBuffer wrapBuffer)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_BLOCK:
                    codecState(CodecState.V0_NAME_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"name\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);
        wrapBuffer.wrap(buffer, limit + headerLength, dataLength);
    }

    public String name()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_BLOCK:
                    codecState(CodecState.V0_NAME_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"name\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);

        if (0 == dataLength)
        {
            return "";
        }

        final byte[] tmp = new byte[dataLength];
        buffer.getBytes(limit + headerLength, tmp, 0, dataLength);

        return new String(tmp, java.nio.charset.StandardCharsets.UTF_8);
    }

    public static int constValueId()
    {
        return 12;
    }

    public static int constValueSinceVersion()
    {
        return 0;
    }

    public static String constValueCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.UTF_8.name();
    }

    public static String constValueMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int constValueHeaderLength()
    {
        return 2;
    }

    public int constValueLength()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_NAME_DONE:
                    codecState(CodecState.V0_CONSTVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"constValue\" in state: " + codecState());
            }
        }

        final int limit = parentMessage.limit();
        return (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }

    public int skipConstValue()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_NAME_DONE:
                    codecState(CodecState.V0_CONSTVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"constValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int dataOffset = limit + headerLength;
        parentMessage.limit(dataOffset + dataLength);

        return dataLength;
    }

    public int getConstValue(final MutableDirectBuffer dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_NAME_DONE:
                    codecState(CodecState.V0_CONSTVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"constValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public int getConstValue(final byte[] dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_NAME_DONE:
                    codecState(CodecState.V0_CONSTVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"constValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public void wrapConstValue(final DirectBuffer wrapBuffer)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_NAME_DONE:
                    codecState(CodecState.V0_CONSTVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"constValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);
        wrapBuffer.wrap(buffer, limit + headerLength, dataLength);
    }

    public String constValue()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_NAME_DONE:
                    codecState(CodecState.V0_CONSTVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"constValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);

        if (0 == dataLength)
        {
            return "";
        }

        final byte[] tmp = new byte[dataLength];
        buffer.getBytes(limit + headerLength, tmp, 0, dataLength);

        return new String(tmp, java.nio.charset.StandardCharsets.UTF_8);
    }

    public static int minValueId()
    {
        return 13;
    }

    public static int minValueSinceVersion()
    {
        return 0;
    }

    public static String minValueCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.UTF_8.name();
    }

    public static String minValueMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int minValueHeaderLength()
    {
        return 2;
    }

    public int minValueLength()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_CONSTVALUE_DONE:
                    codecState(CodecState.V0_MINVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"minValue\" in state: " + codecState());
            }
        }

        final int limit = parentMessage.limit();
        return (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }

    public int skipMinValue()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_CONSTVALUE_DONE:
                    codecState(CodecState.V0_MINVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"minValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int dataOffset = limit + headerLength;
        parentMessage.limit(dataOffset + dataLength);

        return dataLength;
    }

    public int getMinValue(final MutableDirectBuffer dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_CONSTVALUE_DONE:
                    codecState(CodecState.V0_MINVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"minValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public int getMinValue(final byte[] dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_CONSTVALUE_DONE:
                    codecState(CodecState.V0_MINVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"minValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public void wrapMinValue(final DirectBuffer wrapBuffer)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_CONSTVALUE_DONE:
                    codecState(CodecState.V0_MINVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"minValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);
        wrapBuffer.wrap(buffer, limit + headerLength, dataLength);
    }

    public String minValue()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_CONSTVALUE_DONE:
                    codecState(CodecState.V0_MINVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"minValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);

        if (0 == dataLength)
        {
            return "";
        }

        final byte[] tmp = new byte[dataLength];
        buffer.getBytes(limit + headerLength, tmp, 0, dataLength);

        return new String(tmp, java.nio.charset.StandardCharsets.UTF_8);
    }

    public static int maxValueId()
    {
        return 14;
    }

    public static int maxValueSinceVersion()
    {
        return 0;
    }

    public static String maxValueCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.UTF_8.name();
    }

    public static String maxValueMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int maxValueHeaderLength()
    {
        return 2;
    }

    public int maxValueLength()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_MINVALUE_DONE:
                    codecState(CodecState.V0_MAXVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"maxValue\" in state: " + codecState());
            }
        }

        final int limit = parentMessage.limit();
        return (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }

    public int skipMaxValue()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_MINVALUE_DONE:
                    codecState(CodecState.V0_MAXVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"maxValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int dataOffset = limit + headerLength;
        parentMessage.limit(dataOffset + dataLength);

        return dataLength;
    }

    public int getMaxValue(final MutableDirectBuffer dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_MINVALUE_DONE:
                    codecState(CodecState.V0_MAXVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"maxValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public int getMaxValue(final byte[] dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_MINVALUE_DONE:
                    codecState(CodecState.V0_MAXVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"maxValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public void wrapMaxValue(final DirectBuffer wrapBuffer)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_MINVALUE_DONE:
                    codecState(CodecState.V0_MAXVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"maxValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);
        wrapBuffer.wrap(buffer, limit + headerLength, dataLength);
    }

    public String maxValue()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_MINVALUE_DONE:
                    codecState(CodecState.V0_MAXVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"maxValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);

        if (0 == dataLength)
        {
            return "";
        }

        final byte[] tmp = new byte[dataLength];
        buffer.getBytes(limit + headerLength, tmp, 0, dataLength);

        return new String(tmp, java.nio.charset.StandardCharsets.UTF_8);
    }

    public static int nullValueId()
    {
        return 15;
    }

    public static int nullValueSinceVersion()
    {
        return 0;
    }

    public static String nullValueCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.UTF_8.name();
    }

    public static String nullValueMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int nullValueHeaderLength()
    {
        return 2;
    }

    public int nullValueLength()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_MAXVALUE_DONE:
                    codecState(CodecState.V0_NULLVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"nullValue\" in state: " + codecState());
            }
        }

        final int limit = parentMessage.limit();
        return (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }

    public int skipNullValue()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_MAXVALUE_DONE:
                    codecState(CodecState.V0_NULLVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"nullValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int dataOffset = limit + headerLength;
        parentMessage.limit(dataOffset + dataLength);

        return dataLength;
    }

    public int getNullValue(final MutableDirectBuffer dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_MAXVALUE_DONE:
                    codecState(CodecState.V0_NULLVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"nullValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public int getNullValue(final byte[] dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_MAXVALUE_DONE:
                    codecState(CodecState.V0_NULLVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"nullValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public void wrapNullValue(final DirectBuffer wrapBuffer)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_MAXVALUE_DONE:
                    codecState(CodecState.V0_NULLVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"nullValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);
        wrapBuffer.wrap(buffer, limit + headerLength, dataLength);
    }

    public String nullValue()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_MAXVALUE_DONE:
                    codecState(CodecState.V0_NULLVALUE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"nullValue\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);

        if (0 == dataLength)
        {
            return "";
        }

        final byte[] tmp = new byte[dataLength];
        buffer.getBytes(limit + headerLength, tmp, 0, dataLength);

        return new String(tmp, java.nio.charset.StandardCharsets.UTF_8);
    }

    public static int characterEncodingId()
    {
        return 16;
    }

    public static int characterEncodingSinceVersion()
    {
        return 0;
    }

    public static String characterEncodingCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.UTF_8.name();
    }

    public static String characterEncodingMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int characterEncodingHeaderLength()
    {
        return 2;
    }

    public int characterEncodingLength()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_NULLVALUE_DONE:
                    codecState(CodecState.V0_CHARACTERENCODING_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"characterEncoding\" in state: " + codecState());
            }
        }

        final int limit = parentMessage.limit();
        return (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }

    public int skipCharacterEncoding()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_NULLVALUE_DONE:
                    codecState(CodecState.V0_CHARACTERENCODING_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"characterEncoding\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int dataOffset = limit + headerLength;
        parentMessage.limit(dataOffset + dataLength);

        return dataLength;
    }

    public int getCharacterEncoding(final MutableDirectBuffer dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_NULLVALUE_DONE:
                    codecState(CodecState.V0_CHARACTERENCODING_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"characterEncoding\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public int getCharacterEncoding(final byte[] dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_NULLVALUE_DONE:
                    codecState(CodecState.V0_CHARACTERENCODING_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"characterEncoding\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public void wrapCharacterEncoding(final DirectBuffer wrapBuffer)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_NULLVALUE_DONE:
                    codecState(CodecState.V0_CHARACTERENCODING_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"characterEncoding\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);
        wrapBuffer.wrap(buffer, limit + headerLength, dataLength);
    }

    public String characterEncoding()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_NULLVALUE_DONE:
                    codecState(CodecState.V0_CHARACTERENCODING_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"characterEncoding\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);

        if (0 == dataLength)
        {
            return "";
        }

        final byte[] tmp = new byte[dataLength];
        buffer.getBytes(limit + headerLength, tmp, 0, dataLength);

        return new String(tmp, java.nio.charset.StandardCharsets.UTF_8);
    }

    public static int epochId()
    {
        return 17;
    }

    public static int epochSinceVersion()
    {
        return 0;
    }

    public static String epochCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.UTF_8.name();
    }

    public static String epochMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int epochHeaderLength()
    {
        return 2;
    }

    public int epochLength()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_CHARACTERENCODING_DONE:
                    codecState(CodecState.V0_EPOCH_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"epoch\" in state: " + codecState());
            }
        }

        final int limit = parentMessage.limit();
        return (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }

    public int skipEpoch()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_CHARACTERENCODING_DONE:
                    codecState(CodecState.V0_EPOCH_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"epoch\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int dataOffset = limit + headerLength;
        parentMessage.limit(dataOffset + dataLength);

        return dataLength;
    }

    public int getEpoch(final MutableDirectBuffer dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_CHARACTERENCODING_DONE:
                    codecState(CodecState.V0_EPOCH_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"epoch\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public int getEpoch(final byte[] dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_CHARACTERENCODING_DONE:
                    codecState(CodecState.V0_EPOCH_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"epoch\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public void wrapEpoch(final DirectBuffer wrapBuffer)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_CHARACTERENCODING_DONE:
                    codecState(CodecState.V0_EPOCH_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"epoch\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);
        wrapBuffer.wrap(buffer, limit + headerLength, dataLength);
    }

    public String epoch()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_CHARACTERENCODING_DONE:
                    codecState(CodecState.V0_EPOCH_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"epoch\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);

        if (0 == dataLength)
        {
            return "";
        }

        final byte[] tmp = new byte[dataLength];
        buffer.getBytes(limit + headerLength, tmp, 0, dataLength);

        return new String(tmp, java.nio.charset.StandardCharsets.UTF_8);
    }

    public static int timeUnitId()
    {
        return 18;
    }

    public static int timeUnitSinceVersion()
    {
        return 0;
    }

    public static String timeUnitCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.UTF_8.name();
    }

    public static String timeUnitMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int timeUnitHeaderLength()
    {
        return 2;
    }

    public int timeUnitLength()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_EPOCH_DONE:
                    codecState(CodecState.V0_TIMEUNIT_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"timeUnit\" in state: " + codecState());
            }
        }

        final int limit = parentMessage.limit();
        return (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }

    public int skipTimeUnit()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_EPOCH_DONE:
                    codecState(CodecState.V0_TIMEUNIT_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"timeUnit\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int dataOffset = limit + headerLength;
        parentMessage.limit(dataOffset + dataLength);

        return dataLength;
    }

    public int getTimeUnit(final MutableDirectBuffer dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_EPOCH_DONE:
                    codecState(CodecState.V0_TIMEUNIT_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"timeUnit\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public int getTimeUnit(final byte[] dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_EPOCH_DONE:
                    codecState(CodecState.V0_TIMEUNIT_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"timeUnit\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public void wrapTimeUnit(final DirectBuffer wrapBuffer)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_EPOCH_DONE:
                    codecState(CodecState.V0_TIMEUNIT_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"timeUnit\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);
        wrapBuffer.wrap(buffer, limit + headerLength, dataLength);
    }

    public String timeUnit()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_EPOCH_DONE:
                    codecState(CodecState.V0_TIMEUNIT_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"timeUnit\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);

        if (0 == dataLength)
        {
            return "";
        }

        final byte[] tmp = new byte[dataLength];
        buffer.getBytes(limit + headerLength, tmp, 0, dataLength);

        return new String(tmp, java.nio.charset.StandardCharsets.UTF_8);
    }

    public static int semanticTypeId()
    {
        return 19;
    }

    public static int semanticTypeSinceVersion()
    {
        return 0;
    }

    public static String semanticTypeCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.UTF_8.name();
    }

    public static String semanticTypeMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int semanticTypeHeaderLength()
    {
        return 2;
    }

    public int semanticTypeLength()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_TIMEUNIT_DONE:
                    codecState(CodecState.V0_SEMANTICTYPE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"semanticType\" in state: " + codecState());
            }
        }

        final int limit = parentMessage.limit();
        return (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }

    public int skipSemanticType()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_TIMEUNIT_DONE:
                    codecState(CodecState.V0_SEMANTICTYPE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"semanticType\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int dataOffset = limit + headerLength;
        parentMessage.limit(dataOffset + dataLength);

        return dataLength;
    }

    public int getSemanticType(final MutableDirectBuffer dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_TIMEUNIT_DONE:
                    codecState(CodecState.V0_SEMANTICTYPE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"semanticType\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public int getSemanticType(final byte[] dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_TIMEUNIT_DONE:
                    codecState(CodecState.V0_SEMANTICTYPE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"semanticType\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public void wrapSemanticType(final DirectBuffer wrapBuffer)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_TIMEUNIT_DONE:
                    codecState(CodecState.V0_SEMANTICTYPE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"semanticType\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);
        wrapBuffer.wrap(buffer, limit + headerLength, dataLength);
    }

    public String semanticType()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_TIMEUNIT_DONE:
                    codecState(CodecState.V0_SEMANTICTYPE_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"semanticType\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);

        if (0 == dataLength)
        {
            return "";
        }

        final byte[] tmp = new byte[dataLength];
        buffer.getBytes(limit + headerLength, tmp, 0, dataLength);

        return new String(tmp, java.nio.charset.StandardCharsets.UTF_8);
    }

    public static int descriptionId()
    {
        return 20;
    }

    public static int descriptionSinceVersion()
    {
        return 0;
    }

    public static String descriptionCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.UTF_8.name();
    }

    public static String descriptionMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int descriptionHeaderLength()
    {
        return 2;
    }

    public int descriptionLength()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_SEMANTICTYPE_DONE:
                    codecState(CodecState.V0_DESCRIPTION_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"description\" in state: " + codecState());
            }
        }

        final int limit = parentMessage.limit();
        return (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }

    public int skipDescription()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_SEMANTICTYPE_DONE:
                    codecState(CodecState.V0_DESCRIPTION_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"description\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int dataOffset = limit + headerLength;
        parentMessage.limit(dataOffset + dataLength);

        return dataLength;
    }

    public int getDescription(final MutableDirectBuffer dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_SEMANTICTYPE_DONE:
                    codecState(CodecState.V0_DESCRIPTION_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"description\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public int getDescription(final byte[] dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_SEMANTICTYPE_DONE:
                    codecState(CodecState.V0_DESCRIPTION_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"description\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public void wrapDescription(final DirectBuffer wrapBuffer)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_SEMANTICTYPE_DONE:
                    codecState(CodecState.V0_DESCRIPTION_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"description\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);
        wrapBuffer.wrap(buffer, limit + headerLength, dataLength);
    }

    public String description()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_SEMANTICTYPE_DONE:
                    codecState(CodecState.V0_DESCRIPTION_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"description\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);

        if (0 == dataLength)
        {
            return "";
        }

        final byte[] tmp = new byte[dataLength];
        buffer.getBytes(limit + headerLength, tmp, 0, dataLength);

        return new String(tmp, java.nio.charset.StandardCharsets.UTF_8);
    }

    public static int referencedNameId()
    {
        return 21;
    }

    public static int referencedNameSinceVersion()
    {
        return 0;
    }

    public static String referencedNameCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.UTF_8.name();
    }

    public static String referencedNameMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int referencedNameHeaderLength()
    {
        return 2;
    }

    public int referencedNameLength()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_DESCRIPTION_DONE:
                    codecState(CodecState.V0_REFERENCEDNAME_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"referencedName\" in state: " + codecState());
            }
        }

        final int limit = parentMessage.limit();
        return (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }

    public int skipReferencedName()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_DESCRIPTION_DONE:
                    codecState(CodecState.V0_REFERENCEDNAME_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"referencedName\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int dataOffset = limit + headerLength;
        parentMessage.limit(dataOffset + dataLength);

        return dataLength;
    }

    public int getReferencedName(final MutableDirectBuffer dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_DESCRIPTION_DONE:
                    codecState(CodecState.V0_REFERENCEDNAME_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"referencedName\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public int getReferencedName(final byte[] dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_DESCRIPTION_DONE:
                    codecState(CodecState.V0_REFERENCEDNAME_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"referencedName\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public void wrapReferencedName(final DirectBuffer wrapBuffer)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_DESCRIPTION_DONE:
                    codecState(CodecState.V0_REFERENCEDNAME_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"referencedName\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);
        wrapBuffer.wrap(buffer, limit + headerLength, dataLength);
    }

    public String referencedName()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState())
            {
                case V0_DESCRIPTION_DONE:
                    codecState(CodecState.V0_REFERENCEDNAME_DONE);
                    break;
                default:
                    throw new IllegalStateException("Cannot access field \"referencedName\" in state: " + codecState());
            }
        }

        final int headerLength = 2;
        final int limit = parentMessage.limit();
        final int dataLength = (buffer.getShort(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        parentMessage.limit(limit + headerLength + dataLength);

        if (0 == dataLength)
        {
            return "";
        }

        final byte[] tmp = new byte[dataLength];
        buffer.getBytes(limit + headerLength, tmp, 0, dataLength);

        return new String(tmp, java.nio.charset.StandardCharsets.UTF_8);
    }

    public String toString()
    {
        if (null == buffer)
        {
            return "";
        }

        final TokenCodecDecoder decoder = new TokenCodecDecoder();
        decoder.wrap(buffer, initialOffset, actingBlockLength, actingVersion);

        return decoder.appendTo(new StringBuilder()).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        if (null == buffer)
        {
            return builder;
        }

        final int originalLimit = limit();
        limit(initialOffset + actingBlockLength);
        builder.append("[TokenCodec](sbeTemplateId=");
        builder.append(TEMPLATE_ID);
        builder.append("|sbeSchemaId=");
        builder.append(SCHEMA_ID);
        builder.append("|sbeSchemaVersion=");
        if (parentMessage.actingVersion != SCHEMA_VERSION)
        {
            builder.append(parentMessage.actingVersion);
            builder.append('/');
        }
        builder.append(SCHEMA_VERSION);
        builder.append("|sbeBlockLength=");
        if (actingBlockLength != BLOCK_LENGTH)
        {
            builder.append(actingBlockLength);
            builder.append('/');
        }
        builder.append(BLOCK_LENGTH);
        builder.append("):");
        builder.append("tokenOffset=");
        builder.append(this.tokenOffset());
        builder.append('|');
        builder.append("tokenSize=");
        builder.append(this.tokenSize());
        builder.append('|');
        builder.append("fieldId=");
        builder.append(this.fieldId());
        builder.append('|');
        builder.append("tokenVersion=");
        builder.append(this.tokenVersion());
        builder.append('|');
        builder.append("componentTokenCount=");
        builder.append(this.componentTokenCount());
        builder.append('|');
        builder.append("signal=");
        builder.append(this.signal());
        builder.append('|');
        builder.append("primitiveType=");
        builder.append(this.primitiveType());
        builder.append('|');
        builder.append("byteOrder=");
        builder.append(this.byteOrder());
        builder.append('|');
        builder.append("presence=");
        builder.append(this.presence());
        builder.append('|');
        builder.append("deprecated=");
        builder.append(this.deprecated());
        builder.append('|');
        builder.append("name=");
        builder.append('\'').append(name()).append('\'');
        builder.append('|');
        builder.append("constValue=");
        builder.append('\'').append(constValue()).append('\'');
        builder.append('|');
        builder.append("minValue=");
        builder.append('\'').append(minValue()).append('\'');
        builder.append('|');
        builder.append("maxValue=");
        builder.append('\'').append(maxValue()).append('\'');
        builder.append('|');
        builder.append("nullValue=");
        builder.append('\'').append(nullValue()).append('\'');
        builder.append('|');
        builder.append("characterEncoding=");
        builder.append('\'').append(characterEncoding()).append('\'');
        builder.append('|');
        builder.append("epoch=");
        builder.append('\'').append(epoch()).append('\'');
        builder.append('|');
        builder.append("timeUnit=");
        builder.append('\'').append(timeUnit()).append('\'');
        builder.append('|');
        builder.append("semanticType=");
        builder.append('\'').append(semanticType()).append('\'');
        builder.append('|');
        builder.append("description=");
        builder.append('\'').append(description()).append('\'');
        builder.append('|');
        builder.append("referencedName=");
        builder.append('\'').append(referencedName()).append('\'');

        limit(originalLimit);

        return builder;
    }
    
    public TokenCodecDecoder sbeSkip()
    {
        sbeRewind();
        skipName();
        skipConstValue();
        skipMinValue();
        skipMaxValue();
        skipNullValue();
        skipCharacterEncoding();
        skipEpoch();
        skipTimeUnit();
        skipSemanticType();
        skipDescription();
        skipReferencedName();

        return this;
    }
}
