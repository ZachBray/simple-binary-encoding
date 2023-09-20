/* Generated SBE (Simple Binary Encoding) message codec. */
package uk.co.real_logic.sbe.benchmarks;

import org.agrona.concurrent.UnsafeBuffer;


/**
 * Description of a basic Car
 */
@SuppressWarnings("all")
public final class CarEncoder
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
     *       V0_FUELFIGURES_N_BLOCK -> V0_FUELFIGURES_N_BLOCK [label="  fuelFigures.next()\n  where count - newIndex > 1  "];
     *       V0_FUELFIGURES_N -> V0_FUELFIGURES_N_BLOCK [label="  fuelFigures.next()\n  where count - newIndex > 1  "];
     *       V0_FUELFIGURES_N_BLOCK -> V0_FUELFIGURES_1_BLOCK [label="  fuelFigures.next()\n  where count - newIndex == 1  "];
     *       V0_FUELFIGURES_N -> V0_FUELFIGURES_1_BLOCK [label="  fuelFigures.next()\n  where count - newIndex == 1  "];
     *       V0_FUELFIGURES_N_BLOCK -> V0_FUELFIGURES_DONE [label="  fuelFigures.resetCountToIndex()  "];
     *       V0_FUELFIGURES_N -> V0_FUELFIGURES_DONE [label="  fuelFigures.resetCountToIndex()  "];
     *       V0_FUELFIGURES_1_BLOCK -> V0_FUELFIGURES_DONE [label="  fuelFigures.resetCountToIndex()  "];
     *       V0_FUELFIGURES_DONE -> V0_FUELFIGURES_DONE [label="  fuelFigures.resetCountToIndex()  "];
     *       V0_FUELFIGURES_1_BLOCK -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFiguresCount(0)  "];
     *       V0_FUELFIGURES_DONE -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFiguresCount(0)  "];
     *       V0_FUELFIGURES_1_BLOCK -> V0_PERFORMANCEFIGURES_N [label="  performanceFiguresCount(>0)  "];
     *       V0_FUELFIGURES_DONE -> V0_PERFORMANCEFIGURES_N [label="  performanceFiguresCount(>0)  "];
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
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE -> V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE [label="  performanceFigures.acceleration.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_N -> V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE [label="  performanceFigures.acceleration.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK -> V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE [label="  performanceFigures.acceleration.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_N -> V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE [label="  performanceFigures.acceleration.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE [label="  performanceFigures.acceleration.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK -> V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE [label="  performanceFigures.acceleration.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE -> V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE [label="  performanceFigures.acceleration.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_N -> V0_PERFORMANCEFIGURES_N_BLOCK [label="  performanceFigures.next()\n  where count - newIndex > 1  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_N_BLOCK [label="  performanceFigures.next()\n  where count - newIndex > 1  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE -> V0_PERFORMANCEFIGURES_N_BLOCK [label="  performanceFigures.next()\n  where count - newIndex > 1  "];
     *       V0_PERFORMANCEFIGURES_N -> V0_PERFORMANCEFIGURES_1_BLOCK [label="  performanceFigures.next()\n  where count - newIndex == 1  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_1_BLOCK [label="  performanceFigures.next()\n  where count - newIndex == 1  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE -> V0_PERFORMANCEFIGURES_1_BLOCK [label="  performanceFigures.next()\n  where count - newIndex == 1  "];
     *       V0_PERFORMANCEFIGURES_DONE -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFigures.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_N -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFigures.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFigures.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFigures.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFigures.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE -> V0_PERFORMANCEFIGURES_DONE [label="  performanceFigures.resetCountToIndex()  "];
     *       V0_PERFORMANCEFIGURES_DONE -> V0_PERFORMANCEFIGURES_DONE [label="  manufacturerLength()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK -> V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK [label="  manufacturerLength()  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE -> V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE [label="  manufacturerLength()  "];
     *       V0_PERFORMANCEFIGURES_DONE -> V0_MANUFACTURER_DONE [label="  manufacturer(?)  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK -> V0_MANUFACTURER_DONE [label="  manufacturer(?)  "];
     *       V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE -> V0_MANUFACTURER_DONE [label="  manufacturer(?)  "];
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

    private final CarEncoder parentMessage = this;
    private UnsafeBuffer buffer;
    private int initialOffset;
    private int offset;
    private int limit;

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

    public CarEncoder wrap(final UnsafeBuffer buffer, final int offset)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.initialOffset = offset;
        this.offset = offset;
        limit(offset + BLOCK_LENGTH);

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            codecState(CodecStates.V0_BLOCK);
        }

        return this;
    }

    public CarEncoder wrapAndApplyHeader(
        final UnsafeBuffer buffer, final int offset, final MessageHeaderEncoder headerEncoder)
    {
        headerEncoder
            .wrap(buffer, offset)
            .blockLength(BLOCK_LENGTH)
            .templateId(TEMPLATE_ID)
            .schemaId(SCHEMA_ID)
            .version(SCHEMA_VERSION);

        return wrap(buffer, offset + MessageHeaderEncoder.ENCODED_LENGTH);
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

    public CarEncoder serialNumber(final long value)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onSerialNumberAccessed();
        }

        buffer.putInt(offset + 0, (int)value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
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

    public CarEncoder modelYear(final int value)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onModelYearAccessed();
        }

        buffer.putShort(offset + 4, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
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

    public CarEncoder available(final BooleanType value)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onAvailableAccessed();
        }

        buffer.putByte(offset + 6, (byte)value.value());
        return this;
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

    public CarEncoder code(final Model value)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onCodeAccessed();
        }

        buffer.putByte(offset + 7, value.value());
        return this;
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


    public CarEncoder someNumbers(final int index, final int value)
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
        buffer.putInt(pos, value, java.nio.ByteOrder.LITTLE_ENDIAN);

        return this;
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


    public CarEncoder vehicleCode(final int index, final byte value)
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
        buffer.putByte(pos, value);

        return this;
    }

    public static String vehicleCodeCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.US_ASCII.name();
    }

    public CarEncoder putVehicleCode(final byte[] src, final int srcOffset)
    {
        final int length = 6;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onVehicleCodeAccessed();
        }

        buffer.putBytes(offset + 28, src, srcOffset, length);

        return this;
    }

    public CarEncoder vehicleCode(final String src)
    {
        final int length = 6;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onVehicleCodeAccessed();
        }

        buffer.putStringWithoutLengthAscii(offset + 28, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(offset + 28 + start, (byte)0);
        }

        return this;
    }

    public CarEncoder vehicleCode(final CharSequence src)
    {
        final int length = 6;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onVehicleCodeAccessed();
        }

        buffer.putStringWithoutLengthAscii(offset + 28, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(offset + 28 + start, (byte)0);
        }

        return this;
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

    private final OptionalExtrasEncoder extras = new OptionalExtrasEncoder();

    public OptionalExtrasEncoder extras()
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

    private final EngineEncoder engine = new EngineEncoder();

    public EngineEncoder engine()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onEngineAccessed();
        }

        engine.wrap(buffer, offset + 35);
        return engine;
    }

    private final FuelFiguresEncoder fuelFigures = new FuelFiguresEncoder(this);

    public static long fuelFiguresId()
    {
        return 9;
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
                        "Cannot encode count of repeating group \"fuelFigures\" in state: " + CodecStates.name(codecState()) +
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
                        "Cannot encode count of repeating group \"fuelFigures\" in state: " + CodecStates.name(codecState()) +
                        ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                        "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
            }
        }
    }

    public FuelFiguresEncoder fuelFiguresCount(final int count)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onFuelFiguresAccessed(count);
        }

        fuelFigures.wrap(buffer, count);
        return fuelFigures;
    }

    public static final class FuelFiguresEncoder
    {
        public static final int HEADER_SIZE = 4;
        private final CarEncoder parentMessage;
        private UnsafeBuffer buffer;
        private int count;
        private int index;
        private int offset;
        private int initialLimit;

        FuelFiguresEncoder(final CarEncoder parentMessage)
        {
            this.parentMessage = parentMessage;
        }

        public void wrap(final UnsafeBuffer buffer, final int count)
        {
            if (count < 0 || count > 65534)
            {
                throw new IllegalArgumentException("count outside allowed range: count=" + count);
            }

            if (buffer != this.buffer)
            {
                this.buffer = buffer;
            }

            index = 0;
            this.count = count;
            final int limit = parentMessage.limit();
            initialLimit = limit;
            parentMessage.limit(limit + HEADER_SIZE);
            buffer.putShort(limit + 0, (short)6, java.nio.ByteOrder.LITTLE_ENDIAN);
            buffer.putShort(limit + 2, (short)count, java.nio.ByteOrder.LITTLE_ENDIAN);
        }

        private void onNextElementAccessed()
        {
            final int remaining = count - index;
            if (remaining > 1)
            {
               switch (codecState())
               {
                   case CodecStates.V0_FUELFIGURES_N_BLOCK:
                   case CodecStates.V0_FUELFIGURES_N:
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
                    case CodecStates.V0_FUELFIGURES_N_BLOCK:
                    case CodecStates.V0_FUELFIGURES_N:
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
        private void onResetCountToIndex()
        {
           switch (codecState())
           {
               case CodecStates.V0_FUELFIGURES_N_BLOCK:
               case CodecStates.V0_FUELFIGURES_N:
               case CodecStates.V0_FUELFIGURES_1_BLOCK:
               case CodecStates.V0_FUELFIGURES_DONE:
                   codecState(CodecStates.V0_FUELFIGURES_DONE);
                   break;
               default:
                   throw new IllegalStateException("Illegal field access order. " +
                       "Cannot reset count of repeating group \"fuelFigures\" in state: " + CodecStates.name(codecState()) +
                       ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                       "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
           }
        }

        public FuelFiguresEncoder next()
        {
            if (ENABLE_ACCESS_ORDER_CHECKS)
            {
                onNextElementAccessed();
            }

            if (index >= count)
            {
                throw new java.util.NoSuchElementException();
            }

            offset = parentMessage.limit();
            parentMessage.limit(offset + sbeBlockLength());
            ++index;

            return this;
        }


        public int resetCountToIndex()
        {
            if (ENABLE_ACCESS_ORDER_CHECKS)
            {
                onResetCountToIndex();
            }

            count = index;
            buffer.putShort(initialLimit + 2, (short)count, java.nio.ByteOrder.LITTLE_ENDIAN);

            return count;
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

        public FuelFiguresEncoder speed(final int value)
        {
            if (ENABLE_ACCESS_ORDER_CHECKS)
            {
                onSpeedAccessed();
            }

            buffer.putShort(offset + 0, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
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

        public FuelFiguresEncoder mpg(final float value)
        {
            if (ENABLE_ACCESS_ORDER_CHECKS)
            {
                onMpgAccessed();
            }

            buffer.putFloat(offset + 2, value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
        }

    }

    private final PerformanceFiguresEncoder performanceFigures = new PerformanceFiguresEncoder(this);

    public static long performanceFiguresId()
    {
        return 12;
    }

    private void onPerformanceFiguresAccessed(final int remaining)
    {
        if (remaining == 0)
        {
            switch (codecState())
            {
                case CodecStates.V0_FUELFIGURES_1_BLOCK:
                case CodecStates.V0_FUELFIGURES_DONE:
                    codecState(CodecStates.V0_PERFORMANCEFIGURES_DONE);
                    break;
                default:
                    throw new IllegalStateException("Illegal field access order. " +
                        "Cannot encode count of repeating group \"performanceFigures\" in state: " + CodecStates.name(codecState()) +
                        ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                        "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
            }
        }
        else
        {
            switch (codecState())
            {
                case CodecStates.V0_FUELFIGURES_1_BLOCK:
                case CodecStates.V0_FUELFIGURES_DONE:
                    codecState(CodecStates.V0_PERFORMANCEFIGURES_N);
                    break;
                default:
                    throw new IllegalStateException("Illegal field access order. " +
                        "Cannot encode count of repeating group \"performanceFigures\" in state: " + CodecStates.name(codecState()) +
                        ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                        "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
            }
        }
    }

    public PerformanceFiguresEncoder performanceFiguresCount(final int count)
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onPerformanceFiguresAccessed(count);
        }

        performanceFigures.wrap(buffer, count);
        return performanceFigures;
    }

    public static final class PerformanceFiguresEncoder
    {
        public static final int HEADER_SIZE = 4;
        private final CarEncoder parentMessage;
        private UnsafeBuffer buffer;
        private int count;
        private int index;
        private int offset;
        private int initialLimit;
        private final AccelerationEncoder acceleration;

        PerformanceFiguresEncoder(final CarEncoder parentMessage)
        {
            this.parentMessage = parentMessage;
            acceleration = new AccelerationEncoder(parentMessage);
        }

        public void wrap(final UnsafeBuffer buffer, final int count)
        {
            if (count < 0 || count > 65534)
            {
                throw new IllegalArgumentException("count outside allowed range: count=" + count);
            }

            if (buffer != this.buffer)
            {
                this.buffer = buffer;
            }

            index = 0;
            this.count = count;
            final int limit = parentMessage.limit();
            initialLimit = limit;
            parentMessage.limit(limit + HEADER_SIZE);
            buffer.putShort(limit + 0, (short)1, java.nio.ByteOrder.LITTLE_ENDIAN);
            buffer.putShort(limit + 2, (short)count, java.nio.ByteOrder.LITTLE_ENDIAN);
        }

        private void onNextElementAccessed()
        {
            final int remaining = count - index;
            if (remaining > 1)
            {
               switch (codecState())
               {
                   case CodecStates.V0_PERFORMANCEFIGURES_N:
                   case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK:
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
                    case CodecStates.V0_PERFORMANCEFIGURES_N:
                    case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK:
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
        private void onResetCountToIndex()
        {
           switch (codecState())
           {
               case CodecStates.V0_PERFORMANCEFIGURES_DONE:
               case CodecStates.V0_PERFORMANCEFIGURES_N:
               case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK:
               case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK:
               case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE:
               case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE:
                   codecState(CodecStates.V0_PERFORMANCEFIGURES_DONE);
                   break;
               default:
                   throw new IllegalStateException("Illegal field access order. " +
                       "Cannot reset count of repeating group \"performanceFigures\" in state: " + CodecStates.name(codecState()) +
                       ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                       "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
           }
        }

        public PerformanceFiguresEncoder next()
        {
            if (ENABLE_ACCESS_ORDER_CHECKS)
            {
                onNextElementAccessed();
            }

            if (index >= count)
            {
                throw new java.util.NoSuchElementException();
            }

            offset = parentMessage.limit();
            parentMessage.limit(offset + sbeBlockLength());
            ++index;

            return this;
        }


        public int resetCountToIndex()
        {
            if (ENABLE_ACCESS_ORDER_CHECKS)
            {
                onResetCountToIndex();
            }

            count = index;
            buffer.putShort(initialLimit + 2, (short)count, java.nio.ByteOrder.LITTLE_ENDIAN);

            return count;
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

        public PerformanceFiguresEncoder octaneRating(final short value)
        {
            if (ENABLE_ACCESS_ORDER_CHECKS)
            {
                onOctaneRatingAccessed();
            }

            buffer.putByte(offset + 0, (byte)value);
            return this;
        }


        public static long accelerationId()
        {
            return 14;
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
                            "Cannot encode count of repeating group \"performanceFigures.acceleration\" in state: " + CodecStates.name(codecState()) +
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
                            "Cannot encode count of repeating group \"performanceFigures.acceleration\" in state: " + CodecStates.name(codecState()) +
                            ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                            "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
                }
            }
        }

        public AccelerationEncoder accelerationCount(final int count)
        {
            if (ENABLE_ACCESS_ORDER_CHECKS)
            {
                onAccelerationAccessed(count);
            }

            acceleration.wrap(buffer, count);
            return acceleration;
        }

        public static final class AccelerationEncoder
        {
            public static final int HEADER_SIZE = 4;
            private final CarEncoder parentMessage;
            private UnsafeBuffer buffer;
            private int count;
            private int index;
            private int offset;
            private int initialLimit;

            AccelerationEncoder(final CarEncoder parentMessage)
            {
                this.parentMessage = parentMessage;
            }

            public void wrap(final UnsafeBuffer buffer, final int count)
            {
                if (count < 0 || count > 65534)
                {
                    throw new IllegalArgumentException("count outside allowed range: count=" + count);
                }

                if (buffer != this.buffer)
                {
                    this.buffer = buffer;
                }

                index = 0;
                this.count = count;
                final int limit = parentMessage.limit();
                initialLimit = limit;
                parentMessage.limit(limit + HEADER_SIZE);
                buffer.putShort(limit + 0, (short)6, java.nio.ByteOrder.LITTLE_ENDIAN);
                buffer.putShort(limit + 2, (short)count, java.nio.ByteOrder.LITTLE_ENDIAN);
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
            private void onResetCountToIndex()
            {
               switch (codecState())
               {
                   case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_1_BLOCK:
                   case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE:
                   case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_N:
                   case CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_N_BLOCK:
                       codecState(CodecStates.V0_PERFORMANCEFIGURES_N_ACCELERATION_DONE);
                       break;
                   case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_N:
                   case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK:
                   case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_N_BLOCK:
                   case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE:
                       codecState(CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE);
                       break;
                   default:
                       throw new IllegalStateException("Illegal field access order. " +
                           "Cannot reset count of repeating group \"performanceFigures.acceleration\" in state: " + CodecStates.name(codecState()) +
                           ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                           "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
               }
            }

            public AccelerationEncoder next()
            {
                if (ENABLE_ACCESS_ORDER_CHECKS)
                {
                    onNextElementAccessed();
                }

                if (index >= count)
                {
                    throw new java.util.NoSuchElementException();
                }

                offset = parentMessage.limit();
                parentMessage.limit(offset + sbeBlockLength());
                ++index;

                return this;
            }


            public int resetCountToIndex()
            {
                if (ENABLE_ACCESS_ORDER_CHECKS)
                {
                    onResetCountToIndex();
                }

                count = index;
                buffer.putShort(initialLimit + 2, (short)count, java.nio.ByteOrder.LITTLE_ENDIAN);

                return count;
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

            public AccelerationEncoder mph(final int value)
            {
                if (ENABLE_ACCESS_ORDER_CHECKS)
                {
                    onMphAccessed();
                }

                buffer.putShort(offset + 0, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
                return this;
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

            public AccelerationEncoder seconds(final float value)
            {
                if (ENABLE_ACCESS_ORDER_CHECKS)
                {
                    onSecondsAccessed();
                }

                buffer.putFloat(offset + 2, value, java.nio.ByteOrder.LITTLE_ENDIAN);
                return this;
            }

        }
    }

    public static int manufacturerId()
    {
        return 17;
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

    private void onManufacturerAccessed()
    {
        switch (codecState())
        {
            case CodecStates.V0_PERFORMANCEFIGURES_DONE:
            case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_1_BLOCK:
            case CodecStates.V0_PERFORMANCEFIGURES_1_ACCELERATION_DONE:
                codecState(CodecStates.V0_MANUFACTURER_DONE);
                break;
            default:
                throw new IllegalStateException("Illegal field access order. " +
                    "Cannot access field \"manufacturer\" in state: " + CodecStates.name(codecState()) +
                    ". Expected one of these transitions: [" + CodecStates.transitions(codecState()) +
                    "]. Please see the diagram in the Javadoc of the inner class #CodecStates.");
        }
    }

    public CarEncoder putManufacturer(final UnsafeBuffer src, final int srcOffset, final int length)
    {
        if (length > 1073741824)
        {
            throw new IllegalStateException("length > maxValue for type: " + length);
        }

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onManufacturerAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        parentMessage.limit(limit + headerLength + length);
        buffer.putInt(limit, length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(limit + headerLength, src, srcOffset, length);

        return this;
    }

    public CarEncoder putManufacturer(final byte[] src, final int srcOffset, final int length)
    {
        if (length > 1073741824)
        {
            throw new IllegalStateException("length > maxValue for type: " + length);
        }

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onManufacturerAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        parentMessage.limit(limit + headerLength + length);
        buffer.putInt(limit, length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(limit + headerLength, src, srcOffset, length);

        return this;
    }

    public CarEncoder manufacturer(final String value)
    {
        final byte[] bytes = (null == value || value.isEmpty()) ? org.agrona.collections.ArrayUtil.EMPTY_BYTE_ARRAY : value.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);

        final int length = bytes.length;
        if (length > 1073741824)
        {
            throw new IllegalStateException("length > maxValue for type: " + length);
        }

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onManufacturerAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        parentMessage.limit(limit + headerLength + length);
        buffer.putInt(limit, length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(limit + headerLength, bytes, 0, length);

        return this;
    }

    public static int modelId()
    {
        return 18;
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

    public CarEncoder putModel(final UnsafeBuffer src, final int srcOffset, final int length)
    {
        if (length > 1073741824)
        {
            throw new IllegalStateException("length > maxValue for type: " + length);
        }

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onModelAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        parentMessage.limit(limit + headerLength + length);
        buffer.putInt(limit, length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(limit + headerLength, src, srcOffset, length);

        return this;
    }

    public CarEncoder putModel(final byte[] src, final int srcOffset, final int length)
    {
        if (length > 1073741824)
        {
            throw new IllegalStateException("length > maxValue for type: " + length);
        }

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onModelAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        parentMessage.limit(limit + headerLength + length);
        buffer.putInt(limit, length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(limit + headerLength, src, srcOffset, length);

        return this;
    }

    public CarEncoder model(final String value)
    {
        final byte[] bytes = (null == value || value.isEmpty()) ? org.agrona.collections.ArrayUtil.EMPTY_BYTE_ARRAY : value.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);

        final int length = bytes.length;
        if (length > 1073741824)
        {
            throw new IllegalStateException("length > maxValue for type: " + length);
        }

        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            onModelAccessed();
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        parentMessage.limit(limit + headerLength + length);
        buffer.putInt(limit, length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(limit + headerLength, bytes, 0, length);

        return this;
    }

    public String toString()
    {
        if (null == buffer)
        {
            return "";
        }

        return appendTo(new StringBuilder()).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        if (null == buffer)
        {
            return builder;
        }

        final CarDecoder decoder = new CarDecoder();
        decoder.wrap(buffer, initialOffset, BLOCK_LENGTH, SCHEMA_VERSION);

        return decoder.appendTo(builder);
    }

    public void checkEncodingIsComplete()
    {
        if (ENABLE_ACCESS_ORDER_CHECKS)
        {
            switch (codecState)
            {
                case CodecStates.V0_MODEL_DONE:
                    return;
                default:
                    throw new IllegalStateException("Not fully encoded, current state: " +
                        CodecStates.name(codecState) + ", allowed transitions: " +
                        CodecStates.transitions(codecState));
            }
        }
    }

}
