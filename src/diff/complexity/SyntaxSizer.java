package diff.complexity;

import java.util.List;

/**
 * Counts the size of a Syntax object.
 */
public abstract class SyntaxSizer<T> {

    public abstract int size(T obj);

    public int size(List<T> objList) {
        int total = 0;
        for (T obj : objList) {
            total += this.size(obj);
        }
        return total;
    }

    public int size(T... objList) {
        int total = 0;
        for (T obj : objList) {
            total += this.size(obj);
        }
        return total;
    }

}