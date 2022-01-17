package nl.knaw.huygens.elaborate.publication

import io.dropwizard.Application
import io.dropwizard.setup.Environment
import nl.knaw.huygens.elaborate.publication.resources.AboutResource
import nl.knaw.huygens.elaborate.publication.resources.DataResource
import nl.knaw.huygens.elaborate.publication.resources.RootResource
import nl.knaw.huygens.elaborate.publication.resources.SearchResource

class App : Application<AppConfig>() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            App().run(*args)
        }
    }

    override fun getName(): String {
        return "elab4-publication-app"
    }

    override fun run(config: AppConfig, env: Environment) {
        env.jersey().register(RootResource())
        env.jersey().register(AboutResource(config))
        env.jersey().register(DataResource(config))
        env.jersey().register(SearchResource(config.publicationDir))
    }
}