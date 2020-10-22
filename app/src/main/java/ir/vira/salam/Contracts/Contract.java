package ir.vira.salam.Contracts;

import java.util.List;

public interface Contract<T> {
    void addAll(List<T> items);

    void add(T item);
}
