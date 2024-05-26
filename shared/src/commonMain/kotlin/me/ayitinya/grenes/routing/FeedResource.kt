package me.ayitinya.grenes.routing

import io.ktor.resources.*

@Resource("/posts")
class FeedResource(val userId: String? = null, val nextPageNumber: Int? = null) {

    @Resource("/{id}/comment")
    class Comment(val id: String)

    @Resource("/{id}")
    class FeedDetailResource(val id: String) {

        @Resource("reaction")
        class Reaction(val feedId: String) {
            @Resource("add")
            class Add(val userId: String, val feedId: String)

            @Resource("delete")
            class Delete(val reactionId: String)
        }

    }
}