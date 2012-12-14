package jenkem.shared.data;

import java.io.Serializable;

public abstract class AbstractImagePart implements Serializable {
    private static final long serialVersionUID = -5770880658655855416L;

    private final transient String superName;

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
        return prime * 1 + ((superName == null) ? 0 : superName.hashCode());
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
