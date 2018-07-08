
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author As_
 * @email apknet@163.com
 * @github https://github.com/apknet
 * @since 2018/07/07
 */

public class Main {

    //    public static int n = 0;
    public static Map mapKey = new HashMap();

    public static void main(String[] args) {
        //c3e7e3306a10374b801cc39e0de6bdca
        try {

            for (String colId : musToCol("c3e7e3306a10374b801cc39e0de6bdca")) {
                for (Map.Entry<String, String> map : colToHash(colId).entrySet()) {
                    hashToComment(map);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //由歌曲Hash提取推荐歌单
    static List<String> musToCol(String hash) {
        //仅更改歌曲Hash即可得到关联歌单信息
        String str = String.format("http://servicegz.mobile.kugou.com/v1/yueku/special_album_recommend?api_ver=1&album_audio_id=29010106&num=30&hash=%s", hash);
//        String str = "http://servicegz.mobile.kugou.com/v1/yueku/special_album_recommend?api_ver=1&album_audio_id=29010106&num=10&hash=791A040895D0D7998F63E0D9DACBE5B6";
        List<String> list = new ArrayList<>();
        try {
            URL url = new URL(str);
            InputStream in = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            in.close();
            String strJson = stringBuilder.toString();
//            System.out.println(strJson);

            JSONObject jsonObject = new JSONObject(strJson).getJSONObject("data").getJSONObject("info");
//            System.out.println(jsonObject);

            JSONArray jsonArray = jsonObject.getJSONArray("special");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = (JSONObject) jsonArray.get(i);
//                System.out.println(jsonObject.getString("specialid") + '\n');
                list.add(jsonObject.getString("specialid"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //提取歌单中歌曲对应歌曲Hash值
    static Map<String, String> colToHash(String colId) {
        String str = String.format("http://mobilecdngz.kugou.com/api/v3/special/song?version=8983&plat=0&pagesize=-1&area_code=1&page=1&specialid=%s&with_res_tag=1", colId);
//        List<String> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        try {
            URL url = new URL(str);
            InputStream in = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            in.close();
            //去除头尾多余的信息，以免影响json的格式
            String strJson = stringBuilder.toString().replace("<!--KG_TAG_RES_START-->", "").replace("<!--KG_TAG_RES_END-->", "");
//            System.out.println(strJson);

            JSONObject jsonObject = new JSONObject(strJson).getJSONObject("data");
//            System.out.println(jsonObject);

            JSONArray jsonArray = jsonObject.getJSONArray("info");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = (JSONObject) jsonArray.get(i);
                map.put(jsonObject.getString("hash"), jsonObject.getString("remark"));
//                System.out.println(jsonObject.getString("hash") + '\n');
//                list.add(jsonObject.getString("hash"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    //由Hash值得到评论
    static void hashToComment(Map.Entry<String, String> map) {
        String str = String.format("http://m.comment.service.kugou.com/index.php?r=commentsv2/getCommentWithLike&code=fc4be23b4e972707f36b8a828a93ba8a&clientver=8983&extdata=%s&p=1&pagesize=20", map.getKey());
        try {
            URL url = new URL(str);
            InputStream in = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            in.close();
//            System.out.println(stringBuilder.toString());

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            //获取歌曲中的评论
          /*  System.out.println(jsonObject.get("list"));
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            for(int i = 0; i < jsonArray.length(); i++){
                jsonObject = (JSONObject) jsonArray.get(i);
                System.out.println(n + ". " + jsonObject.getString("content") + '\n');
                n++;
            }*/

            //获得歌曲评论数
            int comCount = jsonObject.getInt("combine_count");
//          System.out.println(comCount);

            if (comCount > 150000) {
                System.out.println(comCount + ": " + map.getValue());
                mapKey.put(map.getKey(), map.getValue());
            }

        } catch (Exception e) {
            System.out.println("- - - - - -> 抱歉，该歌曲未提供评论功能！");
            e.printStackTrace();
        }
    }
}