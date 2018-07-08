package uk.co.real_logic.sbe.generation.typescript;

import static uk.co.real_logic.sbe.xml.XmlSchemaParser.parse;

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
