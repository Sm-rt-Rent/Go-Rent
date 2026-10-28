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
                        // Correct syntax: 'docker compose -f file.yml down'
                        bat '"C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" compose -f docker-compose.yml down || exit 0'
                        bat '"C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe" compose -f docker-compose.yml up --build -d'
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