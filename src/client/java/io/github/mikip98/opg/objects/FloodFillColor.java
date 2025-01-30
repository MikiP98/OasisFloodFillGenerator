package io.github.mikip98.opg.objects;

import io.github.mikip98.del.structures.ColorRGBA;

import java.awt.*;

// Format:
// 1) M MDDD DDDD DDDD    for x4096
//    M -> mode
//    Modes:
//     0 (0b00) -> off
//     1 (0b01) -> HSV mode:
//         0 1HHH HSSS VVVV:
//             H -> hue
//             S -> saturation
//             V -> value
//     2/3 (0b1X) -> tinting mode (glass):
//         1 RRRR GGGG BBBB:
//             R -> red
//             G -> green
//             B -> blue

public class FloodFillColor {
    private final ColorRGBA color;
    private final byte luminance;


    public FloodFillColor(ColorRGBA color, byte luminance) {
        this.color = color;
        this.luminance = luminance;
    }
    public FloodFillColor(ColorRGBA color) {
        this.color = color;
        this.luminance = 0;
    }


    // 0 1HHH HSSS VVVV
    public short getEmissiveDataModeHSV() {
        float[] hsb = Color.RGBtoHSB(
                (int) Math.round(color.r * 255),
                (int) Math.round(color.g * 255),
                (int) Math.round(color.b * 255),
                null
        );

        byte hue = (byte) Math.round(hsb[0] * 15);
        byte saturation = (byte) Math.round(hsb[1] * 7);

        return (short) ((0b01 << 11) | (hue << 7) | (saturation << 4) | luminance);
    }

    // 1 RRRR GGGG BBBB
    public short getTintData() {
        byte R = (byte) Math.round(color.r * 15);
        byte G = (byte) Math.round(color.g * 15);
        byte B = (byte) Math.round(color.b * 15);

        return (short) ((0b1 << 12) | (R << 8) | (G << 4) | B);
    }

    // MMRR RBBB YYYY
//    public short getEmissiveDataModeYCbCr() {
//        double Kb = 0.0722;
//        double Kr = 0.2126;
//        double Kg = 1 - Kr - Kb; // 0.7152
//
//        double Y = Kr * color.r + Kg * color.g + Kb * color.b;
//        double Pb = 0.5 * (color.b - Y) / (1 - Kb);
//        double Pr = 0.5 * (color.r - Y) / (1 - Kr);
//
//        byte Cr = (byte) Math.round(Pr * 7);
//        byte Cb = (byte) Math.round(Pb * 7);
//        byte Yl = luminance;
//
//        return (short) ((0b10 << 10) | (Cr << 7) | (Cb << 4) | Yl);
//    }
}

