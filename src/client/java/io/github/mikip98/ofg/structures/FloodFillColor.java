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

import io.github.mikip98.del.structures.ColorRGBA;

public class FloodFillColor {
    public byte b3r, b3g, b3b;  // 3 bit colors
    public byte b4r, b4g, b4b;  // 4 bit colors

    public byte mode;
    // 0b01 -> luminance = max(R, G, B)
    // 0x10 -> luminance = luminosity.of(R, G, B)
    // 0x11 -> tinting mode (translucency, e.g. glass)


    public FloodFillColor(ColorRGBA color, byte luminance) {
        // Constructor for light emitting
        b3r = (byte) Math.round(color.r * 7); // 0b111 -> 7
        b3g = (byte) Math.round(color.g * 7); // 0b111 -> 7
        b3b = (byte) Math.round(color.b * 7); // 0b111 -> 7

        double mode1Luminance = (double) Math.max(b3r, Math.max(b3g, b3b)) / 7 * 15;
        double mode2Luminance = Math.sqrt(0.2126 * b3r / 7 + 0.7152 * b3g / 7 + 0.0722 * b3b / 7) * 15;

        double delta1 = Math.abs(mode1Luminance - luminance);
        double delta2 = Math.abs(mode2Luminance - luminance);

        // TODO: Try to manipulate the color to get the best result

        if (delta1 < delta2) {
            mode = 0b01;
        } else {
            mode = 0b10;
        }
    }
    public FloodFillColor(ColorRGBA color) {
        // Constructor for translucent
        b3r = (byte) Math.round(color.r * 7); // 0b111 -> 7
        b3g = (byte) Math.round(color.g * 7); // 0b111 -> 7
        b3b = (byte) Math.round(color.b * 7); // 0b111 -> 7
        mode = 0b11;
    }


    public short getEntryId(FloodFillFormat format) {
        switch (format) {
            case X2048:
                // 0MMR RRGG GBBB
                // Ensure the variable is within the valid range
                mode &= 0b11; // 2 bits
                // The Rest is the same as in X4096 format
            case X4096:
                // MMMR RRGG GBBB

                // Ensure the variables are within the valid range
                mode &= 0b111; // 3 bits
                b3r  &= 0b111; // 3 bits
                b3g  &= 0b111; // 3 bits
                b3b  &= 0b111; // 3 bits

                // Combine the values into a single int
                return (short) ((mode << 9) | (b3r << 6) | (b3g << 3) | b3b);

            case X8192:
                // 1 RRRR GGGG BBBB

                // Ensure the variables are within the valid range
                b4r &= 0b1111; // 4 bits
                b4g &= 0b1111; // 4 bits
                b4b &= 0b1111; // 4 bits

                // Combine the values into a single int
                return (short) ((b4r << 8) | (b4g << 4) | (b4b));

            default:
                throw new IllegalArgumentException("Unknown format: " + format);
        }
    }
}

