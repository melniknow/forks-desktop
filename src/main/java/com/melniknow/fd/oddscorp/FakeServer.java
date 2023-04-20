package com.melniknow.fd.oddscorp;

public class FakeServer {
    public static String get(String request) {
        return """
            [{
                "fork_id": "f1ac7a88fc584cff08",
                "income": 14.04,
                "ow_income": 0,
                "sport": "tennis",
                "bet_type": "SET_TOTALS",
                "event_id": "21011937",
                "is_middles": "0",
                "is_cyber": "0",
                "BK1_bet": "SET_01__TOTALS__UNDER(8.5)",
                "BK1_bet_type": "SET_TOTALS",
                "BK1_alt_bet": "",
                "BK1_cf": 13,
                "BK1_event_id": "BT3TN0F011943640",
                "BK1_event_native_id": "6V136212429C13A_1_1",
                "BK1_game": "Matteo Martineau vs Lucas Pouille",
                "BK1_href": "https://www.bet365.com/#/IP/EV15889565065C13",
                "BK1_league": "Challenger Tallahassee",
                "BK1_name": "bet365",
                "BK1_score": "0:0 (2:5) (40*:0)",
                "BK1_event_meta": "{\\"raw_league\\":\\"20882422\\",\\"r_ff\\":\\"1~HB\\",\\"fi\\":\\"136212429\\",\\"start_at\\":\\"1681830000\\",\\"start_market_count\\":50,\\"tv\\":1}",
                "BK1_market_meta": "{\\"betId\\":\\"491803864\\",\\"fi\\":\\"136320622\\",\\"od\\":\\"12/1\\",\\"zw\\":\\"136320622-491803864\\",\\"mg\\":\\"130021\\",\\"eventFi\\":\\"136212429\\",\\"name\\":\\"Total Games in Set 1|8.5|Under\\"}",
                "BK2_bet": "SET_01__TOTALS__OVER(8.5)",
                "BK2_bet_type": "SET_TOTALS",
                "BK2_alt_bet": "",
                "BK2_cf": 1.25,
                "BK2_event_id": "BWNTND24E297B066",
                "BK2_event_native_id": "14177743",
                "BK2_game": "Matteo Martineau (FRA) vs Lucas Pouille (FRA)",
                "BK2_href": "https://sports.bwin.com/en/sports/events/matteo-martineau-fra-lucas-pouille-fra-14177743",
                "BK2_league": "World. ATP Challenger Tallahassee (USA) - Clay",
                "BK2_name": "bwin",
                "BK2_score": "0:0 (2:5) (15:00)",
                "BK2_event_meta": "{\\"start_at\\":1681819260.0,\\"raw_start_at\\":\\"2023-04-18T15:01:00Z\\"}",
                "BK2_market_meta": "{\\"marketName\\":\\"Total Games - Set 1\\",\\"selectionName\\":\\"Over 8,5\\",\\"marketId\\":921564275,\\"resId\\":-1588373981}"
            }]
            """;
    }
}
