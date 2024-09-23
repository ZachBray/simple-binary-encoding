/*
 * Copyright 2013-2024 Real Logic Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.real_logic.sbe;

import uk.co.real_logic.protobuf.Simple;
import uk.co.real_logic.sbe.benchmarks.FooEncoder;
import uk.co.real_logic.sbe.benchmarks.MessageHeaderEncoder;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.io.DirectBufferOutputStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Fork(value = 3, jvmArgsPrepend = {
    "-XX:+UnlockDiagnosticVMOptions",
    "-XX:+DebugNonSafepoints",
    "-Dagrona.disable.bounds.checks=true",
    "-Xms1g",
    "-Xmx1g",
    "-XX:+UseParallelGC"})
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class FooBenchmark
{
    @State(Scope.Thread)
    public static class SbeState
    {
        final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[1024]);
        final FooEncoder fooEncoder = new FooEncoder();
    }

    @Benchmark
    public void sbeEncode(final SbeState state, final Blackhole blackhole)
    {
        final FooEncoder fooEncoder = state.fooEncoder;
        fooEncoder.wrapAndApplyHeader(state.buffer, 0, state.messageHeaderEncoder);
        fooEncoder.bar(Long.MIN_VALUE);
        fooEncoder.baz("EURUSD");
        fooEncoder.qux(42.1);
        blackhole.consume(state.buffer);
    }

    @State(Scope.Thread)
    public static class ProtobufState
    {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[1024]);
        final DirectBufferOutputStream outputStream = new DirectBufferOutputStream();
        final CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(outputStream);
        int encodedLength;
    }

    @Benchmark
    public void protobufEncode(final ProtobufState state, final Blackhole blackhole) throws IOException
    {
        state.outputStream.wrap(state.buffer, 0, state.buffer.capacity());
        final Simple.Foo.Builder builder = Simple.Foo.newBuilder();
        builder.setBar(Long.MIN_VALUE);
        builder.setBaz("EURUSD");
        builder.setQux(42.1);
        final Simple.Foo foo = builder.build();
        foo.writeTo(state.codedOutputStream);
        state.encodedLength = state.codedOutputStream.getTotalBytesWritten();
        state.codedOutputStream.flush();
        blackhole.consume(state.buffer);
    }

    @Benchmark
    public void protobufEncode2(final ProtobufState state, final Blackhole blackhole) throws IOException
    {
        state.outputStream.wrap(state.buffer, 0, state.buffer.capacity());
        final Simple.Foo2.Builder builder = Simple.Foo2.newBuilder();
        builder.setBar(Long.MIN_VALUE);
        builder.setBaz("EURUSD");
        builder.setQux(42.1);
        final Simple.Foo2 foo = builder.build();
        foo.writeTo(state.codedOutputStream);
        state.encodedLength = state.codedOutputStream.getTotalBytesWritten();
        state.codedOutputStream.flush();
        blackhole.consume(state.buffer);
    }

    public static void main(final String[] args) throws RunnerException
    {
        new Runner(new OptionsBuilder()
            .addProfiler("perfasm")
            .include(FooBenchmark.class.getName()).shouldFailOnError(true).build())
            .run();
    }
}
