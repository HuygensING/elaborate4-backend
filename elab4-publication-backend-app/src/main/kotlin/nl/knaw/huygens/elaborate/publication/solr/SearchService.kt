package nl.knaw.huygens.elaborate.publication.solr

import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import nl.knaw.huygens.facetedsearch.*
import nl.knaw.huygens.solr.FacetInfo
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.io.IOException
import javax.inject.Singleton
import javax.ws.rs.InternalServerErrorException
import kotlin.math.max
import kotlin.math.min

@Singleton
class SearchService
private constructor() {
    private val searchDataIndex: MutableMap<Long, SearchData> = Maps.newHashMap()

    private var solrServer: SolrServerWrapper? = null
        get() {
            if (field == null) {
                field = LocalSolrServer(solrDir, "entries", ElaborateQueryComposer())
            }
            return field
        }

    var solrDir: String? = null

    private var facetInfoMap: Map<String, FacetInfo>? = null
    private var rangeFields: List<RangeField>? = null
    private var facetFields: Array<String>? = null
    private var defaultSortOrder: Array<String>? = null

    var baseURL: String? = null
        private set

    init {
        loadConfig()
    }

    fun createSearch(elaborateSearchParameters: ElaborateSearchParameters): SearchData {
        elaborateSearchParameters
            .setFacetFields(facetFields)
            .setFacetInfoMap(facetInfoMap)
            .setRanges(rangeFields)
            .setLevelFields(defaultSortOrder!![0], defaultSortOrder!![1], defaultSortOrder!![2])
        return try {
            LOG.info("searchParameters={}", elaborateSearchParameters)
            val result = solrServer!!.search(elaborateSearchParameters)
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
            val sortableFields: MutableList<String> = Lists.newArrayList("id", "name")
            sortableFields.addAll(ImmutableList.copyOf(facetFields))
            resultsMap = searchData.results
            val ids = resultsMap.remove("ids") as List<String>?
            var results = resultsMap.remove("results") as List<MutableMap<String, Any?>>?
            LOG.info("start={}, rows={}", start, rows)
            val lo = toRange(start, 0, ids!!.size)
            val hi = toRange(lo + rows, 0, ids.size)
            LOG.info("lo={}, hi={}", lo, hi)
            results = results!!.subList(lo, hi)
            LOG.info("results={}", results)
            groupMetadata(results)
            LOG.info("after groupMetadata: results={}", results)
            resultsMap["ids"] = ids
            resultsMap["results"] = results
            resultsMap["start"] = lo
            resultsMap["rows"] = hi - lo
            resultsMap["sortableFields"] = sortableFields
        }
        return resultsMap
    }

    fun groupMetadata(results: List<MutableMap<String, Any?>>) {
        for (resultmap in results) {
            val metadata: MutableMap<String, String> = Maps.newHashMap()
            val keys: List<String> = ImmutableList.copyOf(resultmap.keys)
            for (key in keys) {
                if (key.startsWith(SolrUtils.METADATAFIELD_PREFIX)) {
                    val valueObject = resultmap.remove(key)
                    val facetInfo = facetInfoMap!![key]
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
                            } else if (values.size > 1) {
                                LOG.warn("unexpected: multiple values: {}", values)
                                metadata[name] = values[0]
                            }
                        }
                    }
                }
            }
            LOG.info("metadata:{}", metadata)
            resultmap["metadata"] = metadata
        }
    }

    fun removeExpiredSearches() {
        val cutoffDate = DateTime().minusDays(1).millis
        val keySet: Set<Long> = searchDataIndex.keys
        for (key in keySet) {
            if (key < cutoffDate) {
                searchDataIndex.remove(key)
            }
        }
    }

    fun toRange(value: Int, minValue: Int, maxValue: Int): Int {
        return min(max(value, minValue), maxValue)
    }

    fun loadConfig() {
        //		LOG.info("{}", Thread.currentThread().getContextClassLoader().getResource(".").getPath());
        try {
//            val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream("config.json")
//            val configMap = readConfigMap(inputStream)
//            facetInfoMap = toMap(configMap!!["facetInfoMap"])
//            rangeFields = toRangeFieldList(configMap["rangeFields"])
//            facetFields = toStringArray(configMap["facetFields"])
//            defaultSortOrder = toStringArray(configMap["defaultSortOrder"])
//            baseURL = configMap["baseURL"] as String?
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
        private val instance = SearchService()

        fun instance(): SearchService {
            return instance
        }

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

        //        fun toStringArray(obj: Any?): Array<String> {
//            return (obj as List<String?>?).toArray<String>(arrayOf<String>())
//        }
//
//        fun toMap(obj: Any): Map<String, FacetInfo> {
//            val inMap = obj as Map<String, Map<String, String>>
//            val outMap: MutableMap<String, FacetInfo> = Maps.newHashMapWithExpectedSize(inMap!!.size)
//            for ((key, value): Map.Entry<String, Map<String, String>> in inMap) {
//                outMap[key] = FacetInfo()
//                        .setName(value.get("name"))
//                        .setTitle(value.get("title"))
//                        .setType(FacetType.valueOf(value.get("type")!!))
//            }
//            return outMap
//        }
//
//        @Throws(IOException::class)
//        fun readConfigMap(inputStream: InputStream?): Map<String, Any>? {
//            val inputStreamReader = InputStreamReader(inputStream)
//            var configMap: Map<String, Any>? = ObjectMapper().readValue<Map<*, *>>(inputStreamReader, MutableMap::class.java)
//            if (configMap == null) {
//                configMap = Maps.newHashMap()
//            }
//            return configMap
//        }
        val LOG = LoggerFactory.getLogger(SearchService::class.java)
    }
}
