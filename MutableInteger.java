
public class MutableInteger {
    public MutableInteger() {
        this(0);
    }

    public MutableInteger(int value) {
        this.value = value;
    }

    public void increment() {
        value++;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }

    private int value;
}

