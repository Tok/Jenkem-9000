package jenkem.shared.color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import jenkem.shared.AsciiScheme;
import jenkem.shared.CharacterSet;
import jenkem.shared.Power;

/**
 * This Class is used to manipulate a Cube made of colors. X, Y, Z ---> Red,
 * Green, Blue http://en.wikipedia.org/wiki/RGB_color_space
 * http://upload.wikimedia.org/wikipedia/commons/0/03/RGB_farbwuerfel.jpg
 */
public class Cube {
    private final AsciiScheme asciiScheme = new AsciiScheme();
    private final Random random = new Random();
    public static final int NO_OFFSET = 0;
    public static final int NEGATIVE_OFFSET = -10;
    public static final int POSITIVE_OFFSET = 10;
    private Power power = Power.Linear;

    /**
     * Translates the RGB values of the pixel to a colored IRC character.
     * @param colorMap Map<String, Integer>
     * @param preset selected CharacterSet
     * @param red 0-255
     * @param green 0-255
     * @param blue 0-255
     * @param enforceBlackFg true if foreground color should be enforced to be black
     * @return a String with the IRC-color codes and the character to display in IRC
     */
    private String getColorChar(final Map<String, Integer> colorMap,
            final CharacterSet preset, final int red, final int green,
            final int blue, final boolean enforceBlackFg) {
        final int[] col = {red, green, blue};
        final Color c = getTwoNearestColors(colorMap, col);
        final StringBuilder result = new StringBuilder();
        if (!enforceBlackFg) {
            result.append(c.getFg()); // append the foreground color
        } else {
            result.append("1"); // ignore the actual foreground color and use a
                                // black (=1) characters
            // XXX why not use black or white again?
        }
        result.append(",");
        result.append(c.getBg()); // append the background color
        final String character = asciiScheme.getChar(c.getBgStrength(), preset, AsciiScheme.StrengthType.RELATIVE);
        result.append(character); // append the selected ASCII character
        return result.toString();
    }

    public final String getColorChar(final Map<String, Integer> colorMap,
            final CharacterSet preset, final int[] rgb,
            final boolean enforceBlackFg) {
        return getColorChar(colorMap, preset, rgb[0], rgb[1], rgb[2], enforceBlackFg);
    }

    public final String getColorChar(final Map<String, Integer> colorMap,
            final CharacterSet preset, final int[] rgb) {
        return getColorChar(colorMap, preset, rgb[0], rgb[1], rgb[2], false);
    }

    public final String getColorChar(final Map<String, Integer> colorMap,
            final CharacterSet preset, final int red, final int green,
            final int blue) {
        return getColorChar(colorMap, preset, red, green, blue, false);
    }

    public final String getColorChar(final Map<String, Integer> colorMap,
            final CharacterSet preset, final Sample sample,
            final Sample.Xdir xDir) {
        return getColorChar(colorMap, preset, sample.getRgbValues(xDir));
    }

    private WeightedColor createWc(final Map<String, Integer> colorMap,
            final int[] col, final String name) {
        final int[] coords = IrcColor.valueOf(name).getRgb();
        final double weight = calcStrength(col, coords, colorMap.get(name));
        final WeightedColor wc = WeightedColor.getInstance(name, coords, weight);
        return wc;
    }

    private double calcStrength(final int[] col, final int[] comp, final double factor) {
        return calcDistance(col, comp) / factor;
    }

    /**
     * This method is where everything happens. it's the tongue of jenkem.
     * @param colorMap the colormap to use
     * @param col an array with three RGB values representing the pixel to translate.
     * @return a Color object with info to represent the same color in irc
     */
    public final Color getTwoNearestColors(final Map<String, Integer> colorMap,
            final int[] col) {
        final ArrayList<WeightedColor> list = new ArrayList<WeightedColor>();
        for (final IrcColor ic : IrcColor.values()) {
            list.add(createWc(colorMap, col, ic.name()));
        }

        // if the list isn't shuffled the following occurs:
        // the color that happens to be the 1st in the collection is used in
        // favor of the others,
        // when more possible values would apply with the same strength.
        // this is not good, because it often favors one random color of (RGB)
        // and of (CYM) over the others.
        // instead, all possibilities of R, G or B should use Black and C, Y or
        // M should use White instead of the color that
        // is selected by doing nothing.
        // doing this could potentially have a good effect on the output of
        // colorless images with alot of pixels
        // on the black-white scale
        // (=represented by the black to white diagonal in the cube, which has
        // the same distance to all the 3 RGB and the 3 CMY edges).

        // WARNING: this shuffling method may take alot of CPU
        shuffle(list); // shuffle to randomize colors with the same weight

        final SortedMap<Double, WeightedColor> map = new TreeMap<Double, WeightedColor>();
        for (final WeightedColor wc : list) {
            map.put(wc.getWeight(), wc);
        }

        final Iterator<Map.Entry<Double, WeightedColor>> i = map.entrySet().iterator();
        final Map.Entry<Double, WeightedColor> strongest = i.next();
        final Map.Entry<Double, WeightedColor> second = i.next();

        final Color c = new Color();
        c.setRgb(col);
        c.setBg(strongest.getValue().getColor());
        c.setBgRgb(strongest.getValue().getCoords());
        c.setFg(second.getValue().getColor());
        c.setFgRgb(second.getValue().getCoords());

        // strength is used to calculate which character to return.
        // TODO XXX FIXME tune and explain this, #math suggested Vorni diagrams.
        // ..using a variable for power and the formula applied in calcStrength
        // is just an approximative workaround. instead there should be a
        // mathematically provable correct way on how to weigh two colors
        // against each other in regard to their distance to the
        // center of the cube 127,127,127

        final double p = power.getValue();
        final double strongestStrength =
                Math.pow(calcStrength(col, strongest.getValue().getCoords(),
                        colorMap.get(strongest.getValue().getName())), p);
        final double secondStrength =
                Math.pow(calcStrength(col, second.getValue().getCoords(),
                        colorMap.get(second.getValue().getName())), p);
        final double strength = strongestStrength / secondStrength;
        c.setBgStrength(strength);

        return c;
    }

    public final Color getTwoNearestColors(final Map<String, Integer> colorMap,
            final int red, final int green, final int blue) {
        final int[] col = {red, green, blue};
        return getTwoNearestColors(colorMap, col);
    }

    /**
     * Calculates the distance between two points (=colors) in the RGB
     * color-space cube (255^3).
     * @param from RGB colors
     * @param to RGB colors
     * @return the distance between the colors
     */
    private double calcDistance(final int[] from, final int[] to) {
        final double fromRed = from[0];
        final double fromGreen = from[1];
        final double fromBlue = from[2];
        final double toRed = to[0];
        final double toGreen = to[1];
        final double toBlue = to[2];
        final double distance = Math.sqrt((Math.pow(toRed - fromRed, 2.0)
                + Math.pow(toGreen - fromGreen, 2.0) + Math.pow(toBlue
                - fromBlue, 2.0)));
        return distance;
    }

    public final boolean isFirstCloserTo(final int[] first, final int[] second,
            final int[] compare, final double offset) {
        final double firstDist = calcDistance(first, compare);
        final double secondDist = calcDistance(second, compare);
        return ((firstDist + offset) < secondDist);
    }

    /**
     * Compares two colors to a third and returns true if the first one is
     * closer to the third than the second.
     * @param first rgb values of the first color to compare
     * @param second rgb values of the second color to compare
     * @param compare rgb values of the color to compare the others to
     * @return boolean that is true if the first color is closer to the comparation
     */
    public final boolean isFirstCloserTo(final int[] first, final int[] second,
            final int[] compare) {
        // TODO this is awkward. replace this method with a method
        // returning an int that is negative, 0 or positive
        final double firstDist = calcDistance(first, compare);
        final double secondDist = calcDistance(second, compare);
        return (firstDist < secondDist);
    }

    /**
     * Shuffles the weighted color List.
     * @param colors the colors to shuffle
     */
    private void shuffle(final List<WeightedColor> colors) {
        for (int i = colors.size(); i > 1; i--) {
            swap(colors, i - 1, random.nextInt(colors.size()));
        }
    }

    /**
     * Swap method for shuffling.
     * @param list to swap values
     * @param i first index
     * @param ii second index
     */
    private void swap(final List<WeightedColor> list, final int i, final int ii) {
        final WeightedColor s = list.get(i);
        list.set(i, list.get(ii));
        list.set(ii, s);
    }


    public final void setPower(final Power power) {
        this.power = power;
    }
}
