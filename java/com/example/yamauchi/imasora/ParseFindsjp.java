package com.example.yamauchi.imasora;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by yamauchi on 2015/08/12.
 */
public class ParseFindsjp extends ParseJson{

    @Override
    public void loadJson(String str) {

        JsonNode root = getJsonNode(str);
        if (root != null){

            // ツリーオブジェクトから都道府県コードを取得する（2）
            this.content = root.path("result").path("prefecture")
                    .path("pcode").asText();
        }
    }
}
