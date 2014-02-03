/**
 * Copyright (C) 2013 Orbeon, Inc.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The full text of the license is available at http://www.gnu.org/copyleft/lesser.html
 */
package org.orbeon.oxf.fr.persistence

import org.dom4j.Document
import org.junit.Test
import org.orbeon.oxf.fr.relational._
import org.orbeon.oxf.resources.URLFactory
import org.orbeon.oxf.test.{TestSupport, ResourceManagerTestBase}
import org.orbeon.oxf.util._
import org.orbeon.oxf.xml.Dom4j.elemToDocument
import org.orbeon.oxf.xml.dom4j.Dom4jUtils
import org.scalatest.junit.AssertionsForJUnit
import util.Try
import ScalaUtils._
import org.orbeon.oxf.fr.relational.{Specific, Next, Latest, Version}

class RestApiTest extends ResourceManagerTestBase with AssertionsForJUnit with DatabaseConnection with TestSupport {

    val MySQLBase = "http://localhost:8080/orbeon/fr/service/mysql"
    private implicit val Logger = new IndentedLogger(LoggerFactory.createLogger(classOf[RestApiTest]), "")

    def withOrbeonTables[T](block: java.sql.Connection ⇒ T) {
        asTomcat { connection ⇒
            val statement = connection.createStatement
            try {
                // Create tables
                val createDDL = readSQL(s"$Base/mysql-4_4.sql")
                createDDL foreach statement.executeUpdate

                // Run the interesting code
                block(connection)
            } finally {
                // Clean-up database dropping tables
                (getTableNames(connection)
                    map ("drop table " + _)
                    foreach statement.executeUpdate)
            }
        }
    }

    private def http(url: String, method: String, version: Version, body: Option[Document]): ConnectionResult = {
        val documentUrl = URLFactory.createURL(MySQLBase + url)
        val headers = {
            val dataSourceHeader  = Some("Orbeon-Datasource" → Array("mysql_test"))
            val contentTypeHeader = body map (_ ⇒ "Content-Type" → Array("application/xml"))
            val versionHeader =  version match {
                case Latest                  ⇒ None
                case Next                    ⇒ Some("Orbeon-Form-Definition-Version" → Array("next"))
                case Specific(version)       ⇒ Some("Orbeon-Form-Definition-Version" → Array(version.toString))
                case ForDocument(documentId) ⇒ Some("Orbeon-For-Document-Id" → Array(documentId))
            }
            val myHeaders = Seq(dataSourceHeader, contentTypeHeader, versionHeader).flatten.toMap
            Connection.buildConnectionHeaders(None, myHeaders, Option(Connection.getForwardHeaders))
        }
        val messageBody = body map Dom4jUtils.domToString map (_.getBytes)
        Connection(method, documentUrl, credentials = None, messageBody = messageBody, headers = headers,
            loadState = true, logBody = false).connect(saveState = true)
    }

    private def httpPut(url: String, version: Version, body: Document): Integer = {
        val result = http(url, "PUT", version, Some(body))
        val code = result.statusCode
        result.close()
        code
    }

    private def httpGet(url: String, version: Version): (Integer, Try[Document]) =
        useAndClose(http(url, "GET", version, None)) { connectionResult ⇒
            useAndClose(connectionResult.getResponseInputStream) { inputStream ⇒
                (connectionResult.statusCode, Try(Dom4jUtils.readDom4j(inputStream)))
            }
        }

    sealed trait Expected
    case   class ExpectedDoc (doc: Document) extends Expected
    case   class ExpectedCode(code: Integer) extends Expected

    private def assertGet(url: String, version: Version, expected: Expected): Unit = {
        val (resultCode, resultDoc) = httpGet(url, version)
        expected match {
            case ExpectedDoc(expectedDoc) ⇒
                assert(resultCode === 200)
                assertXMLDocuments(resultDoc.get, expectedDoc)
            case ExpectedCode(expectedCode) ⇒
                assert(resultCode === expectedCode)
        }
    }

    private def assertPut(url: String, version: Version, body: Document, expectedCode: Integer): Unit = {
        val actualCode = httpPut(url, version, body)
        assert(actualCode === expectedCode)
    }

    @Test def formDefinitionVersionTest(): Unit = {
        withOrbeonTables { connection =>
            val FormURL = "/crud-ng/acme/address/form/form.xml"

            // First time we put with "latest"
            val first: Document = <gaga1/>
            assertPut(FormURL, Latest, first, 201)
            assertGet(FormURL, Specific(1), ExpectedDoc (first))
            assertGet(FormURL, Latest     , ExpectedDoc (first))
            assertGet(FormURL, Specific(2), ExpectedCode(404))

            // Put again with "latest" updates the current version
            val second: Document = <gaga2/>
            assertPut(FormURL, Latest, second, 201)
            assertGet(FormURL, Specific(1), ExpectedDoc(second))
            assertGet(FormURL, Latest     , ExpectedDoc(second))
            assertGet(FormURL, Specific(2), ExpectedCode(404))

            // Put with "next" to get two versions
            val third: Document = <gaga3/>
            assertPut(FormURL, Next, third, 201)
            assertGet(FormURL, Specific(1), ExpectedDoc(second))
            assertGet(FormURL, Specific(2), ExpectedDoc(third))
            assertGet(FormURL, Latest     , ExpectedDoc(third))
            assertGet(FormURL, Specific(3), ExpectedCode(404))

            // Put a specific version
            val fourth: Document = <gaga4/>
            assertPut(FormURL, Specific(1), fourth, 201)
            assertGet(FormURL, Specific(1), ExpectedDoc(fourth))
            assertGet(FormURL, Specific(2), ExpectedDoc(third))
            assertGet(FormURL, Latest     , ExpectedDoc(third))
            assertGet(FormURL, Specific(3), ExpectedCode(404))
        }
    }

    @Test def formDataVersionTest(): Unit = {
        withOrbeonTables { connection =>
            val DataURL = "/crud-ng/acme/address/data/123/data.xml"

            // Storing for specific form version
            val first: Document = <gaga1/>
            assertPut(DataURL, Specific(1), first, 201)
            assertGet(DataURL, Specific(1), ExpectedDoc(first))
            assertGet(DataURL, Latest     , ExpectedDoc(first))

            // Version must be specified when storing data
            assertPut(DataURL, Latest            , first, 400)
            assertPut(DataURL, Next              , first, 400)
            assertPut(DataURL, ForDocument("123"), first, 400)
        }
    }
}
