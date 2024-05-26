package data.feed

import androidx.paging.PagingState
import app.cash.paging.PagingSource
import data.feed.remote.FeedNetworkDataSource
import me.ayitinya.grenes.data.feed.Feed
import okio.IOException

class FeedPagingSource(private val feedNetworkDataSource: FeedNetworkDataSource) : PagingSource<Int, Feed>() {
    override fun getRefreshKey(state: PagingState<Int, Feed>): Int? {
        // Try to find the page key of the closest page to anchorPosition from
        // either the prevKey or the nextKey; you need to handle nullability
        // here.
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey are null -> anchorPage is the
        //    initial page, so return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Feed> {
        try {
            val nextPageNumber = params.key ?: 1
            val feeds = feedNetworkDataSource.read(nextPageNumber = nextPageNumber)

            return LoadResult.Page(
                data = feeds,
                prevKey = null,
                nextKey = if (feeds.isEmpty()) null else nextPageNumber + 1
            )
        } catch (e: IOException) {
            // IOException for network failures.
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}