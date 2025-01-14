package io.github.mikip98.ofg.structures;

// 3 formats:
//
// 1) 0MMR RRGG GBBB	for x2048
//	  MODE:
//	  	- 0 -> off
//	  	- 1 -> light_range = max(R, G, B)
//	  	- 2 -> light_range = luminosity.of(R, G, B)
//	  	- 3 -> tinting mode (glass)
//
// 2) MMMR RRGG GBBB	for x4096
//	  MODE:
//	  	- 0 -> off
//	  	- 1 -> light_range = max(R, G, B)
//	  	- 2 -> light_range = luminosity.of(R, G, B)
//	  	- 3 -> tinting mode (glass)
//		- 4 -> *Reserved for future use*
//
// 3) 1 RRRR GGGG BBBB	for x8192
//
// M -> MODE
// R -> RED
// G -> GREEN
// B -> BLUE
// 1 -> required bit 1 (switch)
// 0 -> required bit 0

// Formats:
// 1) MMDD DDDD DDDD    for x4096
//    M -> mode
//    Modes:
//     0 (0b00) -> off
//     1 (0b01) -> HSV mode:
//         MMHH HSSS VVVV:
//             H -> hue
//             S -> saturation
//             V -> value
//     2 (0b10) -> YCbCr mode:
//         MMRR RBBB YYYY:
//             R -> Cr -> red-difference
//             B -> Cb -> blue-difference
//             Y -> luminosity
//     3 (0b11) -> tinting mode (glass):
//         MMGG GGRR RBBB:
//             G -> green
//             R -> red
//             B -> blue

import io.github.mikip98.del.structures.ColorRGBA;

import java.awt.*;

public class FloodFillColor {
//    public byte b3r, b3g, b3b;  // 3 bit colors
//    public byte b4r, b4g, b4b;  // 4 bit colors
//
//    public byte mode;
    // 0b01 -> luminance = max(R, G, B)
    // 0x10 -> luminance = luminosity.of(R, G, B)
    // 0x11 -> tinting mode (translucency, e.g. glass)

    private ColorRGBA color;
    private byte luminance;


    public FloodFillColor(ColorRGBA color, byte luminance) {
        this.color = color;
        this.luminance = luminance;
    }
    public FloodFillColor(ColorRGBA color) {
        this.color = color;
    }


    // MMHH HSSS VVVV
    public short getEmissiveDataModeHSV() {
        float[] hsb = Color.RGBtoHSB(
                (int) Math.round(color.r),
                (int) Math.round(color.g),
                (int) Math.round(color.b),
                null
        );

        byte hue = (byte) Math.round(hsb[0] * 7);
        byte saturation = (byte) Math.round(hsb[1] * 7);
        byte value = luminance;

        return (short) ((0b01 << 10) | (hue << 7) | (saturation << 4) | value);
    }

    // MMRR RBBB YYYY
    public short getEmissiveDataModeYCbCr() {
        double Kb = 0.0722;
        double Kr = 0.2126;
        double Kg = 1 - Kr - Kb; // 0.7152

        double Y = Kr * color.r + Kg * color.g + Kb * color.b;
        double Pb = 0.5 * (color.b - Y) / (1 - Kb);
        double Pr = 0.5 * (color.r - Y) / (1 - Kr);

        byte Cr = (byte) Math.round(Pr * 7);
        byte Cb = (byte) Math.round(Pb * 7);
        byte Yl = luminance;

        return (short) ((0b10 << 10) | (Cr << 7) | (Cb << 4) | Yl);
    }

    // MMGG GGRR RBBB
    public short getTintData() {
        byte R = (byte) Math.round(color.r * 7);
        byte G = (byte) Math.round(color.g * 15);
        byte B = (byte) Math.round(color.b * 7);

        return (short) ((0b11 << 10) | (G << 6) | (R << 3) | B);
    }


//    public FloodFillColor(ColorRGBA color, byte luminance) {
        // Constructor for light emitting

//        // Format 1 (0MMR RRGG GBBB)
//        byte r = (byte) Math.round(color.r * 7); // 0b111 -> 7
//        byte g = (byte) Math.round(color.g * 7); // 0b111 -> 7
//        byte b = (byte) Math.round(color.b * 7); // 0b111 -> 7
//
//        double mode1Luminance = (double) Math.max(b3r, Math.max(b3g, b3b)) / 7 * 15;
//        double mode2Luminance = Math.sqrt(0.2126 * b3r / 7 + 0.7152 * b3g / 7 + 0.0722 * b3b / 7) * 15;
//
//        double delta1 = Math.abs(mode1Luminance - luminance);
//        double delta2 = Math.abs(mode2Luminance - luminance);
//
//        // TODO: Try to manipulate the color to get the best result
//
//        short mode;
//        if (delta1 < delta2) {
//            mode = 0b01;
//        } else {
//            mode = 0b10;
//        }
//
//        format1Id = (short) ((mode << 9) | (r << 6) | (g << 3) | b);
//
//        // Format 2 (MMHH HSSS LLLL)
//        float[] hsv = new float[3];
//        Color.RGBtoHSB(
//                (int) Math.round(color.r * 255),
//                (int) Math.round(color.g * 255),
//                (int) Math.round(color.b * 255),
//                hsv
//        ); // Convert to HSV;
//
//        byte h = (byte) Math.round(hsv[0] * 7); // 0b111 -> 7
//        byte s = (byte) Math.round(hsv[1] * 7); // 0b111 -> 7
//        byte v = (byte) Math.round(hsv[2] * 7); // 0b111 -> 7
//
//        format2Id = (short) ((mode << 12) | (h << 9) | (s << 6) | (v << 3));
//
//        // Format 3 (M MLLL LGGG BBBB)
//    }
//    public FloodFillColor(ColorRGBA color) {
//        // Constructor for translucent
//        byte r = (byte) Math.round(color.r * 7); // 0b111 -> 7
//        byte g = (byte) Math.round(color.g * 7); // 0b111 -> 7
//        byte b = (byte) Math.round(color.b * 7); // 0b111 -> 7
//        byte mode = 0b11;
//
//        format3Id = (short) ((mode << 9) | (r << 6) | (g << 3) | b);
//    }


//    public short getEntryId(FloodFillFormat format) {
//        switch (format) {
//            case X2048:
//                // 0MMR RRGG GBBB
//                // Ensure the variable is within the valid range
//                mode &= 0b11; // 2 bits
//                // The Rest is the same as in X4096 format
//            case X4096:
//                // MMMR RRGG GBBB
//
//                // Ensure the variables are within the valid range
//                mode &= 0b111; // 3 bits
//                b3r  &= 0b111; // 3 bits
//                b3g  &= 0b111; // 3 bits
//                b3b  &= 0b111; // 3 bits
//
//                // Combine the values into a single int
//                return (short) ((mode << 9) | (b3r << 6) | (b3g << 3) | b3b);
//
//            case X8192:
//                // 1 RRRR GGGG BBBB
//
//                // Ensure the variables are within the valid range
//                b4r &= 0b1111; // 4 bits
//                b4g &= 0b1111; // 4 bits
//                b4b &= 0b1111; // 4 bits
//
//                // Combine the values into a single int
//                return (short) ((b4r << 8) | (b4g << 4) | (b4b));
//
//            default:
//                throw new IllegalArgumentException("Unknown format: " + format);
//        }
//    }
}

