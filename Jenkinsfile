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

        VAULT_ADDR  = 'http://34.180.3.84:8200'
        VAULT_TOKEN = credentials('vault-token')  // Jenkins secret
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

        stage('Fetch Secrets from Vault') {
            steps {
                script {
                    // Fetch secrets from Vault
                    def dbCreds = sh(
                        script: "vault kv get -format=json secret/data/categorydb",
                        returnStdout: true
                    ).trim()

                    // Set environment variables for later stages
                    env.DB_USERNAME = sh(script: "echo ${dbCreds} | jq -r '.data.data.username'", returnStdout: true).trim()
                    env.DB_PASSWORD = sh(script: "echo ${dbCreds} | jq -r '.data.data.password'", returnStdout: true).trim()
                    env.DB_HOST     = sh(script: "echo ${dbCreds} | jq -r '.data.data.host'", returnStdout: true).trim()
                    env.DB_PORT     = sh(script: "echo ${dbCreds} | jq -r '.data.data.port'", returnStdout: true).trim()
                    env.DB_NAME     = sh(script: "echo ${dbCreds} | jq -r '.data.data.database'", returnStdout: true).trim()

                    echo "✅ Fetched DB credentials from Vault"
                }
            }
        }

        stage('Build JAR') {
            steps {
                sh """
                    mvn clean package -DskipTests
                    ls -l target/
                """
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

        stage('Build Docker Image') {
            steps {
                script {
                    def jar = sh(script: "ls target/*.jar", returnStdout: true).trim()
                    sh """
                        docker build \
                            --build-arg JAR_FILE=${jar} \
                            -t ${REPO}:latest -f Dockerfile .
                    """
                }
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

        stage('Deploy to GKE') {
            steps {
                script {
                    sh "gcloud container clusters get-credentials ${CLUSTER} --zone ${ZONE} --project ${PROJECT_ID}"

                    // Create Kubernetes secret with DB credentials
                    sh """
                        kubectl create secret generic categorydb-secret \
                        --from-literal=username=${DB_USERNAME} \
                        --from-literal=password=${DB_PASSWORD} \
                        --from-literal=host=${DB_HOST} \
                        --from-literal=port=${DB_PORT} \
                        --from-literal=database=${DB_NAME} \
                        --dry-run=client -o yaml | kubectl apply -f -
                    """

                    try {
                        // Deploy app
                        sh "kubectl apply -f k8s/deployment.yaml --wait --timeout=180s"
                        sh "kubectl apply -f k8s/service.yaml --wait --timeout=60s"
                        sh "kubectl wait --for=condition=ready pod -l app=springboot-app --timeout=180s"
                        echo "✅ All Kubernetes pods are ready"
                    } catch (err) {
                        echo "❌ Deployment failed, fetching pod info and logs"
                        sh "kubectl get pods -l app=springboot-app -o wide"
                        sh "kubectl describe pod -l app=springboot-app"
                        sh "kubectl logs -l app=springboot-app --all-containers=true || true"
                        error("Deployment failed, see pod status/logs above")
                    }
                }
            }
        }
    }

    post {
        success { echo '✅ Spring Boot project deployed successfully to GKE!' }
        failure { echo '❌ Deployment failed.' }
    }
}
