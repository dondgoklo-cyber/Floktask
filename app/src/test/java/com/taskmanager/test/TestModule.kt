package com.taskmanager.test

/**
 * TestModule annotations removed to avoid Hilt duplicate binding with AppModule.
 * Unit tests use manual constructor injection with TestLogger — no Hilt module needed.
 *
 * Previous @InstallIn(SingletonComponent::class) + @Provides Logger conflicted with
 * AppModule.provideLogger() during kaptDebugUnitTest, causing compilation failure.
 */
