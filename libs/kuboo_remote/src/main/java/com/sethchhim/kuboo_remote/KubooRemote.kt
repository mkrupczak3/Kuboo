package com.sethchhim.kuboo_remote

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.sethchhim.kuboo_remote.client.OkHttpClient
import com.sethchhim.kuboo_remote.client.OkHttpHelper
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.service.remote.FetchService
import com.sethchhim.kuboo_remote.task.*
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.FetchListener
import java.io.File
import java.util.concurrent.Executor

class KubooRemote(context: Context, val networkIO: Executor, val mainThread: Executor) {

    private var okHttpClient = OkHttpClient(context)

    internal val okHttpHelper = OkHttpHelper(okHttpClient)
    private val fetchService = FetchService(context, okHttpClient, mainThread)

    fun getOkHttpClient() = okHttpClient

    fun getOkHttpHelper() = okHttpHelper

    fun setOkHttpClient(customClient: okhttp3.OkHttpClient) {
        okHttpClient = customClient as OkHttpClient
    }

    fun pingServer(login: Login, stringUrl: String) = Task_Ping(this, login, stringUrl).liveData

    fun getListByUrl(login: Login, stringUrl: String) = Task_RemoteBookList(this, login, stringUrl).liveData

    fun getPaginationByBook(login: Login, stringUrl: String) = Task_RemotePagination(this, login, stringUrl).liveData

    fun getFirstByBook(login: Login, book: Book, stringUrl: String) = Task_RemoteFirstBook(this, login, book, stringUrl).liveData

    fun getItemCountByBook(login: Login, stringUrl: String) = Task_RemoteItemCount(this, login, stringUrl).liveData

    fun getListByQuery(login: Login, stringQuery: String) = Task_RemoteSearch(this, login, stringQuery).liveData

    fun getNeighbors(login: Login, book: Book, stringUrl: String) = Task_RemoteNeighbors(this, login, book, stringUrl).liveData

    fun getFile(login: Login, stringUrl: String, saveDir: File) = Task_RemoteDownloadFile(this, login, stringUrl, saveDir).liveData

    fun getTlsCipherSuite() = Task_RemoteTlsCipherSuite(okHttpHelper).getTlsCipherSuite()

    fun isConnectedEncrypted() = Task_RemoteIsConnectionEncrypted(okHttpHelper).isConnectionEncrypted()

    fun getDownloadsList(liveData: MutableLiveData<List<Download>>) = fetchService.getDownloadsList(liveData)

    fun cancel(download: Download) = fetchService.cancel(download)

    fun addFetchListener(fetchListener: FetchListener) = fetchService.addListener(fetchListener)

    fun removeFetchListener(fetchListener: FetchListener) = fetchService.removeListener(fetchListener)

    fun resume(download: Download) = fetchService.resume(download)

    fun retry(download: Download) = fetchService.retry(download)

    fun download(login: Login, stringUrl: String) = fetchService.download(login, stringUrl)

    fun resumeAll() = fetchService.resumeAll()

    fun pauseAll() = fetchService.pauseAll()

    fun cancelAll() = fetchService.cancelAll()

    fun isPauseEmpty(liveData: MutableLiveData<Boolean>) = fetchService.isPauseEmpty(liveData)

    fun isQueueEmpty(liveData: MutableLiveData<Boolean>) = fetchService.isQueueEmpty(liveData)

    fun remove(download: Download) = fetchService.remove(download)

    fun delete(download: Download) = fetchService.delete(download)

    fun getRemoteUserApi(login: Login, book: Book) = Task_RemoteUserApiGet(this, login, book).liveData

    fun putRemoteUserApi(login: Login, book: Book) = Task_RemoteUserApiPut(this, login, book).liveData

    fun cancelAllByTag(tag: String) = okHttpHelper.cancelAllByTag(tag)

    fun addFinishedToRemoteUserApi(login: Login, book: Book) = putRemoteUserApi(login, book.apply { isFinished = true })

    fun removeFinishedFromRemoteUserApi(login: Login, book: Book) = putRemoteUserApi(login, book.apply { isFinished = false })

    fun addFinishedToRemoteUserApi(login: Login, list: List<Book>): MutableLiveData<Boolean> {
        list.forEach { it.isFinished = true }
        return Task_RemoteUserApiUpdate(this, login, list).liveData
    }

    fun removeFinishedFromRemoteUserApi(login: Login, list: List<Book>): MutableLiveData<Boolean> {
        list.forEach { it.isFinished = false }
        return Task_RemoteUserApiUpdate(this, login, list).liveData
    }

    fun isImageWide(loginItem: Login, stringUrl: String) = Task_RemoteIsImageWide(this, loginItem, stringUrl).liveData

}