package com.abstractstudios.spells.provider;

import com.google.gson.*;

import java.lang.reflect.Type;

public class InterfaceProvider<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    @Override
    public final JsonElement serialize(final T object, final Type interfaceType, final JsonSerializationContext context) {
        final JsonObject member = new JsonObject();
        member.addProperty("type", object.getClass().getName());
        member.add("data", context.serialize(object));
        return member;
    }

    @Override
    public final T deserialize(final JsonElement elem, final Type interfaceType, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject member = (JsonObject) elem;
        final JsonElement typeString = get(member, "type");
        final JsonElement data = get(member, "data");
        final Type actualType = typeForName(typeString);
        return context.deserialize(data, actualType);
    }

    private Type typeForName(final JsonElement element) {

        try {
            return Class.forName(element.getAsString());
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    private JsonElement get(final JsonObject wrapper, final String name) {
        final JsonElement elem = wrapper.get(name);

        if (elem == null) {
            throw new JsonParseException("No '" + name + "' member found in json file.");
        }

        return elem;
    }
}
