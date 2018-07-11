/*
 * Copyright 2013-2018 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.sbe.generation.typescript;

import static uk.co.real_logic.sbe.SbeTool.JAVA_INTERFACE_PACKAGE;
import static uk.co.real_logic.sbe.generation.typescript.TypeScriptGenerator.CodecType.DECODER;
import static uk.co.real_logic.sbe.generation.typescript.TypeScriptGenerator.CodecType.ENCODER;
import static uk.co.real_logic.sbe.generation.typescript.TypeScriptUtil.formatClassName;
import static uk.co.real_logic.sbe.generation.typescript.TypeScriptUtil.formatPropertyName;
import static uk.co.real_logic.sbe.generation.typescript.TypeScriptUtil.generateFlyweightPropertyJsdoc;
import static uk.co.real_logic.sbe.generation.typescript.TypeScriptUtil.generateGroupEncodePropertyJsdoc;
import static uk.co.real_logic.sbe.generation.typescript.TypeScriptUtil.generateLiteral;
import static uk.co.real_logic.sbe.generation.typescript.TypeScriptUtil.generateOptionDecodeJsdoc;
import static uk.co.real_logic.sbe.generation.typescript.TypeScriptUtil.generateOptionEncodeJsdoc;
import static uk.co.real_logic.sbe.generation.typescript.TypeScriptUtil.generateTypeJsdoc;
import static uk.co.real_logic.sbe.generation.typescript.TypeScriptUtil.typeScriptTypeName;
import static uk.co.real_logic.sbe.ir.GenerationUtil.collectFields;
import static uk.co.real_logic.sbe.ir.GenerationUtil.collectGroups;
import static uk.co.real_logic.sbe.ir.GenerationUtil.collectVarData;
import static uk.co.real_logic.sbe.ir.GenerationUtil.concatTokens;
import static uk.co.real_logic.sbe.ir.GenerationUtil.getMessageBody;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.agrona.Verify;
import org.agrona.generation.OutputManager;

import uk.co.real_logic.sbe.PrimitiveType;
import uk.co.real_logic.sbe.generation.CodeGenerator;
import uk.co.real_logic.sbe.generation.Generators;
import uk.co.real_logic.sbe.ir.Encoding;
import uk.co.real_logic.sbe.ir.HeaderStructure;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.ir.Signal;
import uk.co.real_logic.sbe.ir.Token;

public class TypeScriptGenerator implements CodeGenerator
{
    private static final String META_ATTRIBUTE_ENUM = "MetaAttribute";
    private static final String BASE_INDENT = "";
    private static final String INDENT = "    ";
    private static final String GEN_COMPOSITE_DECODER_FLYWEIGHT = "CompositeDecoderFlyweight";
    private static final String GEN_COMPOSITE_ENCODER_FLYWEIGHT = "CompositeEncoderFlyweight";
    private static final String GEN_MESSAGE_DECODER_FLYWEIGHT = "MessageDecoderFlyweight";
    private static final String GEN_MESSAGE_ENCODER_FLYWEIGHT = "MessageEncoderFlyweight";
    private final Ir ir;
    private final OutputManager outputManager;
    private final String mutableBuffer;
    private final String readOnlyBuffer;
    private final boolean shouldGenerateInterfaces;
    private final boolean shouldDecodeUnknownEnumValues;
    public TypeScriptGenerator(
        final Ir ir,
        final boolean shouldGenerateInterfaces,
        final boolean shouldDecodeUnknownEnumValues,
        final OutputManager outputManager)
    {
        Verify.notNull(ir, "ir");
        Verify.notNull(outputManager, "outputManager");

        this.ir = ir;
        this.outputManager = outputManager;

        this.mutableBuffer = "DataView";

        this.readOnlyBuffer = "DataView";

        this.shouldGenerateInterfaces = shouldGenerateInterfaces;
        this.shouldDecodeUnknownEnumValues = shouldDecodeUnknownEnumValues;
    }

    private static CharSequence generateGroupDecoderProperty(
        final String parentMessage,
        final String groupName,
        final Token token,
        final String indent)
    {
        final StringBuilder sb = new StringBuilder();
        final String className = formatClassName(groupName);
        final String propertyName = formatPropertyName(token.name());

        sb.append(String.format("\n" +
            "%s" +
            indent + "    private %sCodec?: %s;\n",
            generateFlyweightPropertyJsdoc(indent + INDENT, token, className),
            propertyName,
            className));

        sb.append(String.format("\n" +
            indent + "    public static %sId(): number {\n" +
            indent + "        return %d;\n" +
            indent + "    }\n",
            formatPropertyName(groupName),
            token.id()));

        sb.append(String.format("\n" +
            indent + "    public static %sSinceVersion(): number {\n" +
            indent + "        return %d;\n" +
            indent + "    }\n",
            formatPropertyName(groupName),
            token.version()));

        final String actingVersionGuard = token.version() == 0 ?
            "" :
            indent + "        if (this.parentMessage.actingVersion < " + token.version() + ") {\n" +
            indent + "            this." + propertyName + ".count = 0;\n" +
            indent + "            this." + propertyName + ".index = -1;\n" +
            indent + "            return this." + propertyName + ";\n" +
            indent + "        }\n\n";

        sb.append(String.format("\n" +
            indent + "    public %2$s(): %1$s {\n" +
            "%3$s" +
            indent + "        if (!this.%2$sCodec) {\n" +
            indent + "            this.%2$sCodec = new %1$s(%4$s);\n" +
            indent + "        }\n" +
            indent + "        this.%2$sCodec.wrap(this.buffer);\n" +
            indent + "        return this.%2$sCodec;\n" +
            indent + "    }\n",
            className,
            propertyName,
            actingVersionGuard,
            parentMessage));

        return sb;
    }

    private static CharSequence generateEnumFileHeader()
    {
        return "/* Generated SBE (Simple Binary Encoding) message codec */\n\n";
    }

    private static CharSequence generateDeclaration(
        final String className, final String implementsString, final Token typeToken)
    {
        return String.format(
            "%s" +
            "export class %s%s {\n",
            generateTypeJsdoc(BASE_INDENT, typeToken),
            className,
            implementsString);
    }

    private static CharSequence generateEnumDeclaration(final String name, final Token typeToken)
    {
        return
            generateTypeJsdoc(BASE_INDENT, typeToken) +
            "export enum " + name + " {\n";
    }

    private static CharSequence generateArrayFieldNotPresentCondition(final int sinceVersion, final String indent)
    {
        if (0 == sinceVersion)
        {
            return "";
        }

        return String.format(
            indent + "        if (this.parentMessage.actingVersion < %d) {\n" +
            indent + "            return 0;\n" +
            indent + "        }\n\n",
            sinceVersion);
    }

    private static CharSequence generateStringNotPresentCondition(final int sinceVersion, final String indent)
    {
        if (0 == sinceVersion)
        {
            return "";
        }

        return String.format(
            indent + "        if (this.parentMessage.actingVersion < %d) {\n" +
            indent + "            return \"\";\n" +
            indent + "        }\n\n",
            sinceVersion);
    }

    private static CharSequence generatePropertyNotPresentCondition(
        final boolean inComposite,
        final CodecType codecType,
        final Token propertyToken,
        final String enumName,
        final String indent)
    {
        if (inComposite || codecType == ENCODER || 0 == propertyToken.version())
        {
            return "";
        }

        return String.format(
            indent + "        if (this.parentMessage.actingVersion < %d) {\n" +
            indent + "            return %s;\n" +
            indent + "        }\n\n",
            propertyToken.version(),
            enumName == null ? "null" : (enumName + ".NULL_VAL"));
    }

    private static void generateArrayLengthMethod(
        final String propertyName, final String indent, final int fieldLength, final StringBuilder sb)
    {
        sb.append(String.format("\n" +
            indent + "    public static %sLength(): number {\n" +
            indent + "        return %d;\n" +
            indent + "    }\n\n",
            propertyName,
            fieldLength));
    }

    private static int sizeOfPrimitive(final Encoding encoding)
    {
        return encoding.primitiveType().size();
    }

    private static void generateCharacterEncodingMethod(
        final StringBuilder sb, final String propertyName, final String characterEncoding, final String indent)
    {
        if (null != characterEncoding)
        {
            sb.append(String.format("\n" +
                indent + "    public static %sCharacterEncoding(): string {\n" +
                indent + "        return \"%s\";\n" +
                indent + "    }\n",
                formatPropertyName(propertyName),
                characterEncoding));
        }
    }

    private static CharSequence generateByteLiteralList(final byte[] bytes)
    {
        final StringBuilder values = new StringBuilder();
        for (final byte b : bytes)
        {
            values.append(b).append(", ");
        }

        if (values.length() > 0)
        {
            values.setLength(values.length() - 2);
        }

        return values;
    }

    private static CharSequence generateFixedFlyweightCode(
        final String className, final int size, final String bufferImplementation)
    {
        return String.format(
            "    public readonly encodedLength = %2$d;\n" +
            "    private buffer = new DataView(new ArrayBuffer(0));\n" +
            "    private offset = 0;\n\n" +
            "    public wrap(buffer: %3$s, offset: number): %1$s {\n" +
            "        this.buffer = buffer;\n" +
            "        this.offset = offset;\n\n" +
            "        return this;\n" +
            "    }\n\n" +
            "    public getBuffer(): %3$s {\n" +
            "        return this.buffer;\n" +
            "    }\n\n" +
            "    public getOffset(): number {\n" +
            "        return this.offset;\n" +
            "    }\n\n" +
            "    public getEncodedLength(): number {\n" +
            "        return this.encodedLength;\n" +
            "    }\n",
            className,
            size,
            bufferImplementation);
    }

    private static void generateFieldIdMethod(final StringBuilder sb, final Token token, final String indent)
    {
        sb.append(String.format(
            "\n" +
            indent + "    public static %sId(): number {\n" +
            indent + "        return %d;\n" +
            indent + "    }\n",
            formatPropertyName(token.name()),
            token.id()));
    }

    private static void generateEncodingOffsetMethod(
        final StringBuilder sb, final String name, final int offset, final String indent)
    {
        sb.append(String.format(
            "\n" +
            indent + "    public static %sEncodingOffset(): number {\n" +
            indent + "        return %d;\n" +
            indent + "    }\n",
            formatPropertyName(name),
            offset));
    }

    private static void generateEncodingLengthMethod(
        final StringBuilder sb, final String name, final int length, final String indent)
    {
        sb.append(String.format(
            "\n" +
            indent + "    public static %sEncodingLength(): number {\n" +
            indent + "        return %d;\n" +
            indent + "    }\n",
            formatPropertyName(name),
            length));
    }

    private static void generateFieldSinceVersionMethod(final StringBuilder sb, final Token token, final String indent)
    {
        sb.append(String.format(
            "\n" +
            indent + "    public static %sSinceVersion(): number {\n" +
            indent + "        return %d;\n" +
            indent + "    }\n",
            formatPropertyName(token.name()),
            token.version()));
    }

    private static void generateFieldMetaAttributeMethod(final StringBuilder sb, final Token token, final String indent)
    {
        final Encoding encoding = token.encoding();
        final String epoch = encoding.epoch() == null ? "" : encoding.epoch();
        final String timeUnit = encoding.timeUnit() == null ? "" : encoding.timeUnit();
        final String semanticType = encoding.semanticType() == null ? "" : encoding.semanticType();

        sb.append(String.format(
            "\n" +
            indent + "    public static %sMetaAttribute(metaAttribute: MetaAttribute): string {\n" +
            indent + "        switch (metaAttribute) {\n" +
            indent + "            case MetaAttribute.EPOCH: return \"%s\";\n" +
            indent + "            case MetaAttribute.TIME_UNIT: return \"%s\";\n" +
            indent + "            case MetaAttribute.SEMANTIC_TYPE: return \"%s\";\n" +
            indent + "            case MetaAttribute.PRESENCE: return \"%s\";\n" +
            indent + "        }\n\n" +
            indent + "        return \"\";\n" +
            indent + "    }\n",
            formatPropertyName(token.name()),
            epoch,
            timeUnit,
            semanticType,
            encoding.presence().toString().toLowerCase()));
    }

    private String encoderName(final String className)
    {
        return className + "Encoder";
    }

    private String decoderName(final String className)
    {
        return className + "Decoder";
    }

    private String implementsInterface(final String interfaceName)
    {
        if (!shouldGenerateInterfaces)
        {
            return "";
        }
        else
        {
            return " implements " + interfaceName;
        }
    }

    private void generateMessageHeaderStub() throws IOException
    {
        generateComposite(ir.headerStructure().tokens());
    }

    private void generateTypeStubs() throws IOException
    {
        generateMetaAttributeEnum();

        for (final List<Token> tokens : ir.types())
        {
            switch (tokens.get(0).signal())
            {
                case BEGIN_ENUM:
                    generateEnum(tokens);
                    break;

                case BEGIN_SET:
                    generateBitSet(tokens);
                    break;

                case BEGIN_COMPOSITE:
                    generateComposite(tokens);
                    break;
            }
        }
    }

    public void generate() throws IOException
    {
        generateTypeStubs();
        generateMessageHeaderStub();

        for (final List<Token> tokens : ir.messages())
        {
            final Token msgToken = tokens.get(0);
            final List<Token> messageBody = getMessageBody(tokens);

            int i = 0;
            final List<Token> fields = new ArrayList<>();
            i = collectFields(messageBody, i, fields);

            final List<Token> groups = new ArrayList<>();
            i = collectGroups(messageBody, i, groups);

            final List<Token> varData = new ArrayList<>();
            collectVarData(messageBody, i, varData);

            generateDecoder(BASE_INDENT, fields, groups, varData, msgToken);
            generateEncoder(BASE_INDENT, fields, groups, varData, msgToken);
        }

        // TODO this is dirty
        if (outputManager instanceof TypeScriptSingleFileOutputManager)
        {
            ((TypeScriptSingleFileOutputManager)outputManager).writeFileEnding();
        }
    }

    private void generateEncoder(
        final String indent,
        final List<Token> fields,
        final List<Token> groups,
        final List<Token> varData,
        final Token msgToken) throws IOException
    {
        final String className = formatClassName(encoderName(msgToken.name()));
        final String implementsString = implementsInterface(GEN_MESSAGE_ENCODER_FLYWEIGHT);

        try (Writer out = outputManager.createOutput(className))
        {
            out.append(generateMainHeader());

            out.append(generateDeclaration(className, implementsString, msgToken));
            out.append(generateEncoderFlyweightCode(className, msgToken));
            out.append(generateEncoderFields(className, fields, indent));

            final StringBuilder sb = new StringBuilder();
            generateEncoderGroups(sb, className, "this", groups, indent);
            out.append(sb);

            out.append(generateEncoderVarData(className, varData, indent));

            // TODO
            // out.append(generateEncoderDisplay(formatClassName(decoderName(msgToken.name())), indent));

            out.append("}\n");
        }
    }

    private void generateDecoder(
        final String indent,
        final List<Token> fields,
        final List<Token> groups,
        final List<Token> varData,
        final Token msgToken) throws IOException
    {
        final String className = formatClassName(decoderName(msgToken.name()));
        final String implementsString = implementsInterface(GEN_MESSAGE_DECODER_FLYWEIGHT);

        try (Writer out = outputManager.createOutput(className))
        {
            out.append(generateMainHeader());

            out.append(generateDeclaration(className, implementsString, msgToken));
            out.append(generateDecoderFlyweightCode(className, msgToken));
            out.append(generateDecoderFields(fields, indent));

            final StringBuilder sb = new StringBuilder();
            generateDecoderGroups(sb, className, "this", groups, indent);
            out.append(sb);

            out.append(generateDecoderVarData(varData, indent));

            // TODO
            // out.append(generateDecoderDisplay(msgToken.name(), fields, groups, varData, indent));

            out.append("}\n");
        }
    }

    private void generateDecoderGroups(
        final StringBuilder sb,
        final String outerClassName,
        final String parentMessage,
        final List<Token> tokens,
        final String indent) throws IOException
    {
        for (int i = 0, size = tokens.size(); i < size; i++)
        {
            final Token groupToken = tokens.get(i);
            if (groupToken.signal() != Signal.BEGIN_GROUP)
            {
                throw new IllegalStateException("tokens must begin with BEGIN_GROUP: token=" + groupToken);
            }
            final Token dimensionsToken = tokens.get(i + 1);

            final String groupName = decoderName(formatClassName(groupToken.name()));
            ++i;
            final int groupHeaderTokenCount = tokens.get(i).componentTokenCount();
            i += groupHeaderTokenCount;
            final List<Token> fields = new ArrayList<>();
            i = collectFields(tokens, i, fields);
            final List<Token> groups = new ArrayList<>();
            i = collectGroups(tokens, i, groups);
            final List<Token> varData = new ArrayList<>();
            i = collectVarData(tokens, i, varData);

            generateGroupDecoderClass(outerClassName, groupToken, dimensionsToken,
                groupName, fields, groups, varData);

            sb.append(generateGroupDecoderProperty(parentMessage, groupName, groupToken, indent));
        }
    }

    // TODO this will suffer from group name collisions across messages
    private void generateGroupDecoderClass(
        final String outerClassName,
        final Token groupToken,
        final Token dimensionsToken,
        final String groupName,
        final List<Token> fields,
        final List<Token> groups,
        final List<Token> varData) throws IOException
    {

        final StringBuilder sb = new StringBuilder();

        generateGroupDecoderClassHeader(sb, groupName, outerClassName, groupToken, dimensionsToken, "");
        sb.append(generateDecoderFields(fields,  ""));
        generateDecoderGroups(sb, outerClassName, "this.parentMessage", groups, "");
        sb.append(generateDecoderVarData(varData, ""));
        // TODO
        // appendGroupInstanceDecoderDisplay(sb, fields, groups, varData, indent + INDENT);
        sb.append("}\n\n");

        try (Writer groupWriter = outputManager.createOutput(groupName))
        {
            groupWriter.write(sb.toString());
        }
    }

    private void generateEncoderGroups(
        final StringBuilder sb,
        final String outerClassName,
        final String parentMessage,
        final List<Token> tokens,
        final String indent) throws IOException
    {
        for (int i = 0, size = tokens.size(); i < size; i++)
        {
            final Token groupToken = tokens.get(i);
            if (groupToken.signal() != Signal.BEGIN_GROUP)
            {
                throw new IllegalStateException("tokens must begin with BEGIN_GROUP: token=" + groupToken);
            }

            final String groupName = groupToken.name();
            final String groupClassName = formatClassName(encoderName(groupName));
            final int groupTokenIndex = i;

            ++i;
            final int groupHeaderTokenCount = tokens.get(i).componentTokenCount();
            i += groupHeaderTokenCount;
            final List<Token> fields = new ArrayList<>();
            i = collectFields(tokens, i, fields);
            final List<Token> groups = new ArrayList<>();
            i = collectGroups(tokens, i, groups);
            final List<Token> varData = new ArrayList<>();
            i = collectVarData(tokens, i, varData);

            generateGroupEncoderClass(outerClassName, tokens, groupName, groupClassName,
                groupTokenIndex, fields, groups, varData);

            sb.append(generateGroupEncoderProperty(parentMessage, groupName, groupToken, indent));
        }
    }

    private void generateGroupEncoderClass(
        final String outerClassName,
        final List<Token> tokens,
        final String groupName,
        final String groupClassName,
        final int groupTokenIndex,
        final List<Token> fields,
        final List<Token> groups,
        final List<Token> varData) throws IOException
    {

        final StringBuilder sb = new StringBuilder();

        generateGroupEncoderClassHeader(sb, groupName, outerClassName, tokens, groupTokenIndex, "");
        sb.append(generateEncoderFields(groupClassName, fields, ""));
        generateEncoderGroups(sb, outerClassName, "this.parentMessage", groups, "");
        sb.append(generateEncoderVarData(groupClassName, varData, ""));
        sb.append("}\n");

        try (Writer writer = outputManager.createOutput(groupClassName))
        {
            writer.write(sb.toString());
        }
    }

    private void generateGroupDecoderClassHeader(
        final StringBuilder sb,
        final String groupName,
        final String parentMessageClassName,
        final Token groupToken,
        final Token dimensionsToken,
        final String indent)
    {
        final String dimensionsClassName = formatClassName(dimensionsToken.name());
        final int dimensionHeaderLen = dimensionsToken.encodedLength();

        generateGroupDecoderClassDeclaration(
            sb, groupToken, groupName, parentMessageClassName, indent, dimensionsClassName, dimensionHeaderLen);

        final int blockLength = groupToken.encodedLength();

        sb.append(String.format(
            indent + "    public readonly sbeHeaderSize: number = this.headerSize;\n" +
            indent + "    public readonly sbeBlockLength: number = %d;\n\n",
            blockLength));

        sb.append(String.format(
            indent + "    public wrap(buffer: %s): void {\n" +
            indent + "        this.buffer = buffer;\n" +
            indent + "        this.dimensions.wrap(buffer, this.parentMessage.getLimit());\n" +
            indent + "        this.blockLength = this.dimensions.blockLength();\n" +
            indent + "        this.elementCount = this.dimensions.numInGroup();\n" +
            indent + "        this.index = -1;\n" +
            indent + "        this.parentMessage.setLimit(this.parentMessage.getLimit() + this.headerSize);\n" +
            indent + "    }\n",
            readOnlyBuffer));

        sb.append(String.format("\n" +
            indent + "    public actingBlockLength(): number {\n" +
            indent + "        return this.blockLength;\n" +
            indent + "    }\n\n" +
            indent + "    public count(): number {\n" +
            indent + "        return this.elementCount;\n" +
            indent + "    }\n\n" +
            indent + "    public hasNext(): boolean {\n" +
            indent + "        return (this.index + 1) < this.elementCount;\n" +
            indent + "    }\n",
            formatClassName(groupName)));

        sb.append(String.format("\n" +
            indent + "    public next() {\n" +
            indent + "        if (this.index + 1 >= this.elementCount) {\n" +
            indent + "            throw new Error(\"No more elements.\");\n" +
            indent + "        }\n\n" +
            indent + "        this.offset = this.parentMessage.getLimit();\n" +
            indent + "        this.parentMessage.setLimit(this.offset + this.blockLength);\n" +
            indent + "        ++this.index;\n\n" +
            indent + "        return this;\n" +
            indent + "    }\n",
            formatClassName(groupName)));
    }

    private void generateGroupEncoderClassHeader(
        final StringBuilder sb,
        final String groupName,
        final String parentMessageClassName,
        final List<Token> tokens,
        final int index,
        final String ind)
    {
        final Token groupToken = tokens.get(index);
        final String dimensionsClassName = formatClassName(encoderName(tokens.get(index + 1).name()));
        final int dimensionHeaderSize = tokens.get(index + 1).encodedLength();

        generateGroupEncoderClassDeclaration(
            sb, groupToken, groupName, parentMessageClassName, ind, dimensionsClassName, dimensionHeaderSize);

        final int blockLength = tokens.get(index).encodedLength();

        sb.append(String.format(
            ind + "    public readonly sbeHeaderSize: number = this.headerSize;\n" +
            ind + "    public readonly sbeBlockLength: number = %d;\n\n",
            blockLength));

        final Token numInGroupToken = tokens.get(index + 3);

        sb.append(String.format(
            ind + "    public wrap(buffer: %2$s, count: number) {\n" +
            ind + "        if (count < %3$d || count > %4$d) {\n" +
            ind + "            throw new Error(\"count outside allowed range: count=\" + count);\n" +
            ind + "        }\n\n" +
            ind + "        this.buffer = buffer;\n" +
            ind + "        this.dimensions.wrap(buffer, this.parentMessage.getLimit());\n" +
            ind + "        this.dimensions.blockLength(%5$d);\n" +
            ind + "        this.dimensions.numInGroup(count);\n" +
            ind + "        this.index = -1;\n" +
            ind + "        this.elementCount = count;\n" +
            ind + "        this.parentMessage.setLimit(this.parentMessage.getLimit() + this.headerSize);\n" +
            ind + "    }\n\n",
            parentMessageClassName,
            mutableBuffer,
            numInGroupToken.encoding().applicableMinValue().longValue(),
            numInGroupToken.encoding().applicableMaxValue().longValue(),
            blockLength));

        sb.append(String.format("\n" +
            ind + "    public next(): %s {\n" +
            ind + "        if (this.index + 1 >= this.elementCount) {\n" +
            ind + "            throw new Error(\"No more elements.\");\n" +
            ind + "        }\n\n" +
            ind + "        this.offset = this.parentMessage.getLimit();\n" +
            ind + "        this.parentMessage.setLimit(this.offset + this.sbeBlockLength);\n" +
            ind + "        ++this.index;\n\n" +
            ind + "        return this;\n" +
            ind + "    }\n",
            formatClassName(encoderName(groupName))));
    }

    private void generateGroupDecoderClassDeclaration(
        final StringBuilder sb,
        final Token groupToken,
        final String groupName,
        final String parentMessageClassName,
        final String indent,
        final String dimensionsClassName,
        final int dimensionHeaderSize)
    {
        // TODO May suffer from group name collisons
        sb.append(String.format(
            "%1$s" +
            indent + "export class %2$s {\n" +
            indent + "    private readonly headerSize = %3$d;\n" +
            indent + "    private readonly dimensions = new %4$s();\n" +
            indent + "    private parentMessage: %5$s;\n" +
            indent + "    private buffer = new DataView(new ArrayBuffer(0));\n" +
            indent + "    private elementCount = 0;\n" +
            indent + "    private index = 0;\n" +
            indent + "    private offset = 0;\n" +
            indent + "    private blockLength = 0;\n\n" +
            indent + "    constructor(parentMessage: %5$s) {\n" +
            indent + "        this.parentMessage = parentMessage;\n" +
            indent + "    }\n\n",
            generateTypeJsdoc(indent, groupToken),
            groupName,
            dimensionHeaderSize,
            decoderName(dimensionsClassName),
            parentMessageClassName));
    }

    private void generateGroupEncoderClassDeclaration(
        final StringBuilder sb,
        final Token groupToken,
        final String groupName,
        final String parentMessageClassName,
        final String indent,
        final String dimensionsClassName,
        final int dimensionHeaderSize)
    {
        // TODO May suffer from group name collisons
        sb.append(String.format("\n" +
            "%1$s" +
            indent + "export class %2$s {\n" +
            indent + "    private readonly headerSize = %3$d;\n" +
            indent + "    private readonly dimensions = new %4$s();\n" +
            indent + "    private parentMessage: %5$s;\n" +
            indent + "    private buffer = new DataView(new ArrayBuffer(0));\n" + // TODO factor out
            indent + "    private elementCount = 0;\n" +
            indent + "    private index = 0;\n" +
            indent + "    private offset = 0;\n\n" +
            indent + "    constructor(parentMessage: %5$s) {\n" +
            indent + "        this.parentMessage = parentMessage;\n" +
            indent + "    }\n\n",
            generateTypeJsdoc(indent, groupToken),
            formatClassName(encoderName(groupName)),
            dimensionHeaderSize,
            dimensionsClassName,
            parentMessageClassName));
    }

    private CharSequence generateGroupEncoderProperty(
        final String parentMessage,
        final String groupName,
        final Token token,
        final String indent)
    {
        final StringBuilder sb = new StringBuilder();
        final String className = formatClassName(encoderName(groupName));
        final String propertyName = formatPropertyName(groupName);

        sb.append(String.format("\n" +
            indent + "    private %sCodec?: %s;\n",
            propertyName,
            className));

        sb.append(String.format("\n" +
            indent + "    public static %sId(): number {\n" +
            indent + "        return %d;\n" +
            indent + "    }\n",
            formatPropertyName(groupName),
            token.id()));

        sb.append(String.format("\n" +
            "%1$s" +
            indent + "    public %3$sCount(count: number): %2$s {\n" +
            indent + "        if (!this.%3$sCodec) {\n" +
            indent + "            this.%3$sCodec = new %2$s(%4$s);\n" +
            indent + "        }\n" +
            indent + "        this.%3$sCodec.wrap(this.buffer, count);\n" +
            indent + "        return this.%3$sCodec;\n" +
            indent + "    }\n",
            generateGroupEncodePropertyJsdoc(indent + INDENT, token, className),
            className,
            propertyName,
            parentMessage));

        return sb;
    }

    private CharSequence generateDecoderVarData(final List<Token> tokens, final String indent)
    {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0, size = tokens.size(); i < size;)
        {
            final Token token = tokens.get(i);
            if (token.signal() != Signal.BEGIN_VAR_DATA)
            {
                throw new IllegalStateException("tokens must begin with BEGIN_VAR_DATA: token=" + token);
            }

            generateFieldIdMethod(sb, token, indent);
            generateFieldSinceVersionMethod(sb, token, indent);

            final String characterEncoding = tokens.get(i + 3).encoding().characterEncoding();
            generateCharacterEncodingMethod(sb, token.name(), characterEncoding, indent);
            generateFieldMetaAttributeMethod(sb, token, indent);

            final String propertyName = Generators.toUpperFirstChar(token.name());
            final Token lengthToken = tokens.get(i + 2);
            final int sizeOfLengthField = lengthToken.encodedLength();
            final Encoding lengthEncoding = lengthToken.encoding();

            sb.append(String.format("\n" +
                indent + "    public static %sHeaderLength(): number {\n" +
                indent + "        return %d;\n" +
                indent + "    }\n",
                Generators.toLowerFirstChar(propertyName),
                sizeOfLengthField));

            sb.append(String.format("\n" +
                indent + "    public %sLength(): number {\n" +
                "%s" +
                indent + "        const limit = this.parentMessage.getLimit();\n" +
                indent + "        return %s;\n" +
                indent + "    }\n",
                Generators.toLowerFirstChar(propertyName),
                generateArrayFieldNotPresentCondition(token.version(), indent),
                generateGet(indent, "limit", lengthEncoding)));

            generateDataDecodeMethods(
                sb, token, propertyName, sizeOfLengthField, lengthEncoding, characterEncoding, indent);

            i += token.componentTokenCount();
        }

        return sb;
    }

    private CharSequence generateEncoderVarData(final String className, final List<Token> tokens, final String indent)
    {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0, size = tokens.size(); i < size;)
        {
            final Token token = tokens.get(i);
            if (token.signal() != Signal.BEGIN_VAR_DATA)
            {
                throw new IllegalStateException("tokens must begin with BEGIN_VAR_DATA: token=" + token);
            }

            generateFieldIdMethod(sb, token, indent);
            final String characterEncoding = tokens.get(i + 3).encoding().characterEncoding();
            generateCharacterEncodingMethod(sb, token.name(), characterEncoding, indent);
            generateFieldMetaAttributeMethod(sb, token, indent);

            final String propertyName = Generators.toUpperFirstChar(token.name());
            final Token lengthToken = tokens.get(i + 2);
            final int sizeOfLengthField = lengthToken.encodedLength();
            final Encoding lengthEncoding = lengthToken.encoding();
            final int maxLengthValue = (int)lengthEncoding.applicableMaxValue().longValue();

            sb.append(String.format("\n" +
                indent + "    public static %sHeaderLength(): number {\n" +
                indent + "        return %d;\n" +
                indent + "    }\n",
                Generators.toLowerFirstChar(propertyName),
                sizeOfLengthField));

            generateDataEncodeMethods(
                sb,
                propertyName,
                sizeOfLengthField,
                maxLengthValue,
                lengthEncoding,
                characterEncoding,
                className,
                indent);

            i += token.componentTokenCount();
        }

        return sb;
    }

    /* TODO
    private CharSequence generateEnumBody(final Token token, final String enumName)
    {
        final String javaEncodingType = primitiveTypeName(token);

        return String.format(
            "    private value: %1$s;\n\n" +
            "    %2$s(final %1$s value)\n" +
            "    {\n" +
            "        this.value = value;\n" +
            "    }\n\n" +
            "    public %1$s value()\n" +
            "    {\n" +
            "        return value;\n" +
            "    }\n\n",
            javaEncodingType,
            enumName);
    }

    private CharSequence generateEnumLookupMethod(final List<Token> tokens, final String enumName)
    {
        final StringBuilder sb = new StringBuilder();

        final PrimitiveType primitiveType = tokens.get(0).encoding().primitiveType();
        sb.append(String.format(
            "    public static %s get(final %s value)\n" +
            "    {\n" +
            "        switch (value)\n" +
            "        {\n",
            enumName,
            typeScriptTypeName(primitiveType)));

        for (final Token token : tokens)
        {
            sb.append(String.format(
                "            case %s: return %s;\n",
                token.encoding().constValue().toString(),
                token.name()));
        }

        final String handleUnknownLogic = shouldDecodeUnknownEnumValues ?
            INDENT + INDENT + "return SBE_UNKNOWN;\n" :
            INDENT + INDENT + "throw new IllegalArgumentException(\"Unknown value: \" + value);\n";

        sb.append(String.format(
            "        }\n\n" +
            "        if (%s == value)\n" +
            "        {\n" +
            "            return NULL_VAL;\n" +
            "        }\n\n" +
            "%s" +
            "    }\n",
            generateLiteral(primitiveType, tokens.get(0).encoding().applicableNullValue().toString()),
            handleUnknownLogic));

        return sb;
    }
    */

    private void generateDataDecodeMethods(
        final StringBuilder sb,
        final Token token,
        final String propertyName,
        final int sizeOfLengthField,
        final Encoding encoding,
        final String characterEncoding,
        final String indent)
    {
        generateDataTypedDecoder(
            sb,
            token,
            propertyName,
            sizeOfLengthField,
            encoding,
            indent);

        if (null != characterEncoding)
        {
            sb.append(String.format("\n" +
                indent + "    public %1$s(): string {\n" +
                "%2$s" +
                indent + "        const headerLength = %3$d;\n" +
                indent + "        const limit = this.parentMessage.getLimit();\n" +
                indent + "        const dataLength = %4$s;\n" +
                indent + "        this.parentMessage.setLimit(limit + headerLength + dataLength);\n\n" +
                indent + "        if (0 === dataLength) {\n" +
                indent + "            return \"\";\n" +
                indent + "        }\n\n" +
                indent + "        const underlyingBuffer = this.buffer.buffer;\n" +
                indent + "        const dataOffset = this.buffer.byteOffset + limit + headerLength;\n" +
                indent + "        const dataView = new DataView(underlyingBuffer, dataOffset, dataLength);\n" +
                indent + "        const decoder = new TextDecoder(\"%5$s\");\n" +
                indent + "        return decoder.decode(dataView);\n" +
                indent + "    }\n",
                formatPropertyName(propertyName),
                generateStringNotPresentCondition(token.version(), indent),
                sizeOfLengthField,
                generateGet(indent, "limit", encoding),
                characterEncoding));
        }
    }

    private void generateDataEncodeMethods(
        final StringBuilder sb,
        final String propertyName,
        final int sizeOfLengthField,
        final int maxLengthValue,
        final Encoding encoding,
        final String characterEncoding,
        final String className,
        final String indent)
    {
        generateDataTypedEncoder(
            sb,
            className,
            propertyName,
            sizeOfLengthField,
            maxLengthValue,
            encoding,
            indent);

        if (null != characterEncoding)
        {
            generateCharArrayEncodeMethods(
                sb,
                propertyName,
                sizeOfLengthField,
                maxLengthValue,
                encoding,
                characterEncoding,
                className,
                indent);
        }
    }

    private void generateCharArrayEncodeMethods(
        final StringBuilder sb,
        final String propertyName,
        final int sizeOfLengthField,
        final int maxLengthValue,
        final Encoding encoding,
        final String characterEncoding,
        final String className,
        final String indent)
    {
        sb.append(String.format("\n" +
            indent + "    public %2$s(value: string): %1$s {\n" +
            indent + "        const encoder = new TextEncoder(); // Bug: should be \"%3$s\" not \"UTF-8\"\n" +
            indent + "        const bytes = encoder.encode(value);\n" +
            indent + "        const length = bytes.length;\n" +
            indent + "        if (length > %4$d) {\n" +
            indent + "            throw new Error(\"length > maxValue for type: \" + length);\n" +
            indent + "        }\n\n" +
            indent + "        const headerLength = %5$d;\n" +
            indent + "        const limit = this.parentMessage.getLimit();\n" +
            indent + "        this.parentMessage.setLimit(limit + headerLength + length);\n" +
            indent + "        %6$s;\n" +
            indent + "        const underlyingBuffer = this.buffer.buffer;\n" +
            indent + "        const dataOffset = this.buffer.byteOffset + limit + headerLength;\n" +
            indent + "        const view = new Uint8Array(underlyingBuffer, dataOffset, length);\n" +
            indent + "        view.set(bytes);\n\n" +
            indent + "        return this;\n" +
            indent + "    }\n",
            className,
            formatPropertyName(propertyName),
            characterEncoding,
            maxLengthValue,
            sizeOfLengthField,
            generatePut(indent + "       ", "limit", "length", encoding)));
    }

    private void generateDataTypedDecoder(
        final StringBuilder sb,
        final Token token,
        final String propertyName,
        final int sizeOfLengthField,
        final Encoding lengthEncoding,
        final String indent)
    {
        sb.append(String.format("\n" +
            indent + "    public get%s(dst: Uint8Array, dstOffset: number, length: number): number {\n" +
            "%s" +
            indent + "        const headerLength = %d;\n" +
            indent + "        const limit = this.parentMessage.getLimit();\n" +
            indent + "        const dataLength = %s;\n" +
            indent + "        const bytesCopied = Math.min(length, dataLength);\n" +
            indent + "        this.parentMessage.setLimit(limit + headerLength + dataLength);\n" +
            indent + "        const underlyingBuffer = this.buffer.buffer;\n" +
            indent + "        const dataOffset = this.buffer.byteOffset + limit + headerLength;\n" +
            indent + "        const view = new Uint8Array(underlyingBuffer, dataOffset, dataLength);\n" +
            indent + "        dst.set(view, dstOffset);\n\n" +
            indent + "        return bytesCopied;\n" +
            indent + "    }\n",
            propertyName,
            generateArrayFieldNotPresentCondition(token.version(), indent),
            sizeOfLengthField,
            generateGet(indent, "limit", lengthEncoding)));
    }

    private void generateDataTypedEncoder(
        final StringBuilder sb,
        final String className,
        final String propertyName,
        final int sizeOfLengthField,
        final int maxLengthValue,
        final Encoding lengthEncoding,
        final String indent)
    {
        sb.append(String.format("\n" +
            indent + "    public put%2$s(src: Uint8Array, srcOffset: number, length: number): %1$s {\n" +
            indent + "        if (length > %3$d) {\n" +
            indent + "            throw new Error(\"length > maxValue for type: \" + length);\n" +
            indent + "        }\n\n" +
            indent + "        const headerLength = %4$d;\n" +
            indent + "        const limit = this.parentMessage.getLimit();\n" +
            indent + "        this.parentMessage.setLimit(limit + headerLength + length);\n" +
            indent + "        %5$s;\n" +
            indent + "        const underlyingBuffer = this.buffer.buffer;\n" +
            indent + "        const dataOffset = this.buffer.byteOffset + limit + headerLength;\n" +
            indent + "        const dataView = new Uint8Array(underlyingBuffer, dataOffset, length);\n" +
            indent + "        const srcView = new Uint8Array(src.buffer, src.byteOffset + srcOffset, length);\n" +
            indent + "        dataView.set(srcView);\n\n" +
            indent + "        return this;\n" +
            indent + "    }\n",
            className,
            propertyName,
            maxLengthValue,
            sizeOfLengthField,
            generatePut(indent + "        ", "limit", "length", lengthEncoding)));
    }

    private void generateBitSet(final List<Token> tokens) throws IOException
    {
        final Token token = tokens.get(0);
        final String bitSetName = formatClassName(token.applicableTypeName());
        final String decoderName = decoderName(bitSetName);
        final String encoderName = encoderName(bitSetName);
        final List<Token> messageBody = getMessageBody(tokens);

        try (Writer out = outputManager.createOutput(decoderName))
        {
            generateFixedFlyweightHeader(token, decoderName, out, readOnlyBuffer);
            out.append(generateChoiceDecoders(messageBody));
            // TODO
            // out.append(generateChoiceDisplay(messageBody));
            out.append("}\n");
        }

        try (Writer out = outputManager.createOutput(encoderName))
        {
            generateFixedFlyweightHeader(token, encoderName, out, mutableBuffer);
            out.append(generateChoiceClear(encoderName, token));
            out.append(generateChoiceEncoders(encoderName, messageBody));
            out.append("}\n");
        }
    }

    private void generateFixedFlyweightHeader(
        final Token token,
        final String typeName,
        final Writer out,
        final String buffer) throws IOException
    {
        out.append(generateFileHeader());
        out.append(generateDeclaration(typeName, "", token));
        out.append(generateFixedFlyweightCode(typeName, token.encodedLength(), buffer));
    }

    private void generateCompositeFlyweightHeader(
        final Token token,
        final String typeName,
        final Writer out,
        final String buffer,
        final String implementsString) throws IOException
    {
        out.append(generateFileHeader());
        out.append(generateDeclaration(typeName, implementsString, token));
        out.append(generateCompositeFlyweightCode(typeName, token.encodedLength(), buffer));
    }

    private void generateEnum(final List<Token> tokens) throws IOException
    {
        final Token enumToken = tokens.get(0);
        final String enumName = formatClassName(enumToken.applicableTypeName());

        try (Writer out = outputManager.createOutput(enumName))
        {
            out.append(generateEnumFileHeader());
            out.append(generateEnumDeclaration(enumName, enumToken));

            out.append(generateEnumValues(getMessageBody(tokens)));
            // TODO revisit
            // out.append(generateEnumBody(enumToken, enumName));
            // out.append(generateEnumLookupMethod(getMessageBody(tokens), enumName));

            out.append("}\n");
        }
    }

    private void generateComposite(final List<Token> tokens) throws IOException
    {
        final Token token = tokens.get(0);
        final String compositeName = formatClassName(token.applicableTypeName());
        final String decoderName = decoderName(compositeName);
        final String encoderName = encoderName(compositeName);

        try (Writer out = outputManager.createOutput(decoderName))
        {
            final String implementsString = implementsInterface(GEN_COMPOSITE_DECODER_FLYWEIGHT);
            generateCompositeFlyweightHeader(
                token, decoderName, out, readOnlyBuffer, implementsString);

            for (int i = 1, end = tokens.size() - 1; i < end;)
            {
                final Token encodingToken = tokens.get(i);
                final String propertyName = formatPropertyName(encodingToken.name());
                final String typeName = formatClassName(decoderName(encodingToken.applicableTypeName()));

                final StringBuilder sb = new StringBuilder();
                generateEncodingOffsetMethod(sb, propertyName, encodingToken.offset(), BASE_INDENT);
                generateEncodingLengthMethod(sb, propertyName, encodingToken.encodedLength(), BASE_INDENT);
                generateFieldSinceVersionMethod(sb, encodingToken, BASE_INDENT);

                switch (encodingToken.signal())
                {
                    case ENCODING:
                        out.append(sb).append(generatePrimitiveDecoder(
                            true, encodingToken.name(), encodingToken, encodingToken, BASE_INDENT));
                        break;

                    case BEGIN_ENUM:
                        out.append(sb).append(generateEnumDecoder(
                            true, encodingToken, propertyName, encodingToken, BASE_INDENT));
                        break;

                    case BEGIN_SET:
                        out.append(sb).append(generateBitSetProperty(
                            true, DECODER, propertyName, encodingToken, encodingToken, BASE_INDENT, typeName));
                        break;

                    case BEGIN_COMPOSITE:
                        out.append(sb).append(generateCompositeProperty(
                            true, DECODER, propertyName, encodingToken, encodingToken, BASE_INDENT, typeName));
                        break;
                }

                i += encodingToken.componentTokenCount();
            }

            // TODO
            // out.append(generateCompositeDecoderDisplay(tokens, BASE_INDENT));
            out.append("}\n");
        }

        try (Writer out = outputManager.createOutput(encoderName))
        {
            final String implementsString = implementsInterface(GEN_COMPOSITE_ENCODER_FLYWEIGHT);
            generateCompositeFlyweightHeader(token, encoderName, out, mutableBuffer, implementsString);

            for (int i = 1, end = tokens.size() - 1; i < end;)
            {
                final Token encodingToken = tokens.get(i);
                final String propertyName = formatPropertyName(encodingToken.name());
                final String typeName = formatClassName(encoderName(encodingToken.applicableTypeName()));

                final StringBuilder sb = new StringBuilder();
                generateEncodingOffsetMethod(sb, propertyName, encodingToken.offset(), BASE_INDENT);
                generateEncodingLengthMethod(sb, propertyName, encodingToken.encodedLength(), BASE_INDENT);

                switch (encodingToken.signal())
                {
                    case ENCODING:
                        out.append(sb).append(generatePrimitiveEncoder(
                            encoderName, encodingToken.name(), encodingToken, BASE_INDENT));
                        break;

                    case BEGIN_ENUM:
                        out.append(sb).append(generateEnumEncoder(
                            encoderName, propertyName, encodingToken, BASE_INDENT));
                        break;

                    case BEGIN_SET:
                        out.append(sb).append(generateBitSetProperty(
                            true, ENCODER, propertyName, encodingToken, encodingToken, BASE_INDENT, typeName));
                        break;

                    case BEGIN_COMPOSITE:
                        out.append(sb).append(generateCompositeProperty(
                            true, ENCODER, propertyName, encodingToken, encodingToken, BASE_INDENT, typeName));
                        break;
                }

                i += encodingToken.componentTokenCount();
            }

            // TODO
            // out.append(generateCompositeEncoderDisplay(decoderName, BASE_INDENT));
            out.append("}\n");
        }
    }

    private CharSequence generateChoiceClear(final String bitSetClassName, final Token token)
    {
        final StringBuilder sb = new StringBuilder();

        final Encoding encoding = token.encoding();
        final String literalValue = generateLiteral(encoding.primitiveType(), "0");

        sb.append(String.format("\n" +
            "    public clear(): %s {\n" +
            "        %s;\n" +
            "        return this;\n" +
            "    }\n",
            bitSetClassName,
            generatePut("        ", "this.offset", literalValue, encoding)));

        return sb;
    }

    private CharSequence generateChoiceDecoders(final List<Token> tokens)
    {
        return concatTokens(
            tokens,
            Signal.CHOICE,
            (token) ->
            {
                final String choiceName = formatPropertyName(token.name());
                final Encoding encoding = token.encoding();
                final String choiceBitIndex = encoding.constValue().toString();
                final String byteOrderStr = byteOrderString(encoding);
                final PrimitiveType primitiveType = encoding.primitiveType();
                final String argType = bitsetArgType(primitiveType);

                return String.format("\n" +
                    "%1$s" +
                    "    public %2$s(): boolean {\n" +
                    "        return %3$s;\n" +
                    "    }\n\n" +
                    "    public static %2$s(value: %4$s): boolean {\n" +
                    "        return %5$s;\n" +
                    "    }\n",
                    generateOptionDecodeJsdoc(INDENT, token),
                    choiceName,
                    generateChoiceGet(primitiveType, choiceBitIndex, byteOrderStr),
                    argType,
                    generateStaticChoiceGet(primitiveType, choiceBitIndex));
            });
    }

    private CharSequence generateChoiceEncoders(final String bitSetClassName, final List<Token> tokens)
    {
        return concatTokens(
            tokens,
            Signal.CHOICE,
            (token) ->
            {
                final String choiceName = formatPropertyName(token.name());
                final Encoding encoding = token.encoding();
                final String choiceBitIndex = encoding.constValue().toString();
                final String byteOrderStr = byteOrderString(encoding);
                final PrimitiveType primitiveType = encoding.primitiveType();
                final String argType = bitsetArgType(primitiveType);

                return String.format("\n" +
                    "%1$s" +
                    "    public %3$s(value: boolean): %2$s {\n" +
                    "%4$s\n" +
                    "        return this;\n" +
                    "    }\n\n" +
                    "    public static %3$s(bits: %5$s, value: boolean): %5$s {\n" +
                    "%6$s" +
                    "    }\n",
                    generateOptionEncodeJsdoc(INDENT, token),
                    bitSetClassName,
                    choiceName,
                    generateChoicePut(encoding.primitiveType(), choiceBitIndex, byteOrderStr),
                    argType,
                    generateStaticChoicePut(encoding.primitiveType(), choiceBitIndex));
            });
    }

    private String bitsetArgType(final PrimitiveType primitiveType)
    {
        switch (primitiveType)
        {
            case UINT8:
            case UINT16:
            case UINT32:
            case UINT64:
                return "number";

            default:
                throw new IllegalStateException("Invalid type: " + primitiveType);
        }
    }

    private CharSequence generateEnumValues(final List<Token> tokens)
    {
        final StringBuilder sb = new StringBuilder();

        for (final Token token : tokens)
        {
            final Encoding encoding = token.encoding();
            final CharSequence constVal = generateLiteral(encoding.primitiveType(), encoding.constValue().toString());
            sb.append(generateTypeJsdoc(INDENT, token));
            sb.append(INDENT).append(token.name()).append(" = ").append(constVal).append(",\n\n");
        }

        final Token token = tokens.get(0);
        final Encoding encoding = token.encoding();
        final CharSequence nullVal = generateLiteral(
            encoding.primitiveType(), encoding.applicableNullValue().toString());

        if (shouldDecodeUnknownEnumValues)
        {
            sb.append(INDENT).append("/**\n");
            sb.append(INDENT).append(" * To be used to represent a not known value from a later version.\n");
            sb.append(INDENT).append(" */\n");
            sb.append(INDENT).append("SBE_UNKNOWN").append(" = ").append(nullVal).append(",\n\n");
        }

        sb.append(INDENT).append("/**\n");
        sb.append(INDENT).append(" * To be used to represent not present or null.\n");
        sb.append(INDENT).append(" */\n");
        sb.append(INDENT).append("NULL_VAL").append(" = ").append(nullVal).append(",\n\n");

        return sb;
    }

    private CharSequence interfaceImportLine()
    {
        if (!shouldGenerateInterfaces)
        {
            return "\n";
        }

        // TODO
        return String.format("import %s.*;\n\n", JAVA_INTERFACE_PACKAGE);
    }

    private CharSequence generateFileHeader()
    {
        return String.format(
            "/* Generated SBE (Simple Binary Encoding) message codec */\n" +
            "%s",
            interfaceImportLine());
    }

    private CharSequence generateMainHeader()
    {
        return generateFileHeader();
    }

    private void generateMetaAttributeEnum() throws IOException
    {
        try (Writer out = outputManager.createOutput(META_ATTRIBUTE_ENUM))
        {
            out.append(
                "/* Generated SBE (Simple Binary Encoding) message codec */\n\n" +
                "export enum MetaAttribute {\n" +
                "    EPOCH,\n" +
                "    TIME_UNIT,\n" +
                "    SEMANTIC_TYPE,\n" +
                "    PRESENCE,\n" +
                "}\n");
        }
    }

    private CharSequence generatePrimitiveDecoder(
        final boolean inComposite,
        final String propertyName,
        final Token propertyToken,
        final Token encodingToken,
        final String indent)
    {
        final StringBuilder sb = new StringBuilder();

        sb.append(generatePrimitiveFieldMetaData(propertyName, encodingToken, indent));

        if (encodingToken.isConstantEncoding())
        {
            sb.append(generateConstPropertyMethods(propertyName, encodingToken, indent));
        }
        else
        {
            sb.append(generatePrimitivePropertyDecodeMethods(
                inComposite, propertyName, propertyToken, encodingToken, indent));
        }

        return sb;
    }

    private CharSequence generatePrimitiveEncoder(
        final String containingClassName, final String propertyName, final Token token, final String indent)
    {
        final StringBuilder sb = new StringBuilder();

        sb.append(generatePrimitiveFieldMetaData(propertyName, token, indent));

        if (!token.isConstantEncoding())
        {
            sb.append(generatePrimitivePropertyEncodeMethods(containingClassName, propertyName, token, indent));
        }
        else
        {
            sb.append(generateConstPropertyMethods(propertyName, token, indent));
        }

        return sb;
    }

    private CharSequence generatePrimitivePropertyDecodeMethods(
        final boolean inComposite,
        final String propertyName,
        final Token propertyToken,
        final Token encodingToken,
        final String indent)
    {
        return encodingToken.matchOnLength(
            () -> generatePrimitivePropertyDecode(inComposite, propertyName, propertyToken, encodingToken, indent),
            () -> generatePrimitiveArrayPropertyDecode(
                inComposite, propertyName, propertyToken, encodingToken, indent));
    }

    private CharSequence generatePrimitivePropertyEncodeMethods(
        final String containingClassName, final String propertyName, final Token token, final String indent)
    {
        return token.matchOnLength(
            () -> generatePrimitivePropertyEncode(containingClassName, propertyName, token, indent),
            () -> generatePrimitiveArrayPropertyEncode(containingClassName, propertyName, token, indent));
    }

    private CharSequence generatePrimitiveFieldMetaData(
        final String propertyName, final Token token, final String indent)
    {
        final StringBuilder sb = new StringBuilder();

        final PrimitiveType primitiveType = token.encoding().primitiveType();
        final String tsTypeName = typeScriptTypeName(primitiveType);

        sb.append(String.format("\n" +
            indent + "    public static %sNullValue(): %s {\n" +
            indent + "        return %s;\n" +
            indent + "    }\n",
            propertyName,
            tsTypeName,
            generateLiteral(primitiveType, token.encoding().applicableNullValue().toString())));

        sb.append(String.format("\n" +
            indent + "    public static %sMinValue(): %s {\n" +
            indent + "        return %s;\n" +
            indent + "    }\n",
            propertyName,
            tsTypeName,
            generateLiteral(primitiveType, token.encoding().applicableMinValue().toString())));

        sb.append(String.format(
            "\n" +
            indent + "    public static %sMaxValue(): %s {\n" +
            indent + "        return %s;\n" +
            indent + "    }\n",
            propertyName,
            tsTypeName,
            generateLiteral(primitiveType, token.encoding().applicableMaxValue().toString())));

        return sb;
    }

    private CharSequence generatePrimitivePropertyDecode(
        final boolean inComposite,
        final String propertyName,
        final Token propertyToken,
        final Token encodingToken,
        final String indent)
    {
        final Encoding encoding = encodingToken.encoding();
        final String tsTypeName = typeScriptTypeName(encoding.primitiveType());

        final int offset = encodingToken.offset();

        return String.format(
            "\n" +
            indent + "    public %s(): %s {\n" +
            "%s" +
            indent + "        return %s;\n" +
            indent + "    }\n",
            propertyName,
            tsTypeName,
            generateFieldNotPresentCondition(inComposite, propertyToken.version(), encoding, indent),
            generateGet(indent, "this.offset + " + offset, encoding));
    }

    private CharSequence generatePrimitivePropertyEncode(
        final String containingClassName, final String propertyName, final Token token, final String indent)
    {
        final Encoding encoding = token.encoding();
        final String tsTypeName = typeScriptTypeName(encoding.primitiveType());
        final int offset = token.offset();

        return String.format(
            "\n" +
            indent + "    public %s(value: %s): %s {\n" +
            indent + "        %s;\n" +
            indent + "        return this;\n" +
            indent + "    }\n",
            propertyName,
            tsTypeName,
            formatClassName(containingClassName),
            generatePut(indent + "        ", "this.offset + " + offset, "value", encoding));
    }

    private CharSequence generateFieldNotPresentCondition(
        final boolean inComposite, final int sinceVersion, final Encoding encoding, final String indent)
    {
        if (inComposite || 0 == sinceVersion)
        {
            return "";
        }

        return String.format(
            indent + "        if (this.parentMessage.actingVersion < %d) {\n" +
            indent + "            return %s;\n" +
            indent + "        }\n\n",
            sinceVersion,
            generateLiteral(encoding.primitiveType(), encoding.applicableNullValue().toString()));
    }

    private CharSequence generatePrimitiveArrayPropertyDecode(
        final boolean inComposite,
        final String propertyName,
        final Token propertyToken,
        final Token encodingToken,
        final String indent)
    {
        final Encoding encoding = encodingToken.encoding();
        final String tsTypeName = typeScriptTypeName(encoding.primitiveType());
        final int offset = encodingToken.offset();
        final int fieldLength = encodingToken.arrayLength();
        final int typeSize = sizeOfPrimitive(encoding);

        final StringBuilder sb = new StringBuilder();

        generateArrayLengthMethod(propertyName, indent, fieldLength, sb);

        sb.append(String.format(
            indent + "    public get%s(index: number): %s {\n" +
            indent + "        if (index < 0 || index >= %d) {\n" +
            indent + "            throw new Error(\"index out of range: index=\" + index);\n" +
            indent + "        }\n\n" +
            "%s" +
            indent + "        const pos = this.offset + %d + (index * %d);\n\n" +
            indent + "        return %s;\n" +
            indent + "    }\n",
            Generators.toUpperFirstChar(propertyName),
            tsTypeName,
            fieldLength,
            generateFieldNotPresentCondition(inComposite, propertyToken.version(), encoding, indent),
            offset,
            typeSize,
            generateGet(indent, "pos", encoding)));

        if (encoding.primitiveType() == PrimitiveType.CHAR)
        {
            generateCharacterEncodingMethod(sb, propertyName, encoding.characterEncoding(), indent);

            sb.append(String.format("\n" +
                indent + "    public get%sBytes(dst: Uint8Array, dstOffset: number): number {\n" +
                indent + "        const length = %d;\n" +
                indent + "        if (dstOffset < 0 || dstOffset > (dst.length - length)) {\n" +
                indent + "            throw new Error(" +
                "\"Copy will go out of range: offset=\" + dstOffset);\n" +
                indent + "        }\n\n" +
                "%s" +
                indent + "        const underlyingBuffer = this.buffer.buffer;\n" +
                indent + "        const dataOffset = this.buffer.byteOffset + this.offset + %d;\n" +
                indent + "        const dataView = new Uint8Array(underlyingBuffer, dataOffset, length);\n" +
                indent + "        dst.set(dataView, dstOffset);\n" +
                indent + "        return length;\n" +
                indent + "    }\n",
                Generators.toUpperFirstChar(propertyName),
                fieldLength,
                generateArrayFieldNotPresentCondition(propertyToken.version(), indent),
                offset));

            sb.append(String.format("\n" +
                indent + "    public %s(): string {\n" +
                "%s" +
                indent + "        const dataLength = %d;\n" +
                indent + "        const underlyingBuffer = this.buffer.buffer;\n" +
                indent + "        const dataOffset = this.buffer.byteOffset + this.offset + %d;\n" +
                indent + "        const dataView = new DataView(underlyingBuffer, dataOffset, dataLength);\n" +
                indent + "        const decoder = new TextDecoder(\"%5$s\");\n" +
                indent + "        return decoder.decode(dataView);\n\n" +
                indent + "    }\n",
                formatPropertyName(propertyName),
                generateStringNotPresentCondition(propertyToken.version(), indent),
                fieldLength,
                offset,
                encoding.characterEncoding()));
        }

        return sb;
    }

    private String byteOrderString(final Encoding encoding)
    {
        final String isLittleEndian = encoding.byteOrder().equals(ByteOrder.LITTLE_ENDIAN) ? "true" : "false";
        return sizeOfPrimitive(encoding) == 1 ? "" : ", " + isLittleEndian;
    }

    private CharSequence generatePrimitiveArrayPropertyEncode(
        final String containingClassName, final String propertyName, final Token token, final String indent)
    {
        final Encoding encoding = token.encoding();
        final String tsTypeName = typeScriptTypeName(encoding.primitiveType());
        final int offset = token.offset();
        final int fieldLength = token.arrayLength();
        final int typeSize = sizeOfPrimitive(encoding);

        final StringBuilder sb = new StringBuilder();

        generateArrayLengthMethod(propertyName, indent, fieldLength, sb);

        sb.append(String.format(
            indent + "    public set%sByte(index: number, value: %s): void {\n" +
            indent + "        if (index < 0 || index >= %d) {\n" +
            indent + "            throw new Error(\"index out of range: index=\" + index);\n" +
            indent + "        }\n\n" +
            indent + "        const pos = this.offset + %d + (index * %d);\n" +
            indent + "        %s;\n" +
            indent + "    }\n",
            Generators.toUpperFirstChar(propertyName),
            tsTypeName,
            fieldLength,
            offset,
            typeSize,
            generatePut(indent + "        ", "pos", "value", encoding)));

        if (encoding.primitiveType() == PrimitiveType.CHAR)
        {
            generateCharArrayEncodeMethods(
                containingClassName, propertyName, indent, encoding, offset, fieldLength, sb);
        }

        return sb;
    }

    private void generateCharArrayEncodeMethods(
        final String containingClassName,
        final String propertyName,
        final String indent,
        final Encoding encoding,
        final int offset,
        final int fieldLength,
        final StringBuilder sb)
    {
        generateCharacterEncodingMethod(sb, propertyName, encoding.characterEncoding(), indent);

        sb.append(String.format("\n" +
            indent + "    public put%s(src: Uint8Array, srcOffset: number): %s {\n" +
            indent + "        const dataLength = %d;\n" +
            indent + "        if (srcOffset < 0 || srcOffset > (src.length - dataLength)) {\n" +
            indent + "            throw new Error(" +
            "\"Copy will go out of range: offset=\" + srcOffset);\n" +
            indent + "        }\n\n" +
            indent + "        const underlyingBuffer = this.buffer.buffer;\n" +
            indent + "        const dataOffset = this.buffer.byteOffset + this.offset + %d;\n" +
            indent + "        const srcView = new Uint8Array(src.buffer, src.byteOffset + srcOffset, dataLength);\n" +
            indent + "        const dataView = new Uint8Array(underlyingBuffer, dataOffset, dataLength);\n" +
            indent + "        dataView.set(srcView);\n\n" +
            indent + "        return this;\n" +
            indent + "    }\n",
            Generators.toUpperFirstChar(propertyName),
            formatClassName(containingClassName),
            fieldLength,
            offset));

        sb.append(String.format("\n" +
            indent + "    public %1$s(src: string): %2$s {\n" +
            indent + "        const length = %3$d;\n" +
            indent + "        const encoder = new TextEncoder(); // Bug: should be \"%4$s\" not \"UTF-8\"\n" + // TODO
            indent + "        const bytes = encoder.encode(src);\n" +
            indent + "        if (bytes.length > length) {\n" +
            indent + "            throw new Error(" +
            "\"String too large for copy: byte length=\" + bytes.length);\n" +
            indent + "        }\n\n" +
            indent + "        const underlyingBuffer = this.buffer.buffer;\n" +
            indent + "        const dataOffset = this.buffer.byteOffset + this.offset + %5$d;\n" +
            indent + "        const dataView = new DataView(underlyingBuffer, dataOffset, length);\n" +
            indent + "        const dst = new Uint8Array(underlyingBuffer, dataOffset, length);\n" +
            indent + "        dst.set(bytes);\n\n" +
            indent + "        for (let i = bytes.length; i < length; ++i) {\n" +
            indent + "            dataView.setUint8(i, 0);\n" +
            indent + "        }\n\n" +
            indent + "        return this;\n" +
            indent + "    }\n",
            propertyName,
            formatClassName(containingClassName),
            fieldLength,
            encoding.characterEncoding(),
            offset));
    }

    private CharSequence generateConstPropertyMethods(
        final String propertyName, final Token token, final String indent)
    {
        final Encoding encoding = token.encoding();
        if (encoding.primitiveType() != PrimitiveType.CHAR)
        {
            return String.format("\n" +
                indent + "    public %s(): %s {\n" +
                indent + "        return %s;\n" +
                indent + "    }\n",
                propertyName,
                typeScriptTypeName(encoding.primitiveType()),
                generateLiteral(encoding.primitiveType(), encoding.constValue().toString()));
        }

        final StringBuilder sb = new StringBuilder();

        final String tsTypeName = typeScriptTypeName(encoding.primitiveType());
        final byte[] constBytes = encoding.constValue().byteArrayValue(encoding.primitiveType());
        final CharSequence values = generateByteLiteralList(
            encoding.constValue().byteArrayValue(encoding.primitiveType()));

        sb.append(String.format(
            "\n" +
            indent + "    private readonly %sBytes = [%s];\n",
            propertyName,
            values));

        generateArrayLengthMethod(propertyName, indent, constBytes.length, sb);

        sb.append(String.format(
            indent + "    public get%sByte(index: number): %s {\n" +
            indent + "        return this.%sBytes[index];\n" +
            indent + "    }\n\n",
            Generators.toUpperFirstChar(propertyName),
            tsTypeName,
            propertyName));

        sb.append(String.format(
            indent + "    public get%s(dst: Uint8Array, offset: number, length: number): number {\n" +
            indent + "        const bytesCopied = Math.min(length, %d);\n" +
            indent + "        const dstView = new Uint8Array(dst.buffer, dst.byteOffset + offset, bytesCopied);\n" +
            indent + "        dstView.set(this.%sBytes);\n\n" +
            indent + "        return bytesCopied;\n" +
            indent + "    }\n",
            Generators.toUpperFirstChar(propertyName),
            constBytes.length,
            propertyName));

        sb.append(String.format("\n" +
            indent + "    public %s(): string {\n" +
            indent + "        return \"%s\";\n" +
            indent + "    }\n",
            propertyName,
            encoding.constValue()));

        return sb;
    }

    private CharSequence generateCompositeFlyweightCode(
        final String className, final int size, final String bufferImplementation)
    {
        return String.format(
            "    public readonly encodedLength = %2$d;\n" +
            "    public readonly isLittleEndian = %4$s;\n\n" +
            "    private offset = 0;\n" +
            "    private buffer = new DataView(new ArrayBuffer(0));\n\n" +
            "    public wrap(buffer: %3$s, offset: number): %1$s {\n" +
            "        this.buffer = buffer;\n" +
            "        this.offset = offset;\n\n" +
            "        return this;\n" +
            "    }\n\n" +
            "    public getBuffer(): %3$s {\n" +
            "        return this.buffer;\n" +
            "    }\n\n" +
            "    public getOffset(): number {\n" +
            "        return this.offset;\n" +
            "    }\n\n" +
            "    public getEncodedLength(): number {\n" +
            "        return this.encodedLength;\n" +
            "    }\n",
            className,
            size,
            bufferImplementation,
            ir.byteOrder().equals(ByteOrder.LITTLE_ENDIAN));
    }

    private CharSequence generateDecoderFlyweightCode(final String className, final Token token)
    {
        final String wrapMethod = String.format(
            "    public wrap(" +
            "buffer: %2$s, offset: number, actingBlockLength: number, actingVersion: number): %1$s {\n" +
            "        this.buffer = buffer;\n" +
            "        this.offset = offset;\n" +
            "        this.actingBlockLength = actingBlockLength;\n" +
            "        this.actingVersion = actingVersion;\n" +
            "        this.setLimit(offset + actingBlockLength);\n\n" +
            "        return this;\n" +
            "    }\n\n",
            className,
            readOnlyBuffer);

        return generateFlyweightCode(DECODER, className, token, wrapMethod, readOnlyBuffer);
    }

    private CharSequence generateFlyweightCode(
        final CodecType codecType,
        final String className,
        final Token token,
        final String wrapMethod,
        final String bufferImplementation)
    {
        final HeaderStructure headerStructure = ir.headerStructure();
        final String blockLengthType = typeScriptTypeName(headerStructure.blockLengthType());
        final String templateIdType = typeScriptTypeName(headerStructure.templateIdType());
        final String schemaIdType = typeScriptTypeName(headerStructure.schemaIdType());
        final String schemaVersionType = typeScriptTypeName(headerStructure.schemaVersionType());
        final String semanticType = token.encoding().semanticType() == null ? "" : token.encoding().semanticType();
        final String actingFields = codecType == CodecType.ENCODER ?
            "" :
            "    protected actingBlockLength = -1;\n" +
            "    protected actingVersion = -1;\n";

        return String.format(
            "    public readonly sbeBlockLength: %1$s = %2$s;\n" +
            "    public readonly sbeTemplateId: %3$s = %4$s;\n" +
            "    public readonly sbeSchemaId: %5$s = %6$s;\n" +
            "    public readonly sbeSchemaVersion: %7$s = %8$s;\n" +
            "    public readonly sbeSemanticType = \"%10$s\";\n" +
            "    public readonly isLittleEndian = %14$s;\n\n" +
            "    public readonly parentMessage: %9$s = this;\n" +
            "    private buffer = new DataView(new ArrayBuffer(0));\n\n" +
            "    protected offset = -1;\n" +
            "    protected limit = -1;\n" +
                "%13$s" +
            "\n" +
            "    public getBuffer(): %11$s {\n" +
            "        return this.buffer;\n" +
            "    }\n\n" +
            "    public getOffset(): number {\n" +
            "        return this.offset;\n" +
            "    }\n\n" +
            "%12$s" +
            "    public encodedLength(): number {\n" +
            "        return this.limit - this.offset;\n" +
            "    }\n\n" +
            "    public getLimit(): number {\n" +
            "        return this.limit;\n" +
            "    }\n\n" +
            "    public setLimit(limit: number) {\n" +
            "        this.limit = limit;\n" +
            "    }\n",
            blockLengthType,
            generateLiteral(headerStructure.blockLengthType(), Integer.toString(token.encodedLength())),
            templateIdType,
            generateLiteral(headerStructure.templateIdType(), Integer.toString(token.id())),
            schemaIdType,
            generateLiteral(headerStructure.schemaIdType(), Integer.toString(ir.id())),
            schemaVersionType,
            generateLiteral(headerStructure.schemaVersionType(), Integer.toString(ir.version())),
            className,
            semanticType,
            bufferImplementation, // TODO remove
            wrapMethod,
            actingFields,
            ir.byteOrder().equals(ByteOrder.LITTLE_ENDIAN));
    }

    private CharSequence generateEncoderFlyweightCode(final String className, final Token token)
    {
        final String wrapMethod = String.format(
            "    public wrap(buffer: %2$s, offset: number): %1$s {\n" +
            "        this.buffer = buffer;\n" +
            "        this.offset = offset;\n" +
            "        this.setLimit(offset + this.sbeBlockLength);\n\n" +
            "        return this;\n" +
            "    }\n\n",
            className,
            mutableBuffer);

        final String wrapAndApplyHeaderMethod = String.format(
            "    public wrapAndApplyHeader(\n" +
            "        buffer: %2$s, offset: number, headerEncoder: %3$s): %1$s {\n" +
            "        headerEncoder\n" +
            "            .wrap(buffer, offset)\n" +
            "            .blockLength(this.sbeBlockLength)\n" +
            "            .templateId(this.sbeTemplateId)\n" +
            "            .schemaId(this.sbeSchemaId)\n" +
            "            .version(this.sbeSchemaVersion);\n\n" +
            "        return this.wrap(buffer, offset + headerEncoder.encodedLength);\n" +
            "    }\n\n",
            className,
            mutableBuffer,
            formatClassName(ir.headerStructure().tokens().get(0).applicableTypeName() + "Encoder"));

        return generateFlyweightCode(
            CodecType.ENCODER, className, token, wrapMethod + wrapAndApplyHeaderMethod, mutableBuffer);
    }

    private CharSequence generateEncoderFields(
        final String containingClassName, final List<Token> tokens, final String indent)
    {
        final StringBuilder sb = new StringBuilder();

        Generators.forEachField(
            tokens,
            (fieldToken, typeToken) ->
            {
                final String propertyName = formatPropertyName(fieldToken.name());
                final String typeName = formatClassName(encoderName(typeToken.name()));

                generateFieldIdMethod(sb, fieldToken, indent);
                generateFieldSinceVersionMethod(sb, fieldToken, indent);
                generateEncodingOffsetMethod(sb, fieldToken.name(), fieldToken.offset(), indent);
                generateEncodingLengthMethod(sb, fieldToken.name(), typeToken.encodedLength(), indent);
                generateFieldMetaAttributeMethod(sb, fieldToken, indent);

                switch (typeToken.signal())
                {
                    case ENCODING:
                        sb.append(generatePrimitiveEncoder(containingClassName, propertyName, typeToken, indent));
                        break;

                    case BEGIN_ENUM:
                        sb.append(generateEnumEncoder(containingClassName, propertyName, typeToken, indent));
                        break;

                    case BEGIN_SET:
                        sb.append(generateBitSetProperty(
                            false, ENCODER, propertyName, fieldToken, typeToken, indent, typeName));
                        break;

                    case BEGIN_COMPOSITE:
                        sb.append(generateCompositeProperty(
                            false, ENCODER, propertyName, fieldToken, typeToken, indent, typeName));
                        break;
                }
            });

        return sb;
    }

    private CharSequence generateDecoderFields(final List<Token> tokens, final String indent)
    {
        final StringBuilder sb = new StringBuilder();

        Generators.forEachField(
            tokens,
            (fieldToken, typeToken) ->
            {
                final String propertyName = formatPropertyName(fieldToken.name());
                final String typeName = decoderName(formatClassName(typeToken.name()));

                generateFieldIdMethod(sb, fieldToken, indent);
                generateFieldSinceVersionMethod(sb, fieldToken, indent);
                generateEncodingOffsetMethod(sb, fieldToken.name(), fieldToken.offset(), indent);
                generateEncodingLengthMethod(sb, fieldToken.name(), typeToken.encodedLength(), indent);
                generateFieldMetaAttributeMethod(sb, fieldToken, indent);

                switch (typeToken.signal())
                {
                    case ENCODING:
                        sb.append(generatePrimitiveDecoder(false, propertyName, fieldToken, typeToken, indent));
                        break;

                    case BEGIN_ENUM:
                        sb.append(generateEnumDecoder(false, fieldToken, propertyName, typeToken, indent));
                        break;

                    case BEGIN_SET:
                        sb.append(generateBitSetProperty(
                            false, DECODER, propertyName, fieldToken, typeToken, indent, typeName));
                        break;

                    case BEGIN_COMPOSITE:
                        sb.append(generateCompositeProperty(
                            false, DECODER, propertyName, fieldToken, typeToken, indent, typeName));
                        break;
                }
            });

        return sb;
    }

    private CharSequence generateEnumDecoder(
        final boolean inComposite,
        final Token fieldToken,
        final String propertyName,
        final Token typeToken,
        final String indent)
    {
        final String enumName = formatClassName(typeToken.applicableTypeName());
        final Encoding encoding = typeToken.encoding();

        if (fieldToken.isConstantEncoding())
        {
            return String.format(
                "\n" +
                indent + "    public %s(): %s {\n" +
                indent + "        return %s;\n" +
                indent + "    }\n\n",
                propertyName,
                enumName,
                fieldToken.encoding().constValue().toString());
        }
        else
        {
            return String.format(
                "\n" +
                indent + "    public %s(): %s {\n" +
                "%s" +
                indent + "        return %s as %s;\n" +
                indent + "    }\n",
                propertyName,
                enumName,
                generatePropertyNotPresentCondition(inComposite, DECODER, fieldToken, enumName, indent),
                generateGet(indent, "this.offset + " + typeToken.offset(), encoding),
                enumName);
        }
    }

    private CharSequence generateEnumEncoder(
        final String containingClassName, final String propertyName, final Token token, final String indent)
    {
        if (token.isConstantEncoding())
        {
            return "";
        }

        final String enumName = formatClassName(token.applicableTypeName());
        final Encoding encoding = token.encoding();
        final int offset = token.offset();
        final String tsTypeName = typeScriptTypeName(encoding.primitiveType());
        final String coercedValue = String.format("value as %s", tsTypeName);

        return String.format("\n" +
            indent + "    public %s(value: %s): %s {\n" +
            indent + "        %s;\n" +
            indent + "        return this;\n" +
            indent + "    }\n",
            propertyName,
            enumName,
            formatClassName(containingClassName),
            generatePut(indent + "        ", "this.offset + " + offset, coercedValue, encoding));
    }

    private CharSequence generateBitSetProperty(
        final boolean inComposite,
        final CodecType codecType,
        final String propertyName,
        final Token propertyToken,
        final Token bitsetToken,
        final String indent,
        final String bitSetName)
    {
        final StringBuilder sb = new StringBuilder();

        sb.append(String.format("\n" +
            indent + "    private readonly %sCodec = new %s();\n",
            propertyName,
            bitSetName));

        sb.append(String.format("\n" +
            "%s" +
            indent + "    public %s(): %s {\n" +
            "%s" +
            indent + "        this.%sCodec.wrap(this.buffer, this.offset + %d);\n" +
            indent + "        return this.%sCodec;\n" +
            indent + "    }\n",
            generateFlyweightPropertyJsdoc(indent + INDENT, propertyToken, bitSetName),
            propertyName,
            bitSetName,
            generatePropertyNotPresentCondition(inComposite, codecType, propertyToken, null, indent),
            propertyName,
            bitsetToken.offset(),
            propertyName));

        return sb;
    }

    private CharSequence generateCompositeProperty(
        final boolean inComposite,
        final CodecType codecType,
        final String propertyName,
        final Token propertyToken,
        final Token compositeToken,
        final String indent,
        final String compositeName)
    {
        final StringBuilder sb = new StringBuilder();

        sb.append(String.format("\n" +
            indent + "    private readonly %sCodec = new %s();\n",
            propertyName,
            compositeName));

        sb.append(String.format("\n" +
            "%s" +
            indent + "    public %s(): %s {\n" +
            "%s" +
            indent + "        this.%sCodec.wrap(this.buffer, this.offset + %d);\n" +
            indent + "        return this.%sCodec;\n" +
            indent + "    }\n",
            generateFlyweightPropertyJsdoc(indent + INDENT, propertyToken, compositeName),
            propertyName,
            compositeName,
            generatePropertyNotPresentCondition(inComposite, codecType, propertyToken, null, indent),
            propertyName,
            compositeToken.offset(),
            propertyName));

        return sb;
    }

    private String generateGet(final String outerIndent, final String index, final Encoding encoding)
    {
        final String byteOrder = byteOrderString(encoding);
        final PrimitiveType type = encoding.primitiveType();
        final String indent = outerIndent + "            "; // TODO clean up
        switch (type)
        {
            case CHAR:
            case INT8:
                return "this.buffer.getInt8(" + index + ")";

            case UINT8:
                return "this.buffer.getUint8(" + index + ")";

            case INT16:
                return "this.buffer.getInt16(" + index + byteOrder + ")";

            case UINT16:
                return "this.buffer.getUint16(" + index + byteOrder + ")";

            case INT32:
                return "this.buffer.getInt32(" + index + byteOrder + ")";

            case UINT32:
                return "this.buffer.getUint32(" + index + byteOrder + ")";

            case FLOAT:
                return "this.buffer.getFloat32(" + index + byteOrder + ")";

            case INT64:
                // TODO test
                if (encoding.byteOrder().equals(ByteOrder.LITTLE_ENDIAN))
                {
                    return "(this.buffer.getInt32(" + index + ", true) |\n" +
                            indent + "(this.buffer.getInt32(" + index + " + 4, true) << 32))";
                }
                return "((this.buffer.getInt32(" + index + ", false) << 32) |\n" +
                        indent + "this.buffer.getInt32(" + index + " + 4, false))";

            case UINT64:
                // TODO test
                if (encoding.byteOrder().equals(ByteOrder.LITTLE_ENDIAN))
                {
                    return "(this.buffer.getUint32(" + index + ", true) |\n" +
                            indent + "(this.buffer.getUint32(" + index + " + 4, true) << 32))";
                }
                return "((this.buffer.getUint32(" + index + ", false) << 32) |\n" +
                        indent + "this.buffer.getUint32(" + index + " + 4, false))";

            case DOUBLE:
                return "this.buffer.getFloat64(" + index + byteOrder + ")";
        }

        throw new IllegalArgumentException("primitive type not supported: " + type);
    }

    private String generatePut(final String indent, final String index, final String value, final Encoding encoding)
    {
        final String byteOrder = byteOrderString(encoding);
        final PrimitiveType type = encoding.primitiveType();
        switch (type)
        {
            case CHAR:
            case INT8:
                return "this.buffer.setInt8(" + index + ", " + value + ")";

            case UINT8:
                return "this.buffer.setUint8(" + index + ", " + value + ")";

            case INT16:
                return "this.buffer.setInt16(" + index + ", " + value + byteOrder + ")";

            case UINT16:
                return "this.buffer.setUint16(" + index + ", " + value + byteOrder + ")";

            case INT32:
                return "this.buffer.setInt32(" + index + ", " + value + byteOrder + ")";

            case UINT32:
                return "this.buffer.setUint32(" + index + ", " + value + byteOrder + ")";

            case FLOAT:
                return "this.buffer.setFloat32(" + index + ", " + value + byteOrder + ")";

            case INT64:
                // TODO test
                if (encoding.byteOrder().equals(ByteOrder.LITTLE_ENDIAN))
                {
                    return "this.buffer.setInt32(" + index + " + 4, " + value + " & ((1 << 32) - 1), true);\n" +
                            indent + "this.buffer.setInt32(" + index + ", " + value + " >> 32, true)";
                }
                return "this.buffer.setInt32(" + index + " + 4, " + value + " >> 32, false);\n" +
                        indent + "this.buffer.setInt32(" + index + ", " + value + " & ((1 << 32) - 1), false)";

            case UINT64:
                // TODO test
                if (encoding.byteOrder().equals(ByteOrder.LITTLE_ENDIAN))
                {
                    return "this.buffer.setUint32(" + index + " + 4, " + value + " & ((1 << 32) - 1), true);\n" +
                            indent + "this.buffer.setUint32(" + index + ", " + value + " >> 32, true)";
                }
                return "this.buffer.setUint32(" + index + " + 4, " + value + " >> 32, false);\n" +
                        indent + "this.buffer.setUint32(" + index + ", " + value + " & ((1 << 32) - 1), false)";

            case DOUBLE:
                return "this.buffer.setFloat64(" + index + ", " + value + byteOrder + ")";
        }

        throw new IllegalArgumentException("primitive type not supported: " + type);
    }

    private String generateChoiceGet(final PrimitiveType type, final String bitIndex, final String byteOrder)
    {
        switch (type)
        {
            case UINT8:
                return "0 !== (this.buffer.getUint8(this.offset) & (1 << " + bitIndex + "))";

            case UINT16:
                return "0 !== (this.buffer.getUint16(this.offset" + byteOrder + ") & (1 << " + bitIndex + "))";

            case UINT32:
                return "0 !== (this.buffer.getUint32(this.offset" + byteOrder + ") & (1 << " + bitIndex + "))";

            case UINT64:
                // TODO
        }

        throw new IllegalArgumentException("primitive type not supported: " + type);
    }

    private String generateStaticChoiceGet(final PrimitiveType type, final String bitIndex)
    {
        switch (type)
        {
            case UINT8:
                return "0 !== (value & (1 << " + bitIndex + "))";

            case UINT16:
                return "0 !== (value & (1 << " + bitIndex + "))";

            case UINT32:
                return "0 !== (value & (1 << " + bitIndex + "))";

            case UINT64:
                return "0 !== (value & (1L << " + bitIndex + "))";
        }

        throw new IllegalArgumentException("primitive type not supported: " + type);
    }

    private String generateChoicePut(final PrimitiveType type, final String bitIdx, final String byteOrder)
    {
        final String accessor;

        switch (type)
        {
            case UINT8:
                accessor = "Uint8";
                break;

            case UINT16:
                accessor = "Uint16";
                break;

            case UINT32:
                accessor = "Uint32";
                break;

            // TODO UINT64

            default:
                throw new IllegalArgumentException("primitive type not supported: " + type);
        }
        return "        let bits = this.buffer.get" + accessor + "(this.offset" + byteOrder + ");\n" +
               "        bits = value ? bits | (1 << " + bitIdx + ") : bits & ~(1 << " + bitIdx + ");\n" +
               "        this.buffer.set" + accessor + "(this.offset, bits" + byteOrder + ");";

    }

    private String generateStaticChoicePut(final PrimitiveType type, final String bitIdx)
    {
        switch (type)
        {
            case UINT8:
            case UINT16:
            case UINT32:
                return
                    "        return value ? bits | (1 << " + bitIdx + ") : bits & ~(1 << " + bitIdx + ");\n";

            // TODO UINT64
        }

        throw new IllegalArgumentException("primitive type not supported: " + type);
    }

    enum CodecType
    {
        DECODER,
        ENCODER
    }

    /* TODO

    private CharSequence generateEncoderDisplay(final String decoderName, final String baseIndent)
    {
        final String indent = baseIndent + INDENT;
        final StringBuilder sb = new StringBuilder();

        sb.append('\n');
        appendToString(sb, indent);
        sb.append('\n');
        append(sb, indent, "public StringBuilder appendTo(final StringBuilder builder)");
        append(sb, indent, "{");
        append(sb, indent, INDENT + decoderName + " writer = new " + decoderName + "();");
        append(sb, indent, "    writer.wrap(buffer, offset, this.blockLength, this.schemaVersion);");
        sb.append('\n');
        append(sb, indent, "    return writer.appendTo(builder);");
        append(sb, indent, "}");

        return sb.toString();
    }

    private CharSequence generateCompositeEncoderDisplay(final String decoderName, final String baseIndent)
    {
        final String indent = baseIndent + INDENT;
        final StringBuilder sb = new StringBuilder();
        appendToString(sb, indent);
        sb.append('\n');
        append(sb, indent, "public StringBuilder appendTo(final StringBuilder builder)");
        append(sb, indent, "{");
        append(sb, indent, INDENT + decoderName + " writer = new " + decoderName + "();");
        append(sb, indent, "    writer.wrap(buffer, offset);");
        sb.append('\n');
        append(sb, indent, "    return writer.appendTo(builder);");
        append(sb, indent, "}");

        return sb.toString();
    }

    private CharSequence generateCompositeDecoderDisplay(final List<Token> tokens, final String baseIndent)
    {
        final String indent = baseIndent + INDENT;
        final StringBuilder sb = new StringBuilder();

        appendToString(sb, indent);
        sb.append('\n');
        append(sb, indent, "public StringBuilder appendTo(final StringBuilder builder)");
        append(sb, indent, "{");
        Separators.BEGIN_COMPOSITE.appendToGeneratedBuilder(sb, indent + INDENT, "builder");

        int lengthBeforeLastGeneratedSeparator = -1;

        for (int i = 1, end = tokens.size() - 1; i < end;)
        {
            final Token encodingToken = tokens.get(i);
            final String propertyName = formatPropertyName(encodingToken.name());
            lengthBeforeLastGeneratedSeparator = writeTokenDisplay(propertyName, encodingToken, sb, indent + INDENT);
            i += encodingToken.componentTokenCount();
        }

        if (-1 != lengthBeforeLastGeneratedSeparator)
        {
            sb.setLength(lengthBeforeLastGeneratedSeparator);
        }

        Separators.END_COMPOSITE.appendToGeneratedBuilder(sb, indent + INDENT, "builder");
        sb.append('\n');
        append(sb, indent, "    return builder;");
        append(sb, indent, "}");

        return sb.toString();
    }

    private CharSequence generateChoiceDisplay(final List<Token> tokens)
    {
        final String indent = INDENT;
        final StringBuilder sb = new StringBuilder();

        appendToString(sb, indent);
        sb.append('\n');
        append(sb, indent, "public StringBuilder appendTo(final StringBuilder builder)");
        append(sb, indent, "{");
        Separators.BEGIN_SET.appendToGeneratedBuilder(sb, indent + INDENT, "builder");
        append(sb, indent, "    boolean atLeastOne = false;");

        tokens
            .stream()
            .filter((token) -> token.signal() == Signal.CHOICE)
            .forEach((token) ->
            {
                final String choiceName = formatPropertyName(token.name());
                append(sb, indent, "    if (" + choiceName + "())");
                append(sb, indent, "    {");
                append(sb, indent, "        if (atLeastOne)");
                append(sb, indent, "        {");
                Separators.ENTRY.appendToGeneratedBuilder(sb, indent + INDENT + INDENT + INDENT, "builder");
                append(sb, indent, "        }");
                append(sb, indent, "        builder.append(\"" + choiceName + "\");");
                append(sb, indent, "        atLeastOne = true;");
                append(sb, indent, "    }");
            });

        Separators.END_SET.appendToGeneratedBuilder(sb, indent + INDENT, "builder");
        sb.append('\n');
        append(sb, indent, "    return builder;");
        append(sb, indent, "}");

        return sb.toString();
    }

    private CharSequence generateDecoderDisplay(
        final String name,
        final List<Token> tokens,
        final List<Token> groups,
        final List<Token> varData,
        final String baseIndent)
    {
        final String indent = baseIndent + INDENT;
        final StringBuilder sb = new StringBuilder();

        sb.append('\n');
        appendToString(sb, indent);
        sb.append('\n');
        append(sb, indent, "public StringBuilder appendTo(final StringBuilder builder)");
        append(sb, indent, "{");
        append(sb, indent, "    final int originalLimit = this.getLimit();");
        append(sb, indent, "    this.setLimit(offset + actingBlockLength);");
        append(sb, indent, "    builder.append(\"[" + name + "](sbeTemplateId=\");");
        append(sb, indent, "    builder.append(this.templateId);");
        append(sb, indent, "    builder.append(\"|sbeSchemaId=\");");
        append(sb, indent, "    builder.append(this.schemaId);");
        append(sb, indent, "    builder.append(\"|sbeSchemaVersion=\");");
        append(sb, indent, "    if (parentMessage.actingVersion != this.schemaVersion)");
        append(sb, indent, "    {");
        append(sb, indent, "        builder.append(parentMessage.actingVersion);");
        append(sb, indent, "        builder.append('/');");
        append(sb, indent, "    }");
        append(sb, indent, "    builder.append(this.schemaVersion);");
        append(sb, indent, "    builder.append(\"|sbeBlockLength=\");");
        append(sb, indent, "    if (actingBlockLength != this.blockLength)");
        append(sb, indent, "    {");
        append(sb, indent, "        builder.append(actingBlockLength);");
        append(sb, indent, "        builder.append('/');");
        append(sb, indent, "    }");
        append(sb, indent, "    builder.append(this.blockLength);");
        append(sb, indent, "    builder.append(\"):\");");
        appendDecoderDisplay(sb, tokens, groups, varData, indent + INDENT);
        sb.append('\n');
        append(sb, indent, "    this.setLimit(originalLimit);");
        sb.append('\n');
        append(sb, indent, "    return builder;");
        append(sb, indent, "}");

        return sb.toString();
    }

    private void appendGroupInstanceDecoderDisplay(
        final StringBuilder sb,
        final List<Token> fields,
        final List<Token> groups,
        final List<Token> varData,
        final String baseIndent)
    {
        final String indent = baseIndent + INDENT;

        sb.append('\n');
        appendToString(sb, indent);
        sb.append('\n');
        append(sb, indent, "public StringBuilder appendTo(final StringBuilder builder)");
        append(sb, indent, "{");
        Separators.BEGIN_COMPOSITE.appendToGeneratedBuilder(sb, indent + INDENT, "builder");
        appendDecoderDisplay(sb, fields, groups, varData, indent + INDENT);
        Separators.END_COMPOSITE.appendToGeneratedBuilder(sb, indent + INDENT, "builder");
        append(sb, indent, "    return builder;");
        append(sb, indent, "}");
    }

    private void appendDecoderDisplay(
        final StringBuilder sb,
        final List<Token> fields,
        final List<Token> groups,
        final List<Token> varData,
        final String indent)
    {
        int lengthBeforeLastGeneratedSeparator = -1;

        for (int i = 0, size = fields.size(); i < size;)
        {
            final Token fieldToken = fields.get(i);
            if (fieldToken.signal() == Signal.BEGIN_FIELD)
            {
                final Token encodingToken = fields.get(i + 1);

                final String fieldName = formatPropertyName(fieldToken.name());
                append(sb, indent, "//" + fieldToken);
                lengthBeforeLastGeneratedSeparator = writeTokenDisplay(fieldName, encodingToken, sb, indent);

                i += fieldToken.componentTokenCount();
            }
            else
            {
                ++i;
            }
        }

        for (int i = 0, size = groups.size(); i < size; i++)
        {
            final Token groupToken = groups.get(i);
            if (groupToken.signal() != Signal.BEGIN_GROUP)
            {
                throw new IllegalStateException("tokens must begin with BEGIN_GROUP: token=" + groupToken);
            }

            append(sb, indent, "//" + groupToken);

            final String groupName = formatPropertyName(groupToken.name());
            final String groupDecoderName = decoderName(formatClassName(groupToken.name()));

            append(
                sb, indent, "builder.append(\"" + groupName + Separators.KEY_VALUE + Separators.BEGIN_GROUP + "\");");
            append(sb, indent, groupDecoderName + " " + groupName + " = " + groupName + "();");
            append(sb, indent, "if (" + groupName + ".count() > 0)");
            append(sb, indent, "{");
            append(sb, indent, "    while (" + groupName + ".hasNext())");
            append(sb, indent, "    {");
            append(sb, indent, "        " + groupName + ".next().appendTo(builder);");
            Separators.ENTRY.appendToGeneratedBuilder(sb, indent + INDENT + INDENT, "builder");
            append(sb, indent, "    }");
            append(sb, indent, "    builder.setLength(builder.length() - 1);");
            append(sb, indent, "}");
            Separators.END_GROUP.appendToGeneratedBuilder(sb, indent, "builder");

            lengthBeforeLastGeneratedSeparator = sb.length();
            Separators.FIELD.appendToGeneratedBuilder(sb, indent, "builder");

            i = findEndSignal(groups, i, Signal.END_GROUP, groupToken.name());
        }

        for (int i = 0, size = varData.size(); i < size;)
        {
            final Token varDataToken = varData.get(i);
            if (varDataToken.signal() != Signal.BEGIN_VAR_DATA)
            {
                throw new IllegalStateException("tokens must begin with BEGIN_VAR_DATA: token=" + varDataToken);
            }

            append(sb, indent, "//" + varDataToken);

            final String characterEncoding = varData.get(i + 3).encoding().characterEncoding();
            final String varDataName = formatPropertyName(varDataToken.name());
            append(sb, indent, "builder.append(\"" + varDataName + Separators.KEY_VALUE + "\");");
            if (null == characterEncoding)
            {
                append(sb, indent, "builder.append(" + varDataName + "Length() + \" bytes of raw data\");");
                append(sb, indent,
                    "parentMessage.setLimit(parentMessage.setLimit() + " + varDataName + "HeaderLength() + " +
                    varDataName + "Length());");
            }
            else
            {
                append(sb, indent, "builder.append('\\'' + " + varDataName + "() + '\\'');");
            }

            lengthBeforeLastGeneratedSeparator = sb.length();
            Separators.FIELD.appendToGeneratedBuilder(sb, indent, "builder");

            i += varDataToken.componentTokenCount();
        }

        if (-1 != lengthBeforeLastGeneratedSeparator)
        {
            sb.setLength(lengthBeforeLastGeneratedSeparator);
        }
    }

    private int writeTokenDisplay(
        final String fieldName,
        final Token typeToken,
        final StringBuilder sb,
        final String indent)
    {
        append(sb, indent, "//" + typeToken);

        if (typeToken.encodedLength() <= 0 || typeToken.isConstantEncoding())
        {
            return -1;
        }

        append(sb, indent, "builder.append(\"" + fieldName + Separators.KEY_VALUE + "\");");

        switch (typeToken.signal())
        {
            case ENCODING:
                if (typeToken.arrayLength() > 1)
                {
                    if (typeToken.encoding().primitiveType() == PrimitiveType.CHAR)
                    {
                        append(sb, indent,
                            "for (int i = 0; i < " + fieldName + "Length() && " + fieldName + "(i) > 0; i++)");
                        append(sb, indent, "{");
                        append(sb, indent, "    builder.append((char)" + fieldName + "(i));");
                        append(sb, indent, "}");
                    }
                    else
                    {
                        Separators.BEGIN_ARRAY.appendToGeneratedBuilder(sb, indent, "builder");
                        append(sb, indent, "if (" +  fieldName + "Length() > 0)");
                        append(sb, indent, "{");
                        append(sb, indent, "    for (int i = 0; i < " + fieldName + "Length(); i++)");
                        append(sb, indent, "    {");
                        append(sb, indent, "        builder.append(" + fieldName + "(i));");
                        Separators.ENTRY.appendToGeneratedBuilder(sb, indent + INDENT + INDENT, "builder");
                        append(sb, indent, "    }");
                        append(sb, indent, "    builder.setLength(builder.length() - 1);");
                        append(sb, indent, "}");
                        Separators.END_ARRAY.appendToGeneratedBuilder(sb, indent, "builder");
                    }
                }
                else
                {
                    // have to duplicate because of checkstyle :/
                    append(sb, indent, "builder.append(" + fieldName + "());");
                }
                break;

            case BEGIN_ENUM:
            case BEGIN_SET:
                append(sb, indent, "builder.append(" + fieldName + "());");
                break;

            case BEGIN_COMPOSITE:
                append(sb, indent, fieldName + "().appendTo(builder);");
                break;
        }

        final int lengthBeforeFieldSeparator = sb.length();
        Separators.FIELD.appendToGeneratedBuilder(sb, indent, "builder");

        return lengthBeforeFieldSeparator;
    }

    private void appendToString(final StringBuilder sb, final String indent)
    {
        sb.append('\n');
        append(sb, indent, "public String toString()");
        append(sb, indent, "{");
        append(sb, indent, "    return appendTo(new StringBuilder(100)).toString();");
        append(sb, indent, "}");
    }
    */
}
