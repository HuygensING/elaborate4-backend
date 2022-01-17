package nl.knaw.huygens.elaborate.publication

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration

class AppConfig(
    @JsonProperty("projectName") val projectName: String,
    @JsonProperty("dataDir") val dataDir: String,
    @JsonProperty("solrDir") val solrDir: String
) : Configuration()