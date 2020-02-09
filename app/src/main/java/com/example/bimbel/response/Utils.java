package com.example.bimbel.response;

import com.example.bimbel.network.ApiError;
import com.example.bimbel.network.RetrofitClient;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Converter;

public class Utils {

    public static ApiError converErrors(ResponseBody response){
        Converter<ResponseBody, ApiError> converter = RetrofitClient.getRetrofit().responseBodyConverter(ApiError.class, new Annotation[0]);
        ApiError apiError = null;
        try {
            apiError = converter.convert(response);
        }catch (IOException e){
            e.printStackTrace();
        }
        return apiError;
    }
}
