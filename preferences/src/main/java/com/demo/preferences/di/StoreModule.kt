package com.demo.preferences.di
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import com.demo.preferences.general.GeneralPrefsStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class StoreModule {

 @Binds
  abstract fun bindPrefsStore(generalPrefsStoreImpl: GeneralGeneralPrefsStoreImpl): GeneralPrefsStore

}