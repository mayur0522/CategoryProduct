pipeline {
    agent any

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
        PROJECT_ID   = 'gke-springboot-472605'
        REGION       = 'asia-south1'
        ZONE         = 'asia-south1-b'
        CLUSTER      = 'gke-cluster'
        IMAGE_NAME   = 'springboot-app'
        REPO         = "asia-south1-docker.pkg.dev/${PROJECT_ID}/springboot-artifacts/${IMAGE_NAME}"

        // Vault config
        VAULT_ADDR   = 'http://34.180.3.84:8200/'      // Replace with your Vault URL
        VAULT_TOKEN  = credentials('vault-token')      // Jenkins Vault token credential

        // Cloud SQL
        CLOUD_SQL_INSTANCE = 'project:asia-south1:cloudsql-instance'  // Replace with your instance
    }

    tools {
        maven 'maven3'
        jdk 'jdk-21'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/mayur0522/CategoryProduct.git'
            }
        }

        stage('Build') {
            steps {
                sh "mvn clean package -DskipTests"
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh """
                            ${env.SCANNER_HOME}/bin/sonar-scanner \
                            -Dsonar.projectKey=springboot-app \
                            -Dsonar.projectName=springboot-app \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.token=$SONAR_TOKEN
                        """
                    }
                }
            }
        }

        stage('OWASP Dependency Check') {
            steps {
                dependencyCheck additionalArguments: '--scan ./', odcInstallation: 'DC'
                dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
            }
        }

        stage('Deploy to Nexus') {
            steps {
                withMaven(globalMavenSettingsConfig: 'global-maven', jdk: 'jdk-21', maven: 'maven3') {
                    sh "mvn deploy -DskipTests"
                }
            }
        }

        stage('Retrieve DB Credentials from Vault') {
            steps {
                script {
                    // Fetch DB username/password from Vault
                    env.DB_USERNAME = sh(script: "vault kv get -field=username secret/springboot-db", returnStdout: true).trim()
                    env.DB_PASSWORD = sh(script: "vault kv get -field=password secret/springboot-db", returnStdout: true).trim()
                    echo "✅ Retrieved DB credentials from Vault"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    docker build \
                        --build-arg DB_USERNAME=${DB_USERNAME} \
                        --build-arg DB_PASSWORD=${DB_PASSWORD} \
                        -t ${REPO}:latest -f Dockerfile .
                """
            }
        }

        stage('Authenticate with GCP') {
            steps {
                withCredentials([file(credentialsId: 'gcp-service-account-key', variable: 'GOOGLE_APPLICATION_CREDENTIALS')]) {
                    sh 'gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS'
                    sh "gcloud config set project ${PROJECT_ID}"
                    sh "gcloud auth configure-docker ${REGION}-docker.pkg.dev --quiet"
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                sh "docker push ${REPO}:latest"
            }
        }

        stage('Deploy to GKE with Vault secrets') {
            steps {
                script {
                    // Get GKE credentials
                    sh "gcloud container clusters get-credentials ${CLUSTER} --zone ${ZONE} --project ${PROJECT_ID}"

                    // Apply deployment YAML with environment variables from Vault
                    sh """
                        kubectl set env deployment/springboot-app \
                        DB_USERNAME=${DB_USERNAME} \
                        DB_PASSWORD=${DB_PASSWORD} \
                        CLOUD_SQL_CONNECTION_NAME=${CLOUD_SQL_INSTANCE}
                    """

                    // Apply Kubernetes manifests
                    sh 'kubectl apply -f k8s/deployment.yaml --wait'
                    sh 'kubectl apply -f k8s/service.yaml --wait'
                    echo "✅ Spring Boot app deployed to GKE with Vault secrets"
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed.'
        }
    }
}
