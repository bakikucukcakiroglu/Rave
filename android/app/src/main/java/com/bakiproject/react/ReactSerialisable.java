package com.bakiproject.react;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;

import java.util.Collection;

public interface ReactSerialisable {
    WritableWrapper toReact();
}
