package com.example.okhttp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown;charset=utf-8");
    private final static String TAG = "MainActivity";
    private Button bt_get, bt_post;
    final private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_get = (Button) findViewById(R.id.bt_get);
        bt_post = (Button) findViewById(R.id.bt_post);
        bt_post.setOnClickListener(this);
        bt_get.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_get:
//                getRequest();
                //同步get
//                    synchronousGet();
                //异步get
//                    aynchronousGet();
                //Accessing Headers 提取响应头
//                    AccessingHeaders();

                break;
            case R.id.bt_post:
//                postRequest();
                //post a string post方式提交string
//                    PostingaString();
                //Post Streaming（post方式提交流）
//                    postStreaming();
                //post a file(post方式提交文件)
                postingaFile();
                break;
        }
    }

    //get
    private void getRequest() {
        final Request request = new Request.Builder().get().tag(this).url("https://www.baidu.com").build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Log.i(TAG, "打印get响应数据：" + response.body().string());
                    } else {
                        throw new IOException("Unexpect code" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //post
    private void postRequest() {
        RequestBody formBody = new FormBody.Builder().add("", "").build();

        final Request request = new Request.Builder().url("https://www.baidu.com").post(formBody).build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Log.i(TAG, "打印post响应数据");
                    } else {
                        throw new IOException("Unexepected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //Synchronous Get
    private void synchronousGet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runSynchronousGet();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void runSynchronousGet() throws IOException {
        Request request = new Request.Builder().url("https://publicobject.com/helloworld.txt").build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            Log.i("Synchronous", responseHeaders.name(i) + ":" + responseHeaders.value(i));
        }
        Log.i("Synchronous", response.body().string());
    }

    //Asynchronous Get
    private void aynchronousGet()  {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runAsynchronousGet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void runAsynchronousGet() throws Exception {
        Request request = new Request.Builder().url("https://publicobject.com/helloworld.txt").build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException(response + "");

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    Log.i("Synchronous", responseHeaders.name(i) + ":" + responseHeaders.value(i));
                }
                Log.i("Synchronous", response.body().string());
            }
        });
    }

    //提取响应头
    private void AccessingHeaders() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runAccessingHeaders();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void runAccessingHeaders() throws Exception {
        Request request = new Request.Builder().url("https://api.github.com/repos/square/okhttp/issues")
                .header("User-Agent", "OkHttp Headers.java").addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json").build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexcepted code" + response);

        Log.i(TAG, "Server:" + response.header("Server"));
        Log.i(TAG, "Data:" + response.header("Data"));
        Log.i(TAG, "Vary:" + response.header("Vary"));
    }

    //Post a String (post的方式提交string)
//    使用HTTP POST提交请求到服务。这个例子提交了一个markdown文档到web服务，以HTML方式渲染markdown。因为整个请求体都在内存中，因此避免使用此api提交大文档（大于1MB）。

    private void runPostingaString() throws Exception {
        String postBody = "" +
                "Release\n" +
                "-------\n" +
                "\n" +
                "*_1.0_May 6,2013\n" +
                "*_1.1_June 15,2013\n" +
                "*_1.2_August 11,2013\n";

        Request request = new Request.Builder().url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody)).build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code" + response);

        Log.i(TAG, response.body().string());
    }

    private void PostingaString() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runPostingaString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //Post Streaming（post方式提交流）
    private void runPostStreaming() throws Exception {
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MEDIA_TYPE_MARKDOWN;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("Numbers\n");
                sink.writeUtf8("-------\n");
                for (int i = 2; i <= 997; i++) {
                    sink.writeUtf8(String.format("*%s=%s\n", i, factor(i)));
                }
            }

            private String factor(int n) {
                for (int i = 2; i < n; i++) {
                    int x = n / i;
                    if (x * i == n) return factor(x) + 'x' + i;
                }
                return Integer.toString(n);
            }

        };

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(requestBody).build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code" + response);

        Log.i(TAG, response.body().string());
    }

    private void postStreaming() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runPostStreaming();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //post a file(post方式提交文件)
    private void runPostingaFile() throws IOException{
        File file = new File("README.md");
        Request request = new Request.Builder().url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file)).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        Log.i(TAG, response.body().string());
    }

    private void postingaFile(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runPostingaFile();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
