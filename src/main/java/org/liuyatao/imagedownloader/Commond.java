package org.liuyatao.imagedownloader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import org.liuyatao.imagedownloader.entity.SearchResponse;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author liuyatao
 * @date 2018/7/1
 */
public class Commond {

    private static final Logger logger = Logger.getLogger("Commond");


    private static final String SERACH_REQEUST_URL = "http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearchDG.html";

    private static final String GET_INFORID_URL = "http://sbgg.saic.gov.cn:9080/tmann/annInfoView/selectInfoidBycode.html";

    private static final String GET_IMAGE_URL = "http://sbgg.saic.gov.cn:9080/tmann/annInfoView/imageView.html";

    private static final String GET_DOCNO_URL = "http://sbgg.saic.gov.cn:9080/tmann/annInfoView/selectDocNoByUrl.html";

    private static String InforId;


    private List<Map> imagesList;


    public List<SearchResponse> getSearchRespones(Integer annNum, Integer page, String rows) throws IOException {

        List<SearchResponse> searchResponseList = new ArrayList<>();

        Map map = new HashMap();
        map.put("page", page);
        map.put("annNum", annNum);
        map.put("rows", rows);
        map.put("annType", "TMZCSQ");
        String result = okHttpPost(SERACH_REQEUST_URL, map);
        JSONObject jsonObject = JSON.parseObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("rows");

        for (int i = 0; i < jsonArray.size(); i++) {
            SearchResponse searchResponse = new SearchResponse();
            JSONObject row = jsonArray.getJSONObject(i);
            searchResponse.setId(row.getString("id"));
            searchResponse.setRegname(row.getString("regname"));
            searchResponse.setPage_no(row.getInteger("page_no"));
            searchResponseList.add(searchResponse);
        }
        return searchResponseList;
    }


    private String getInforId(Integer annNum) throws IOException {
        Map body = new HashMap();
        body.put("annNum", annNum);
        body.put("annTypecode", "TMZCSQ");

        InforId = okHttpPost(GET_INFORID_URL, body);
        return InforId;

    }

    public String getDocNO(String imageUrl) throws IOException {
        Map item = new HashMap();
        Map body = new HashMap();
        body.put("imgurl", imageUrl);
        String result = okHttpPost(GET_DOCNO_URL, body);
        JSONObject jsonObject = JSON.parseObject(result);
        item.put("imgurl", imageUrl);
        item.put("docno", jsonObject.getString("docno"));
        imagesList.add(item);
        return jsonObject.getString("docno");
    }


    private void getImageUrls(Integer annNum, Integer pageNum) throws IOException {
        logger.info("获取图片列表\n");
        Map body = new HashMap();
        body.put("id", InforId == null ? getInforId(annNum) : InforId);
        body.put("pageNum", pageNum);
        body.put("flag", 1);
        String result = okHttpPost(GET_IMAGE_URL, body);
        JSONObject jsonObject = JSON.parseObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("imaglist");
        imagesList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            getDocNO(jsonArray.getString(i));
        }
    }

    /**
     * 获取图片url
     *
     * @param pageNum
     * @return
     * @throws IOException
     */
    public String getImageUrl(Integer pageNum, Integer annNum) throws IOException {

        //如果在列表中了则显示直接获取
         String url = null;

        if (imagesList == null) {
            getImageUrls(annNum, pageNum);
        }

        boolean isMatch =false;
        for (Map map : imagesList) {
            if (map.get("docno").equals(pageNum.toString())) {
                logger.info("列表中找到"+pageNum);
                isMatch=true;
                return map.get("imgurl").toString();
            }
        }

        if (!isMatch){
            logger.info("没有找到"+pageNum);
            getImageUrls(annNum, pageNum);
            logger.info("imageurls 执行结束");
            url =getImageUrl(pageNum, annNum);
        }


        return url;

    }

    /**
     * 根据连接下载图片到本地
     *
     * @param url
     */
    public void download( String url, File destFile) throws IOException {
        logger.info("下载文件" + url + "\n");
        Request request = new Request.Builder().url(url).build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)
                .writeTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000, TimeUnit.SECONDS)
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        BufferedSource source = body.source();

        BufferedSink sink = Okio.buffer(Okio.sink(destFile));
        Buffer sinkBuffer = sink.buffer();

        int bufferSize = 8 * 1024;
        for (long bytesRead; (bytesRead = source.read(sinkBuffer, bufferSize)) != -1; ) {
            sink.emit();
        }
        sink.flush();
        sink.close();
        source.close();
    }

    /**
     * http post 请求
     *
     * @param url
     * @param body
     * @return
     * @throws IOException
     */
    public String okHttpPost(String url, Map body) throws IOException {
        Set<String> keys = body.keySet();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (String key : keys) {
            builder.addFormDataPart(key, body.get(key).toString());
        }
        RequestBody requestBody = builder.setType(MultipartBody.FORM).build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)
                .writeTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000, TimeUnit.SECONDS)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();

    }
}
