package com.ebay.dataquality.common

import com.ebay.dataquality.pojos.ClassNameAllowedValues
import com.ebay.dataquality.util.{EnvUtil, SpecialTagCheck}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

trait TDao {

  def readFile(path: String): RDD[String] = {
    EnvUtil.take().sparkContext.textFile(path)
  }

  def executeSparkSQL(sparkSQL: String): DataFrame = {
    EnvUtil.take().sql(sparkSQL)
  }

  def prptyTagMap: Map[String, (String, String)] = {
    val spark: SparkSession = EnvUtil.take()

    val sql: String =
      """
        | SELECT
        |  PRPTY_DATA_TYPE_TXT,
        |  PRPTY_SOJ_NAME,
        |  PRPTY_ALLOWED_VALUE_TXT
        | FROM
        |  GDW_TABLES.DW_SOJ_PRPTY_LKP
        | WHERE
        |  PRPTY_DATA_TYPE_TXT IS NOT NULL
        |  AND lower(PRPTY_DATA_TYPE_TXT) LIKE 'java%'
        |  AND PRPTY_ALLOWED_VALUE_TXT IS NOT NULL
        |  AND length(trim(PRPTY_ALLOWED_VALUE_TXT)) > 0
        |  AND lower(trim(PRPTY_ALLOWED_VALUE_TXT)) NOT LIKE '%test%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%string%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%missing%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%?%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%na%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%n/a%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%to%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%comma separated%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%null%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%list%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%any%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%keyword%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%time%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%1-%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%...%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%*%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%image%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%sample%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%true/false%'
        |  AND lower(PRPTY_ALLOWED_VALUE_TXT) NOT LIKE '%tt%'
        |  AND lower(PRPTY_SOJ_NAME) NOT LIKE '%test%'
        |  AND PRPTY_SOJ_NAME NOT IN(
        |    'xs_seeditm',
        |    'hpbb',
        |    'bt_user',
        |    'pfcm',
        |    'mbsa',
        |    'tst',
        |    'noep',
        |    'ShoHidModules',
        |    'psaenable',
        |    'SRT',
        |    'lnavt',
        |    'testProp0228',
        |    'ShoPrefUserId',
        |    'nofp',
        |    'fimbsa',
        |    'ADCBS',
        |    'nozp',
        |    'sopt',
        |    'bulkToolDisplayedLotIds',
        |    'TestBraavosProperty0910-2',
        |    'lsrg',
        |    'gdipp'
        |  )
        |""".stripMargin

    spark.sql(sql).map(row => (row.getString(0), (row.getString(1), row.getString(2))))(Encoders.product).collect().toMap
  }

  def specialTagValues(): Map[String, List[Any]] = {
    val map = mutable.Map[String, List[Any]]()
    map.put("t", siteIdRule())
    map.toMap
  }

  def siteIdRule() = {
    val spark: SparkSession = EnvUtil.take()
    val sql =
      """
        | select site_id from ACCESS_VIEWS.dw_sites
        |""".stripMargin
    spark.sql(sql).map(row => row.getDecimal(0))(Encoders.DECIMAL).collect().toList
  }

  def profileTagSize(yesterday: String, classNameToAllowedValues: Map[String, ClassNameAllowedValues], specialTagAllowedValues: Map[String, List[Any]]): DataFrame = {
    val spark: SparkSession = EnvUtil.take()
    spark.conf.set("spark.sql.parquet.enableVectorizedReader", "false")
    spark.conf.set("spark.sql.objectHashAggregate.sortBased.fallbackThreshold", 4096)
    spark.udf.register("tagSizeUDAF", functions.udaf(new TagSizeUDAF(classNameToAllowedValues, specialTagAllowedValues)))
    spark.sql(s"select PAGE_ID, sojlib.soj_nvl(soj, 'app') as app, sojlib.soj_nvl(soj,'efam') as event_family, str_to_map(soj, '&', '=') as sojMap, SESSION_START_DT dt FROM UBI_V.UBI_EVENT WHERE SESSION_START_DT = '$yesterday'").createOrReplaceTempView("t1")
    spark.sql("select PAGE_ID page_id, app, event_family, tagSizeUDAF(sojMap) as tag_size_attr, dt from t1 group by dt, PAGE_ID, app, event_family")
  }

  def profileTagSizeBot(yesterday: String, classNameToAllowedValues: Map[String, ClassNameAllowedValues], specialTagAllowedValues: Map[String, List[Any]]): DataFrame = {
    val spark: SparkSession = EnvUtil.take()
    spark.conf.set("spark.sql.parquet.enableVectorizedReader", "false")
    spark.conf.set("spark.sql.objectHashAggregate.sortBased.fallbackThreshold", 4096)
    spark.udf.register("tagSizeUDAF", functions.udaf(new TagSizeUDAF(classNameToAllowedValues, specialTagAllowedValues)))
    spark.sql(s"select PAGE_ID, sojlib.soj_nvl(soj, 'app') as app, sojlib.soj_nvl(soj,'efam') as event_family, str_to_map(soj, '&', '=') as sojMap, SESSION_START_DT dt FROM UBI_V.UBI_EVENT_SKEW WHERE SESSION_START_DT = '$yesterday'").createOrReplaceTempView("t1skew")
    spark.sql("select PAGE_ID page_id, app, event_family, tagSizeUDAF(sojMap) as tag_size_attr, dt from t1skew group by dt, PAGE_ID, app, event_family")
  }

  def collectPageTagMapping(yesterday: String, env: String): Unit = {
    val spark: SparkSession = EnvUtil.take()
    spark.conf.set("spark.sql.parquet.enableVectorizedReader", "false")
    val dataFrame = spark.sql(s"select PAGE_ID, str_to_map(soj, '&', '=') as sojMap, SESSION_START_DT dt FROM UBI_V.UBI_EVENT WHERE SESSION_START_DT = '$yesterday'")
    val df: Dataset[PageTagMapping] = dataFrame.flatMap(row => {
      val page_id = row.getInt(0)
      val date = row.getDate(2)
      val dt = FastDateFormat.getInstance("yyyy-MM-dd").format(date)
      val list = new ListBuffer[PageTagMapping]
      val map = row.getMap[String, String](1)
      map.foreach {
        case (key, _) =>
          list.append(PageTagMapping(page_id, key, "non-bot", dt))
      }
      list
    })(Encoders.product[PageTagMapping]).distinct()
    df.write.mode(SaveMode.Append).option("path", "hdfs://hercules/sys/edw/working/ubi/ubi_w/tdq/tdq_metadata_page_tag").insertInto("ubi_w.tdq_metadata_page_tag")
  }

  def collectPageTagMappingBot(yesterday: String, env: String): Unit = {
    val spark: SparkSession = EnvUtil.take()
    spark.conf.set("spark.sql.parquet.enableVectorizedReader", "false")
    val dataFrame = spark.sql(s"select PAGE_ID, str_to_map(soj, '&', '=') as sojMap, SESSION_START_DT dt FROM UBI_V.UBI_EVENT_SKEW WHERE SESSION_START_DT = '$yesterday'")
    val df: Dataset[PageTagMapping] = dataFrame.flatMap(row => {
      val page_id = row.getInt(0)
      val date = row.getDate(2)
      val dt = FastDateFormat.getInstance("yyyy-MM-dd").format(date)
      val list = new ListBuffer[PageTagMapping]
      val map = row.getMap[String, String](1)
      map.foreach {
        case (key, _) =>
          list.append(PageTagMapping(page_id, key, "bot", dt))
      }
      list
    })(Encoders.product[PageTagMapping]).distinct()
    df.write.mode(SaveMode.Append).option("path", "hdfs://hercules/sys/edw/working/ubi/ubi_w/tdq/tdq_metadata_page_tag").insertInto("ubi_w.tdq_metadata_page_tag")
  }

  def collectPageModuleMapping(yesterday: String, env: String): Unit = {
    val spark: SparkSession = EnvUtil.take()

    val sql =
      s"""
        | select
        |   cast(
        |     case
        |       when sojlib.soj_nvl(soj, 'sid') like 'p%' then str_to_map(sojlib.soj_nvl(soj, 'sid'), '\\.', '') ['p']
        |       else null
        |     end as bigint
        |   ) as page_id,
        |   cast(
        |     case
        |       when sojlib.soj_nvl(soj, 'moduledtl') like '%mi:%' then str_to_map(sojlib.soj_nvl(soj, 'moduledtl'), '\\|', ':') ['mi']
        |       when cast(
        |         regexp_replace(sojlib.soj_nvl(soj, 'moduledtl'), '[^0-9.]', '') as double
        |       ) = sojlib.soj_nvl(soj, 'moduledtl') then sojlib.soj_nvl(soj, 'moduledtl')
        |       when sojlib.soj_nvl(soj, 'sid') like '%.m%' then str_to_map(sojlib.soj_nvl(soj, 'sid'), '\\.', '') ['m']
        |       else null
        |     end as bigint
        |   ) as module_id,
        |   SESSION_START_DT dt
        | from
        |   UBI_V.UBI_EVENT
        | where
        |   sojlib.soj_nvl(soj, 'eactn') in ("EXPM", "VIEW", "VIEWDTLS")
        |   and SESSION_START_DT = '$yesterday'
        |   and page_id in (
        |     select
        |       PAGE_ID
        |     from
        |       ACCESS_VIEWS.PAGES
        |     WHERE
        |       PAGE_FMLY4_NAME in ("GR", "GR-1")
        |   )
        | group by 1, 2, 3
        |""".stripMargin

    val dataFrame = spark.sql(sql)
    dataFrame.printSchema()
    println(dataFrame.count())
    dataFrame.collect().foreach(row => {
      println(row.get(0) + ", " + row.get(1) + ", " + row.get(2))
    })

    dataFrame.write.mode(SaveMode.Append).option("path", "hdfs://hercules/sys/edw/working/ubi/ubi_w/tdq/tdq_metadata_page_module").insertInto("ubi_w.tdq_metadata_page_module")
  }

  def collectPageClickMapping(yesterday: String, env: String): Unit = {
    val spark: SparkSession = EnvUtil.take()
    val sql =
      s"""
        | insert into table `ubi_w`.`tdq_metadata_page_click` partition(dt = "$yesterday")
        | select
        |   cast(
        |     case
        |       when sojlib.soj_nvl(soj, 'sid') like 'p%' then str_to_map(sojlib.soj_nvl(soj, 'sid'), '\\.', '') ['p']
        |       else null
        |     end as bigint
        |   ) as page_id,
        |   cast(
        |     case
        |       when sojlib.soj_nvl(soj, 'sid') like '%.l%' then str_to_map(sojlib.soj_nvl(soj, 'sid'), '\\.', '') ['l']
        |       when sojlib.soj_nvl(soj, 'moduledtl') like '%|li:%' then str_to_map(sojlib.soj_nvl(soj, 'moduledtl'), '\\|', ':') ['li']
        |       else null
        |     end as bigint
        |   ) as click_id
        | from
        |   UBI_V.UBI_EVENT
        | where
        |   sojlib.soj_nvl(soj, 'sid') like 'p%m%l%'
        |   and SESSION_START_DT = "$yesterday"
        |   and page_id in (
        |     select
        |       PAGE_ID
        |     from
        |       ACCESS_VIEWS.PAGES
        |     WHERE
        |       PAGE_FMLY4_NAME in ("GR", "GR-1")
        |   )
        |   group by 1, 2
        |""".stripMargin

    spark.sql(sql)
  }

}

/**
 *
 * @param total total of tag size
 * @param count count of tag
 * @param max   max of tag size
 */
case class TagAttribute(var total: Long, var count: Long, var max: Long, var inconsistentFormatTagsCount: Long, var inaccurateTagCount: Long, var errors: ListBuffer[String])

/**
 *
 * @param total  total of pageId
 * @param tagMap tag size map
 */
case class TagBuffer(var total: Long, var tagMap: mutable.Map[String, TagAttribute], var totalLength: Long)

class TagSizeUDAF(classNameToAllowedValues: Map[String, ClassNameAllowedValues], specialTagAllowedValues: Map[String, List[Any]]) extends Aggregator[Map[String, String], TagBuffer, String] {

  override def zero: TagBuffer = TagBuffer(0L, mutable.Map[String, TagAttribute](), 0L)

  def checkValueType(className: String, s: String): Boolean = {
    try {
      className match {
        case "java.lang.String" =>
          return true
        case "java.lang.Long" =>
          s.toLong
        case "java.lang.Integer" =>
          s.toInt
        case "java.lang.Double" =>
          s.toDouble
        case "java.lang.Boolean" =>
          s.toLowerCase.toBoolean
        case "java.lang.Float" =>
          s.toFloat
      }
      true
    } catch {
      case _: NumberFormatException => false
      case _: IllegalArgumentException => false
    }
  }

  def strToValue(className: String, s: String): Any = {
    className match {
      case "java.lang.String" =>
        s
      case "java.lang.Long" =>
        s.toLong
      case "java.lang.Integer" =>
        s.toInt
      case "java.lang.Double" =>
        s.toDouble
      case "java.lang.Boolean" =>
        s.toLowerCase.toBoolean
      case "java.lang.Float" =>
        s.toFloat
      case _ => null
    }
  }

  // we only need to add rule name and rule function
  val func: PartialFunction[String, String => Boolean] = {
    case "guid" => guid => SpecialTagCheck.checkGuid(guid)
    case "siteId" => siteId => SpecialTagCheck.checkSiteId(siteId, specialTagAllowedValues.get("t"))
    case "mav" => mobileVersion => SpecialTagCheck.mobileVersionCheck(mobileVersion)
    case "icpp" => pagination => SpecialTagCheck.paginationCheck(pagination)
    case "ist" => ist => SpecialTagCheck.isTableOrNot(ist)
    case "order_cnt" => orderCnt => SpecialTagCheck.orderCntCheck(orderCnt)
  }

  // we only add mapping for tag and its ruleName
  val checkTagMap: Map[String, String] = Map(
    "g" -> "guid",
    "t" -> "siteId",
    "mav" -> "mav",
    "icpp" -> "icpp",
    "ist" -> "ist",
    "order_cnt" -> "order_cnt"
  )

  def checkTagValue(tagName: String, tagValue: String, checkTagMap: Map[String, String]): Boolean = {
    val ruleOption: Option[String] = checkTagMap.get(tagName)
    if (ruleOption.isDefined) {
      val ruleName: String = ruleOption.get
      val ruleFun: String => Boolean = func(ruleName)
      return ruleFun(tagValue)
    }
    true
  }

  override def reduce(buf: TagBuffer, map1: Map[String, String]): TagBuffer = {
    val sojMap = map1.filter {
      case (tagName, _) => !(tagName.startsWith(".") || tagName.endsWith("."))
    }
    var total: Long = buf.total
    total = total + 1
    val map: mutable.Map[String, TagAttribute] = buf.tagMap
    val tagSizeMap: Map[String, Int] = sojMap.mapValues(_.length).map(identity)
    tagSizeMap.foreach {
      case (tagName, len) =>
        val tagAttribute: TagAttribute = map.getOrElse(tagName, TagAttribute(0L, 0L, 0L, 0L, 0L, ListBuffer[String]()))
        tagAttribute.total += len
        tagAttribute.count += 1
        tagAttribute.max = math.max(tagAttribute.max, len)
        val tagValueOption: Option[String] = sojMap.get(tagName)
        if (tagValueOption.isDefined) {
          val tagValue: String = tagValueOption.get
          if (classNameToAllowedValues.contains(tagName)) {
            val classNameToValuesOption: Option[ClassNameAllowedValues] = classNameToAllowedValues.get(tagName)
            if (classNameToValuesOption.isDefined) {
              val classNameAndValues: ClassNameAllowedValues = classNameToValuesOption.get
              val judgeResult: Boolean = checkValueType(classNameAndValues.className, tagValue)
              if (!judgeResult) {
                tagAttribute.inconsistentFormatTagsCount += 1
                tagAttribute.inaccurateTagCount += 1
              } else {
                val result: Any = strToValue(classNameAndValues.className, tagValue)
                if (!classNameAndValues.allowedValues.contains(result)) {
                  if (tagName == "g"){
                    if (tagAttribute.errors.size < 10)
                      tagAttribute.errors.append("guid:" + tagValue + ", allowedValues:" + classNameAndValues.allowedValues.mkString(", "))
                  }
                  tagAttribute.inaccurateTagCount += 1
                }
              }
            }
          } else {
            if (!checkTagValue(tagName, tagValue, checkTagMap)) {
              if (tagName == "g"){
                tagAttribute.errors.append("guid:" + tagValue + ", checkTagValue")
              }
              tagAttribute.inaccurateTagCount += 1
            }
          }
        }
        map.update(tagName, tagAttribute)
    }
    buf.total = total
    buf.tagMap = map
    buf
  }

  override def merge(b1: TagBuffer, b2: TagBuffer): TagBuffer = {
    b1.total += b2.total
    val tagMap1: mutable.Map[String, TagAttribute] = b1.tagMap
    val tagMap2: mutable.Map[String, TagAttribute] = b2.tagMap
    tagMap2.foreach {
      case (tagName, tagAttribute) =>
        val maybeAttribute: Option[TagAttribute] = tagMap1.get(tagName)
        if (maybeAttribute.isEmpty) {
          tagMap1.update(tagName, tagAttribute)
        } else {
          val tagAttribute1: TagAttribute = maybeAttribute.get
          tagAttribute1.total += tagAttribute.total
          tagAttribute1.count += tagAttribute.count
          tagAttribute1.inconsistentFormatTagsCount += tagAttribute.inconsistentFormatTagsCount
          tagAttribute1.inaccurateTagCount += tagAttribute.inaccurateTagCount
          tagAttribute1.max = math.max(tagAttribute1.max, tagAttribute.max)
          if (tagAttribute1.errors.isEmpty)
            tagAttribute1.errors.appendAll(tagAttribute.errors)
          tagMap1.update(tagName, tagAttribute1)
        }
    }
    b1.tagMap = tagMap1
    b1
  }

  override def finish(reduction: TagBuffer): String = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    val tagMap: mutable.Map[String, TagAttribute] = reduction.tagMap
    reduction.totalLength = tagMap.values.map(tagAttr => tagAttr.total).sum
    mapper.writeValueAsString(reduction)
  }

  override def bufferEncoder: Encoder[TagBuffer] = Encoders.product

  override def outputEncoder: Encoder[String] = Encoders.STRING
}

case class PageTagMapping(page_id: Int, tag_name: String, bot_flag: String, dt: String)

case class PageModuleMapping(page_id: Int, module_id: Long, dt: String)
case class PageClickMapping(page_id: Int, click_id: Long, dt: String)