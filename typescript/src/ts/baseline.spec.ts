import {
    BooleanType, BoostType, CarDecoder, CarEncoder, MessageHeaderDecoder, MessageHeaderEncoder,
    Model,
} from "./generated/baseline";

// "Polyfill" for node
const encoding: any = require("text-encoding");
(global as any).TextDecoder = encoding.TextDecoder;
(global as any).TextEncoder = encoding.TextEncoder;

describe("Example", () => {
    const bytes = [ 49, 0, 1, 0, 1, 0, 0, 0, 210, 4, 0, 0, 0, 0, 0, 0, 221, 7, 1, 65, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4, 0, 0, 0, 97, 98, 99, 100, 101, 102, 6, 208, 7, 4, 49, 50, 51, 35, 1, 78, 200, 6, 0, 3, 0, 30, 0, 154, 153, 15, 66, 11, 0, 0, 0, 85, 114, 98, 97, 110, 32, 67, 121, 99, 108, 101, 55, 0, 0, 0, 68, 66, 14, 0, 0, 0, 67, 111, 109, 98, 105, 110, 101, 100, 32, 67, 121, 99, 108, 101, 75, 0, 0, 0, 32, 66, 13, 0, 0, 0, 72, 105, 103, 104, 119, 97, 121, 32, 67, 121, 99, 108, 101, 1, 0, 2, 0, 95, 6, 0, 3, 0, 30, 0, 0, 0, 128, 64, 60, 0, 0, 0, 240, 64, 100, 0, 51, 51, 67, 65, 99, 6, 0, 3, 0, 30, 0, 51, 51, 115, 64, 60, 0, 51, 51, 227, 64, 100, 0, 205, 204, 60, 65, 5, 0, 0, 0, 72, 111, 110, 100, 97, 9, 0, 0, 0, 67, 105, 118, 105, 99, 32, 86, 84, 105, 6, 0, 0, 0, 97, 98, 99, 100, 101, 102 ];

    it("should decode bytes encoded by Java codecs", () => {
        // Arrange
        const headerDecoder = new MessageHeaderDecoder();
        const buffer = new ArrayBuffer(bytes.length);
        const typedBuffer = new Int8Array(buffer);
        typedBuffer.set(bytes);
        const bufferView = new DataView(buffer, 0, bytes.length);
        const car = new CarDecoder();

        // Act
        car.wrap(bufferView, headerDecoder.encodedLength, car.sbeBlockLength, car.sbeSchemaVersion);

        // Assert
        expect(car.serialNumber()).toEqual(1234);
        expect(car.modelYear()).toEqual(2013);
        expect(car.available()).toEqual(BooleanType.T);
        expect(car.code()).toEqual(Model.A);

        for (let i = 0; i < CarDecoder.someNumbersLength(); i++) {
            expect(car.getSomeNumbers(i)).toEqual(i);
        }

        expect(car.vehicleCode()).toEqual("abcdef");

        const extras = car.extras();
        expect(extras.cruiseControl()).toEqual(true);
        expect(extras.sportsPack()).toEqual(true);
        expect(extras.sunRoof()).toEqual(false);

        const engine = car.engine();
        expect(engine.capacity()).toEqual(2000);
        expect(engine.numCylinders()).toEqual(4);
        expect(engine.manufacturerCode()).toEqual("123");
        expect(engine.efficiency()).toEqual(35);
        expect(engine.boosterEnabled()).toEqual(BooleanType.T);

        const booster = engine.booster();
        expect(booster.boostType()).toEqual(BoostType.NITROUS);
        expect(booster.horsePower()).toEqual(200);

        const fuelFigures = car.fuelFigures();
        expect(fuelFigures.count()).toEqual(3);

        fuelFigures.next();
        expect(fuelFigures.speed()).toEqual(30);
        expect(fuelFigures.mpg()).toBeCloseTo(35.9, 5);
        expect(fuelFigures.usageDescription()).toEqual("Urban Cycle");

        fuelFigures.next();
        expect(fuelFigures.speed()).toEqual(55);
        expect(fuelFigures.mpg()).toBeCloseTo(49.0, 5);
        expect(fuelFigures.usageDescription()).toEqual("Combined Cycle");

        fuelFigures.next();
        expect(fuelFigures.speed()).toEqual(75);
        expect(fuelFigures.mpg()).toBeCloseTo(40.0, 5);
        expect(fuelFigures.usageDescription()).toEqual("Highway Cycle");

        const perfFigures = car.performanceFigures();
        expect(perfFigures.count()).toEqual(2);

        perfFigures.next();
        expect(perfFigures.octaneRating()).toEqual(95);
        let acceleration = perfFigures.acceleration();
        expect(acceleration.count()).toEqual(3);
        acceleration.next();
        expect(acceleration.mph()).toEqual(30);
        expect(acceleration.seconds()).toBeCloseTo(4.0, 5);
        acceleration.next();
        expect(acceleration.mph()).toEqual(60);
        expect(acceleration.seconds()).toBeCloseTo(7.5, 5);
        acceleration.next();
        expect(acceleration.mph()).toEqual(100);
        expect(acceleration.seconds()).toBeCloseTo(12.2, 5);

        perfFigures.next();
        expect(perfFigures.octaneRating()).toEqual(99);
        acceleration = perfFigures.acceleration();
        expect(acceleration.count()).toEqual(3);
        acceleration.next();
        expect(acceleration.mph()).toEqual(30);
        expect(acceleration.seconds()).toBeCloseTo(3.8, 5);
        acceleration.next();
        expect(acceleration.mph()).toEqual(60);
        expect(acceleration.seconds()).toBeCloseTo(7.1, 5);
        acceleration.next();
        expect(acceleration.mph()).toEqual(100);
        expect(acceleration.seconds()).toBeCloseTo(11.8, 5);

        expect(car.manufacturer()).toEqual("Honda");
        expect(car.model()).toEqual("Civic VTi");
        expect(car.activationCode()).toEqual("abcdef");
    });

    it("should encode the same bytes as Java codecs", () => {
        // Arrange
        const buffer = new ArrayBuffer(bytes.length);
        const bufferView = new DataView(buffer, 0, bytes.length);
        const headerEncoder = new MessageHeaderEncoder();
        const car = new CarEncoder();

        // Act
        car.wrapAndApplyHeader(bufferView, 0, headerEncoder);
        car.serialNumber(1234);
        car.modelYear(2013);
        car.available(BooleanType.T);
        car.code(Model.A);

        for (let i = 0; i < CarDecoder.someNumbersLength(); i++) {
            car.setSomeNumbersByte(i, i);
        }

        car.vehicleCode("abcdef");

        const extras = car.extras();
        extras.cruiseControl(true);
        extras.sportsPack(true);

        const engine = car.engine();
        engine.capacity(2000);
        engine.numCylinders(4);
        engine.manufacturerCode("123");
        engine.efficiency(35);
        engine.boosterEnabled(BooleanType.T);

        const booster = engine.booster();
        booster.boostType(BoostType.NITROUS);
        booster.horsePower(200);

        const fuelFigures = car.fuelFiguresCount(3);

        fuelFigures.next();
        fuelFigures.speed(30);
        fuelFigures.mpg(35.9);
        fuelFigures.usageDescription("Urban Cycle");

        fuelFigures.next();
        fuelFigures.speed(55);
        fuelFigures.mpg(49.0);
        fuelFigures.usageDescription("Combined Cycle");

        fuelFigures.next();
        fuelFigures.speed(75);
        fuelFigures.mpg(40.0);
        fuelFigures.usageDescription("Highway Cycle");

        const perfFigures = car.performanceFiguresCount(2);

        perfFigures.next();
        perfFigures.octaneRating(95);
        let acceleration = perfFigures.accelerationCount(3);
        acceleration.next();
        acceleration.mph(30);
        acceleration.seconds(4.0);
        acceleration.next();
        acceleration.mph(60);
        acceleration.seconds(7.5);
        acceleration.next();
        acceleration.mph(100);
        acceleration.seconds(12.2);

        perfFigures.next();
        perfFigures.octaneRating(99);
        acceleration = perfFigures.accelerationCount(3);
        acceleration.next();
        acceleration.mph(30);
        acceleration.seconds(3.8);
        acceleration.next();
        acceleration.mph(60);
        acceleration.seconds(7.1);
        acceleration.next();
        acceleration.mph(100);
        acceleration.seconds(11.8);

        car.manufacturer("Honda");
        car.model("Civic VTi");
        car.activationCode("abcdef");

        // Assert
        const emitted = Array.prototype.slice.call(new Uint8Array(buffer, 0, bytes.length));
        expect(emitted).toEqual(bytes);
    });
});
