package jenkem.shared;


public class ProcessionSettings {
    private static final int DEFAULT_OFFSET = 32;
    private int offset;
    private boolean doVline;
    private boolean doHline;
    private boolean doEdge;
    private boolean doDiagonal;

    public ProcessionSettings() {
        this.offset = DEFAULT_OFFSET;
        this.doVline = true;
        this.doHline = false;
        this.doEdge = true;
        this.doDiagonal = true;
    }

    public ProcessionSettings(final int offset, final boolean doVline, final boolean doHline,
            final boolean doEdge, final boolean doDiagonal) {
        this.offset = offset;
        this.doVline = doVline;
        this.doHline = doHline;
        this.doEdge = doEdge;
        this.doDiagonal = doDiagonal;
    }

    public final int getOffset() { return offset; }
    public final boolean isDoVline() { return doVline; }
    public final boolean isDoHline() { return doHline; }
    public final boolean isDoEdge() { return doEdge; }
    public final boolean isDoDiagonal() { return doDiagonal; }
}