import colour
import sys

def sRGB2Munsell(sRGB):
    C = colour.CCS_ILLUMINANTS["CIE 1931 2 Degree Standard Observer"]["C"]

    Munsell = colour.xyY_to_munsell_colour(colour.XYZ_to_xyY(colour.sRGB_to_XYZ(sRGB, C)))
    print(Munsell)
    return Munsell

Munsell = None
sRGB = [float (sys.argv[1]), float(sys.argv[2]), float(sys.argv[3])]
sRGBSave = [sRGB[0], sRGB[1], sRGB[2]]
i = 0
k = 0
while (Munsell == None):
    try:
        Munsell = sRGB2Munsell(sRGB)
    except:
        sRGB[i] += 0.01
        if i < 2:
            i += 1
        else:
            i = 0
        if k > 9:
            sRGB = sRGBSave
            sRGB[i] += 0.05
            k += 1
        elif k > 12:
            sRGB = sRGBSave
            sRGB[i] += 0.07
            k += 1
        elif k > 15:
            sRGB = [0.2, 0.4, 0.6]
            Munsell = sRGB2Munsell(sRGB)
        else:
            k += 1
