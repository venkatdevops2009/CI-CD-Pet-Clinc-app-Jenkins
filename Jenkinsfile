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

        // Ensure Spring Boot and Maven produce ANSI color codes
        SPRING_OUTPUT_ANSI_ENABLED = 'ALWAYS'
        MAVEN_OPTS = '-Djansi.passthrough=true -Dspring.output.ansi.enabled=always'
        TERM = 'xterm-256color'
    }

    options {
        // Wrap entire pipeline console output with AnsiColor so ANSI escape sequences render
        ansiColor('xterm')
        disableConcurrentBuilds()
        timeout(time: 25, unit: 'MINUTES')
        timestamps()
    }

    parameters {
        // Which Git branch to build (leave blank to use repository default)
        string(name: 'BRANCH', defaultValue: 'main', description: 'Git branch to checkout and build (e.g. main, develop)')

        // Optionally override application version read from pom.xml
        string(name: 'APP_VERSION_OVERRIDE', defaultValue: '', description: 'Override version read from pom.xml (leave empty to use pom.xml version)')

        // Deploy target environment
        choice(name: 'DEPLOY_ENV', choices: ['dev', 'staging', 'prod'], description: 'Select deployment target environment')

        // Use ECR images pushed by this pipeline instead of local builds
        booleanParam(name: 'USE_ECR_IMAGES', defaultValue: true, description: 'If true, docker compose will pull images from ECR for deployment')

        // Skip optional verification steps for faster iterative runs
        booleanParam(name: 'SKIP_SONAR', defaultValue: false, description: 'Skip SonarQube analysis (not recommended)')
        booleanParam(name: 'SKIP_SONARQULITY', defaultValue: false, description: 'Skip SonarQube qulity gate check (not recommended)')
        booleanParam(name: 'SKIP_TRIVY_FS', defaultValue: false, description: 'Skip Trivy filesystem scan')
        booleanParam(name: 'SKIP_TRIVY_IMG', defaultValue: false, description: 'Skip Trivy image scans')

        // Force docker rebuild even if an image with same tag exists
        booleanParam(name: 'FORCE_REBUILD', defaultValue: false, description: 'Force docker build instead of reusing existing images')

        // Allow the operator to override deployed image names (advanced)
        string(name: 'APP_IMAGE_OVERRIDE', defaultValue: '', description: 'Optional full image name to deploy (overrides ECR image), e.g. 123456789012.dkr.ecr.us-east-1.amazonaws.com/petclinic:1.2.0')
        string(name: 'MYSQL_IMAGE_OVERRIDE', defaultValue: '', description: 'Optional MySQL image name to deploy (overrides ECR image)')

        // Keep the existing DEPLOY toggle for backward-compatibility
        booleanParam(name: 'DEPLOY', defaultValue: true, description: 'Deploy after successful build, Sonar, and Trivy checks')

        // Credentials id to use for AWS operations (use stored credential id)
        string(name: 'AWS_CREDS_ID', defaultValue: 'aws-creds', description: 'Jenkins credentials id for AWS (used to push/pull from ECR)')
    }

    stages {
        stage('Checkout') {
           steps {
               ansiColor('xterm') {
                   echo "\u001B[1;34m=== Starting stage: Checkout ===\u001B[0m"
                   script {
                       // If a branch parameter is provided, checkout that branch; otherwise fall back to scm
                       if (params.BRANCH && params.BRANCH.trim()) {
                           echo "Checking out branch: ${params.BRANCH}"
                           checkout([$class: 'GitSCM', branches: [[name: "refs/heads/${params.BRANCH}"]], userRemoteConfigs: scm.userRemoteConfigs])
                       } else {
                           checkout scm
                       }
                   }
                   echo "\u001B[1;32m=== Completed stage: Checkout ===\u001B[0m"
               }
           }
        }

        stage('Read Version') {
           steps {
               ansiColor('xterm') {
                   echo "\u001B[1;34m=== Starting stage: Read Version ===\u001B[0m"
                   script {
                       def pom = readMavenPom(file: 'petclinc/pom.xml')
                       def pomVersion = pom.version ?: '1.0.0'
                       // Allow explicit override via parameter
                       if (params.APP_VERSION_OVERRIDE && params.APP_VERSION_OVERRIDE.trim()) {
                           appVersion = params.APP_VERSION_OVERRIDE.trim()
                           echo "Application version overridden by parameter: ${appVersion}"
                       } else {
                           appVersion = pomVersion
                           echo "Application version from pom.xml: ${appVersion}"
                       }
                   }
                   echo "\u001B[1;32m=== Completed stage: Read Version ===\u001B[0m"
               }
           }
        }

        stage('Build & Unit Tests') {
           steps {
               ansiColor('xterm') {
                   echo "\u001B[1;34m=== Starting stage: Build & Unit Tests ===\u001B[0m"
                   sh '''
                       cd petclinc
                       mvn -B -Dstyle.color=always -Dspring.output.ansi.enabled=always clean verify
                   '''
                   echo "\u001B[1;32m=== Completed stage: Build & Unit Tests ===\u001B[0m"
               }
           }
        }

        stage('Verify Build Artifacts') {
           steps {
               ansiColor('xterm') {
                   echo "\u001B[1;34m=== Starting stage: Verify Build Artifacts ===\u001B[0m"
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
                   echo "\u001B[1;32m=== Completed stage: Verify Build Artifacts ===\u001B[0m"
               }
           }
        }

        stage('SonarQube Analysis') {
           when {
               expression { return !params.SKIP_SONAR }
           }
           steps {
               ansiColor('xterm') {
                   echo "\u001B[1;34m=== Starting stage: SonarQube Analysis ===\u001B[0m"
                   withSonarQubeEnv('sonar-server') {
                       sh '''
                           cd petclinc
                           mvn -B -Dstyle.color=always -Dspring.output.ansi.enabled=always sonar:sonar \
                             -Dsonar.projectKey=petclinic-cicd \
                             -Dsonar.projectName=petclinic-cicd \
                             -Dsonar.sources=src/main/java \
                             -Dsonar.tests=src/test/java \
                             -Dsonar.java.binaries=target/classes \
                             -Dsonar.java.test.binaries=target/test-classes \
                             -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                       '''
                   }
                   echo "\u001B[1;32m=== Completed stage: SonarQube Analysis ===\u001B[0m"
               }
           }
        }

        stage('SonarQube Quality Gate') {
           when {
               expression { return !params.SKIP_SONARQULITY }
           }
           steps {
               ansiColor('xterm') {
                   echo "\u001B[1;34m=== Starting stage: SonarQube Quality Gate ===\u001B[0m"
                   timeout(time: 10, unit: 'MINUTES') {
                       script {
                           def qg = waitForQualityGate()
                           if (qg.status != 'OK') {
                               error "Pipeline aborted: ${qg.status}"
                           }
                       }
                   }
                   echo "\u001B[1;32m=== Completed stage: SonarQube Quality Gate ===\u001B[0m"
               }
           }
        }

        stage('Trivy File System Scan') {
           when {
               expression { return !params.SKIP_TRIVY_FS }
           }
           steps {
               ansiColor('xterm') {
                   echo "\u001B[1;34m=== Starting stage: Trivy File System Scan ===\u001B[0m"
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
                   echo "\u001B[1;32m=== Completed stage: Trivy File System Scan ===\u001B[0m"
               }
           }
        }

        stage('Docker Build') {
           steps {
               ansiColor('xterm') {
                   echo "\u001B[1;34m=== Starting stage: Docker Build ===\u001B[0m"
                   script {
                       // Allow selecting AWS credentials id from parameters
                       withAWS(credentials: params.AWS_CREDS_ID ?: 'aws-creds', region: 'us-east-1') {
                           sh """
                               aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${acc_id}.dkr.ecr.${region}.amazonaws.com

                               // Decide whether to force build or trust existing images
                               if [ '${params.FORCE_REBUILD}' = 'true' ]; then
                                 echo "Forcing a rebuild of images"
                                 docker build -t ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion} -f petclinc/Dockerfile petclinc
                                 docker build -t ${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion} -f mysql/Dockerfile mysql
                               else
                                 echo "Building images (may reuse cache)"
                                 docker build -t ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion} -f petclinc/Dockerfile petclinc
                                 docker build -t ${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion} -f mysql/Dockerfile mysql
                               fi
                           """
                       }
                   }
                   echo "\u001B[1;32m=== Completed stage: Docker Build ===\u001B[0m"
               }
           }
        }

        stage('Trivy Image Scan') {
           steps {
               ansiColor('xterm') {
                   echo "\u001B[1;34m=== Starting stage: Trivy Image Scan ===\u001B[0m"
                   script {
                       def dockerfileScan = sh(
                           script: """
                               set +e
                               echo "=== Trivy Dockerfile debug ==="
                               trivy config --exit-code 1 --severity HIGH,CRITICAL --format table ./petclinc/Dockerfile || true
                               trivy config --exit-code 1 --severity HIGH,CRITICAL --format table ./mysql/Dockerfile || true
                           """,
                           returnStatus: true
                       )

                       def appImageScan = sh(
                           script: """
                               set +e
                               echo "=== Trivy app image debug ==="
                               trivy image --scanners vuln --pkg-types os,library --exit-code 1 --severity HIGH,CRITICAL --format table ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion} || true
                           """,
                           returnStatus: true
                       )

                       def mysqlImageScan = sh(
                           script: """
                               set +e
                               echo "=== Trivy mysql image debug ==="
                               trivy image --scanners vuln --pkg-types os,library --exit-code 1 --severity HIGH,CRITICAL --format table ${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion} || true
                           """,
                           returnStatus: true
                       )

                       echo "Dockerfile scan exit code: ${dockerfileScan}"
                       echo "App image scan exit code: ${appImageScan}"
                       echo "MySQL image scan exit code: ${mysqlImageScan}"
                   }
                   echo "\u001B[1;32m=== Completed stage: Trivy Image Scan ===\u001B[0m"
               }
           }
        }

        stage('ECR Image Push') {
           steps {
               ansiColor('xterm') {
                   echo "\u001B[1;34m=== Starting stage: ECR Image Push ===\u001B[0m"
                   script {
                       withAWS(credentials: 'aws-creds', region: 'us-east-1') {
                           sh """
                               aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${acc_id}.dkr.ecr.${region}.amazonaws.com
                               // Push images (use provided AWS creds id)
                               docker push ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion}
                               docker push ${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion}

                               // Optionally tag as 'latest' for convenience (comment out if undesired)
                               docker tag ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion} ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:latest || true
                               docker push ${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:latest || true
                           """
                       }
                   }
                   echo "\u001B[1;32m=== Completed stage: ECR Image Push ===\u001B[0m"
               }
           }
        }

        stage('Deploy') {
           when {
               expression { return params.DEPLOY == true }
           }
           steps {
               ansiColor('xterm') {
                   echo "\u001B[1;34m=== Starting stage: Deploy ===\u001B[0m"
                   script {
                       // Compute image names with possible overrides from parameters
                       def defaultAppImage = "${acc_id}.dkr.ecr.${region}.amazonaws.com/${app_repo}:${appVersion}"
                       def defaultMysqlImage = "${acc_id}.dkr.ecr.${region}.amazonaws.com/${mysql_repo}:${appVersion}"

                       def appImage = params.APP_IMAGE_OVERRIDE?.trim() ? params.APP_IMAGE_OVERRIDE.trim() : defaultAppImage
                       def mysqlImage = params.MYSQL_IMAGE_OVERRIDE?.trim() ? params.MYSQL_IMAGE_OVERRIDE.trim() : defaultMysqlImage

                       withAWS(credentials: params.AWS_CREDS_ID ?: 'aws-creds', region: region) {
                           sh """
                               echo "Deploying Pet Clinic application using images"
                               echo "APP_IMAGE=${appImage}"
                               echo "MYSQL_IMAGE=${mysqlImage}"

                               aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${acc_id}.dkr.ecr.${region}.amazonaws.com

                               # Export env vars used by docker-compose (compose file uses ${APP_IMAGE} and ${MYSQL_IMAGE})
                               export APP_IMAGE=${appImage}
                               export MYSQL_IMAGE=${mysqlImage}

                               # If requested, pull from registry to ensure latest scanned image
                               if [ '${params.USE_ECR_IMAGES}' = 'true' ]; then
                                 echo "Pulling images from registry"
                                 docker compose pull || true
                               fi

                               # Start using pulled images (no build in deploy phase)
                               docker compose down || true
                               docker compose up -d --no-build
                           """
                       }
                   }
                   echo "\u001B[1;32m=== Completed stage: Deploy ===\u001B[0m"
               }
           }
        }
    }

    post {
        always {
           sh '''
           
           docker image prune -f || true
           
           '''
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
