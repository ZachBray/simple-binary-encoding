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

import static java.lang.reflect.Modifier.STATIC;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import uk.co.real_logic.sbe.PrimitiveType;
import uk.co.real_logic.sbe.SbeTool;
import uk.co.real_logic.sbe.generation.Generators;
import uk.co.real_logic.sbe.ir.Token;
import uk.co.real_logic.sbe.util.ValidationUtil;

/**
 * Utilities for mapping between IR and the TypeScript language.
 */
public class TypeScriptUtil
{
    public enum Separators
    {
        BEGIN_GROUP('['),
        END_GROUP(']'),
        BEGIN_COMPOSITE('('),
        END_COMPOSITE(')'),
        BEGIN_SET('{'),
        END_SET('}'),
        BEGIN_ARRAY('['),
        END_ARRAY(']'),
        FIELD('|'),
        KEY_VALUE('='),
        ENTRY(',');

        public final char symbol;

        Separators(final char symbol)
        {
            this.symbol = symbol;
        }

        /**
         * Add separator to a generated StringBuilder
         *
         * @param builder     the code generation builder to which information should be added
         * @param indent      the current generated code indentation
         * @param builderName of the generated StringBuilder to which separator should be added
         */
        public void appendToGeneratedBuilder(final StringBuilder builder, final String indent, final String builderName)
        {
            append(builder, indent, builderName + ".append('" + symbol + "');");
        }

        public String toString()
        {
            return String.valueOf(symbol);
        }
    }

    private static final Map<PrimitiveType, String> TYPE_NAME_BY_PRIMITIVE_TYPE_MAP =
        new EnumMap<>(PrimitiveType.class);

    static
    {
        TYPE_NAME_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.CHAR, "number");
        TYPE_NAME_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.INT8, "number");
        TYPE_NAME_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.INT16, "number");
        TYPE_NAME_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.INT32, "number");
        TYPE_NAME_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.INT64, "number");
        TYPE_NAME_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.UINT8, "number");
        TYPE_NAME_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.UINT16, "number");
        TYPE_NAME_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.UINT32, "number");
        TYPE_NAME_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.UINT64, "number");
        TYPE_NAME_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.FLOAT, "number");
        TYPE_NAME_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.DOUBLE, "number");
    }

    /**
     * Indexes known charset aliases to the name of the instance in {@link StandardCharsets}.
     */
    private static final Map<String, String> STD_CHARSETS = new HashMap<>();

    static
    {
        try
        {
            for (final Field field : StandardCharsets.class.getDeclaredFields())
            {
                if (Charset.class.isAssignableFrom(field.getType()) && ((field.getModifiers() & STATIC) == STATIC))
                {
                    final Charset charset = (Charset)field.get(null);
                    STD_CHARSETS.put(charset.name(), field.getName());
                    charset.aliases().forEach((alias) -> STD_CHARSETS.put(alias, field.getName()));
                }
            }
        }
        catch (final IllegalAccessException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Map the name of a {@link PrimitiveType} to a TypeScript primitive type name.
     *
     * @param primitiveType to map.
     * @return the name of the TypeScript primitive that most closely maps.
     */
    public static String typeScriptTypeName(final PrimitiveType primitiveType)
    {
        return TYPE_NAME_BY_PRIMITIVE_TYPE_MAP.get(primitiveType);
    }

    /**
     * Format a property name for generated code.
     * <p>
     * If the formatted property name is a keyword then {@link SbeTool#KEYWORD_APPEND_TOKEN} is appended if set.
     *
     * @param value to be formatted.
     * @return the string formatted as a property name.
     * @throws IllegalStateException if a keyword and {@link SbeTool#KEYWORD_APPEND_TOKEN} is not set.
     */
    public static String formatPropertyName(final String value)
    {
        String formattedValue = Generators.toLowerFirstChar(value);

        if (ValidationUtil.isTypeScriptKeyword(formattedValue))
        {
            final String keywordAppendToken = System.getProperty(SbeTool.KEYWORD_APPEND_TOKEN);
            if (null == keywordAppendToken)
            {
                throw new IllegalStateException(
                    "Invalid property name='" + formattedValue +
                    "' please correct the schema or consider setting system property: " + SbeTool.KEYWORD_APPEND_TOKEN);
            }

            formattedValue += keywordAppendToken;
        }

        return formattedValue;
    }

    /**
     * Format a class name for the generated code.
     *
     * @param value to be formatted.
     * @return the string formatted as a class name.
     */
    public static String formatClassName(final String value)
    {
        return Generators.toUpperFirstChar(value);
    }


    /**
     * Shortcut to append a line of generated code
     *
     * @param builder string builder to which to append the line
     * @param indent  current text indentation
     * @param line    line to be appended
     */
    public static void append(final StringBuilder builder, final String indent, final String line)
    {
        builder.append(indent).append(line).append('\n');
    }

    /**
     * Generate a literal value to be used in code generation.
     *
     * @param type  of the lateral value.
     * @param value of the lateral.
     * @return a String representation of the TypeScript literal.
     */
    public static String generateLiteral(final PrimitiveType type, final String value)
    {
        // No conversion in TypeScript
        return value;
    }

    /**
     * Generate the jsdoc comment header for a type.
     *
     * @param indent    level for the comment.
     * @param typeToken for the type.
     * @return a string representation of the jsdoc comment.
     */
    public static String generateTypeJsdoc(final String indent, final Token typeToken)
    {
        final String description = typeToken.description();
        if (null == description || description.isEmpty())
        {
            return "";
        }

        return
            indent + "/**\n" +
            indent + " * " + description + '\n' +
            indent + " */\n";
    }

    /**
     * Generate the jsdoc comment header for a bitset choice option decode method.
     *
     * @param indent      level for the comment.
     * @param optionToken for the type.
     * @return a string representation of the jsdoc comment.
     */
    public static String generateOptionDecodeJsdoc(final String indent, final Token optionToken)
    {
        final String description = optionToken.description();
        if (null == description || description.isEmpty())
        {
            return "";
        }

        return
            indent + "/**\n" +
            indent + " * " + description + '\n' +
            indent + " *\n" +
            indent + " * @return true if " + optionToken.name() + " is set or false if not\n" +
            indent + " */\n";
    }

    /**
     * Generate the jsdoc comment header for a bitset choice option encode method.
     *
     * @param indent      level for the comment.
     * @param optionToken for the type.
     * @return a string representation of the jsdoc comment.
     */
    public static String generateOptionEncodeJsdoc(final String indent, final Token optionToken)
    {
        final String description = optionToken.description();
        if (null == description || description.isEmpty())
        {
            return "";
        }

        return
            indent + "/**\n" +
            indent + " * " + description + '\n' +
            indent + " *\n" +
            indent + " * @param value true if " + optionToken.name() + " is set or false if not\n" +
            indent + " */\n";
    }

    /**
     * Generate the jsdoc comment header for flyweight property.
     *
     * @param indent        level for the comment.
     * @param propertyToken for the property name.
     * @param typeName      for the property type.
     * @return a string representation of the jsdoc comment.
     */
    public static String generateFlyweightPropertyJsdoc(
        final String indent, final Token propertyToken, final String typeName)
    {
        final String description = propertyToken.description();
        if (null == description || description.isEmpty())
        {
            return "";
        }

        return
            indent + "/**\n" +
            indent + " * " + description + '\n' +
            indent + " *\n" +
            indent + " * @return " + typeName + " : " + description + "\n" +
            indent + " */\n";
    }

    /**
     * Generate the jsdoc comment header for group encode property.
     *
     * @param indent        level for the comment.
     * @param propertyToken for the property name.
     * @param typeName      for the property type.
     * @return a string representation of the jsdoc comment.
     */
    public static String generateGroupEncodePropertyJsdoc(
        final String indent, final Token propertyToken, final String typeName)
    {
        final String description = propertyToken.description();
        if (null == description || description.isEmpty())
        {
            return "";
        }

        return
            indent + "/**\n" +
            indent + " * " + description + "\n" +
            indent + " *\n" +
            indent + " * @param count of times the group will be encoded\n" +
            indent + " * @return " + typeName + " : encoder for the group\n" +
            indent + " */\n";
    }
}
