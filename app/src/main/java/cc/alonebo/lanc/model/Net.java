package cc.alonebo.lanc.model;

import java.io.IOException;

import cc.alonebo.lanc.Constants;
import cc.alonebo.lanc.model.listener.LoadUriSuccessListener;
import cc.alonebo.lanc.utils.SPUtils;
import cc.alonebo.lanc.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by alonebo on 17-5-23.
 */

public class Net {
    public void getBintPicLink(final LoadUriSuccessListener listener){
        final String url =  "http://guolin.tech/api/bing_pic";
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request build = new okhttp3.Request.Builder().url(url).build();
        client.newCall(build).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String picLink = response.body().string();
                SPUtils.put(Utils.getContext(), Constants.SP_BING_LINK,picLink);
                listener.onSuccess(picLink);
            }
        });
    }
}
