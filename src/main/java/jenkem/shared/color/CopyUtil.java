package jenkem.shared.color;

public final class CopyUtil {

    private CopyUtil() { }

    public static int[] makeCopy(final int[] in) {
        final int[] copy = new int[in.length];
        System.arraycopy(in, 0, copy, 0, in.length);
        return copy;
    }
}
