package jenkem;

import jenkem.shared.Engine;

public class EngineTest extends AbstractReflectionTestCase {
    private final Engine engine = new Engine();

    public final void testIsUpOrDownExtremeValues() throws Exception {
        final int offset = 0;
        final int[] top = {0, 0, 0};
        final int[] bottom = {255, 255, 255};
        final Object[] parameters = {top, bottom, offset};
        final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
        assertTrue(upOutput.booleanValue());
        final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
        assertFalse(downOutput.booleanValue());
    }

    public final void testIsDownOrUpExtremeValues() throws Exception {
        final int offset = 0;
        final int[] top = {255, 255, 255};
        final int[] bottom = {0, 0, 0};
        final Object[] parameters = {top, bottom, offset};
        final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
        assertTrue(downOutput.booleanValue());
        final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
        assertFalse(upOutput.booleanValue());
    }

    public final void testIsUpOrDownBoundaryValues() throws Exception {
        final int offset = 0;
        final int[] top = {126, 126, 126};
        final int[] bottom = {128, 128, 128};
        final Object[] parameters = {top, bottom, offset};
        final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
        assertTrue(upOutput.booleanValue());
        final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
        assertFalse(downOutput.booleanValue());
    }

    public final void testIsDownOrUpBoundaryValues() throws Exception {
        final int offset = 0;
        final int[] top = {128, 128, 128};
        final int[] bottom = {126, 126, 126};
        final Object[] parameters = {top, bottom, offset};
        final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
        assertTrue(downOutput.booleanValue());
        final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
        assertFalse(upOutput.booleanValue());
    }

    public final void testIsUpOrDownOffsetValues() throws Exception {
        final int offset = 100;
        final int[] top = {26, 26, 26};
        final int[] bottom = {228, 228, 228};
        final Object[] parameters = {top, bottom, offset};
        final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
        assertTrue(upOutput.booleanValue());
        final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
        assertFalse(downOutput.booleanValue());
    }

    public final void testIsDownOrUpOffsetValues() throws Exception {
        final int offset = 100;
        final int[] top = {228, 228, 228};
        final int[] bottom = {26, 26, 26};
        final Object[] parameters = {top, bottom, offset};
        final Boolean downOutput = (Boolean) invokePrivateMethod(engine, "isDown", parameters);
        assertTrue(downOutput.booleanValue());
        final Boolean upOutput = (Boolean) invokePrivateMethod(engine, "isUp", parameters);
        assertFalse(upOutput.booleanValue());
    }
}
