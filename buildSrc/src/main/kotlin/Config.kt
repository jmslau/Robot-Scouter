@Suppress("MayBeConstant") // Improve perf when changing values
object Config {
    private const val kotlinVersion = "1.2.51"

    object SdkVersions {
        val compile = 28
        val target = 28
        val min = 16
    }

    object Plugins {
        val android = "com.android.tools.build:gradle:3.2.0-beta02"
        val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"

        val google = "com.google.gms:google-services:4.0.1"
        val firebase = "com.google.firebase:firebase-plugins:1.1.5"
        val fabric = "io.fabric.tools:gradle:1.25.4"
        val publishing = "com.github.Triple-T:gradle-play-publisher:42ffe9922c"

        val ktlint = "com.github.shyiko:ktlint:0.23.1"
    }

    object Libs {
        object Kotlin {
            private const val coroutinesVersion = "0.23.4"

            val jvm = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
            val js = "org.jetbrains.kotlin:kotlin-stdlib-js:$kotlinVersion"
            val coroutinesAndroid =
                    "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion"
            val coroutinesJs =
                    "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$coroutinesVersion"
        }

        object Anko {
            private const val version = "0.10.5"

            val common = "org.jetbrains.anko:anko-common:$version"
            val coroutines = "org.jetbrains.anko:anko-coroutines:$version"
            val appCompat = "org.jetbrains.anko:anko-appcompat-v7-commons:$version"
            val design = "org.jetbrains.anko:anko-design:$version"
        }

        object Jetpack {
            private const val version = "1.0.0-beta01"
            private const val lifecycleVersion = "2.0.0-beta01"
            private const val workVersion = "1.0.0-alpha04"

            val core = "androidx.core:core-ktx:$version"

            val multidex = "androidx.multidex:multidex:2.0.0"
            val appCompat = "androidx.appcompat:appcompat:$version"
            val fragment = "androidx.fragment:fragment-ktx:$version"
            val material = "com.google.android.material:material:$version"
            val constraint = "androidx.constraintlayout:constraintlayout:1.1.2"
            val cardView = "androidx.cardview:cardview:$version"
            val palette = "androidx.palette:palette-ktx:$version"
            val emoji = "androidx.emoji:emoji-appcompat:$version"
            val browser = "androidx.browser:browser:$version"
            val pref = "androidx.preference:preference:$version"
            val prefKtx = "androidx.preference:preference-ktx:$version"

            val common = "androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion"
            val extensions = "androidx.lifecycle:lifecycle-extensions:$lifecycleVersion"
            val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion"

            val work = "android.arch.work:work-runtime-ktx:$workVersion"
            val workFirebase = "android.arch.work:work-firebase:$workVersion"
        }

        object Firebase {
            val core = "com.google.firebase:firebase-core:16.0.1"
            val auth = "com.google.firebase:firebase-auth:16.0.2"
            val firestore = "com.google.firebase:firebase-firestore:17.0.2"
            val functions = "com.google.firebase:firebase-functions:16.0.1"
            val storage = "com.google.firebase:firebase-storage:16.0.1"
            val config = "com.google.firebase:firebase-config:16.0.0"
            val indexing = "com.google.firebase:firebase-appindexing:16.0.1"
            val messaging = "com.google.firebase:firebase-messaging:17.1.0"
            val invites = "com.google.firebase:firebase-invites:16.0.1"
            val perf = "com.google.firebase:firebase-perf:16.0.0"

            val crashlytics = "com.crashlytics.sdk.android:crashlytics:2.9.4"
        }

        object PlayServices {
            private const val version = "15.0.1"

            val auth = "com.google.android.gms:play-services-auth:$version"
            val nearby = "com.google.android.gms:play-services-nearby:$version"
            val playCore = "com.google.android.play:core:1.2.0"
        }

        object FirebaseUi {
            private const val version = "c280098444"

            val firestore =
                    "com.github.SUPERCILEX.FirebaseUI-Android:firebase-ui-firestore:$version"
            val auth = "com.github.SUPERCILEX.FirebaseUI-Android:firebase-ui-auth:$version"
            val facebook = "com.facebook.android:facebook-login:4.34.0"
            val twitter = "com.twitter.sdk.android:twitter-core:3.1.1@aar"
        }

        object Misc {
            private const val leakCanaryVersion = "1.5.4"
            private const val retrofitVersion = "2.4.0"
            private const val poiVersion = "3.17"

            private const val glideVersion = "4.7.1"

            val leakCanary = "com.squareup.leakcanary:leakcanary-android:$leakCanaryVersion"
            val leakCanaryNoop =
                    "com.squareup.leakcanary:leakcanary-android-no-op:$leakCanaryVersion"
            val retrofit = "com.squareup.retrofit2:retrofit:$retrofitVersion"
            val retrofitGson = "com.squareup.retrofit2:converter-gson:$retrofitVersion"
            val gson = "com.google.code.gson:gson:2.8.5"
            val poi = "com.github.SUPERCILEX.poi-android:poi:$poiVersion"
            val poiProguard = "com.github.SUPERCILEX.poi-android:proguard:$poiVersion"

            val glide = "com.github.bumptech.glide:glide:$glideVersion"
            val glideRv = "com.github.bumptech.glide:recyclerview-integration:$glideVersion"
            val snap = "com.github.rubensousa:gravitysnaphelper:1.5"
            val permissions = "pub.devrel:easypermissions:1.2.0"
            val mttp = "uk.co.samuelwall:material-tap-target-prompt:2.9.0"
            val billing = "com.android.billingclient:billing:1.1"
            val licenses = "com.github.yshrsmz:LicenseAdapter:7130638755"
        }
    }
}