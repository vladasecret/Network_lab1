public class Pair<T, U> {
    private T first;
    private U second;

    public Pair(T frst, U scnd) {
        first = frst;
        second = scnd;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    public String toString() {
        return new String(first.toString() + "\t" + second.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair<?, ?> other = (Pair<?, ?>) obj;
        return first.equals(other.first) && second.equals(other.second);
    }
    public int hashCode() {
        return (first != null && second != null) ? first.hashCode() + second.hashCode() : 0;
    }

}
