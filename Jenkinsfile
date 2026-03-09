pipeline {
    agent any

    environment {
        IMAGE_NAME = "218468/week7_calculator"
        IMAGE_TAG  = "latest"
    }

    stages {

        stage('Checkout') {
            steps {
                echo '📥 Cloning repository...'
                checkout scm
            }
        }

        stage('Build JAR') {
            steps {
                echo '🔨 Building fat JAR with Maven...'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '🐳 Building Docker image...'
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('Push Docker Image') {
            steps {
                echo '📤 Pushing image to Docker Hub...'
                sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }

        stage('Start Services') {
            steps {
                echo '🚀 Starting DB container with docker compose...'
                sh 'docker compose up -d db'
            }
        }

        stage('Verify DB & Tables') {
            steps {
                echo '⏳ Waiting 30s for MariaDB to fully initialise...'
                sh 'sleep 30'
                echo '🔍 Checking that all tables exist...'
                sh "docker compose exec -T db mariadb -uroot -pTest12 calc_data -e 'SHOW TABLES; DESCRIBE calc_results;'"
            }
        }
    }

    post {
        success {
            echo '🎉 Pipeline completed successfully! DB and tables verified.'
        }
        failure {
            echo '❌ Pipeline failed. Check the logs above.'
            bat 'docker compose logs --tail=50'
        }
        always {
            echo '🧹 Pipeline finished.'
        }
    }
}