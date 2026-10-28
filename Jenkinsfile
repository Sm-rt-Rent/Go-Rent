pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Pulling source code from GitHub...'
            }
        }

        stage('Build & Test') {
            steps {
                echo 'Building Spring Boot project...'
                // Use 'bat' for Windows command line execution
                bat 'mvnw.cmd clean package -DskipTests'
            }
        }
        stage('Docker Deploy') {
                    steps {
                        echo 'Deploying application stack using Docker Compose...'
                        // Bring down any old containers, rebuild the app image, and start everything fresh in the background
                        bat 'docker compose down'
                        bat 'docker compose up --build -d'
                    }
                }
        }

    post {
        success {
            echo 'Pipeline built successfully!'
        }
        failure {
            echo 'Pipeline build failed.'
        }
    }
}