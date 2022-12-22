package com.bakiproject.react;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;

import java.io.Serializable;

public interface ReactSerialisable extends Serializable {
    WritableWrapper toReact();
}
