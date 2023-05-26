package com.melniknow.fd.profile;

public enum Query {
    SELECT_JSON_BY_PROFILE_NAME("SELECT data FROM profile_name_to_json_data WHERE profile_name = ?"),
    CREATE_PROFILE("INSERT INTO profile_name_to_json_data (profile_name, data) VALUES (?, ?)"),
    UPDATE_PROFILE("UPDATE profile_name_to_json_data set data = ? where profile_name = ?"),
    GET_ALL_PROFILES("SELECT profile_name FROM profile_name_to_json_data");

    public final String text;
    Query(String text) {
        this.text = text;
    }
}
