package io.army.dialect.mysql;

import io.army.dialect.func.SQLFuncDescribe;

import java.util.Map;

abstract class MySQL80DDLUtils extends MySQL57DDLUtils {

    static String parseDefaultValueExpression(Map<String, SQLFuncDescribe> funcMap, final String defaultValue){
            return "";
    }

    static String replaceStandardFuncIfNeed(Map<String, SQLFuncDescribe> funcMap, final String defaultValue){
        int start,index;
        for (Map.Entry<String,SQLFuncDescribe> entry: funcMap.entrySet()) {
            start = 0;
            index = defaultValue.indexOf(entry.getKey(),start);
            if(index > 0 ){
                if(entry.getValue().hasArguments()){

                }else {

                }
            }

        }
        return "";
    }

}
