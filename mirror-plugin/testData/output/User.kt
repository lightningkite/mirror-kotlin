package com.lightningkite.rekwest.server

import com.lightningkite.kommon.exception.ExceptionNames
import com.lightningkite.lokalize.TimeStamp
import com.lightningkite.mirror.archive.*
import com.lightningkite.mirror.info.Indexed
import com.lightningkite.mirror.info.Mutates
import com.lightningkite.mirror.info.ThrowsTypes
import com.lightningkite.rekwest.ServerFunction


data class User(
        override var id: Long? = null,
        @Indexed var email: String,
        var password: String,
        var role: User.Role = User.Role.Citizen,
        var rejectTokensBefore: TimeStamp = TimeStamp(0)
) : Model<Long> {

    fun getIdentifiers(): List<String> = listOf(email)


    //region Helper Data


    enum class Role {
        Admin,
        Citizen
    }


    data class Session(
            val user: User,
            val token: String
    )

    //endregion

    //region Server Functions


    @ThrowsTypes(ExceptionNames.NoSuchElementException)
    class Get(val id: Reference<User, Long>) : ServerFunction<User>


    @Mutates
    @ThrowsTypes(ExceptionNames.ForbiddenException)
    class Insert(val value: User) : ServerFunction<User.Session>


    @Mutates
    @ThrowsTypes(ExceptionNames.ForbiddenException, ExceptionNames.NoSuchElementException)
    class Update(val value: User) : ServerFunction<User>


    @Mutates
    @ThrowsTypes(ExceptionNames.ForbiddenException, ExceptionNames.NoSuchElementException)
    class Modify(val id: Reference<User, Long>, val modifications: List<ModificationOnItem<User, *>>) : ServerFunction<User>


    @ThrowsTypes(ExceptionNames.ForbiddenException)
    class Query(
            val condition: ConditionOnItem<User> = ConditionOnItem.Always(),
            val sortedBy: List<SortOnItem<User, *>> = listOf(),
            val continuationToken: String? = null,
            val count: Int = 100
    ) : ServerFunction<QueryResult<User>>


    @Mutates
    @ThrowsTypes(ExceptionNames.ForbiddenException, ExceptionNames.NoSuchElementException)
    class Delete(val id: Reference<User, Long>) : ServerFunction<Unit>


    @ThrowsTypes(ExceptionNames.ForbiddenException, ExceptionNames.NoSuchElementException)
    class ResetPassword(val email: String) : ServerFunction<Unit>


    @ThrowsTypes(ExceptionNames.ForbiddenException, ExceptionNames.NoSuchElementException)
    class Login(val email: String, val password: String) : ServerFunction<User.Session>

    //endregion
}
