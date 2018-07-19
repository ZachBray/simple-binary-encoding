import {expect} from "chai";
import {CarDecoder, CarEncoder, MessageHeaderDecoder} from "./generated/baseline";

const MAX_SAFE_INTEGER = 9007199254740991;
const MIN_SAFE_INTEGER = -MAX_SAFE_INTEGER;

describe("53-bit integer serialization", () => {
    const cases = [
        MIN_SAFE_INTEGER,
        -1 * (1 << 30) * (1 << 20),
        -1 * (1 << 30) * (1 << 10),
        -1 * (1 << 30) * (1 << 10) - 39,
        -1 * (1 << 30),
        -999,
        -41,
        -1,
        0,
        1,
        41,
        999,
        (1 << 30),
        (1 << 30) * (1 << 10),
        (1 << 30) * (1 << 10) + 39,
        (1 << 30) * (1 << 20),
        MAX_SAFE_INTEGER,
    ];

    for (const value of cases) {
        const headerDecoder = new MessageHeaderDecoder();
        const buffer = new ArrayBuffer(1024);
        const bufferView = new DataView(buffer, 0, buffer.byteLength);
        const encoder = new CarEncoder();
        const decoder = new CarDecoder();
        encoder.wrap(bufferView, 0);
        decoder.wrap(bufferView, 0, encoder.sbeBlockLength, encoder.sbeSchemaVersion);

        it(`should serialize and deserialize ${value}`, () => {
            // Act
            encoder.serialNumber(value);
            const observation = decoder.serialNumber();
            // Assert
            expect(observation).to.eq(value);
        });
    }
});
