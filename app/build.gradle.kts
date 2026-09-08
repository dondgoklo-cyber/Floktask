plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.taskmanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.taskmanager"
        minSdk = 24
        targetSdk = 34
        versionCode = 10300
        versionName = "1.3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("../keystore/release.keystore")
            storePassword = System.getenv('i9I=%}M%9%9}MQ=I}AMM]=I(共̀􁍥͑ѕؠ9I=%}M%9%9}1%L(偅͍ݽɐ􁍥͑ѕؠ9I=%}M%9%9}AMM]=I(����եQ偕́쨀՜쨀͵啹􁙅͔(Aѥ%Mՙ4՜(ٕɍ9Mՙ4՜(��ɕ͔쨀ͥ􁍥̹щ幅՜(͵啹􁙅͔(IՅɑ̠(ёձсɽՅɑIՅɐɽQ锹ѡЈ(IՅɐɕ̹I((����e=Q́쨀ͽՉEѥѤ􁩅مYɍYIM%=9|Ĝ(хɝэEѥѤ􁩅مYɍYIM%=9|Ĝ(͍ɕ1Ʌɥ͕ɥ􁑉Ք(��ѱ=Q́쨀ٵQɝЀ􀈄܈(��Ё쨀ɑ=ɉȀ􁙅͔(I͕	ե̀􁙅͔(��եѕɕ́쨀}͔􁑉Ք(ե􁑉Ք(٥݉􁙅͔(��E쨀ɕͽՉ́쨀፱Ց̀􀈽5Q%9텰ȸ11A0ȸŴ(����)Ú쨀qхѥ̹ѱ͑(qхѥ̹ѱํɽՑ̹ɽ(qхѥ̹ѱํɥ酑ͽ(qхѥ̹ɽํɔѠ(qхѥ̹ɽ๱危ɕѥѠ(qхѥ̹ɽ๱危٥ݵ}͔(qхѥ̹ɽ๱危ɕѥ}͔(qхѥ̹ɽๅѥ٥Ѥ}͔(qхѥqљɴ̹ɽํ}͔(qхѥ̹ɽํ}͔դ(qхѥ̹ɽํ}͔դɅa̤(qхѥ̹ɽํ}͔դѽI٥ܤ(qхѥ̹ɽํ}͔ѕɥ̤(qхѥ̹ɽํ}͔ѕɥ̹ᑕ(qхѥ̹ɽ๹٥ѥ}͔(՝%qхѥ̹ɽํ}͔դѽ(qхѥ̹ɽ้ɕѥ(qхѥ̹ɽ้Ѡ(̹̀ɽ้eȤ(qхѥ̹йɽ(̹̀йeȤ(qхѥ̹ɽ๡й٥ѥ}͔(qхѥ̹}͔(qхѥ̹͙(qхѥ̹ɽ๝AݥФ(qхѥ̹ɽ๝ѕɥ̤(qхѥ̹ѥȤ(qхѥɽํՉѤ鍕ՉѤɥQ脸8(ɕ1Ʌɥ͕ɥ̹͕ȹ̤(՝%qхѥ̹ɤ(ѕ͑%qхѥ̹չФ(ѕ͑%qхѥ̹ѱํɽՑ̹ѕ͐(ѕ͑%qхѥ̹(ѕ͑%qхѥ̹йٴ(ѕ͑%qхѥ̹ѕɉ(ѕ͑%qхѥ̹ɽѕ͑(ɽQ͑%qхѥ̹ɽ๑͐ᐹչФ(ɽQ͑%qхѥ̹ɽ๑͐́ɕ͍ɔ(ɽQ͑%qхѥqљɴ̹ɽํ}͔(ɽQ͑%qхѥ̹ɽํ}͔դѕ͐չА(՝%qхѥ̹ɽํ}͔դѕ͐͐)��