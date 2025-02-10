package me.padej.displayAPI.render;

import java.util.HashMap;
import java.util.Map;

public class RenderEntityGroup {

    private final Map<Object, RenderEntity<?>> items = new HashMap<>();

    public Map<Object, RenderEntity<?>> getItems() {
        return items;
    }

    public void add(Object id, RenderEntity<?> item) {
        items.put(id, item);
    }

    public void add(Object id, RenderEntityGroup item) {
        for (Map.Entry<Object, RenderEntity<?>> entry : item.getItems().entrySet()) {
            Object subId = entry.getKey();
            RenderEntity<?> part = entry.getValue();
            items.put(new Pair<>(id, subId), part);
        }
    }

    // Вспомогательный класс Pair для хранения составного ключа (id, subId)
    public static class Pair<A, B> {
        private final A first;
        private final B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair<?, ?> pair = (Pair<?, ?>) o;

            if (!first.equals(pair.first)) return false;
            return second.equals(pair.second);
        }

        @Override
        public int hashCode() {
            int result = first.hashCode();
            result = 31 * result + second.hashCode();
            return result;
        }
    }
}
