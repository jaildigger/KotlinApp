package com.ec.expresscheck.rest.interceptor

import android.util.Log
import com.ec.expresscheck.TAG
import com.ec.expresscheck.rest.Services
import com.ec.expresscheck.rest.dto.RefreshTokenBody
import com.ec.expresscheck.util.getRefreshToken
import com.ec.expresscheck.util.getToken
import com.ec.expresscheck.util.logout
import com.ec.expresscheck.util.putToken
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val builder = request.newBuilder()
        builder.header("Accept", "application/json")

        val token = getToken()
        setAuthHeader(builder, token)

        request = builder.build()
        val response = chain.proceed(request)
        Log.d(TAG, "request sent to ${request.url()})")
        if (response.code() == 401) {
            synchronized(Services.oAuthClient) {
                val currentToken = getToken()
                if (currentToken != null && currentToken == token) {
                    val code = refreshToken() / 100
                    if (code != 2) {
                        if (code == 4)
                            logout()
                        return response
                    }
                }

                if (getToken() != null) {
                    setAuthHeader(builder, getToken())
                    request = builder.build()
                    return chain.proceed(request)
                }
            }
        }

        return response
    }

    private fun setAuthHeader(builder: Request.Builder, token: String?) {
        if (token != null) {
            builder.header("Authorization", "Bearer $token")
            Log.d(TAG, "auth token is $token")
        }
    }

    private fun refreshToken(): Int {
        val response = Services.authApi
            .refreshToken(RefreshTokenBody(refresh_token = getRefreshToken())).execute()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                putToken(body.access_token)
            }
        } else return response.code()


        return 200
    }


}