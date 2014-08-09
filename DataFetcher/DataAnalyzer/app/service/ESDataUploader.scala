package service

import play.api.db.DB
import anorm.SQL
import anorm.SqlParser._
import play.api.Logger
import play.api.libs.json._
import play.api.Play.current
import scala.concurrent.{future, ExecutionContext, Future}
import ExecutionContext.Implicits.{global => executeGlobal}


/**
 * 数据上传器，将数据库中的数据上传到ES中去
 *
 * 老牛 - 2014-8-9
 */
object ESDataUploader {

  /** 上传商户信息 */
  def uploadMerchatsFromDB: Unit = {
    DB.withConnection { implicit conn =>
      val sql = "select * from Merchant"
      SQL(sql)().map { r => Json.obj(
        "businessId" -> r[String]("businessId"),
        "name"       -> r[String]("name"),
        "branchName" -> r[Option[String]]("branchName"),
        "address"    -> r[String]("address"),
        "phone"      -> r[Option[String]]("phone"),
        "city"       -> r[Option[String]]("city"),
        "regions"    -> r[Option[String]]("regions"),
        "latitude"   -> r[Option[String]]("latitude"),
        "longitude"  -> r[Option[String]]("longitude"),
        "avgRating"  -> r[Int]("avgRating"),
        "productGrade" -> r[Int]("productGrade"),
        "decorationGrade" -> r[Int]("decorationGrade"),
        "serviceGrade" -> r[Int]("serviceGrade"),
        "productScore" -> r[Long]("productScore"),
        "decorationScore" -> r[Long]("decorationScore"),
        "serviceScore" -> r[Long]("serviceScore"),
        "logo" -> r[Option[String]]("logo"),
        "businessUrl" -> r[Option[String]]("businessUrl"),
        "reviewCount" -> r[Int]("reviewCount"),
        "distance"    -> r[Int]("distance")
      )}.toList.map { merch =>
        
        //TODO
      }
    }
  }

  private def getReviewsByMerchatIds(ids: List[Long]): Map[Long, Review] = {
    if(ids.nonEmpty) {
      import anorm.SeqParameter
      DB.withConnection { implicit conn =>
        SQL("""
          select 
            reviewId, busiId, textExcerpt, 
            reviewRating, productRating, decorationRating, serviceRating
          from Review where ids in ({businessIds})
        """).on("businessIds" -> SeqParameter(ids))().map { row =>
          val businessId = row[Long]("busiId")
          val crrReview = Review(
            businessId, 
            row[Long]("reviewId"), 
            row[String]("textExcerpt"),
            row[Int]("reviewRating"),
            row[Int]("productRating"),
            row[Int]("decorationRating"),
            row[Int]("serviceRating")
          )

          businessId -> crrReview
        }.toMap
      }
    } else {
      Map()
    }
  }
}

sealed case class Review(
  businessId: Long, 
  reviewId: Long, 
  textExcerpt: String,
  reviewRating: Int,
  productRating: Int, 
  decorationRating: Int,
  serviceRating: Int
)