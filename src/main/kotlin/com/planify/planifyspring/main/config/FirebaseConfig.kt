package com.planify.planifyspring.main.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class FirebaseConfig {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun initialize() {
        try {
            // Для локальной разработки используем service account key
            // В продакшене использовать переменную окружения GOOGLE_APPLICATION_CREDENTIALS
            val serviceAccount = javaClass.classLoader.getResourceAsStream("planify-7df34-firebase-adminsdk-fbsvc-785c49d55a.json")

            if (serviceAccount != null) {
                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build()

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options)
                    logger.info("Firebase initialized successfully")
                }
            } else {
                logger.warn("Firebase service account key not found. Push notifications will not work.")
            }
        } catch (e: Exception) {
            logger.error("Failed to initialize Firebase: ${e.message}", e)
        }
    }
}
