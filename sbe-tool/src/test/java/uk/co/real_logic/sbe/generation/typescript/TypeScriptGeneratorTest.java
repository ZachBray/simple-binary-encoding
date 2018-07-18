package uk.co.real_logic.sbe.generation.typescript;

import static org.junit.Assert.assertEquals;
import static uk.co.real_logic.sbe.xml.XmlSchemaParser.parse;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.agrona.generation.StringWriterOutputManager;
import org.junit.Test;

import uk.co.real_logic.sbe.TestUtil;
import uk.co.real_logic.sbe.generation.CodeGenerator;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.ParserOptions;

/**
 * A small set of smoke tests that don't verify much except that no exceptions are thrown.
 */
public class TypeScriptGeneratorTest
{
    @Test
    public void shouldNotThrowWhenGeneratingCodeFromExampleSchema() throws Exception
    {
        generateFromSchema("example-schema.xml");
    }

    @Test
    public void shouldNotThrowWhenGeneratingCodeFromExampleBigEndianSchema() throws Exception
    {
        generateFromSchema("example-bigendian-test-schema.xml");
    }

    @Test
    public void shouldNotThrowWhenGeneratingCodeFromExtendedSchema() throws Exception
    {
        generateFromSchema("extension-schema.xml");
    }

    @Test
    public void shouldGenerateJavaCompatibleNegative53BitLongs()
    {
        final long expectedValue = -9007199254740991L; // Min safe JS integer
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[] {
            (byte)255, (byte)224, 0, 0, 0, 0, 0, 1
        });
        buffer.order(ByteOrder.BIG_ENDIAN);
        final long observedValue = buffer.getLong(0);
        assertEquals(expectedValue, observedValue);
    }

    @Test
    public void shouldGenerateJavaCompatiblePositive53BitLongs()
    {
        final long expectedValue = 9007199254740991L; // Min safe JS integer
        final ByteBuffer buffer = ByteBuffer.wrap(new byte[] {
            0, 31, (byte)255, (byte)255, (byte)255, (byte)255, (byte)255, (byte)255
        });
        buffer.order(ByteOrder.BIG_ENDIAN);
        final long observedValue = buffer.getLong(0);
        assertEquals(expectedValue, observedValue);
    }

    private void generateFromSchema(final String schemaResource) throws Exception
    {
        final ParserOptions options = ParserOptions.builder().stopOnError(true).build();
        final MessageSchema schema = parse(TestUtil.getLocalResource(schemaResource), options);
        final IrGenerator irg = new IrGenerator();
        final Ir ir = irg.generate(schema);
        final CodeGenerator generator = new TypeScriptGenerator(
            ir, false, true,
            new StringWriterOutputManager());
        generator.generate();
    }
}
