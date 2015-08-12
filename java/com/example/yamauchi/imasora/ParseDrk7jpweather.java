package com.example.yamauchi.imasora;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;

/**
 * Created by yamauchi on 2015/08/12.
 */
public class ParseDrk7jpweather extends ParseJson {
    @Override
    public void loadJson(String str) {

        // JSON文字列以外を削除（1）
        str = str.replace("drk7jpweather.callback(", "");
        str = str.replace(");", "");

        // JsonNodeオブジェクトに読み込む
        JsonNode root = getJsonNode(str);

        if (root != null){

            // XML版のリンク情報を取得
            String link = root.path("link").asText();
            // 大阪府と香川県以外（2）
            if ( (link.indexOf("27.xml") == -1 ) && (link.indexOf("37.xml") == -1 ) ) {

                // 都道府県名
                this.content = root.path("pref").path("id").asText() + "\n\n";

                JsonNode area = root.path("pref").path("area");

                // 細分単位で取得する（3）
                String date = "";
                Iterator<String> infoNodeFields = area.fieldNames();
                while (infoNodeFields.hasNext()) {
                    String infoNodeField = infoNodeFields.next();

                    JsonNode area2 = area.path(infoNodeField);

                    // 当日の天気予報、概況、日付（4）
                    this.content += infoNodeField + "\n" +
                            area2.path("info").path(0).path("weather").asText() + "\n" +
                            area2.path("info").path(0).path("weather_detail").asText() + "\n\n";
                    date = area2.path("info").path(0).path("date").asText();
                }
                this.content = date + "\n" + this.content;

            }
            else {
                JsonNode area = root.path("pref").path("area");

                // 都道府県名
                this.content = area.path("id").asText() + "\n";

                // 当日の日付、天気予報、概況
                this.content += area.path("info").path(0).path("date").asText() + "\n\n" +
                        area.path("info").path(0).path("weather").asText() + "\n" +
                        area.path("info").path(0).path("weather_detail").asText() + "\n";

            }
        }
    }
}
