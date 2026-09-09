package com.example.fridgetracker.data.remote

import com.example.fridgetracker.domain.model.Product

/** Local stand-in for the future REST API. It never requires network access. */
class RemoteApiStub : RemoteApi {
    override suspend fun register(login: String, password: String): ApiResult<Unit> = ApiResult.NotConfigured
    override suspend fun login(login: String, password: String): ApiResult<Session> = ApiResult.NotConfigured
    override suspend fun refresh(refreshToken: String): ApiResult<Session> = ApiResult.NotConfigured
    override suspend fun syncProduct(product: Product): ApiResult<Product> = ApiResult.NotConfigured
    override suspend fun findBarcode(barcode: String): ApiResult<BarcodeResult> = ApiResult.NotConfigured
}

interface RemoteApi {
    suspend fun register(login: String, password: String): ApiResult<Unit>
    suspend fun login(login: String, password: String): ApiResult<Session>
    suspend fun refresh(refreshToken: String): ApiResult<Session>
    suspend fun syncProduct(product: Product): ApiResult<Product>
    suspend fun findBarcode(barcode: String): ApiResult<BarcodeResult>
}

sealed interface ApiResult<out T> {
    data class Success<T>(val value: T) : ApiResult<T>
    data class Failure(val message: String, val retryable: Boolean = true) : ApiResult<Nothing>
    data object NotConfigured : ApiResult<Nothing>
}

data class Session(val accessToken: String, val refreshToken: String)
data class BarcodeResult(val barcode: String, val name: String, val brand: String? = null, val imageUrl: String? = null)