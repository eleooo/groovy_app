package tool

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
/**
 * Created by LINJE2 on 7/29/2017.
 */
class CompareJson {

    /**
     * @param str
     * @return JsonObject or JsonArray
     * parse String into Object
     */
    Object JsonObj(String str){
        JsonParser parser=new JsonParser();  //create JSON
        Object object=parser.parse(str);  //create JsonObject or JsonArray
        return object
    }

    /**
     * @param obj
     * @return true or false
     * check Object is or not is JsonObject
     */
    boolean isJsonObject(Object obj){
        return obj instanceof JsonObject
    }
    /**
     * @param obj
     * @return true or false
     * check Object is or not is JsonArray
     */
    boolean isJsonArray(Object obj){
        return obj instanceof JsonArray
    }

    /**
     * @param str1
     * @param str2
     * @return true or false
     * compare two Json String
     */
    boolean compare(String str1,String str2){
        Object obj1=JsonObj(str1)
        Object obj2=JsonObj(str2)

        if(isJsonObject(obj1)==isJsonObject(obj2) || isJsonArray(obj1)==isJsonArray(obj2)){
            if(obj1.toString()==obj2.toString()) return true
            cleanJson(obj1)
            cleanJson(obj2)
            return obj1?.toString()==obj2?.toString()?true:false
        }else{
            return false
        }
    }

    /**
     * @param json
     * @return
     * clean Json which value is [] {} null "null"
     */
    boolean cleanJson(Object json){
        if(isJsonObject(json)){
            json=(JsonObject)json
            Iterator<Map.Entry<String, Object>> it = json.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry<String, Object> temp = it.next();
                //println temp
                if(isJsonObject(temp.value) || isJsonArray(temp.value )){
                    if(temp.value.size()>0){
                        cleanJson(temp.value)
                    }else{
                        it.remove()
                    }
                }else if (temp.getValue().toString()=="\"\""||temp.getValue().toString()=="\"null\"" || temp.value.isJsonNull()){
                    it.remove()
                }
            }
        }else if(isJsonArray(json)){
            for (int i = 0; i < json.size(); i++) {
                Object obj = json.get(i);
                if(isJsonObject(obj)|| isJsonArray(obj)){
                    if(obj.size()>0){
                        cleanJson(obj)
                    }else{
                        json.remove(obj)
                    }
                }else if (obj.toString()=="\"\"" || obj.toString()=="\"null\"" || obj.isJsonNull())
                    json.remove(obj)
            }
        }
    }


}
