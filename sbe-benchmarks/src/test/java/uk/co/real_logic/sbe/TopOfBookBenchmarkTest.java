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
import uk.co.real_logic.sbe.benchmarks.MessageHeaderDecoder;
import uk.co.real_logic.sbe.benchmarks.TopOfBookDataDecoder;
import org.agrona.io.DirectBufferInputStream;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TopOfBookBenchmarkTest
{
    private final Blackhole blackhole = new Blackhole(
        "Today's password is swordfish. I understand instantiating Blackholes directly is dangerous."
    );

    @Test
    void sbeBenchmarkShouldEncodeData()
    {
        final TopOfBookBenchmark.SbeState state = new TopOfBookBenchmark.SbeState();
        new TopOfBookBenchmark().sbeEncode(state, blackhole);
        final TopOfBookDataDecoder decoder = new TopOfBookDataDecoder();
        final MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();
        messageHeaderDecoder.wrap(state.buffer, 0);
        decoder.wrap(
            state.buffer,
            messageHeaderDecoder.encodedLength(),
            messageHeaderDecoder.blockLength(),
            messageHeaderDecoder.version()
        );

        assertEquals(1L, decoder.bestBid().mantissa());
        assertEquals(2L, decoder.bestAsk().mantissa());
        assertEquals(4L, decoder.lastTradedPrice().mantissa());
        assertEquals(8L, decoder.totalTradedVolume());
        assertEquals(16L, decoder.high().mantissa());
        assertEquals(32L, decoder.low().mantissa());
        assertEquals(67L, decoder.limit()); // message length
    }

    @Test
    void variableLengthProtobufBenchmarkShouldEncodeData() throws IOException
    {
        final TopOfBookBenchmark.ProtobufState state = new TopOfBookBenchmark.ProtobufState();
        new TopOfBookBenchmark().variableLengthProtobufEncode(state, blackhole);
        final DirectBufferInputStream inputStream = new DirectBufferInputStream(state.buffer, 0, state.encodedLength);
        final TopOfBook.TopOfBookData data = TopOfBook.TopOfBookData.parseFrom(inputStream);

        assertEquals(1L, data.getBestBid().getMantissa());
        assertEquals(2L, data.getBestAsk().getMantissa());
        assertEquals(4L, data.getLastTradedPrice().getMantissa());
        assertEquals(8L, data.getTotalTradedVolume());
        assertEquals(16L, data.getHigh().getMantissa());
        assertEquals(32L, data.getLow().getMantissa());
        assertEquals(40L, state.encodedLength); // message length
    }

    @Test
    void fixedLengthProtobufBenchmarkShouldEncodeData() throws IOException
    {
        final TopOfBookBenchmark.ProtobufState state = new TopOfBookBenchmark.ProtobufState();
        new TopOfBookBenchmark().fixedLengthProtobufEncode(state, blackhole);
        final DirectBufferInputStream inputStream = new DirectBufferInputStream(state.buffer, 0, state.encodedLength);
        final TopOfBook.FixedTopOfBookData data = TopOfBook.FixedTopOfBookData.parseFrom(inputStream);

        assertEquals(1L, data.getBestBid().getMantissa());
        assertEquals(2L, data.getBestAsk().getMantissa());
        assertEquals(4L, data.getLastTradedPrice().getMantissa());
        assertEquals(8L, data.getTotalTradedVolume());
        assertEquals(16L, data.getHigh().getMantissa());
        assertEquals(32L, data.getLow().getMantissa());
        assertEquals(97L, state.encodedLength); // message length
    }
}
