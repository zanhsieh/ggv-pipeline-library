#!/usr/bin/groovy
def call(body) {

  def namespace = 'default'
  echo 'using namespace: ' + namespace

  def builderImage = 'adriagalin/jenkins-jnlp-slave'
  echo 'using builder image: ' + builderImage

  def nlabel = "buildpod.${env.JOB_NAME}.${env.BUILD_NUMBER}".replace('-', '_').replace('/', '_')
  echo 'podTemplate label: ' + nlabel

  podTemplate(
    label: nlabel,
    containers: [
      containerTemplate(
        name: 'bc', 
        image: builderImage,
        workingDir: '/home/jenkins',
        privileged: true,
        ttyEnabled: true,
        args: '${computer.jnlpmac} ${computer.name}',
        envVars: [
          containerEnvVar(key: 'JENKINS_URL', value: 'http://jenkins.gogovanapp.com:8080')
        ]
      )
    ],
    volumes: [
      hostPathVolume(
        hostPath: '/var/run/docker.sock', 
        mountPath: '/var/run/docker.sock'
      )
    ]
  ) {
    node(nlabel) {
      body()
    }
  }
}
