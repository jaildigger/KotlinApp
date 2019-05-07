package com.ec.expresscheck.rest.interceptor

import com.ec.expresscheck.util.isTestMode
import okhttp3.Interceptor
import okhttp3.Response


class TestInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!isTestMode()) {
            return chain.proceed(request)
        }

        val url = request.url().newBuilder().addQueryParameter("demo", "true").build()
        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }


}