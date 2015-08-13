package com.example.yamauchi.imasora;

import android.util.Log;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by yamauchi on 2015/08/12.
 */
public class ParseJson {
    protected String content;

    // JSON文字列を、JsonNodeオブジェクトに変換する（1）
    protected JsonNode getJsonNode(String str) {

        Log.d(getClass().getName(), str);

        try {
            return new ObjectMapper().readTree(str);
        }
        catch (IOException e) {
            Log.d(getClass().getName(), e.getMessage());
        }
        return null;
    }

    // JSON文字列を読み込む
    public void loadJson(String str) {
    }

    public String getContent() {
        return this.content;
    }
}
