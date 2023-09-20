/* Generated SBE (Simple Binary Encoding) message codec. */
package uk.co.real_logic.sbe.benchmarks;

import org.agrona.concurrent.UnsafeBuffer;


/**
 * Description of a basic Car
 */
@SuppressWarnings("all")
public final class CarDecoder
{
    private static final boolean ENABLE_BOUNDS_CHECKS = !Boolean.getBoolean("agrona.disable.bounds.checks");

    private static final boolean ENABLE_ACCESS_ORDER_CHECKS = Boolean.parseBoolean(System.getProperty(
        "sbe.enable.access.order.checks",
        Boolean.toString(ENABLE_BOUNDS_CHECKS)));

    /**
     * The states in which a encoder/decoder/codec can live.
     *
     * <p>The state machine diagram below, encoded in the dot language, describes
     * the valid state transitions according to the order in which fields may be
     * accessed safely. Tools such as PlantUML and Graphviz can render it.
     *
     * <pre>{@code
     *   digraph G {
     *       NOT_WRAPPED -> V0_BLOCK [label="  wrap(version=0)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  serialNumber(?)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  modelYear(?)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  available(?)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  code(?)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  someNumbers(?)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  vehicleCode(?)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  extras(?)  "];
     *       V0_BLOCK -> V0_BLOCK [label="  engine(?)  "];
     *       V0_BLOCK -> V0_FUELFIGURES_DONE [label="  fuelFiguresCount(0)  "];
     *       V0_BLOCK -> V0_FUELFIGURES_N [label="  fuelFiguresCount(>0)  "];
     *       V0_FUELFIGURES_N_BLOCK -> V0_FUELFIGURES_N_BLOCK [label="  fuelFigures.speed(?)  "];
     *       V0_FUELFIGURES_1_BLOCK -> V0_FUELFIGURES_1_BLOCK [label="  fuelFigures.speed(?)  "];
     *       V0_FUELFIGURES_N_BLOCK -> V0_FUELFIGURES_N_BLOCK [label="  fuelFigures.mpg(?)  "];
     *       V0_FUELFIGURES_1_BLOCK -> V0_FUELFIGURES_1_BLOCK [label="  fuelFigures.mpg(?)  "];
     *       V0_FUELFIGURES_N -> V0_FUELFIGURES_N_BLOCK [label="  fuelFigures.next()\n  where count - newIndex > 1  "];
     *       V0_FUELFIGURES_N_BLOCK -> V0_FUELFIGURES_N_BLOCK [label="  fuelFigures.next()\n  where count - newIndex > 1  "];
     *       V0_FUELFIGURES_N -> V0_FUELFIGURES_1_BLOCK [label="  fuelFigures.next()\n  where count - newIndex == 1  "];
     *       V0_FUELFIGURES_N_BLOCK -> V0_FUELFIGURES_1_BLOCK [label="  fuelFigures.next()\n  where count - newIndex == 1  "];
     *       V0_FUELFIGURES_N -> V0_FUELFIGURES_DONE [label="  fuelFigures.resetCountToIndex()  "];
     *       V0_FUELFIGURES_DONE -> V0_FUELFIGURES_DONE [label="  fuelFigures.resetCountToIndex()  "];
     *       V0_FUELFIGURES_1_BLOCK -> V0_FUELFIGURES_DONE [label="  fuelFigures.resetCountToIndex()  "];
     *       V0_FUELFIGURES_N_BLOCK -> V0_FUELFIGURES_DONE [label="  fuelFigures.resetCountToIndex()  "];
     *       V0_FUELFIGURES_DONE -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFiguresCount(0)  "];
     *       V0_FUELFIGURES_1_BLOCK -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFiguresCount(0)  "];
     *       V0_FUELFIGURES_DONE -> V0_PERFORMANCEFIGURES_N [label="  performanceFiguresCount(>0)  "];
     *       V0_FUELFIGURES_1_BLOCK -> V0_PERFORMANCEFIGURES_N [label="  performanceFiguresCount(>0)  "];
     *       V0_PERFORMANCEFIGURES_N_BLOCK -> V0_PERFORMANCEFIGURES_N_BLOCK [label="  performanceFigures.octaneRating(?)  "];
     *       V0_PERFORMANCEFIGURES_1_BLOCK -> V0_PERFORMANCEFIGURES_1_BLOCK [label="  performanceFigures.octaneRating(?)  "];
     *       V0_PERFORMANCEFIGURES_N_BLOCK -> V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE [label="  performanceFigures.accelerationCount(0)  "];
     *       V0_PERFORMANCEFIGURES_1_BLOCK -> V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE [label="  performanceFigures.accelerationCount(0)  "];
     *       V0_PERFORMANCEFIGURES_N_BLOCK -> V0_PERFORMANCEFIGURES_N_ACCELERATION_N [label="  performanceFigures.accelerationCount(>0)  "];
     *       V0_PERFORMANCEFIGURES_1_BLOCK -> V0_PERFORMANCEFIGURES_1_ACCELERATION_N [label="  performanceFigures.accelerationCount(>0)  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK -> V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK [label="  performanceFigures.acceleration.mph(?)  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK [label="  performanceFigures.acceleration.mph(?)  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK -> V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK [label="  performanceFigures.acceleration.mph(?)  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK [label="  performanceFigures.acceleration.mph(?)  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK -> V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK [label="  performanceFigures.acceleration.seconds(?)  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK [label="  performanceFigures.acceleration.seconds(?)  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK -> V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK [label="  performanceFigures.acceleration.seconds(?)  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK [label="  performanceFigures.acceleration.seconds(?)  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_N -> V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK [label="  performanceFigures.acceleration.next()\n  where count - newIndex > 1  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK -> V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK [label="  performanceFigures.acceleration.next()\n  where count - newIndex > 1  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_N -> V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK [label="  performanceFigures.acceleration.next()\n  where count - newIndex > 1  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK -> V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK [label="  performanceFigures.acceleration.next()\n  where count - newIndex > 1  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_N -> V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK [label="  performanceFigures.acceleration.next()\n  where count - newIndex == 1  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK -> V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK [label="  performanceFigures.acceleration.next()\n  where count - newIndex == 1  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_N -> V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK [label="  performanceFigures.acceleration.next()\n  where count - newIndex == 1  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK -> V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK [label="  performanceFigures.acceleration.next()\n  where count - newIndex == 1  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE [label="  performanceFigures.acceleration.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_N -> V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE [label="  performanceFigures.acceleration.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK -> V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE [label="  performanceFigures.acceleration.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE -> V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE [label="  performanceFigures.acceleration.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE -> V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE [label="  performanceFigures.acceleration.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE [label="  performanceFigures.acceleration.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_N -> V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE [label="  performanceFigures.acceleration.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK -> V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE [label="  performanceFigures.acceleration.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_N_BLOCK [label="  performanceFigures.next()\n  where count - newIndex > 1  "];
     *       V0_PERFORMANCEFIGURES_N -> V0_PERFORMANCEFIGURES_N_BLOCK [label="  performanceFigures.next()\n  where count - newIndex > 1  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE -> V0_PERFORMANCEFIGURES_N_BLOCK [label="  performanceFigures.next()\n  where count - newIndex > 1  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_1_BLOCK [label="  performanceFigures.next()\n  where count - newIndex == 1  "];
     *       V0_PERFORMANCEFIGURES_N -> V0_PERFORMANCEFIGURES_1_BLOCK [label="  performanceFigures.next()\n  where count - newIndex == 1  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE -> V0_PERFORMANCEFIGURES_1_BLOCK [label="  performanceFigures.next()\n  where count - newIndex == 1  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFigures.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFigures.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFigures.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_DONE -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFigures.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_N -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFigures.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFigures.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE -> V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE [label="  manufacturerLength()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK [label="  manufacturerLength()  "];
     *       V0_PERFORMANCEFIGURES_DONE -> V0_PERFORMANCEFIGURES_DONE [label="  manufacturerLength()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE -> V0_MANUFACTURER_DONE [label="  manufacturer(?)  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK -> V0_MANUFACTURER_DONE [label="  manufacturer(?)  "];
     *       V0_PERFORMANCEFIGURES_DONE -> V0_MANUFACTURER_DONE [label="  manufacturer(?)  "];
     *       V0_MANUFACTURER_DONE -> V0_MANUFACTURER_DONE [label="  modelLength()  "];
     *       V0_MANUFACTURER_DONE -> V0_MODEL_DONE [label="  model(?)  "];
     *   }
     * }</pre>
     */
    private static class CodecStates
    {
        private static final int NOT_WRAPPED = 0;
        private static final int V0_BLOCK = 1;
        private static final int V0_FUELFIGURES_N = 2;
        private static final int V0_FUELFIGURES_N_BLOCK = 3;
        private static final int V0_FUELFIGURES_1_BLOCK = 4;
        private static final int V0_FUELFIGURES_DONE = 5;
        private static final int V0_PERFORMANCEFIGURES_N = 6;
        private static final int V0_PERFORMANCEFIGURES_N_BLOCK = 7;
        private static final int V0_PERFORMANCEFIGURES_1_BLOCK = 8;
        private static final int V0_PERFORMANCEFIGURES_DONE = 9;
        private static final int V0_PERFORMANCEFIGURES_N_ACCELERATION_N = 10;
        private static final int V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK = 11;
        private static final int V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK = 12;
        private static final int V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE = 13;
        private static final int V0_PERFORMANCEFIGURES_1_ACCELERATION_N = 14;
        private static final int V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK = 15;
        private static final int V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK = 16;
        private static final int V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE = 17;
        private static final int V0_MANUFACTURER_DONE = 18;
        private static final int V0_MODEL_DONE = 19;

        private static final String[] STATE_NAME_LOOKUP =
        {
            "NOT_WRAPPED",
            "V0_BLOCK",
            "V0_FUELFIGURES_N",
            "V0_FUELFIGURES_N_BLOCK",
            "V0_FUELFIGURES_1_BLOCK",
            "V0_FUELFIGURES_DONE",
            "V0_PERFORMANCEFIGURES_N",
            "V0_PERFORMANCEFIGURES_N_BLOCK",
            "V0_PERFORMANCEFIGURES_1_BLOCK",
            "V0_PERFORMANCEFIGURES_DONE",
            "V0_PERFORMANCEFIGURES_N_ACCELERATION_N",
            "V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK",
            "V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK",
            "V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE",
            "V0_PERFORMANCEFIGURES_1_ACCELERATION_N",
            "V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK",
            "V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK",
            "V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE",
            "V0_MANUFACTURER_DONE",
            "V0_MODEL_DONE",
        };

        private static final String[] STATE_TRANSITIONS_LOOKUP =
        {
            "\"wrap(version=0)\"",
            "\"serialNumber(?)\", \"modelYear(?)\", \"available(?)\", \"code(?)\", \"someNumbers(?)\", \"vehicleCode(?)\", \"extras(?)\", \"engine(?)\", \"fuelFiguresCount(0)\", \"fuelFiguresCount(>0)\"",
            "\"fuelFigures.next()\", \"fuelFigures.resetCountToIndex()\"",
            "\"fuelFigures.speed(?)\", \"fuelFigures.mpg(?)\", \"fuelFigures.next()\", \"fuelFigures.resetCountToIndex()\"",
            "\"fuelFigures.speed(?)\", \"fuelFigures.mpg(?)\", \"fuelFigures.resetCountToIndex()\", \"performanceFiguresCount(0)\", \"performanceFiguresCount(>0)\"",
            "\"fuelFigures.resetCountToIndex()\", \"performanceFiguresCount(0)\", \"performanceFiguresCount(>0)\"",
            "\"performanceFigures.next()\", \"performanceFigures.resetCountToIndex()\"",
            "\"performanceFigures.octaneRating(?)\", \"performanceFigures.accelerationCount(0)\", \"performanceFigures.accelerationCount(>0)\"",
            "\"performanceFigures.octaneRating(?)\", \"performanceFigures.accelerationCount(0)\", \"performanceFigures.accelerationCount(>0)\"",
            "\"performanceFigures.resetCountToIndex()\", \"manufacturerLength()\", \"manufacturer(?)\"",
            "\"performanceFigures.acceleration.next()\", \"performanceFigures.acceleration.resetCountToIndex()\"",
            "\"performanceFigures.acceleration.mph(?)\", \"performanceFigures.acceleration.seconds(?)\", \"performanceFigures.acceleration.next()\", \"performanceFigures.acceleration.resetCountToIndex()\"",
            "\"performanceFigures.acceleration.mph(?)\", \"performanceFigures.acceleration.seconds(?)\", \"performanceFigures.acceleration.resetCountToIndex()\", \"performanceFigures.next()\", \"performanceFigures.resetCountToIndex()\"",
            "\"performanceFigures.acceleration.resetCountToIndex()\", \"performanceFigures.next()\", \"performanceFigures.resetCountToIndex()\"",
            "\"performanceFigures.acceleration.next()\", \"performanceFigures.acceleration.resetCountToIndex()\"",
            "\"performanceFigures.acceleration.mph(?)\", \"performanceFigures.acceleration.seconds(?)\", \"performanceFigures.acceleration.next()\", \"performanceFigures.acceleration.resetCountToIndex()\"",
            "\"performanceFigures.acceleration.mph(?)\", \"performanceFigures.acceleration.seconds(?)\", \"performanceFigures.acceleration.resetCountToIndex()\", \"performanceFigures.resetCountToIndex()\", \"manufacturerLength()\", \"manufacturer(?)\"",
            "\"performanceFigures.acceleration.resetCountToIndex()\", \"performanceFigures.resetCountToIndex()\", \"manufacturerLength()\", \"manufacturer(?)\"",
            "\"modelLength()\", \"model(?)\"",
            "",
        };

        private static String name(final int state)
        {
            return STATE_NAME_LOOKUP[state];
        }

        private static String transitions(final int state)
        {
            return STATE_TRANSITIONS_LOOKUP[state];
        }
    }

    private int codecState = CodecStates.NOT_WRAPPED;

    private int codecState()
    {
        return codecState;
    }

    private void codecState(int newState)
    {
        codecState = newState;
    }

    public static final int BLOCK_LENGTH = 41;
    public static final int TEMPLATE_ID = 1;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final String SEMANTIC_VERSION = "5.2";
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final CarDecoder parentMessage = this;
    private UnsafeBuffer buffer;
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

    public UnsafeBuffer buffer()
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

    private void onWrap(final int actingVersion)
    {
        switch(actingVersion)
        {
            case 0:
                codecState(CodecStates.V0_BLOCK);
                break;
            default:
                codecState(CodecStates.V0_BLOCK);
                break;
        }
    }

    public CarDecoder wrap(
        final UnsafeBuffer buffer,
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
            onWrap(actingVersion);
        }

        return this;
    }

    public CarDecoder wrapAndApplyHeader(
        final UnsafeBuffer buffer,
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

    public CarDecoder sbeRewind()
    {
        return wrap(buffer, initialOffset, actingBlockLength, actingVersion);
    }

    public int sbeDecodedLength()
    {
        final int currentLimit = limit();
        final int currentCodecState = codecState();
        sbeSkip();
        final int decodedLength = encodedLength();
        limit(currentLimit);

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            codecState(currentCodecState);
        }

        return decodedLength;
    }

    public int actingVersion()
    {
        return actingVersion;
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

    public static int serialNumberId()
    {
        return 1;
    }

    public static int serialNumberSinceVersion()
    {
        return 0;
    }

    public static int serialNumberEncodingOffset()
    {
        return 0;
    }

    public static int serialNumberEncodingLength()
    {
        return 4;
    }

    public static String serialNumberMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    private void onSerialNumberAccessed()
    {
        if (codecState() == CodecStates.NOT_WRAPPED)
        {
            throw new IllegalStateException("Illegal field access order. " +
                "Cannot access field \"serialNumber\" in state: " + CodecStates.name(codecState()) +
                ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
        }
    }

    public static long serialNumberNullValue()
    {
        return 4294967295L;
    }

    public static long serialNumberMinValue()
    {
        return 0L;
    }

    public static long serialNumberMaxValue()
    {
        return 4294967294L;
    }

    public long serialNumber()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onSerialNumberAccessed();
        }

        return (buffer.getInt(offset + 0, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF_FFFFL);
    }


    public static int modelYearId()
    {
        return 2;
    }

    public static int modelYearSinceVersion()
    {
        return 0;
    }

    public static int modelYearEncodingOffset()
    {
        return 4;
    }

    public static int modelYearEncodingLength()
    {
        return 2;
    }

    public static String modelYearMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    private void onModelYearAccessed()
    {
        if (codecState() == CodecStates.NOT_WRAPPED)
        {
            throw new IllegalStateException("Illegal field access order. " +
                "Cannot access field \"modelYear\" in state: " + CodecStates.name(codecState()) +
                ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
        }
    }

    public static int modelYearNullValue()
    {
        return 65535;
    }

    public static int modelYearMinValue()
    {
        return 0;
    }

    public static int modelYearMaxValue()
    {
        return 65534;
    }

    public int modelYear()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onModelYearAccessed();
        }

        return (buffer.getShort(offset + 4, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }


    public static int availableId()
    {
        return 3;
    }

    public static int availableSinceVersion()
    {
        return 0;
    }

    public static int availableEncodingOffset()
    {
        return 6;
    }

    public static int availableEncodingLength()
    {
        return 1;
    }

    public static String availableMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    private void onAvailableAccessed()
    {
        if (codecState() == CodecStates.NOT_WRAPPED)
        {
            throw new IllegalStateException("Illegal field access order. " +
                "Cannot access field \"available\" in state: " + CodecStates.name(codecState()) +
                ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
        }
    }

    public short availableRaw()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onAvailableAccessed();
        }

        return ((short)(buffer.getByte(offset + 6) & 0xFF));
    }

    public BooleanType available()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onAvailableAccessed();
        }

        return BooleanType.get(((short)(buffer.getByte(offset + 6) & 0xFF)));
    }


    public static int codeId()
    {
        return 4;
    }

    public static int codeSinceVersion()
    {
        return 0;
    }

    public static int codeEncodingOffset()
    {
        return 7;
    }

    public static int codeEncodingLength()
    {
        return 1;
    }

    public static String codeMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    private void onCodeAccessed()
    {
        if (codecState() == CodecStates.NOT_WRAPPED)
        {
            throw new IllegalStateException("Illegal field access order. " +
                "Cannot access field \"code\" in state: " + CodecStates.name(codecState()) +
                ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
        }
    }

    public byte codeRaw()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onCodeAccessed();
        }

        return buffer.getByte(offset + 7);
    }

    public Model code()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onCodeAccessed();
        }

        return Model.get(buffer.getByte(offset + 7));
    }


    public static int someNumbersId()
    {
        return 5;
    }

    public static int someNumbersSinceVersion()
    {
        return 0;
    }

    public static int someNumbersEncodingOffset()
    {
        return 8;
    }

    public static int someNumbersEncodingLength()
    {
        return 20;
    }

    public static String someNumbersMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    private void onSomeNumbersAccessed()
    {
        if (codecState() == CodecStates.NOT_WRAPPED)
        {
            throw new IllegalStateException("Illegal field access order. " +
                "Cannot access field \"someNumbers\" in state: " + CodecStates.name(codecState()) +
                ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
        }
    }

    public static int someNumbersNullValue()
    {
        return -2147483648;
    }

    public static int someNumbersMinValue()
    {
        return -2147483647;
    }

    public static int someNumbersMaxValue()
    {
        return 2147483647;
    }

    public static int someNumbersLength()
    {
        return 5;
    }


    public int someNumbers(final int index)
    {
        if (index < 0 || index >= 5)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onSomeNumbersAccessed();
        }

        final int pos = offset + 8 + (index * 4);

        return buffer.getInt(pos, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int vehicleCodeId()
    {
        return 6;
    }

    public static int vehicleCodeSinceVersion()
    {
        return 0;
    }

    public static int vehicleCodeEncodingOffset()
    {
        return 28;
    }

    public static int vehicleCodeEncodingLength()
    {
        return 6;
    }

    public static String vehicleCodeMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    private void onVehicleCodeAccessed()
    {
        if (codecState() == CodecStates.NOT_WRAPPED)
        {
            throw new IllegalStateException("Illegal field access order. " +
                "Cannot access field \"vehicleCode\" in state: " + CodecStates.name(codecState()) +
                ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
        }
    }

    public static byte vehicleCodeNullValue()
    {
        return (byte)0;
    }

    public static byte vehicleCodeMinValue()
    {
        return (byte)32;
    }

    public static byte vehicleCodeMaxValue()
    {
        return (byte)126;
    }

    public static int vehicleCodeLength()
    {
        return 6;
    }


    public byte vehicleCode(final int index)
    {
        if (index < 0 || index >= 6)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onVehicleCodeAccessed();
        }

        final int pos = offset + 28 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String vehicleCodeCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.US_ASCII.name();
    }

    public int getVehicleCode(final byte[] dst, final int dstOffset)
    {
        final int length = 6;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onVehicleCodeAccessed();
        }

        buffer.getBytes(offset + 28, dst, dstOffset, length);

        return length;
    }

    public String vehicleCode()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onVehicleCodeAccessed();
        }

        final byte[] dst = new byte[6];
        buffer.getBytes(offset + 28, dst, 0, 6);

        int end = 0;
        for (; end < 6 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public int getVehicleCode(final Appendable value)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onVehicleCodeAccessed();
        }

        for (int i = 0; i < 6; ++i)
        {
            final int c = buffer.getByte(offset + 28 + i) & 0xFF;
            if (c == 0)
            {
                return i;
            }

            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException ex)
            {
                throw new java.io.UncheckedIOException(ex);
            }
        }

        return 6;
    }


    public static int extrasId()
    {
        return 7;
    }

    public static int extrasSinceVersion()
    {
        return 0;
    }

    public static int extrasEncodingOffset()
    {
        return 34;
    }

    public static int extrasEncodingLength()
    {
        return 1;
    }

    public static String extrasMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    private void onExtrasAccessed()
    {
        if (codecState() == CodecStates.NOT_WRAPPED)
        {
            throw new IllegalStateException("Illegal field access order. " +
                "Cannot access field \"extras\" in state: " + CodecStates.name(codecState()) +
                ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
        }
    }

    private final OptionalExtrasDecoder extras = new OptionalExtrasDecoder();

    public OptionalExtrasDecoder extras()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onExtrasAccessed();
        }

        extras.wrap(buffer, offset + 34);
        return extras;
    }

    public static int engineId()
    {
        return 8;
    }

    public static int engineSinceVersion()
    {
        return 0;
    }

    public static int engineEncodingOffset()
    {
        return 35;
    }

    public static int engineEncodingLength()
    {
        return 6;
    }

    public static String engineMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    private void onEngineAccessed()
    {
        if (codecState() == CodecStates.NOT_WRAPPED)
        {
            throw new IllegalStateException("Illegal field access order. " +
                "Cannot access field \"engine\" in state: " + CodecStates.name(codecState()) +
                ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
        }
    }

    private final EngineDecoder engine = new EngineDecoder();

    public EngineDecoder engine()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onEngineAccessed();
        }

        engine.wrap(buffer, offset + 35);
        return engine;
    }

    private final FuelFiguresDecoder fuelFigures = new FuelFiguresDecoder(this);

    public static long fuelFiguresDecoderId()
    {
        return 9;
    }

    public static int fuelFiguresDecoderSinceVersion()
    {
        return 0;
    }

    private void onFuelFiguresAccessed(final int remaining)
    {
        if (remaining == 0)
        {
            switch (codecState())
            {
                case CodecStates.V0_BLOCK:
                    codecState(CodecStates.V0_FUELFIGURES_DONE);
                    break;
                default:
                    throw new IllegalStateException("Illegal field access order. " +
                        "Cannot decode count of repeating group \"fuelFigures\" in state: " + CodecStates.name(codecState()) +
                        ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                        "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
            }
        }
        else
        {
            switch (codecState())
            {
                case CodecStates.V0_BLOCK:
                    codecState(CodecStates.V0_FUELFIGURES_N);
                    break;
                default:
                    throw new IllegalStateException("Illegal field access order. " +
                        "Cannot decode count of repeating group \"fuelFigures\" in state: " + CodecStates.name(codecState()) +
                        ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                        "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
            }
        }
    }

    public FuelFiguresDecoder fuelFigures()
    {
        fuelFigures.wrap(buffer);
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onFuelFiguresAccessed(fuelFigures.count);
        }

        return fuelFigures;
    }

    public static final class FuelFiguresDecoder
        implements Iterable<FuelFiguresDecoder>, java.util.Iterator<FuelFiguresDecoder>
    {
        public static final int HEADER_SIZE = 4;
        private final CarDecoder parentMessage;
        private UnsafeBuffer buffer;
        private int count;
        private int index;
        private int offset;
        private int blockLength;

        FuelFiguresDecoder(final CarDecoder parentMessage)
        {
            this.parentMessage = parentMessage;
        }

        public void wrap(final UnsafeBuffer buffer)
        {
            if (buffer != this.buffer)
            {
                this.buffer = buffer;
            }

            index = 0;
            final int limit = parentMessage.limit();
            parentMessage.limit(limit + HEADER_SIZE);
            blockLength = (buffer.getShort(limit + 0, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
            count = (buffer.getShort(limit + 2, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        }

        private void onNextElementAccessed()
        {
            final int remaining = count - index;
            if (remaining > 1)
            {
               switch (codecState())
               {
                   case CodecStates.V0_FUELFIGURES_N:
                   case CodecStates.V0_FUELFIGURES_N_BLOCK:
                       codecState(CodecStates.V0_FUELFIGURES_N_BLOCK);
                       break;
                   default:
                       throw new IllegalStateException("Illegal field access order. " +
                           "Cannot access next element in repeating group \"fuelFigures\" in state: " + CodecStates.name(codecState()) +
                           ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                           "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
               }
            }
            else if (remaining == 1)
            {
                switch (codecState())
                {
                    case CodecStates.V0_FUELFIGURES_N:
                    case CodecStates.V0_FUELFIGURES_N_BLOCK:
                        codecState(CodecStates.V0_FUELFIGURES_1_BLOCK);
                        break;
                    default:
                        throw new IllegalStateException("Illegal field access order. " +
                            "Cannot access next element in repeating group \"fuelFigures\" in state: " + CodecStates.name(codecState()) +
                            ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                            "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
                }
            }
        }

        public FuelFiguresDecoder next()
        {
            if (index >= count)
            {
                throw new java.util.NoSuchElementException();
            }

            if (ENABLE_ACCESS_ORDER_CHECKS)
            {
                onNextElementAccessed();
            }

            offset = parentMessage.limit();
            parentMessage.limit(offset + blockLength);
            ++index;

            return this;
        }

        public static int countMinValue()
        {
            return 0;
        }

        public static int countMaxValue()
        {
            return 65534;
        }

        public static int sbeHeaderSize()
        {
            return HEADER_SIZE;
        }

        public static int sbeBlockLength()
        {
            return 6;
        }

        public int actingBlockLength()
        {
            return blockLength;
        }

        public int count()
        {
            return count;
        }

        public java.util.Iterator<FuelFiguresDecoder> iterator()
        {
            return this;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext()
        {
            return index < count;
        }

        private int codecState()
        {
            return parentMessage.codecState();
        }

        private void codecState(final int newState)
        {
            parentMessage.codecState(newState);
        }

        public static int speedId()
        {
            return 10;
        }

        public static int speedSinceVersion()
        {
            return 0;
        }

        public static int speedEncodingOffset()
        {
            return 0;
        }

        public static int speedEncodingLength()
        {
            return 2;
        }

        public static String speedMetaAttribute(final MetaAttribute metaAttribute)
        {
            if (MetaAttribute.PRESENCE == metaAttribute)
            {
                return "required";
            }

            return "";
        }

        private void onSpeedAccessed()
        {
            switch (codecState())
            {
                case CodecStates.V0_FUELFIGURES_N_BLOCK:
                    codecState(CodecStates.V0_FUELFIGURES_N_BLOCK);
                    break;
                case CodecStates.V0_FUELFIGURES_1_BLOCK:
                    codecState(CodecStates.V0_FUELFIGURES_1_BLOCK);
                    break;
                default:
                    throw new IllegalStateException("Illegal field access order. " +
                        "Cannot access field \"fuelFigures.speed\" in state: " + CodecStates.name(codecState()) +
                        ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                        "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
            }
        }

        public static int speedNullValue()
        {
            return 65535;
        }

        public static int speedMinValue()
        {
            return 0;
        }

        public static int speedMaxValue()
        {
            return 65534;
        }

        public int speed()
        {
            if (ENABLE_ACCESS_ORDER_CHECKS)
            {
                onSpeedAccessed();
            }

            return (buffer.getShort(offset + 0, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        }


        public static int mpgId()
        {
            return 11;
        }

        public static int mpgSinceVersion()
        {
            return 0;
        }

        public static int mpgEncodingOffset()
        {
            return 2;
        }

        public static int mpgEncodingLength()
        {
            return 4;
        }

        public static String mpgMetaAttribute(final MetaAttribute metaAttribute)
        {
            if (MetaAttribute.PRESENCE == metaAttribute)
            {
                return "required";
            }

            return "";
        }

        private void onMpgAccessed()
        {
            switch (codecState())
            {
                case CodecStates.V0_FUELFIGURES_N_BLOCK:
                    codecState(CodecStates.V0_FUELFIGURES_N_BLOCK);
                    break;
                case CodecStates.V0_FUELFIGURES_1_BLOCK:
                    codecState(CodecStates.V0_FUELFIGURES_1_BLOCK);
                    break;
                default:
                    throw new IllegalStateException("Illegal field access order. " +
                        "Cannot access field \"fuelFigures.mpg\" in state: " + CodecStates.name(codecState()) +
                        ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                        "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
            }
        }

        public static float mpgNullValue()
        {
            return Float.NaN;
        }

        public static float mpgMinValue()
        {
            return 1.401298464324817E-45f;
        }

        public static float mpgMaxValue()
        {
            return 3.4028234663852886E38f;
        }

        public float mpg()
        {
            if (ENABLE_ACCESS_ORDER_CHECKS)
            {
                onMpgAccessed();
            }

            return buffer.getFloat(offset + 2, java.nio.ByteOrder.LITTLE_ENDIAN);
        }


        public StringBuilder appendTo(final StringBuilder builder)
        {
            if (null == buffer)
            {
                return builder;
            }

            builder.append('(');
            builder.append("speed=");
            builder.append(this.speed());
            builder.append('|');
            builder.append("mpg=");
            builder.append(this.mpg());
            builder.append(')');

            return builder;
        }
        
        public FuelFiguresDecoder sbeSkip()
        {

            return this;
        }
    }

    private final PerformanceFiguresDecoder performanceFigures = new PerformanceFiguresDecoder(this);

    public static long performanceFiguresDecoderId()
    {
        return 12;
    }

    public static int performanceFiguresDecoderSinceVersion()
    {
        return 0;
    }

    private void onPerformanceFiguresAccessed(final int remaining)
    {
        if (remaining == 0)
        {
            switch (codecState())
            {
                case CodecStates.V0_FUELFIGURES_DONE:
                case CodecStates.V0_FUELFIGURES_1_BLOCK:
                    codecState(CodecStates.V0_PERFORMANCEFIGURES_DONE);
                    break;
                default:
                    throw new IllegalStateException("Illegal field access order. " +
                        "Cannot decode count of repeating group \"performanceFigures\" in state: " + CodecStates.name(codecState()) +
                        ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                        "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
            }
        }
        else
        {
            switch (codecState())
            {
                case CodecStates.V0_FUELFIGURES_DONE:
                case CodecStates.V0_FUELFIGURES_1_BLOCK:
                    codecState(CodecStates.V0_PERFORMANCEFIGURES_N);
                    break;
                default:
                    throw new IllegalStateException("Illegal field access order. " +
                        "Cannot decode count of repeating group \"performanceFigures\" in state: " + CodecStates.name(codecState()) +
                        ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                        "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
            }
        }
    }

    public PerformanceFiguresDecoder performanceFigures()
    {
        performanceFigures.wrap(buffer);
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onPerformanceFiguresAccessed(performanceFigures.count);
        }

        return performanceFigures;
    }

    public static final class PerformanceFiguresDecoder
        implements Iterable<PerformanceFiguresDecoder>, java.util.Iterator<PerformanceFiguresDecoder>
    {
        public static final int HEADER_SIZE = 4;
        private final CarDecoder parentMessage;
        private UnsafeBuffer buffer;
        private int count;
        private int index;
        private int offset;
        private int blockLength;
        private final AccelerationDecoder acceleration;

        PerformanceFiguresDecoder(final CarDecoder parentMessage)
        {
            this.parentMessage = parentMessage;
            acceleration = new AccelerationDecoder(parentMessage);
        }

        public void wrap(final UnsafeBuffer buffer)
        {
            if (buffer != this.buffer)
            {
                this.buffer = buffer;
            }

            index = 0;
            final int limit = parentMessage.limit();
            parentMessage.limit(limit + HEADER_SIZE);
            blockLength = (buffer.getShort(limit + 0, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
            count = (buffer.getShort(limit + 2, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        }

        private void onNextElementAccessed()
        {
            final int remaining = count - index;
            if (remaining > 1)
            {
               switch (codecState())
               {
                   case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK:
                   case CodecStates.V0_PERFORMANCEFIGURES_N:
                   case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE:
                       codecState(CodecStates.V0_PERFORMANCEFIGURES_N_BLOCK);
                       break;
                   default:
                       throw new IllegalStateException("Illegal field access order. " +
                           "Cannot access next element in repeating group \"performanceFigures\" in state: " + CodecStates.name(codecState()) +
                           ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                           "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
               }
            }
            else if (remaining == 1)
            {
                switch (codecState())
                {
                    case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK:
                    case CodecStates.V0_PERFORMANCEFIGURES_N:
                    case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE:
                        codecState(CodecStates.V0_PERFORMANCEFIGURES_1_BLOCK);
                        break;
                    default:
                        throw new IllegalStateException("Illegal field access order. " +
                            "Cannot access next element in repeating group \"performanceFigures\" in state: " + CodecStates.name(codecState()) +
                            ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                            "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
                }
            }
        }

        public PerformanceFiguresDecoder next()
        {
            if (index >= count)
            {
                throw new java.util.NoSuchElementException();
            }

            if (ENABLE_ACCESS_ORDER_CHECKS)
            {
                onNextElementAccessed();
            }

            offset = parentMessage.limit();
            parentMessage.limit(offset + blockLength);
            ++index;

            return this;
        }

        public static int countMinValue()
        {
            return 0;
        }

        public static int countMaxValue()
        {
            return 65534;
        }

        public static int sbeHeaderSize()
        {
            return HEADER_SIZE;
        }

        public static int sbeBlockLength()
        {
            return 1;
        }

        public int actingBlockLength()
        {
            return blockLength;
        }

        public int count()
        {
            return count;
        }

        public java.util.Iterator<PerformanceFiguresDecoder> iterator()
        {
            return this;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext()
        {
            return index < count;
        }

        private int codecState()
        {
            return parentMessage.codecState();
        }

        private void codecState(final int newState)
        {
            parentMessage.codecState(newState);
        }

        public static int octaneRatingId()
        {
            return 13;
        }

        public static int octaneRatingSinceVersion()
        {
            return 0;
        }

        public static int octaneRatingEncodingOffset()
        {
            return 0;
        }

        public static int octaneRatingEncodingLength()
        {
            return 1;
        }

        public static String octaneRatingMetaAttribute(final MetaAttribute metaAttribute)
        {
            if (MetaAttribute.PRESENCE == metaAttribute)
            {
                return "required";
            }

            return "";
        }

        private void onOctaneRatingAccessed()
        {
            switch (codecState())
            {
                case CodecStates.V0_PERFORMANCEFIGURES_N_BLOCK:
                    codecState(CodecStates.V0_PERFORMANCEFIGURES_N_BLOCK);
                    break;
                case CodecStates.V0_PERFORMANCEFIGURES_1_BLOCK:
                    codecState(CodecStates.V0_PERFORMANCEFIGURES_1_BLOCK);
                    break;
                default:
                    throw new IllegalStateException("Illegal field access order. " +
                        "Cannot access field \"performanceFigures.octaneRating\" in state: " + CodecStates.name(codecState()) +
                        ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                        "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
            }
        }

        public static short octaneRatingNullValue()
        {
            return (short)255;
        }

        public static short octaneRatingMinValue()
        {
            return (short)0;
        }

        public static short octaneRatingMaxValue()
        {
            return (short)254;
        }

        public short octaneRating()
        {
            if (ENABLE_ACCESS_ORDER_CHECKS)
            {
                onOctaneRatingAccessed();
            }

            return ((short)(buffer.getByte(offset + 0) & 0xFF));
        }


        public static long accelerationDecoderId()
        {
            return 14;
        }

        public static int accelerationDecoderSinceVersion()
        {
            return 0;
        }

        private void onAccelerationAccessed(final int remaining)
        {
            if (remaining == 0)
            {
                switch (codecState())
                {
                    case CodecStates.V0_PERFORMANCEFIGURES_N_BLOCK:
                        codecState(CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE);
                        break;
                    case CodecStates.V0_PERFORMANCEFIGURES_1_BLOCK:
                        codecState(CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE);
                        break;
                    default:
                        throw new IllegalStateException("Illegal field access order. " +
                            "Cannot decode count of repeating group \"performanceFigures.acceleration\" in state: " + CodecStates.name(codecState()) +
                            ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                            "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
                }
            }
            else
            {
                switch (codecState())
                {
                    case CodecStates.V0_PERFORMANCEFIGURES_N_BLOCK:
                        codecState(CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_N);
                        break;
                    case CodecStates.V0_PERFORMANCEFIGURES_1_BLOCK:
                        codecState(CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_N);
                        break;
                    default:
                        throw new IllegalStateException("Illegal field access order. " +
                            "Cannot decode count of repeating group \"performanceFigures.acceleration\" in state: " + CodecStates.name(codecState()) +
                            ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                            "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
                }
            }
        }

        public AccelerationDecoder acceleration()
        {
            acceleration.wrap(buffer);
            if (ENABLE_ACCESS_ORDER_CHECKS)
            {
                onAccelerationAccessed(acceleration.count);
            }

            return acceleration;
        }

        public static final class AccelerationDecoder
            implements Iterable<AccelerationDecoder>, java.util.Iterator<AccelerationDecoder>
        {
            public static final int HEADER_SIZE = 4;
            private final CarDecoder parentMessage;
            private UnsafeBuffer buffer;
            private int count;
            private int index;
            private int offset;
            private int blockLength;

            AccelerationDecoder(final CarDecoder parentMessage)
            {
                this.parentMessage = parentMessage;
            }

            public void wrap(final UnsafeBuffer buffer)
            {
                if (buffer != this.buffer)
                {
                    this.buffer = buffer;
                }

                index = 0;
                final int limit = parentMessage.limit();
                parentMessage.limit(limit + HEADER_SIZE);
                blockLength = (buffer.getShort(limit + 0, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
                count = (buffer.getShort(limit + 2, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
            }

            private void onNextElementAccessed()
            {
                final int remaining = count - index;
                if (remaining > 1)
                {
                   switch (codecState())
                   {
                       case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_N:
                       case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK:
                           codecState(CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK);
                           break;
                       case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_N:
                       case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK:
                           codecState(CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK);
                           break;
                       default:
                           throw new IllegalStateException("Illegal field access order. " +
                               "Cannot access next element in repeating group \"performanceFigures.acceleration\" in state: " + CodecStates.name(codecState()) +
                               ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                               "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
                   }
                }
                else if (remaining == 1)
                {
                    switch (codecState())
                    {
                        case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_N:
                        case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK:
                            codecState(CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK);
                            break;
                        case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_N:
                        case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK:
                            codecState(CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK);
                            break;
                        default:
                            throw new IllegalStateException("Illegal field access order. " +
                                "Cannot access next element in repeating group \"performanceFigures.acceleration\" in state: " + CodecStates.name(codecState()) +
                                ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                                "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
                    }
                }
            }

            public AccelerationDecoder next()
            {
                if (index >= count)
                {
                    throw new java.util.NoSuchElementException();
                }

                if (ENABLE_ACCESS_ORDER_CHECKS)
                {
                    onNextElementAccessed();
                }

                offset = parentMessage.limit();
                parentMessage.limit(offset + blockLength);
                ++index;

                return this;
            }

            public static int countMinValue()
            {
                return 0;
            }

            public static int countMaxValue()
            {
                return 65534;
            }

            public static int sbeHeaderSize()
            {
                return HEADER_SIZE;
            }

            public static int sbeBlockLength()
            {
                return 6;
            }

            public int actingBlockLength()
            {
                return blockLength;
            }

            public int count()
            {
                return count;
            }

            public java.util.Iterator<AccelerationDecoder> iterator()
            {
                return this;
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            public boolean hasNext()
            {
                return index < count;
            }

            private int codecState()
            {
                return parentMessage.codecState();
            }

            private void codecState(final int newState)
            {
                parentMessage.codecState(newState);
            }

            public static int mphId()
            {
                return 15;
            }

            public static int mphSinceVersion()
            {
                return 0;
            }

            public static int mphEncodingOffset()
            {
                return 0;
            }

            public static int mphEncodingLength()
            {
                return 2;
            }

            public static String mphMetaAttribute(final MetaAttribute metaAttribute)
            {
                if (MetaAttribute.PRESENCE == metaAttribute)
                {
                    return "required";
                }

                return "";
            }

            private void onMphAccessed()
            {
                switch (codecState())
                {
                    case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK:
                        codecState(CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK);
                        break;
                    case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK:
                        codecState(CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK);
                        break;
                    case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK:
                        codecState(CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK);
                        break;
                    case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK:
                        codecState(CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK);
                        break;
                    default:
                        throw new IllegalStateException("Illegal field access order. " +
                            "Cannot access field \"performanceFigures.acceleration.mph\" in state: " + CodecStates.name(codecState()) +
                            ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                            "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
                }
            }

            public static int mphNullValue()
            {
                return 65535;
            }

            public static int mphMinValue()
            {
                return 0;
            }

            public static int mphMaxValue()
            {
                return 65534;
            }

            public int mph()
            {
                if (ENABLE_ACCESS_ORDER_CHECKS)
                {
                    onMphAccessed();
                }

                return (buffer.getShort(offset + 0, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
            }


            public static int secondsId()
            {
                return 16;
            }

            public static int secondsSinceVersion()
            {
                return 0;
            }

            public static int secondsEncodingOffset()
            {
                return 2;
            }

            public static int secondsEncodingLength()
            {
                return 4;
            }

            public static String secondsMetaAttribute(final MetaAttribute metaAttribute)
            {
                if (MetaAttribute.PRESENCE == metaAttribute)
                {
                    return "required";
                }

                return "";
            }

            private void onSecondsAccessed()
            {
                switch (codecState())
                {
                    case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK:
                        codecState(CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK);
                        break;
                    case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK:
                        codecState(CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK);
                        break;
                    case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK:
                        codecState(CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK);
                        break;
                    case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK:
                        codecState(CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK);
                        break;
                    default:
                        throw new IllegalStateException("Illegal field access order. " +
                            "Cannot access field \"performanceFigures.acceleration.seconds\" in state: " + CodecStates.name(codecState()) +
                            ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                            "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
                }
            }

            public static float secondsNullValue()
            {
                return Float.NaN;
            }

            public static float secondsMinValue()
            {
                return 1.401298464324817E-45f;
            }

            public static float secondsMaxValue()
            {
                return 3.4028234663852886E38f;
            }

            public float seconds()
            {
                if (ENABLE_ACCESS_ORDER_CHECKS)
                {
                    onSecondsAccessed();
                }

                return buffer.getFloat(offset + 2, java.nio.ByteOrder.LITTLE_ENDIAN);
            }


            public StringBuilder appendTo(final StringBuilder builder)
            {
                if (null == buffer)
                {
                    return builder;
                }

                builder.append('(');
                builder.append("mph=");
                builder.append(this.mph());
                builder.append('|');
                builder.append("seconds=");
                builder.append(this.seconds());
                builder.append(')');

                return builder;
            }
            
            public AccelerationDecoder sbeSkip()
            {

                return this;
            }
        }

        public StringBuilder appendTo(final StringBuilder builder)
        {
            if (null == buffer)
            {
                return builder;
            }

            builder.append('(');
            builder.append("octaneRating=");
            builder.append(this.octaneRating());
            builder.append('|');
            builder.append("acceleration=[");
            final int accelerationOriginalOffset = acceleration.offset;
            final int accelerationOriginalIndex = acceleration.index;
            final AccelerationDecoder acceleration = this.acceleration();
            if (acceleration.count() > 0)
            {
                while (acceleration.hasNext())
                {
                    acceleration.next().appendTo(builder);
                    builder.append(',');
                }
                builder.setLength(builder.length() - 1);
            }
            acceleration.offset = accelerationOriginalOffset;
            acceleration.index = accelerationOriginalIndex;
            builder.append(']');
            builder.append(')');

            return builder;
        }
        
        public PerformanceFiguresDecoder sbeSkip()
        {
            AccelerationDecoder acceleration = this.acceleration();
            if (acceleration.count() > 0)
            {
                while (acceleration.hasNext())
                {
                    acceleration.next();
                    acceleration.sbeSkip();
                }
            }

            return this;
        }
    }

    public static int manufacturerId()
    {
        return 17;
    }

    public static int manufacturerSinceVersion()
    {
        return 0;
    }

    public static String manufacturerCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.ISO_8859_1.name();
    }

    public static String manufacturerMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int manufacturerHeaderLength()
    {
        return 4;
    }

    void onManufacturerLengthAccessed()
    {
        switch (codecState())
        {
            case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE:
                codecState(CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE);
                break;
            case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK:
                codecState(CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK);
                break;
            case CodecStates.V0_PERFORMANCEFIGURES_DONE:
                codecState(CodecStates.V0_PERFORMANCEFIGURES_DONE);
                break;
            default:
                throw new IllegalStateException("Illegal field access order. " +
                    "Cannot decode length of var data \"manufacturer\" in state: " + CodecStates.name(codecState()) +
                    ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                    "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
        }
    }

    private void onManufacturerAccessed()
    {
        switch (codecState())
        {
            case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE:
            case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK:
            case CodecStates.V0_PERFORMANCEFIGURES_DONE:
                codecState(CodecStates.V0_MANUFACTURER_DONE);
                break;
            default:
                throw new IllegalStateException("Illegal field access order. " +
                    "Cannot access field \"manufacturer\" in state: " + CodecStates.name(codecState()) +
                    ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                    "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
        }
    }

    public int manufacturerLength()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onManufacturerLengthAccessed();
        }

        final int limit = parentMessage.limit();
        return (int)(buffer.getInt(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF_FFFFL);
    }

    public int skipManufacturer()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onManufacturerAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        final int dataLength = (int)(buffer.getInt(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF_FFFFL);
        final int dataOffset = limit + headerLength;
        parentMessage.limit(dataOffset + dataLength);

        return dataLength;
    }

    public int getManufacturer(final UnsafeBuffer dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onManufacturerAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        final int dataLength = (int)(buffer.getInt(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF_FFFFL);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public int getManufacturer(final byte[] dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onManufacturerAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        final int dataLength = (int)(buffer.getInt(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF_FFFFL);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public void wrapManufacturer(final UnsafeBuffer wrapBuffer)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onManufacturerAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        final int dataLength = (int)(buffer.getInt(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF_FFFFL);
        parentMessage.limit(limit + headerLength + dataLength);
        wrapBuffer.wrap(buffer, limit + headerLength, dataLength);
    }

    public String manufacturer()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onManufacturerAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        final int dataLength = (int)(buffer.getInt(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF_FFFFL);
        parentMessage.limit(limit + headerLength + dataLength);

        if (0 == dataLength)
        {
            return "";
        }

        final byte[] tmp = new byte[dataLength];
        buffer.getBytes(limit + headerLength, tmp, 0, dataLength);

        return new String(tmp, java.nio.charset.StandardCharsets.ISO_8859_1);
    }

    public static int modelId()
    {
        return 18;
    }

    public static int modelSinceVersion()
    {
        return 0;
    }

    public static String modelCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.ISO_8859_1.name();
    }

    public static String modelMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static int modelHeaderLength()
    {
        return 4;
    }

    void onModelLengthAccessed()
    {
        switch (codecState())
        {
            case CodecStates.V0_MANUFACTURER_DONE:
                codecState(CodecStates.V0_MANUFACTURER_DONE);
                break;
            default:
                throw new IllegalStateException("Illegal field access order. " +
                    "Cannot decode length of var data \"model\" in state: " + CodecStates.name(codecState()) +
                    ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                    "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
        }
    }

    private void onModelAccessed()
    {
        switch (codecState())
        {
            case CodecStates.V0_MANUFACTURER_DONE:
                codecState(CodecStates.V0_MODEL_DONE);
                break;
            default:
                throw new IllegalStateException("Illegal field access order. " +
                    "Cannot access field \"model\" in state: " + CodecStates.name(codecState()) +
                    ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                    "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
        }
    }

    public int modelLength()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onModelLengthAccessed();
        }

        final int limit = parentMessage.limit();
        return (int)(buffer.getInt(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF_FFFFL);
    }

    public int skipModel()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onModelAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        final int dataLength = (int)(buffer.getInt(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF_FFFFL);
        final int dataOffset = limit + headerLength;
        parentMessage.limit(dataOffset + dataLength);

        return dataLength;
    }

    public int getModel(final UnsafeBuffer dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onModelAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        final int dataLength = (int)(buffer.getInt(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF_FFFFL);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public int getModel(final byte[] dst, final int dstOffset, final int length)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onModelAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        final int dataLength = (int)(buffer.getInt(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF_FFFFL);
        final int bytesCopied = Math.min(length, dataLength);
        parentMessage.limit(limit + headerLength + dataLength);
        buffer.getBytes(limit + headerLength, dst, dstOffset, bytesCopied);

        return bytesCopied;
    }

    public void wrapModel(final UnsafeBuffer wrapBuffer)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onModelAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        final int dataLength = (int)(buffer.getInt(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF_FFFFL);
        parentMessage.limit(limit + headerLength + dataLength);
        wrapBuffer.wrap(buffer, limit + headerLength, dataLength);
    }

    public String model()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onModelAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        final int dataLength = (int)(buffer.getInt(limit, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF_FFFFL);
        parentMessage.limit(limit + headerLength + dataLength);

        if (0 == dataLength)
        {
            return "";
        }

        final byte[] tmp = new byte[dataLength];
        buffer.getBytes(limit + headerLength, tmp, 0, dataLength);

        return new String(tmp, java.nio.charset.StandardCharsets.ISO_8859_1);
    }

    public String toString()
    {
        if (null == buffer)
        {
            return "";
        }

        final CarDecoder decoder = new CarDecoder();
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
        builder.append("[Car](sbeTemplateId=");
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
        builder.append("serialNumber=");
        builder.append(this.serialNumber());
        builder.append('|');
        builder.append("modelYear=");
        builder.append(this.modelYear());
        builder.append('|');
        builder.append("available=");
        builder.append(this.available());
        builder.append('|');
        builder.append("code=");
        builder.append(this.code());
        builder.append('|');
        builder.append("someNumbers=");
        builder.append('[');
        if (someNumbersLength() > 0)
        {
            for (int i = 0; i < someNumbersLength(); i++)
            {
                builder.append(this.someNumbers(i));
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
        builder.append(']');
        builder.append('|');
        builder.append("vehicleCode=");
        for (int i = 0; i < vehicleCodeLength() && this.vehicleCode(i) > 0; i++)
        {
            builder.append((char)this.vehicleCode(i));
        }
        builder.append('|');
        builder.append("extras=");
        this.extras().appendTo(builder);
        builder.append('|');
        builder.append("engine=");
        final EngineDecoder engine = this.engine();
        if (engine != null)
        {
            engine.appendTo(builder);
        }
        else
        {
            builder.append("null");
        }
        builder.append('|');
        builder.append("fuelFigures=[");
        final int fuelFiguresOriginalOffset = fuelFigures.offset;
        final int fuelFiguresOriginalIndex = fuelFigures.index;
        final FuelFiguresDecoder fuelFigures = this.fuelFigures();
        if (fuelFigures.count() > 0)
        {
            while (fuelFigures.hasNext())
            {
                fuelFigures.next().appendTo(builder);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
        fuelFigures.offset = fuelFiguresOriginalOffset;
        fuelFigures.index = fuelFiguresOriginalIndex;
        builder.append(']');
        builder.append('|');
        builder.append("performanceFigures=[");
        final int performanceFiguresOriginalOffset = performanceFigures.offset;
        final int performanceFiguresOriginalIndex = performanceFigures.index;
        final PerformanceFiguresDecoder performanceFigures = this.performanceFigures();
        if (performanceFigures.count() > 0)
        {
            while (performanceFigures.hasNext())
            {
                performanceFigures.next().appendTo(builder);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
        performanceFigures.offset = performanceFiguresOriginalOffset;
        performanceFigures.index = performanceFiguresOriginalIndex;
        builder.append(']');
        builder.append('|');
        builder.append("manufacturer=");
        builder.append('\'').append(manufacturer()).append('\'');
        builder.append('|');
        builder.append("model=");
        builder.append('\'').append(model()).append('\'');

        limit(originalLimit);

        return builder;
    }
    
    public CarDecoder sbeSkip()
    {
        sbeRewind();
        FuelFiguresDecoder fuelFigures = this.fuelFigures();
        if (fuelFigures.count() > 0)
        {
            while (fuelFigures.hasNext())
            {
                fuelFigures.next();
                fuelFigures.sbeSkip();
            }
        }
        PerformanceFiguresDecoder performanceFigures = this.performanceFigures();
        if (performanceFigures.count() > 0)
        {
            while (performanceFigures.hasNext())
            {
                performanceFigures.next();
                performanceFigures.sbeSkip();
            }
        }
        skipManufacturer();
        skipModel();

        return this;
    }
}
