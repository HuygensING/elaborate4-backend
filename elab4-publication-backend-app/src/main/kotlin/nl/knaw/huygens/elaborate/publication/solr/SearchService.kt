package nl.knaw.huygens.elaborate.publication.solr

import java.io.File
import java.io.IOException
import javax.inject.Singleton
import javax.ws.rs.InternalServerErrorException
import kotlin.math.max
import kotlin.math.min
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import nl.knaw.huygens.facetedsearch.*
import nl.knaw.huygens.solr.FacetInfo
import nl.knaw.huygens.solr.FacetType

@Singleton
class SearchService(private val solrDir: String, private val solrConfigFile: File) {

    private val searchDataIndex: MutableMap<Long, SearchData> = mutableMapOf()

    private val solrServer: SolrServerWrapper by lazy {
        LocalSolrServer(solrDir, "entries", ElaborateQueryComposer())
    }

    private lateinit var facetInfoMap: Map<String, FacetInfo>
    private lateinit var rangeFields: List<RangeField>
    private lateinit var facetFields: Array<String?>
    private lateinit var defaultSortOrder: Array<String?>

    private var lastCleanUpDay = 0

    init {
        loadConfig()
    }

    fun createSearch(elaborateSearchParameters: ElaborateSearchParameters): SearchData {
        val currentDay = DateTime.now().dayOfYear
        if (currentDay != lastCleanUpDay) {
            removeExpiredSearches()
            lastCleanUpDay = currentDay
        }
        elaborateSearchParameters
                .setFacetFields(facetFields)
                .setFacetInfoMap(facetInfoMap)
                .setRanges(rangeFields)
                .setLevelFields(defaultSortOrder[0], defaultSortOrder[1], defaultSortOrder[2])
        return try {
            LOG.info("searchParameters={}", elaborateSearchParameters)
            val result = solrServer.search(elaborateSearchParameters)
            LOG.info("result={}", result)
            val searchData = SearchData().setResults(result)
            searchDataIndex[searchData.id] = searchData
            searchData
        } catch (e: Exception) {
            LOG.error(e.message)
            LOG.error("e={}", e)
            e.printStackTrace()
            throw InternalServerErrorException(e.message)
        }
    }

    fun getSearchResult(searchId: Long, start: Int, rows: Int): Map<String, Any> {
        var resultsMap: MutableMap<String, Any> = Maps.newHashMap()
        val searchData = searchDataIndex[searchId]
        //		Map<String, String> fieldnameMap = getFieldnameMap();
        if (searchData != null) {
            val sortableFields: MutableList<String?> = Lists.newArrayList("id", "name")
            sortableFields.addAll(facetFields.asList())
            resultsMap = searchData.results
            val ids = resultsMap.remove("ids") as List<String>?
            var results = resultsMap.remove("results") as List<MutableMap<String, Any?>>?
//            LOG.info("start={}, rows={}", start, rows)
            val lo = toRange(start, 0, ids!!.size)
            val hi = toRange(lo + rows, 0, ids.size)
//            LOG.info("lo={}, hi={}", lo, hi)
            results = results!!.subList(lo, hi)
            LOG.info("results={}", results)
            results.groupMetadata()
            LOG.info("after groupMetadata: results={}", results)
            resultsMap["ids"] = ids
            resultsMap["results"] = results
            resultsMap["start"] = lo
            resultsMap["rows"] = hi - lo
            resultsMap["sortableFields"] = sortableFields
        }
        return resultsMap
    }

    private fun List<MutableMap<String, Any?>>.groupMetadata() {
        for (resultmap in this) {
            val metadata: MutableMap<String, String> = Maps.newHashMap()
            val keys: List<String> = ImmutableList.copyOf(resultmap.keys)
            for (key in keys) {
                if (key.startsWith(SolrUtils.METADATAFIELD_PREFIX)) {
                    val valueObject = resultmap.remove(key)
                    val facetInfo = facetInfoMap[key]
                    if (facetInfo != null) {
                        val name = facetInfo.title
                        if (valueObject == null) {
                            metadata[name] = SolrUtils.EMPTYVALUE_SYMBOL
                        } else if (valueObject is List<*>) {
                            val values = valueObject as List<String>
                            if (values.isEmpty()) {
                                metadata[name] = SolrUtils.EMPTYVALUE_SYMBOL
                            } else if (values.size == 1) {
                                metadata[name] = values[0]
                            } else {
                                LOG.warn("unexpected: multiple values: {}", values)
                                metadata[name] = values[0]
                            }
                        }
                    }
                }
            }
//            LOG.info("metadata:{}", metadata)
            resultmap["metadata"] = metadata
        }
    }

    private fun removeExpiredSearches() {
        val cutoffDate = DateTime().minusDays(1).millis
        for (key in searchDataIndex.keys) {
            if (key < cutoffDate) {
                searchDataIndex.remove(key)
            }
        }
    }

    private fun toRange(value: Int, minValue: Int, maxValue: Int): Int {
        return min(max(value, minValue), maxValue)
    }

    private fun loadConfig() {
        try {
            val json = solrConfigFile.readText()
            val configMap = readConfigMap(json)
            facetInfoMap = toMap(configMap["facetInfoMap"])
            rangeFields = toRangeFieldList(configMap["rangeFields"])
            facetFields = toStringArray(configMap["facetFields"])
            defaultSortOrder = toStringArray(configMap["defaultSortOrder"])
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getAllSearchResultIds(searchId: Long): List<String>? {
        try {
            val searchData = searchDataIndex[searchId]
            if (searchData != null) {
                val resultsMap = searchData.results
                return resultsMap.remove("ids") as List<String>?
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return ImmutableList.of()
    }

    companion object {

        fun toRangeFieldList(obj: Any?): List<RangeField> {
            val list: MutableList<RangeField> = Lists.newArrayList()
            if (obj == null) {
                return list
            }
            val mapList = obj as List<Map<String, Any>>
            for (map in mapList) {
                list.add(
                        RangeField(
                                map["name"] as String?,
                                map["lowerField"] as String?,
                                map["upperField"] as String?
                        )
                )
            }
            return list
        }

        fun toStringArray(obj: Any?): Array<String?> {
            return (obj as List<String?>?)!!.toTypedArray()
        }

        fun toMap(obj: Any?): Map<String, FacetInfo> {
            val inMap = obj as Map<String, Map<String, String>>
            val outMap: MutableMap<String, FacetInfo> = Maps.newHashMapWithExpectedSize(inMap.size)
            for ((key, value) in inMap) {
                outMap[key] = FacetInfo()
                        .setName(value["name"])
                        .setTitle(value["title"])
                        .setType(FacetType.valueOf(value["type"]!!))

            }
            return outMap
        }

        fun readConfigMap(json: String): Map<String, Any> {
            val mapper = ObjectMapper().registerModule(KotlinModule())
            var configMap: MutableMap<String, Any> = mapper.readValue(json)
            if (configMap == null) {
                configMap = Maps.newHashMap()
            }
            return configMap
        }

        val LOG: Logger = LoggerFactory.getLogger(SearchService::class.java)
    }
}
