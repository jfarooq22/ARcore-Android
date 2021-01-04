package com.example.arcoredemoapp

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.AugmentedFace
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var ArFragment: ArFragment
    private var clickNo = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        if(checkSystemSupport(this)){
            ArFragment = supportFragmentManager.findFragmentById(R.id.fragment) as ArFragment 
//            ArFragment.arSceneView.planeRenderer.isVisible = false

            ArFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
                    clickNo++
                    if(clickNo<3) {
                    val anchor: Anchor = hitResult.createAnchor()
                    ModelRenderable.builder()
                            .setSource(this, R.raw.sofa_model)
                            .setIsFilamentGltf(true)
                            .build()
                            .thenAccept { modelRenderable -> addModel(anchor, modelRenderable) }
                            .exceptionally {
                                val toast = Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT)
                                toast.show()
                                null
                            }
                }
            }

        }


    }

    private fun addModel(anchor: Anchor, modelRenderable: ModelRenderable) {

        // Creating a AnchorNode with a specific anchor
        val anchorNode = AnchorNode(anchor)

        // attaching the anchorNode with the ArFragment
        anchorNode.setParent(ArFragment.arSceneView.scene)

        // attaching the anchorNode with the TransformableNode
        val model = TransformableNode(ArFragment.transformationSystem)
        model.scaleController.maxScale = 0.60f
        model.scaleController.elasticity = 0.30f
        model.scaleController.minScale = 0.10f
        model.rotationController
        model.translationController
        model.setParent(anchorNode)

        // attaching the 3d model with the TransformableNode
        // that is already attached with the node
        model.renderable = modelRenderable
        model.select()
    }

    fun checkSystemSupport(activity: Activity): Boolean {

        // checking whether the API version of the running Android >= 24
        // that means Android Nougat 7.0
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val openGlVersion: String = (Objects.requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE)) as ActivityManager).getDeviceConfigurationInfo().getGlEsVersion()

            // checking whether the OpenGL version >= 3.0
            if (openGlVersion.toDouble() >= 3.0) {
                true
            } else {
                Toast.makeText(activity, "App needs OpenGl Version 3.0 or later", Toast.LENGTH_SHORT).show()
                activity.finish()
                false
            }
        } else {
            Toast.makeText(activity, "App does not support required Build Version", Toast.LENGTH_SHORT).show()
            activity.finish()
            false
        }
    }

}