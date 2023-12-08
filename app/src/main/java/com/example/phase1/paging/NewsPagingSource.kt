package com.example.phase1.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.phase1.model.Article
import com.example.phase1.repository.NewsRepository
import javax.inject.Inject



class NewsPagingSource @Inject constructor(private val repository: NewsRepository) :
    PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        try {
            val offset = params.key ?: 0
            val response = repository.getSpaceflightNews(limit = params.loadSize, offset = offset)

            if (response.isSuccessful) {
                val data = response.body()?.results ?: emptyList()

                return LoadResult.Page(
                    data = data,
                    prevKey = if (offset == 0) null else offset - params.loadSize,
                    nextKey = if (data.isEmpty()) null else offset + params.loadSize
                )
            } else {
                return LoadResult.Error(Exception("Error loading data"))
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}


class SearchNewsPagingSource @Inject constructor(
    private val repository: NewsRepository,
    private val query: String
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        try {
            val offset = params.key ?: 0
            val response = repository.searchSpaceflightNews(
                query = query,
                limit = params.loadSize,
                offset = offset
            )

            if (response.isSuccessful) {
                val data = response.body()?.results ?: emptyList()

                return LoadResult.Page(
                    data = data,
                    prevKey = if (offset == 0) null else offset - params.loadSize,
                    nextKey = if (data.isEmpty()) null else offset + params.loadSize
                )
            } else {
                return LoadResult.Error(Exception("Error loading search results"))
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}
