package jenkem.client;

import java.util.Map;

import jenkem.shared.CharacterSet;
import jenkem.shared.ColorScheme;
import jenkem.shared.Engine;
import jenkem.shared.color.Cube;
import jenkem.shared.color.IrcColor;

public class CubeTester extends AbstractReflectionTestCase {
	final Cube cube = new Cube();
	final Engine engine = new Engine(null);

	public void testSamePointDistance() throws Exception {
		final int[] from = { 0, 0, 0 };
		final int[] to = { 0, 0, 0 };
		final int[][] parameters = { from, to };
		Double distance = (Double) invokePrivateMethod(cube, "calcDistance", parameters);
		assertEquals(0.0, distance.doubleValue());
	}
	
	public void testCenterToNearEdgeDistance() throws Exception {
		final int[] from = { 127, 127, 127 };
		final int[] to = { 0, 0, 0 };
		final int[][] parameters = { from, to };
		Double distance = (Double) invokePrivateMethod(cube, "calcDistance", parameters);
		assertEquals(219.9704525612474, distance.doubleValue());
	}

	public void testCenterToFarEdgeDistance() throws Exception {
		final int[] from = { 127, 127, 127 };
		final int[] to = { 255, 255, 255 };
		final int[][] parameters = { from, to };
		Double distance = (Double) invokePrivateMethod(cube, "calcDistance", parameters);
		assertEquals(221.70250336881628, distance.doubleValue());
	}

	public void testSideDistance() throws Exception {
		final int[] from = { 0, 0, 0 };
		final int[] to = { 255, 0, 0 };
		final int[][] parameters = { from, to };
		Double distance = (Double) invokePrivateMethod(cube, "calcDistance", parameters);
		assertEquals(255.0, distance.doubleValue());
	}

	public void testAreaDiagonalDistance() throws Exception {
		final int[] from = { 0, 0, 0 };
		final int[] to = { 255, 255, 0 };
		final int[][] parameters = { from, to };
		Double distance = (Double) invokePrivateMethod(cube, "calcDistance", parameters);
		assertEquals(360.62445840513925, distance.doubleValue());
	}

	public void testSpaceDiagonalDistance() throws Exception {
		final int[] from = { 0, 0, 0 }; //black
		final int[] to = { 255, 255, 255 }; //white
		final int[][] parameters = { from, to };
		Double distance = (Double) invokePrivateMethod(cube, "calcDistance", parameters);
		assertEquals(441.6729559300637, distance.doubleValue());
	}
	
	public void testCenterCloserToNearEdge() throws Exception {
		final int[] first = { 0, 0, 0 }; //black
		final int[] second = { 255, 255, 255 }; //white
		final int[] compare = { 127, 127, 127 }; //gray
		assertTrue(cube.isFirstCloserTo(first, second, compare));
		assertTrue(cube.isFirstCloserTo(first, second, compare, 0));
		assertTrue(cube.isFirstCloserTo(first, second, compare, -10));
		assertFalse(cube.isFirstCloserTo(first, second, compare, 10));
	}

	public void testRedCloserToBlackThanWhite() throws Exception {
		final int[] red = { 255, 0, 0 };
		final int[] black = { 0, 0, 0 };
		final int[] white = { 255, 255, 255 }; 
		assertTrue(cube.isFirstCloserTo(red, black, white));
		assertTrue(cube.isFirstCloserTo(red, black, white, 0));
	}

	public void testYellowCloserToWhiteThanBlack() throws Exception {
		final int[] yellow = { 255, 255, 0 };
		final int[] black = { 0, 0, 0 };
		final int[] white = { 255, 255, 255 };
		assertTrue(cube.isFirstCloserTo(yellow, white, black));
		assertTrue(cube.isFirstCloserTo(yellow, white, black, 0));
	}

	public void testColorCharBlack() throws Exception {
		for (final ColorScheme scheme : ColorScheme.values()) {
			Object[] schemeParameter = { scheme };
			final Object object = invokePrivateMethod(engine, "prepareColorMap", schemeParameter);
			@SuppressWarnings("unchecked")
			final Map<String, Integer> colorMap = (Map<String, Integer>) object; 
			for (CharacterSet set : CharacterSet.values()) {
				final String result = cube.getColorChar(colorMap, set, 0, 0, 0, false);
				final String[] splitted = result.split(",");
				final String backgroundAndChar = splitted[splitted.length-1];
				final String background = backgroundAndChar.substring(backgroundAndChar.length() -2, backgroundAndChar.length() -1);
				assertEquals(IrcColor.black.getValue().intValue(), Integer.parseInt(background));
			}
		}
	}
	
	public void testColorCharRed() throws Exception {
		for (final ColorScheme scheme : ColorScheme.values()) {
			if (!scheme.equals(ColorScheme.Bw) && !scheme.equals(ColorScheme.Bwg)) { //exclude schemes without red
				Object[] schemeParameter = { scheme };
				final Object object = invokePrivateMethod(engine, "prepareColorMap", schemeParameter);
				@SuppressWarnings("unchecked")
				final Map<String, Integer> colorMap = (Map<String, Integer>) object; 
				for (CharacterSet set : CharacterSet.values()) {
					final String result = cube.getColorChar(colorMap, set, 255, 0, 0, false);
					final String[] splitted = result.split(",");
					final String backgroundAndChar = splitted[splitted.length-1];
					final String background = backgroundAndChar.substring(backgroundAndChar.length() -2, backgroundAndChar.length() -1);
					assertEquals(IrcColor.red.getValue().intValue(), Integer.parseInt(background));
				}
			}
		}
	}

	public void testColorCharYellow() throws Exception {
		for (final ColorScheme scheme : ColorScheme.values()) {
			if (!scheme.equals(ColorScheme.Bw) && !scheme.equals(ColorScheme.Bwg)) { //exclude schemes without yellow
				Object[] schemeParameter = { scheme };
				final Object object = invokePrivateMethod(engine, "prepareColorMap", schemeParameter);
				@SuppressWarnings("unchecked")
				final Map<String, Integer> colorMap = (Map<String, Integer>) object; 
				for (CharacterSet set : CharacterSet.values()) {
					final String result = cube.getColorChar(colorMap, set, 255, 255, 0, false);
					final String[] splitted = result.split(",");
					final String backgroundAndChar = splitted[splitted.length-1];
					final String background = backgroundAndChar.substring(backgroundAndChar.length() -2, backgroundAndChar.length() -1);
					assertEquals(IrcColor.yellow.getValue().intValue(), Integer.parseInt(background));
				}
			}
		}
	}

	public void testColorCharPurple() throws Exception {
		for (final ColorScheme scheme : ColorScheme.values()) {
			if (!scheme.equals(ColorScheme.Bw) && !scheme.equals(ColorScheme.Bwg) && !scheme.equals(ColorScheme.Mono)) { //exclude schemes without purple
				Object[] schemeParameter = { scheme };
				final Object object = invokePrivateMethod(engine, "prepareColorMap", schemeParameter);
				@SuppressWarnings("unchecked")
				final Map<String, Integer> colorMap = (Map<String, Integer>) object; 
				for (CharacterSet set : CharacterSet.values()) {
					final String result = cube.getColorChar(colorMap, set, 156, 0, 156, false);
					final String[] splitted = result.split(",");
					final String backgroundAndChar = splitted[splitted.length-1];
					final String background = backgroundAndChar.substring(backgroundAndChar.length() -2, backgroundAndChar.length() -1);
					assertEquals(IrcColor.purple.getValue().intValue(), Integer.parseInt(background));
				}
			}
		}
	}

	public void testColorCharGray() throws Exception {
		for (final ColorScheme scheme : ColorScheme.values()) {
			if (!scheme.equals(ColorScheme.Bw) && !scheme.equals(ColorScheme.Mono) 
					&& !scheme.equals(ColorScheme.Lsd) && !scheme.equals(ColorScheme.Skin)) { //exclude schemes with no or almost no gray
				Object[] schemeParameter = { scheme };
				final Object object = invokePrivateMethod(engine, "prepareColorMap", schemeParameter);
				@SuppressWarnings("unchecked")
				final Map<String, Integer> colorMap = (Map<String, Integer>) object; 
				for (CharacterSet set : CharacterSet.values()) {
					final String result = cube.getColorChar(colorMap, set, 127, 127, 127, false);
					final String[] splitted = result.split(",");
					final String backgroundAndChar = splitted[splitted.length-1];
					final String background = backgroundAndChar.substring(backgroundAndChar.length() -3, backgroundAndChar.length() -1);
					assertEquals(IrcColor.gray.getValue().intValue(), Integer.parseInt(background));
				}
			}
		}
	}
}
