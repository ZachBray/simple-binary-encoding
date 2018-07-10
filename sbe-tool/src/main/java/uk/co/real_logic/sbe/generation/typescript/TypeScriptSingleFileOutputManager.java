package uk.co.real_logic.sbe.generation.typescript;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

import org.agrona.Verify;
import org.agrona.generation.OutputManager;

public class TypeScriptSingleFileOutputManager implements OutputManager
{
    private final Set<String> writtenRegions = new HashSet<>();
    private final File outputFile;
    private boolean hasWrittenRegion;

    public TypeScriptSingleFileOutputManager(final String outputFilePath)
    {
        Verify.notNull(outputFilePath, "outputFilePath");

        outputFile = new File(outputFilePath);
        final File outputDir = outputFile.getParentFile();

        if (!outputDir.exists() && !outputDir.mkdirs())
        {
            throw new IllegalStateException("Unable to create directory: " + outputDir);
        }

        if (outputFile.exists() && !outputFile.delete())
        {
            throw new IllegalStateException("Unable to delete existing file: " + outputFile);
        }
    }

    @Override
    public Writer createOutput(final String s) throws IOException
    {
        final boolean hasAlreadyWrittenRegion = !writtenRegions.add(s);
        if (hasAlreadyWrittenRegion)
        {
            // We drop regions that have already been written.
            // TODO why are they written multiple times?
            return new StringWriter();
        }

        final BufferedWriter writer = createAppender();
        if (hasWrittenRegion)
        {
            writer.write("//#endregion");
            writer.newLine();
            writer.newLine();
        }
        else
        {
            writer.write("/* tslint:disable */");
            writer.newLine();
        }
        writer.write("//#region " + s);
        writer.newLine();
        writer.newLine();
        hasWrittenRegion = true;
        return writer;
    }

    public void writeFileEnding() throws IOException
    {
        try (BufferedWriter writer = createAppender())
        {
            writer.write("//#endregion");
            writer.newLine();
            writer.close();
        }
    }

    private BufferedWriter createAppender() throws IOException
    {
        final StandardOpenOption option = outputFile.exists() ?
            StandardOpenOption.APPEND :
            StandardOpenOption.CREATE_NEW;
        return Files.newBufferedWriter(
                outputFile.toPath(), StandardCharsets.UTF_8, option);
    }
}
