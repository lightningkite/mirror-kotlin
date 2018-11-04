package com.test

@ExternalReflection
data class Post(
        @Indexed var userId: Long = 0,
        override var id: Long? = null,
        var title: String = "",
        var body: String = "",
        @ForeignKey(Post::class) var parent: Long? = null,
        var time: TimeStamp = TimeStamp(0)
) : Model<Long> {
    @ExternalReflection
    class Get(val id: Long) : ServerFunction<Post>

    @ExternalReflection
    class Insert(val value: Post) : ServerFunction<Post>

    @ExternalReflection
    class Update(val value: Post) : ServerFunction<Post>

    @ExternalReflection
    class Modify(val id: Long, val modifications: List<ModificationOnItem<Post, *>>) : ServerFunction<Post>

    @ExternalReflection
    class Query(
            val condition: ConditionOnItem<Post> = ConditionOnItem.Always(),
            val sortedBy: List<SortOnItem<Post, *>> = listOf(),
            val continuationToken: String? = null,
            val count: Int = 100
    ) : ServerFunction<QueryResult<Post>>

    @ExternalReflection
    class Delete(val id: Long) : ServerFunction<Unit>
}
