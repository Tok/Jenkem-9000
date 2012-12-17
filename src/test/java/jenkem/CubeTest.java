package jenkem;

import java.util.HashMap;
import java.util.Map;
import jenkem.shared.CharacterSet;
import jenkem.shared.ColorScheme;
import jenkem.shared.color.Cube;
import jenkem.shared.color.IrcColor;

public class CubeTest extends AbstractReflectionTestCase {
    private static final double CENTER_TO_NEAR_EDGE_DISTANCE = 219.9704525612474;
    private static final double CENTER_TO_FAR_EDGE_DISTANCE = 221.70250336881628;
    private static final double SIDE_DISTANCE = 255.0;
    private static final double AREA_DIAGONAL_DISTANCE = 360.62445840513925;
    private static final double SPACE_DIAGONAL_DISTANCE = 441.6729559300637;

    private final Cube cube = new Cube();

    public final void testSamePointDistance() throws Exception {
        final int[] from = {0, 0, 0};
        final int[] to = {0, 0, 0};
        final int[][] parameters = {from, to};
        final Double distance = (Double) invokePrivateMethod(cube,
                "calcDistance", parameters);
        assertEquals(0.0, distance.doubleValue());
    }

    public final void testCenterToNearEdgeDistance() throws Exception {
        final int[] from = {127, 127, 127};
        final int[] to = {0, 0, 0};
        final int[][] parameters = {from, to};
        final Double distance = (Double) invokePrivateMethod(cube,
                "calcDistance", parameters);
        assertEquals(CENTER_TO_NEAR_EDGE_DISTANCE, distance.doubleValue());
    }

    public final void testCenterToFarEdgeDistance() throws Exception {
        final int[] from = {127, 127, 127};
        final int[] to = {255, 255, 255};
        final int[][] parameters = {from, to};
        final Double distance = (Double) invokePrivateMethod(cube,
                "calcDistance", parameters);
        assertEquals(CENTER_TO_FAR_EDGE_DISTANCE, distance.doubleValue());
    }

    public final void testSideDistance() throws Exception {
        final int[] from = {0, 0, 0};
        final int[] to = {255, 0, 0};
        final int[][] parameters = {from, to};
        final Double distance = (Double) invokePrivateMethod(cube,
                "calcDistance", parameters);
        assertEquals(SIDE_DISTANCE, distance.doubleValue());
    }

    public final void testAreaDiagonalDistance() throws Exception {
        final int[] from = {0, 0, 0};
        final int[] to = {255, 255, 0};
        final int[][] parameters = {from, to};
        final Double distance = (Double) invokePrivateMethod(cube,
                "calcDistance", parameters);
        assertEquals(AREA_DIAGONAL_DISTANCE, distance.doubleValue());
    }

    public final void testSpaceDiagonalDistance() throws Exception {
        final int[] from = {0, 0, 0}; // black
        final int[] to = {255, 255, 255}; // white
        final int[][] parameters = {from, to};
        final Double distance = (Double) invokePrivateMethod(cube,
                "calcDistance", parameters);
        assertEquals(SPACE_DIAGONAL_DISTANCE, distance.doubleValue());
    }

    public final void testCenterCloserToNearEdge() throws Exception {
        final int[] first = {0, 0, 0}; // black
        final int[] second = {255, 255, 255}; // white
        final int[] compare = {127, 127, 127}; // gray
        assertTrue(cube.isFirstCloserTo(first, second, compare));
        assertTrue(cube.isFirstCloserTo(first, second, compare, Cube.NO_OFFSET));
        assertTrue(cube.isFirstCloserTo(first, second, compare,
                Cube.NEGATIVE_OFFSET));
        assertFalse(cube.isFirstCloserTo(first, second, compare,
                Cube.POSITIVE_OFFSET));
    }

    public final void testRedCloserToBlackThanWhite() throws Exception {
        final int[] red = {255, 0, 0};
        final int[] black = {0, 0, 0};
        final int[] white = {255, 255, 255};
        assertTrue(cube.isFirstCloserTo(red, black, white));
        assertTrue(cube.isFirstCloserTo(red, black, white, 0));
    }

    public final void testYellowCloserToWhiteThanBlack() throws Exception {
        final int[] yellow = {255, 255, 0};
        final int[] black = {0, 0, 0};
        final int[] white = {255, 255, 255};
        assertTrue(cube.isFirstCloserTo(yellow, white, black));
        assertTrue(cube.isFirstCloserTo(yellow, white, black, 0));
    }

    public final void testColorCharBlack() throws Exception {
        for (final ColorScheme scheme : ColorScheme.values()) {
            final Map<String, Integer> colorMap = new HashMap<String, Integer>();
            for (final IrcColor ic : IrcColor.values()) {
                colorMap.put(ic.name(), ic.getOrder(scheme));
            }
            for (final CharacterSet set : CharacterSet.values()) {
                final int[] black = {0, 0, 0};
                final String result = cube.getColorChar(colorMap, set, black, false);
                final String[] splitted = result.split(",");
                final String backgroundAndChar = splitted[splitted.length - 1];
                final String background = backgroundAndChar.substring(
                        backgroundAndChar.length() - 2,
                        backgroundAndChar.length() - 1);
                assertEquals(IrcColor.black.getValue().intValue(),
                        Integer.parseInt(background));
            }
        }
    }

    public final void testColorCharRed() throws Exception {
        for (final ColorScheme scheme : ColorScheme.values()) {
            // exclude schemes without red
            if (!scheme.equals(ColorScheme.Bw) && !scheme.equals(ColorScheme.Bwg)) {
                final Map<String, Integer> colorMap = new HashMap<String, Integer>();
                for (final IrcColor ic : IrcColor.values()) {
                    colorMap.put(ic.name(), ic.getOrder(scheme));
                }
                for (final CharacterSet set : CharacterSet.values()) {
                    final int[] red = {255, 0, 0};
                    final String result = cube.getColorChar(colorMap, set, red, false);
                    final String[] splitted = result.split(",");
                    final String backgroundAndChar = splitted[splitted.length - 1];
                    final String background =
                            backgroundAndChar.substring(
                            backgroundAndChar.length() - 2,
                            backgroundAndChar.length() - 1);
                    assertEquals(IrcColor.red.getValue().intValue(),
                            Integer.parseInt(background));
                }
            }
        }
    }

    public final void testColorCharYellow() throws Exception {
        for (final ColorScheme scheme : ColorScheme.values()) {
            // exclude schemes without yellow
            if (!scheme.equals(ColorScheme.Bw) && !scheme.equals(ColorScheme.Bwg)) {
                final Map<String, Integer> colorMap = new HashMap<String, Integer>();
                for (final IrcColor ic : IrcColor.values()) {
                    colorMap.put(ic.name(), ic.getOrder(scheme));
                }
                for (final CharacterSet set : CharacterSet.values()) {
                    final int[] yellow = {255, 255, 0};
                    final String result = cube.getColorChar(colorMap, set,
                            yellow, false);
                    final String[] splitted = result.split(",");
                    final String backgroundAndChar = splitted[splitted.length - 1];
                    final String background =
                            backgroundAndChar.substring(
                            backgroundAndChar.length() - 2,
                            backgroundAndChar.length() - 1);
                    assertEquals(IrcColor.yellow.getValue().intValue(),
                            Integer.parseInt(background));
                }
            }
        }
    }

    public final void testColorCharPurple() throws Exception {
        for (final ColorScheme scheme : ColorScheme.values()) {
            // exclude schemes without purple
            if (!scheme.equals(ColorScheme.Bw)
                    && !scheme.equals(ColorScheme.Bwg)
                    && !scheme.equals(ColorScheme.Mono)) {
                final Map<String, Integer> colorMap = new HashMap<String, Integer>();
                for (final IrcColor ic : IrcColor.values()) {
                    colorMap.put(ic.name(), ic.getOrder(scheme));
                }
                for (final CharacterSet set : CharacterSet.values()) {
                    final int[] purple = {156, 0, 156};
                    final String result = cube.getColorChar(colorMap, set, purple, false);
                    final String[] splitted = result.split(",");
                    final String backgroundAndChar = splitted[splitted.length - 1];
                    final String background =
                            backgroundAndChar.substring(
                            backgroundAndChar.length() - 2,
                            backgroundAndChar.length() - 1);
                    assertEquals(IrcColor.purple.getValue().intValue(),
                            Integer.parseInt(background));
                }
            }
        }
    }

    public final void testColorCharGray() throws Exception {
        for (final ColorScheme scheme : ColorScheme.values()) {
            if (!scheme.equals(ColorScheme.Bw)
                    && !scheme.equals(ColorScheme.Mono)
                    && !scheme.equals(ColorScheme.Lsd)) { // exclude schemes with no or almost no gray
                final Map<String, Integer> colorMap = new HashMap<String, Integer>();
                for (final IrcColor ic : IrcColor.values()) {
                    colorMap.put(ic.name(), ic.getOrder(scheme));
                }
                for (final CharacterSet set : CharacterSet.values()) {
                    final int[] gray = {127, 127, 127};
                    final String result = cube.getColorChar(colorMap, set, gray, false);
                    final String[] splitted = result.split(",");
                    final String backgroundAndChar = splitted[splitted.length - 1];
                    final String background =
                            backgroundAndChar.substring(
                            backgroundAndChar.length() - 3,
                            backgroundAndChar.length() - 1);
                    assertEquals(IrcColor.gray.getValue().intValue(), Integer.parseInt(background));
                }
            }
        }
    }

}
