package jenkem.shared.data;

public abstract class AbstractImagePart {
    private final String superName;

    public AbstractImagePart(final String name) {
        this.superName = name;
    }

    @Override
    public final String toString() {
        return superName;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((superName == null) ? 0 : superName.hashCode());
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        final AbstractImagePart other = (AbstractImagePart) obj;
        if (superName == null) {
            if (other.toString() != null) { return false; }
        } else if (!superName.equals(other.toString())) {
            return false;
        }
        return true;
    }
}
