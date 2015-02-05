package com.next.eswaraj.volley;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.util.Log;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.eswaraj.web.dto.SaveComplaintRequestDto;
import com.google.gson.Gson;


public class ComplaintPostVolleyRequest extends Request<String> {

    private MultipartEntity entity = new MultipartEntity();


    private final Response.Listener<String> mListener;
    private  File issueImage;


    public ComplaintPostVolleyRequest(Response.ErrorListener errorListener, Response.Listener<String> listener, SaveComplaintRequestDto saveComplaintRequestDto, File image, String url) throws UnsupportedEncodingException {
        super(Method.POST, url, errorListener);

        mListener = listener;
        addIssueDetail("SaveComplaintRequest", saveComplaintRequestDto);
        addIssueDetailImage("img", image);

    }

    private void addIssueDetail(String partName, SaveComplaintRequestDto saveComplaintRequestDto) throws UnsupportedEncodingException {
        String requestBody = new Gson().toJson(saveComplaintRequestDto);
        Log.i("eswaraj", "requestBody : " + requestBody);
        entity.addPart(partName, new StringBody(requestBody));
    }

    private void addIssueDetailImage(String partName, File image) {
        if(image != null) {
            entity.addPart(partName, new FileBody(image));
        }
    }


    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            Log.i("eswaraj", "Status Code = "+response.statusCode);
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.i("eswaraj", "json response = "+json);
            return Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        Log.i("eswaraj", " response = "+response);
        if(mListener != null){
            mListener.onResponse(response);
        }

    }
}
