<?xml version='1.0' encoding='UTF-8'?>
<!--
  #%L
  elab4-backend
  =======
  Copyright (C) 2011 - 2014 Huygens ING
  =======
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public
  License along with this program.  If not, see
  <http://www.gnu.org/licenses/gpl-3.0.html>.
  #L%
  -->


<!-- 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 -->

<!-- 
  Simple transform of Solr query results to RSS
 -->

<xsl:stylesheet version='1.0'
    xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>

  <xsl:output
       method="xml"
       encoding="utf-8"
       media-type="application/xml"
  />
  <xsl:template match='/'>
    <rss version="2.0">
       <channel>
	 <title>Example Solr RSS 2.0 Feed</title>
         <link>http://localhost:8983/solr</link>
         <description>
          This has been formatted by the sample "example_rss.xsl" transform -
          use your own XSLT to get a nicer RSS feed.
         </description>
         <language>en-us</language>
         <docs>http://localhost:8983/solr</docs>
         <xsl:apply-templates select="response/result/doc"/>
       </channel>
    </rss>
  </xsl:template>
  
  <!-- search results xslt -->
  <xsl:template match="doc">
    <xsl:variable name="id" select="str[@name='id']"/>
    <xsl:variable name="timestamp" select="date[@name='timestamp']"/>
    <item>
      <title><xsl:value-of select="str[@name='name']"/></title>
      <link>
        http://localhost:8983/solr/select?q=id:<xsl:value-of select="$id"/>
      </link>
      <description>
        <xsl:value-of select="arr[@name='features']"/>
      </description>
      <pubDate><xsl:value-of select="$timestamp"/></pubDate>
      <guid>
        http://localhost:8983/solr/select?q=id:<xsl:value-of select="$id"/>
      </guid>
    </item>
  </xsl:template>
</xsl:stylesheet>
