package relaxeddd.simplediary.di

import relaxeddd.simplediary.ContextArgs
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object InjectorCommon {

    lateinit var contextArgs: ContextArgs

    fun provideContextArgs(contextArgs: ContextArgs): ContextArgs {
        this.contextArgs = contextArgs
        return this.contextArgs
    }
}