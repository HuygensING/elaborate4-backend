package nl.knaw.huygens.elaborate.publication

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration

class AppConfig(
        @JsonProperty("projectName") val projectName: String,
        @JsonProperty("publicationDir") val publicationDir: String
) : Configuration()