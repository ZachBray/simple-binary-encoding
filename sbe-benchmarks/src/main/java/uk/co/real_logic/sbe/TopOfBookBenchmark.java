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

import uk.co.real_logic.protobuf.TopOfBook;
import uk.co.real_logic.sbe.benchmarks.MessageHeaderEncoder;
import uk.co.real_logic.sbe.benchmarks.TopOfBookDataEncoder;
import org.agrona.BitUtil;
import org.agrona.BufferUtil;
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

@Fork(value = 1, jvmArgsPrepend = {
    "-XX:+UnlockDiagnosticVMOptions",
//    "-XX:+DebugNonSafepoints",
//    "-XX:+PrintAssembly",
    "-Dagrona.disable.bounds.checks=true",
    "-Xms1g",
    "-Xmx1g",
    "-XX:+UseZGC"})
@Warmup(iterations = 4, time = 6)
@Measurement(iterations = 2, time = 10)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class TopOfBookBenchmark
{
    private static final long[] VALUES = new long[64];

    static
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            VALUES[i] = 1L << i;
        }
    }

    @State(Scope.Thread)
    public static class SbeState
    {
        final UnsafeBuffer buffer = new UnsafeBuffer(BufferUtil.allocateDirectAligned(1024, BitUtil.CACHE_LINE_LENGTH));
        final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();
        final TopOfBookDataEncoder encoder = new TopOfBookDataEncoder();
        int index = -1;

        int nextIndex()
        {
            return index = (index + 1) & 63;
        }
    }

    @Benchmark
    public void sbeEncode(final SbeState state, final Blackhole blackhole)
    {
        final TopOfBookDataEncoder encoder = state.encoder;
        encoder.wrapAndApplyHeader(state.buffer, 0, state.messageHeaderEncoder);
        encoder.bestBid().mantissa(VALUES[state.nextIndex()]).exponent((byte)42);
        encoder.bestAsk().mantissa(VALUES[state.nextIndex()]).exponent((byte)42);
        encoder.lastTradedPrice().mantissa(VALUES[state.nextIndex()]).exponent((byte)42);
        encoder.totalTradedVolume(VALUES[state.nextIndex()]);
        encoder.high().mantissa(VALUES[state.nextIndex()]).exponent((byte)42);
        encoder.low().mantissa(VALUES[state.nextIndex()]).exponent((byte)42);
        // I think this is sufficient to prevent the compiler optimising away our code; however, it'd be good to have
        // a review. I think in some places we use a checksum of the encoded data to prevent this.
        blackhole.consume(state.buffer);
    }

    @State(Scope.Thread)
    public static class ProtobufState
    {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[1024]);
        final DirectBufferOutputStream outputStream = new DirectBufferOutputStream();
        final CodedOutputStream codedOutputStream = CodedOutputStream.newInstance(outputStream);
        int encodedLength;
        int index = -1;

        int nextIndex()
        {
            return index = (index + 1) & 63;
        }
    }

    @Benchmark
    public void protobufEncode(final ProtobufState state, final Blackhole blackhole) throws IOException
    {
        state.outputStream.wrap(state.buffer, 0, state.buffer.capacity());
        final TopOfBook.TopOfBookData.Builder builder = TopOfBook.TopOfBookData.newBuilder();
        builder.setSymbol("EURUSD");
        builder.getBestBidBuilder().setMantissa(VALUES[state.nextIndex()]).setExponent(42);
        builder.getBestAskBuilder().setMantissa(VALUES[state.nextIndex()]).setExponent(42);
        builder.getLastTradedPriceBuilder().setMantissa(VALUES[state.nextIndex()]).setExponent(42);
        builder.setTotalTradedVolume(VALUES[state.nextIndex()]);
        builder.getHighBuilder().setMantissa(VALUES[state.nextIndex()]).setExponent(42);
        builder.getLowBuilder().setMantissa(VALUES[state.nextIndex()]).setExponent(42);
        builder.build().writeTo(state.codedOutputStream);
        state.encodedLength = state.codedOutputStream.getTotalBytesWritten();
        state.codedOutputStream.flush();
        // I think this is sufficient to prevent the compiler optimising away our code; however, it'd be good to have
        // a review. I think in some places we use a checksum of the encoded data to prevent this.
        blackhole.consume(state.buffer);
    }

    public static void main(final String[] args) throws RunnerException
    {
        System.setProperty("jmh.blackhole.autoDetect", "false");

        new Runner(new OptionsBuilder()
            .addProfiler("perfasm")
            .include(TopOfBookBenchmark.class.getName()).shouldFailOnError(true).build())
            .run();
    }
}
