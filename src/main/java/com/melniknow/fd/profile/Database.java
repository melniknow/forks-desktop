package com.melniknow.fd.profile;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String url = "jdbc:mysql://151.248.119.228:3306/u2028999_forks_desktop";
    private static final String user = "u2028999_forks";
    private static final String password = "Nhfccf60$";

    public static String getJsonByProfileName(String profileName) {
        try (var connection = DriverManager.getConnection(url, user, password);
             var preparedStatement = connection.prepareStatement(Query.SELECT_JSON_BY_PROFILE_NAME.text)) {

            preparedStatement.setString(1, profileName);

            var set = preparedStatement.executeQuery();
            set.next();

            return set.getString("data");
        } catch (SQLException e) {
            return null;
        }
    }

    public static void createProfile(String profileName) {
        try (var connection = DriverManager.getConnection(url, user, password);
             var preparedStatement = connection.prepareStatement(Query.CREATE_PROFILE.text)) {

            preparedStatement.setString(1, profileName);
            preparedStatement.setString(2, "{}");

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateJsonData(String profileName, String jsonData) {
        try (var connection = DriverManager.getConnection(url, user, password);
             var preparedStatement = connection.prepareStatement(Query.UPDATE_PROFILE.text)) {

            preparedStatement.setString(1, jsonData);
            preparedStatement.setString(2, profileName);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getAllProfiles() {
        var profiles = new ArrayList<String>();

        try (var connection = DriverManager.getConnection(url, user, password);
             var preparedStatement = connection.prepareStatement(Query.GET_ALL_PROFILES.text)) {

            var set = preparedStatement.executeQuery();

            while (set.next()) profiles.add(set.getString("profile_name"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return profiles;
    }
}
