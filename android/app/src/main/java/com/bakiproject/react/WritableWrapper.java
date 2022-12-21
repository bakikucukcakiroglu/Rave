package com.bakiproject.react;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.Collection;
import java.util.Map;

public abstract class WritableWrapper implements ReactSerialisable {
    public abstract void addTo(WritableArray arr);

    public abstract void addTo(WritableMap map, String name);

    public abstract Object getObj();

    @Override
    public WritableWrapper toReact() {
        return this;
    }

    public static WrappedArray wrap(Collection<? extends ReactSerialisable> coll) {
        WritableArray arr = Arguments.createArray();
        coll.forEach(o -> o.toReact().addTo(arr));
        return new WrappedArray(arr);
    }

    public static WrappedArray wrap(WritableArray array) {
        return new WrappedArray(array);
    }

    public static WrappedMap wrap(WritableMap map) {
        return new WrappedMap(map);
    }

    public static WrappedString wrap(String string) {
        return new WrappedString(string);
    }

    public static WrappedString wrap(Enum<?> e) {
        return new WrappedString(e.name());
    }

    public static class WrappedArray extends WritableWrapper {
        private final WritableArray inner;

        private WrappedArray(WritableArray inner) {
            this.inner = inner;
        }

        @Override
        public void addTo(WritableArray arr) {
            arr.pushArray(inner);
        }

        @Override
        public void addTo(WritableMap map, String name) {
            map.putArray(name, inner);
        }

        @Override
        public WritableArray getObj() {
            return inner;
        }
    }

    public static class WrappedMap extends WritableWrapper {
        private final WritableMap inner;

        private WrappedMap(WritableMap inner) {
            this.inner = inner;
        }

        @Override
        public void addTo(WritableArray arr) {
            arr.pushMap(inner);
        }

        @Override
        public void addTo(WritableMap map, String name) {
            map.putMap(name, inner);
        }

        @Override
        public WritableMap getObj() {
            return inner;
        }
    }

    public static class WrappedString extends WritableWrapper {
        private final String inner;

        private WrappedString(String inner) {
            this.inner = inner;
        }

        @Override
        public void addTo(WritableArray arr) {
            arr.pushString(inner);
        }

        @Override
        public void addTo(WritableMap map, String name) {
            map.putString(name, inner);
        }

        @Override
        public String getObj() {
            return inner;
        }
    }

}
