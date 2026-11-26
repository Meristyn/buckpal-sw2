def runGradle(String tasks) {
    if (isUnix()) {
        sh "chmod +x gradlew && ./gradlew --no-daemon ${tasks}"
    } else {
        bat ".\\gradlew.bat --no-daemon ${tasks}"
    }
}

pipeline {
    agent any

    tools {
        jdk 'JDK17'
    }

    parameters {
        booleanParam(
            name: 'RUN_SELENIUM',
            defaultValue: true,
            description: 'Ejecutar pruebas E2E de caja negra con Selenium (requiere Chrome/Chromium en el agente).'
        )
    }

    options {
        timestamps()
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 20, unit: 'MINUTES')
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Obteniendo codigo del repositorio...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Compilando codigo fuente y pruebas...'
                runGradle('clean compileJava compileTestJava')
            }
        }

        stage('Unit & Integration Tests') {
            steps {
                echo 'Ejecutando pruebas unitarias, de arquitectura, integracion y sistema...'
                runGradle('test jacocoTestReport')
            }
        }

        stage('E2E Selenium Tests') {
            when {
                expression { params.RUN_SELENIUM }
            }
            steps {
                echo 'Ejecutando pruebas E2E de caja negra con Selenium...'
                runGradle('blackboxTest jacocoTestReport')
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo 'Ejecutando analisis de calidad y cobertura con SonarQube...'
                withSonarQubeEnv('SonarQube') {
                    runGradle('sonar')
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo 'Consultando Quality Gate en SonarQube (modo informativo)...'
                catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE', catchInterruptions: true) {
                    timeout(time: 2, unit: 'MINUTES') {
                        def qg = waitForQualityGate(abortPipeline: false)
                        echo "Estado del Quality Gate de SonarQube: ${qg.status}"
                    }
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Empaquetando artefacto Spring Boot (bootJar)...'
                runGradle('bootJar')
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/build/test-results/**/*.xml'
            script {
                try {
                    recordCoverage(
                        tools: [[parser: 'JACOCO', pattern: 'build/reports/jacoco/test/jacocoTestReport.xml']]
                    )
                } catch (NoSuchMethodError ignored) {
                    echo 'Plugin "Coverage" no instalado en Jenkins; se omite la publicacion grafica de JaCoCo.'
                }
            }
            archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true, allowEmptyArchive: true
        }

        success {
            echo 'Pipeline ejecutado correctamente.'
        }

        unstable {
            echo 'Pipeline finalizado con advertencias. Revisar resultados de pruebas o Quality Gate.'
        }

        failure {
            echo 'Error en el pipeline. Revisar los resultados.'
        }
    }
}
