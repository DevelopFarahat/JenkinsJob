// seedJob.groovy
job('seed-job') {
    description('Seed job that generates all pipeline jobs from DSL scripts')

    scm {
        git {
            remote {
                url('https://github.com/DevelopFarahat/JenkinsJob')
            }
            branch('main')
        }
    }

    triggers {
        scm('H/5 * * * *') // poll every 5 minutes
    }

    steps {
        dsl {
            // Scan all groovy files under jenkins-dsl/jobs/ directory
            external('jenkins-dsl/jobs/*.groovy')
            removeAction('DELETE')   // delete jobs not in DSL
            ignoreExisting(false)
        }
    }
}
