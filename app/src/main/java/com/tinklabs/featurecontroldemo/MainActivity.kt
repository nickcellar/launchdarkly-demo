package com.tinklabs.featurecontroldemo

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.launchdarkly.android.LDClient
import com.launchdarkly.android.LDConfig
import com.launchdarkly.android.LDUser
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var client: LDClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("AAAAAA", "onCreate")
        setContentView(R.layout.activity_main)
        val ldConfig = LDConfig.Builder()
                .setMobileKey("mob-1bcb6833-a788-4d9b-b624-013bea71e902")
                .build()
        val user = LDUser.Builder("test-device")
                .name("Test device")
                .custom("model", Build.MODEL)
                .build()
        client = LDClient.init(application, ldConfig, user, 5)
        featureFlagBoolean("feature-1").subscribe {
            Log.e("AAAAAA", "feature-1: " + it)
            feature_1.text = it.toString()
        }
        featureFlagBoolean("feature-2").subscribe {
            Log.e("AAAAAA", "feature-2: " + it)
            feature_2.text = it.toString()
        }
        featureFlagString("feature-3").subscribe {
            Log.e("AAAAAA", "feature-3: " + it)
            feature_3.text = it
        }
    }

    fun featureFlagBoolean(flagKey: String): BehaviorSubject<Boolean> {
        val subject: BehaviorSubject<Boolean> = BehaviorSubject.create()
        val value = client!!.boolVariation(flagKey, false)!!
        subject.onNext(value)
        client!!.registerFeatureFlagListener(flagKey, {
            val newValue = client!!.boolVariation(flagKey, false)!!
            subject.onNext(newValue)
        })
        return subject
    }

    fun featureFlagString(flagKey: String): BehaviorSubject<String> {
        val subject: BehaviorSubject<String> = BehaviorSubject.create()
        val value    = client!!.stringVariation(flagKey, "unknown")!!
        subject.onNext(value)
        client!!.registerFeatureFlagListener(flagKey, {
            val newValue = client!!.stringVariation(flagKey, "unknown")!!
            subject.onNext(newValue)
        })
        return subject
    }
}
