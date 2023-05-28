package com.melniknow.fd.profile;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Profile {
    private final String profileName;
    public final JsonObject json;

    public Profile(String profileName, JsonElement json) {
        this.profileName = profileName;
        this.json = json.getAsJsonObject();
    }

    public void save() {
        Database.updateJsonData(profileName, json.toString());
    }
}
