import com.android.build.gradle.BaseExtension
import java.util.Base64
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
    id("jacoco")
}

tasks.withType<Test> {
    extensions.configure(JacocoTaskExtension::class) {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}


// Le fichier google-services.json est exclu de Git
// Son contenu est dans un secret GitHub KEY_GOOGLE_SERVICES_JSON_BASE64 (encodé en Base64)
// Il faut donc recréer ce fichier en environnement GitHub
// Vu que je n'ai pas besoin de son contenu dans l'appli,
// Sa création est faite directement dans le .yaml


android {
    namespace = "com.openclassrooms.rebonnte"
    compileSdk = 34

    signingConfigs {

        // Fichier properties permettant de signer l'application dans le cadre du CD
        val keystoreProperties = Properties()
        val keystorePropertiesFile = rootProject.file("keystore.properties")
        // Si le fichier keystore existe (= build en local)
        if (keystorePropertiesFile.exists()) {
            // Chargement du fichier keystore.properties (non présent sur gitHub)
            keystoreProperties.load(keystorePropertiesFile.inputStream())
        }
        else{
            // Build depuis GitHub Action
            // Charger depuis les secrets GitHub (Déclarer dans GitHub -> mon repo -> Settings -> Actions secrets and variables)
            // Le .yaml crée les variables d'environnement KEYSTORE_PASSWORD / KEY_ALIAS /....

            keystoreProperties["storePassword"] = System.getenv("KEYSTORE_PASSWORD")
            keystoreProperties["keyAlias"] = System.getenv("KEY_ALIAS")
            keystoreProperties["keyPassword"] = System.getenv("KEY_PASSWORD")

            // le storeFile est stocké dans les secrets mais encodé en base 64, il faut donc le décoder
            val encodedbase64content = System.getenv("KEYSTORE_BASE64")
            val decodedKeystore = Base64.getDecoder().decode(encodedbase64content)
            val keystoreFileTemp = layout.buildDirectory.dir("temp_keystore.jks").get().asFile

            // pour que le répertoire parent existe (sinon erreur "No such file or directory")
            keystoreFileTemp.parentFile?.mkdirs()

            // Créer un fichier temporaire pour stocker le keystore décodé
            keystoreFileTemp.writeBytes(decodedKeystore)

            // Je fais çà dans le gradle car j'ai besoin du chemin du fichier ici (sinon j'aurai pu le faire uniquement dans le yaml comme google-services.json)
            keystoreProperties["storeFile"] = keystoreFileTemp.absolutePath

        }

        create("release") {
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword =  keystoreProperties.getProperty("keyPassword")
        }
    }

    defaultConfig {
        applicationId = "com.openclassrooms.rebonnte"
        minSdk = 24 // Android 7
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        //testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "com.openclassrooms.rebonnte.CustomTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true // code obfusqué
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

val androidExtension = extensions.getByType<BaseExtension>()
val jacocoTestReport by tasks.registering(JacocoReport::class) {
    dependsOn("testDebugUnitTest", "createDebugCoverageReport")
    group = "Reporting"
    description = "Generate Jacoco coverage reports"

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    //val debugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug") => DEPRECATED
    val debugTree = fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug"))
    val mainSrc = androidExtension.sourceSets.getByName("main").java.srcDirs

    classDirectories.setFrom(debugTree)
    sourceDirectories.setFrom(files(mainSrc))
//    executionData.setFrom(fileTree(buildDir) { => DEPRECATED
//        include("**/*.exec", "**/*.ec")
//    })

    // exclusion des classes Hilt du rapport de couverture => marche pas
    val fileFilter = mutableSetOf(
        "**dagger**",
        "**hilt**"
    )

    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("**/*.exec", "**/*.ec")
        exclude(fileFilter)
    })
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Firebase

    // Import the Firebase BoM
    // garantit que toutes les bibliothèques Firebase utilisées dans un projet sont compatibles entre elles.
    implementation(platform(libs.firebase.bom))

    // Authentification
    implementation(libs.firebase.ui.auth)

    // Firestore = base de données NoSQL
    implementation(libs.firebase.firestore)

    // Firebase Storage (Stockage des images)
    //implementation(libs.firebase.storage)

    //DI
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    androidTestImplementation(libs.hilt.android.testing)

    // Tests

    testImplementation(libs.mockito.core)
    //testImplementation(libs.mockito.inline)

    androidTestImplementation(libs.hilt.android.testing)
    //kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.51.1")

    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    // Test unitaire des viewModels
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.coroutines.core)
}