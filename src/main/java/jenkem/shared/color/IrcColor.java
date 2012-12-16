package jenkem.shared.color;

import java.util.HashMap;
import java.util.Map;
import jenkem.shared.ColorScheme;

/**
 * Enum to represent IRC colors.
 */
public enum IrcColor {
    //   IRC,                dark,                             def, old, viv, mon, lsd, ski, bwg, bw
    white(0,      new int[] {255, 255, 255}, false, new int[] {100, 100,  90, 100,  10,  90, 100, 100}),
    black(1,      new int[] {0,     0,   0},  true, new int[] {100, 100,  90, 100,  10,  90, 100, 100}),
    darkBlue(2,   new int[] {0,     0, 127},  true, new int[] {70,   65,  70,   0,  75,  60,   0,   0}),
    darkGreen(3,  new int[] {0,   147,   0},  true, new int[] {75,   65,  70,   0,  75,  60,   0,   0}),
    red(4,        new int[] {255,   0,   0},  true, new int[] {100, 100, 100, 100, 100,  90,   0,   0}),
    brown(5,      new int[] {127,   0,   0},  true, new int[] {65,   65,  70,   0,  75,  75,   0,   0}),
    purple(6,     new int[] {156,   0, 156},  true, new int[] {55,   60,  65,   0,  60,  20,   0,   0}),
    orange(7,     new int[] {252, 127,   0}, false, new int[] {75,   80,  85,   0,  75,  75,   0,   0}),
    yellow(8,     new int[] {255, 255,   0}, false, new int[] {100, 100, 100, 100, 100, 100,   0,   0}),
    green(9,      new int[] {0,   255,   0}, false, new int[] {100, 100, 100, 100, 100,  90,   0,   0}),
    teal(10,      new int[] {0,   147, 147},  true, new int[] {55,   60,  65,   0,  75,  60,   0,   0}),
    cyan(11,      new int[] {0,   255, 255}, false, new int[] {100, 100, 100, 100, 100,  90,   0,   0}),
    blue(12,      new int[] {0,     0, 255},  true, new int[] {100, 100, 100, 100, 100,  90,   0,   0}),
    magenta(13,   new int[] {255,   0, 255}, false, new int[] {100, 100, 100, 100, 100, 100,   0,   0}),
    gray(14,      new int[] {127, 127, 127},  true, new int[] {10,   10,  10,   0,   2,   5, 100,   0}),
    lightGray(15, new int[] {210, 210, 210}, false, new int[] {30,   30,  30,   0,   5,  10, 100,   0});

    private Integer value;
    private int[] rgb;
    private boolean isDark; // color looks better on white background if true

    private Map<ColorScheme, Integer> orders = new HashMap<ColorScheme, Integer>();

    private IrcColor(final Integer value, final int[] rgb,
            final boolean isDark, final int[] scheme) {
        this.setValue(value);
        this.setRgb(rgb);
        this.setDark(isDark);
        for (ColorScheme cs : ColorScheme.values()) {
            orders.put(cs, scheme[cs.getOrder()]);
        }
    }

    public void setRgb(final int[] rgb) {
        this.rgb = CopyUtil.makeCopy(rgb);
    }

    public int[] getRgb() {
        return CopyUtil.makeCopy(rgb);
    }

    public void setValue(final Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setOrder(final ColorScheme cs, final Integer order) {
        orders.put(cs, order);
    }

    public Integer getOrder(final ColorScheme cs) {
        return orders.get(cs);
    }

    public boolean isDark() {
        return isDark;
    }

    public void setDark(final boolean isDark) {
        this.isDark = isDark;
    }
}
