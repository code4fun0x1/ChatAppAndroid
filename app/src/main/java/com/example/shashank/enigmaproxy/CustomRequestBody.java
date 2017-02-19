package com.example.shashank.enigmaproxy;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;



/**
 * Created by Shashank on 05-01-2017.
 */

public class CustomRequestBody extends RequestBody {

    private RequestBody mBody;
    private Listener listener;
    private CountingSink mSink;
    private long bytesWritten=0;
    public static final String TAG="ImageUpload";

    public CustomRequestBody(RequestBody mBody, Listener listener) {
        this.mBody = mBody;
        this.listener = listener;
    }

    public interface Listener{
        void onProgress(int progress);
    }

    @Override
    public MediaType contentType() {
        return mBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {

        Log.d(TAG, "contentLength: "+mBody.contentLength());

        try{
            return mBody.contentLength();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        mSink=new CountingSink(sink);
        BufferedSink bSink= Okio.buffer(mSink);
        mBody.writeTo(bSink);
        bSink.flush();
    }

    protected final class CountingSink extends ForwardingSink{



        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten+=byteCount;
            listener.onProgress((int)(100*bytesWritten/contentLength()));
        }
    }

}
