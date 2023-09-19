@NonCPS
def getChangelog() {
    def changeLogSets = currentBuild.changeSets
    def lines = []
    lines << "${env.GIT_COMMIT},-,${new Date()},Build ${env.BUILD_NUMBER}"
    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.length; j++) {
            def entry = entries[j]
            lines << "${entry.commitId},${entry.author.toString()},${new Date(entry.timestamp)},${entry.msg}"
        }
    }
    return lines.join("\n").toString()
}

def isGiac = env.BRANCH_NAME.matches("dependabot.*giac.*")
def isEditor = env.BRANCH_NAME.matches("dev|(.*editor)")
def hasSourcemap = env.BRANCH_NAME.matches("dev|apps-4963")
def modules = isEditor ? '-Pgmodule="org.geogebra.web.SuperWeb,org.geogebra.web.WebSimple,org.geogebra.web.Editor"' : ''
def nodeLabel = isGiac ? "Ubuntu" : "posix"
def s3buildDir = "geogebra/branches/${env.BRANCH_NAME}/${env.BUILD_NUMBER}/"

def s3uploadDefault = { dir, pattern, encoding, excludes="**/*.mjs", contentType="" ->
    withAWS (region:'eu-central-1', credentials:'aws-credentials') {
        if (!pattern.contains("editor/")) {
            s3Upload(bucket: 'apps-builds', workingDir: dir, path: s3buildDir, contentType: contentType,
               includePathPattern: pattern, acl: 'PublicRead', contentEncoding: encoding, excludePathPattern: excludes)
        }
        s3Upload(bucket: 'apps-builds', workingDir: dir, path: "geogebra/branches/${env.GIT_BRANCH}/latest/",
            includePathPattern: pattern, acl: 'PublicRead', contentEncoding: encoding, excludePathPattern: excludes,
            contentType: contentType)
    }
}

pipeline {
    options {
        gitLabConnection('git.geogebra.org')
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }
    agent {label nodeLabel}
    stages {
        stage('cancel prev builds') {
            when {
                expression { return env.BRANCH_NAME != 'master' && env.BRANCH_NAME != 'dev' }
            }
            steps {
                milestone label: '', ordinal:  Integer.parseInt(env.BUILD_ID) - 1
                milestone label: '', ordinal:  Integer.parseInt(env.BUILD_ID)
            }
        }
        stage('build') {
            steps {
                updateGitlabCommitStatus name: 'build', state: 'pending'
                writeFile file: 'changes.csv', text: getChangelog()
                sh label: 'build web', script: "./gradlew :web:prepareS3Upload :web:mergeDeploy ${modules} -Pgdraft=true -PdeployggbRoot=https://apps-builds.s3-eu-central-1.amazonaws.com/${s3buildDir}"
            }
        }
        stage('tests and reports') {
            when {
               expression {return !isGiac}
            }
            steps {
                sh label: 'test', script: "./gradlew test :common-jre:jacocoTestReport"
                sh label: 'static analysis', script: './gradlew pmdMain spotbugsMain -x common:spotbugsMain  -x renderer-base:spotbugsMain --max-workers=1'
                sh label: 'spotbugs common', script: './gradlew :common:spotbugsMain'
                sh label: 'code style', script: './gradlew :web:cpdCheck checkStyleMain checkStyleTest'
                junit '**/build/test-results/test/*.xml'
                recordIssues tools: [
                    cpd(pattern: '**/build/reports/cpd/cpdCheck.xml')
                ]
                recordIssues qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]], tools: [
                    spotBugs(pattern: '**/build/reports/spotbugs/*.xml', useRankAsPriority: true), 
                    pmdParser(pattern: '**/build/reports/pmd/main.xml'),
                    checkStyle(pattern: '**/build/reports/checkstyle/*.xml')
                ]
                publishCoverage adapters: [jacocoAdapter('**/build/reports/jacoco/test/*.xml')],
                    sourceFileResolver: sourceFiles('NEVER_STORE')

            }
        }
        stage('giac test') {
            when {
                expression {return isGiac}
            }
            parallel {
                stage('mac-amd64') {
                    agent {label 'ios-test'}
                    steps {
                        sh label: 'test', script: "./gradlew :desktop:test"
                        junit '**/build/test-results/test/*.xml'
                    }
                    post {
                        always { deleteDir() }
                    }
                }
                stage('mac-arm64') {
                    agent {label 'mac-mini'}
                    steps {
                        sh label: 'test', script: "./gradlew :desktop:test"
                        junit '**/build/test-results/test/*.xml'
                    }
                    post {
                        always { deleteDir() }
                    }
                }
                stage('linux') {
                    agent {label 'Ubuntu'}
                    steps {
                        sh label: 'test', script: "./gradlew :desktop:test"
                        junit '**/build/test-results/test/*.xml'
                    }
                    post {
                        always { deleteDir() }
                    }
                }
                stage('windows') {
                    agent {label 'winbuild'}
                    steps {
                        bat label: 'test', script: ".\\gradlew.bat :desktop:test"
                        junit '**/build/test-results/test/*.xml'
                    }
                    post {
                        always { deleteDir() }
                    }
                }
            }
        }
        stage('archive') {
            steps {
                script {
                    withAWS (region:'eu-central-1', credentials:'aws-credentials') {
                       s3Delete(bucket: 'apps-builds', path: "geogebra/branches/${env.GIT_BRANCH}/latest/")
                       if (hasSourcemap) {
                           s3Upload(bucket: 'apps-builds', workingDir: "web/build/symbolMapsGz", path: "geogebra/sourcemaps/",
                                   includePathPattern: "**/*.json", acl: 'PublicRead', contentEncoding: "gzip")
                       }
                    }
                    s3uploadDefault(".", "changes.csv", "")
                    s3uploadDefault("web/build/s3", "webSimple/**", "gzip")
                    s3uploadDefault("web/build/s3", "web3d/**", "gzip")
                    if (isEditor) {
                        s3uploadDefault("web/build/s3", "editor/**", "gzip")
                        s3uploadDefault("web/build/s3", "editor/**/*.mjs", "gzip", "", "text/javascript")
                    }
                    s3uploadDefault("web/build/s3", "web3d/**/*.mjs", "gzip", "", "text/javascript")
                    s3uploadDefault("web/war", "**/*.html", "")
                    s3uploadDefault("web/war", "**/deployggb.js", "")
                    s3uploadDefault("web/war", "geogebra-live.js", "")
                    s3uploadDefault("web/war", "platform.js", "")
                    s3uploadDefault("web/war", "css/**", "")
                }
            }
        }
    }
    post {
        unsuccessful {
            updateGitlabCommitStatus name: 'build', state: 'failed'
        }
        success {
            updateGitlabCommitStatus name: 'build', state: 'success'
        }
        always {
           cleanAndNotify()
        }
    }
}
