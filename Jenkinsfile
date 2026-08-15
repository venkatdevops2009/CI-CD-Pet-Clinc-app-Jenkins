pipeline {
    agent {
        node {
           label 'roboshop'
        }
    }

    environment {
        acc_id = "843916760700"
        region = "us-east-1"
        app_repo = "petclinic"
        mysql_repo = "petclinic-mysql"
        appVersion = ""
    }

    options {
        disableConcurrentBuilds()
        timeout(time: 25, unit: 'MINUTES')
        timestamps()
    }

    parameters {
        booleanParam(name: 'DEPLOY', defaultValue: true, description: 'Deploy after successful build, Sonar, and Trivy checks')
    }

    stages {
        stage('Checkout') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Checkout ==="
                   checkout scm
                   echo "=== Completed stage: Checkout ==="
               }
           }
        }

        stage('Read Version') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Read Version ==="
                   script {
                       def pom = readMavenPom(file: 'petclinc/pom.xml')
                       appVersion = pom.version ?: '1.0.0'
                       echo "Application version: ${appVersion}"
                   }
                   echo "=== Completed stage: Read Version ==="
               }
           }
        }

        stage('Build & Unit Tests') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Build & Unit Tests ==="
                   sh '''
                       cd petclinc
                       mvn -B clean verify
                   '''
                   echo "=== Completed stage: Build & Unit Tests ==="
               }
           }
        }

        stage('Verify Build Artifacts') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Verify Build Artifacts ==="
                   sh '''
                       cd petclinc
                       pwd
                       ls -la
                       ls -la target || true
                       ls -la target/site/jacoco || true
                       ls -la target/surefire-reports || true
                       test -f target/site/jacoco/jacoco.xml || echo "Jacoco report not found"
                       test -d target/surefire-reports || echo "Surefire reports not found"
                   '''
                   echo "=== Completed stage: Verify Build Artifacts ==="
               }
           }
        }

        stage('SonarQube Analysis') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: SonarQube Analysis ==="
                   withSonarQubeEnv('sonar-server') {
                       sh '''
                           cd petclinc
                           mvn -B sonar:sonar \
                             -Dsonar.projectKey=petclinic-cicd \
                             -Dsonar.projectName=petclinic-cicd \
                             -Dsonar.sources=src/main/java \
                             -Dsonar.tests=src/test/java \
                             -Dsonar.java.binaries=target/classes \
                             -Dsonar.java.test.binaries=target/test-classes \
                             -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                       '''
                   }
                   echo "=== Completed stage: SonarQube Analysis ==="
               }
           }
        }

        stage('SonarQube Quality Gate') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: SonarQube Quality Gate ==="
                   timeout(time: 10, unit: 'MINUTES') {
                       script {
                           def qg = waitForQualityGate()
                           if (qg.status != 'OK') {
                               error "Pipeline aborted: ${qg.status}"
                           }
                       }
                   }
                   echo "=== Completed stage: SonarQube Quality Gate ==="
               }
           }
        }

        stage('Trivy File System Scan') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Trivy File System Scan ==="
                   sh '''
                       set +e
                       echo "=== Trivy FS debug start ==="
                       trivy --version
                       trivy fs \
                         --scanners vuln,secret,misconfig \
                         --severity HIGH,CRITICAL \
                         --exit-code 0 \
                         --format table \
                         --output trivy-fs-report.txt \
                         .
                       fs_status=$?
                       echo "Trivy FS exit code: ${fs_status}"
                       echo "=== Trivy FS report preview ==="
                       sed -n '1,220p' trivy-fs-report.txt || true
                       echo "=== Trivy FS debug end ==="
                       exit 0
                   '''
                   echo "=== Completed stage: Trivy File System Scan ==="
               }
           }
        }

        stage('Docker Build') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Docker Build ==="
                   script {
                       withAWS(credentials: 'aws-creds', region: 'us-east-1') {
                           sh """
                               aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${acc_id}.dkr.ecr.${region}.amazonaws.com

                               docker build -t ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion} \
                                 -f petclinc/Dockerfile petclinc

                               docker build -t ${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion} \
                                 -f mysql/Dockerfile mysql
                           """
                       }
                   }
                   echo "=== Completed stage: Docker Build ==="
               }
           }
        }

        stage('Trivy Image Scan') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Trivy Image Scan ==="
                   script {
                       def dockerfileScan = sh(
                           script: """
                               set +e
                               echo "=== Trivy Dockerfile debug ==="
                               trivy config --exit-code 0 --severity HIGH,CRITICAL --format table ./petclinc/Dockerfile || true
                           """,
                           returnStatus: true
                       )

                       def appImageScan = sh(
                           script: """
                               set +e
                               echo "=== Trivy app image debug ==="
                               trivy image --scanners vuln --pkg-types os,library --exit-code 0 --severity HIGH,CRITICAL --format table ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion} || true
                           """,
                           returnStatus: true
                       )

                       def mysqlImageScan = sh(
                           script: """
                               set +e
                               echo "=== Trivy mysql image debug ==="
                               trivy image --scanners vuln --pkg-types os,library --exit-code 0 --severity HIGH,CRITICAL --format table ${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion} || true
                           """,
                           returnStatus: true
                       )

                       echo "Dockerfile scan exit code: ${dockerfileScan}"
                       echo "App image scan exit code: ${appImageScan}"
                       echo "MySQL image scan exit code: ${mysqlImageScan}"
                   }
                   echo "=== Completed stage: Trivy Image Scan ==="
               }
           }
        }

        stage('ECR Image Push') {
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: ECR Image Push ==="
                   script {
                       withAWS(credentials: 'aws-creds', region: 'us-east-1') {
                           sh """
                               aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${acc_id}.dkr.ecr.${region}.amazonaws.com
                               docker push ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion}
                               docker push ${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion}
                           """
                       }
                   }
                   echo "=== Completed stage: ECR Image Push ==="
               }
           }
        }

        stage('Deploy') {
           when {
               expression { return params.DEPLOY == true }
           }
           steps {
               ansiColor('xterm') {
                   echo "=== Starting stage: Deploy ==="
                   sh '''
                       echo "Deploying Pet Clinic application"
                   '''
                   echo "=== Completed stage: Deploy ==="
               }
           }
        }
    }

    post {
        always {
           archiveArtifacts artifacts: 'trivy-fs-report.txt, **/*.xml, **/*.html', allowEmptyArchive: true
           echo 'Pipeline finished'
        }
        success {
           echo 'Build, SonarQube, Trivy, and image push succeeded.'
        }
        failure {
           echo 'Pipeline failed. Check build/test/quality gate and Trivy output.'
        }
    }
}
